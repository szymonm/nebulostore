package org.nebulostore.testing.moduletest.gossiptest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.PeerDescriptor;
import org.nebulostore.communication.gossip.PeerGossipService;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;

/**
 * @author grzegorzmilka
 */
class MessageMedium implements Runnable {
  private final Map<CommAddress, PeerGossipService> gossipModules_;
  private final BlockingQueue<Message> inQueue_;
  private static Logger logger_ = Logger.getLogger(MessageMedium.class);

  MessageMedium(Map<CommAddress, PeerGossipService> gossipModules, BlockingQueue<Message> inQueue) {
    gossipModules_ = gossipModules;
    inQueue_ = inQueue;
  }

  public void run() {
    logger_.debug("Starting " + this);
    try {
      while (true) {
        dispatchMessage(inQueue_.take());
      }
    } catch (InterruptedException e) {
      logger_.debug("Stopping " + this + "due to interrupt.");
    }
  }

  public String toString() {
    return "General MessageMedium";
  }

  private void dispatchMessage(Message message) throws InterruptedException {
    assert message instanceof CommMessage || message instanceof ErrorCommMessage;
    CommAddress destAddress;
    if (message instanceof CommMessage) {
      CommMessage commMsg = (CommMessage) message;
      destAddress = commMsg.getDestinationAddress();
    } else {
      destAddress = ((ErrorCommMessage) message).getMessage().
        getDestinationAddress();
    }

    synchronized (gossipModules_) {
      PeerGossipService gossiper = gossipModules_.get(destAddress);
      if (gossiper == null && message instanceof CommMessage)  {
        inQueue_.put(new ErrorCommMessage((CommMessage) message,
              new Exception("Testing Arbitrary Exception")));
      } else {
        gossiper.getInQueue().put(message);
      }
    }
  }

}

/**
 * Manages churn of gossip modules.
 *
 * It creates new and deletes gossip modules as it wishes. In this
 * implementation it creates a given number of gossipers and destroys them only
 * when entire module is told to stop.
 *
 */
final class ChurnFactory {
  private final int gossipPeriod_;
  private final int maxPeersSize_;
  private final int healingFactor_;
  private final int swappingFactor_;
  private final CommAddress bootstrapCommAddr_;
  private final Map<CommAddress, PeerGossipService> gossipModules_;
  private final Map<CommAddress, Thread> gossipThreads_;
  private final int nGossipers_;
  private final BlockingQueue<Message> mediumInQueue_;
  private static Logger logger_ = Logger.getLogger(ChurnFactory.class);

  public ChurnFactory(Map<CommAddress, PeerGossipService> gossipModules,
      int nGossipers, BlockingQueue<Message> mediumInQueue,
      int gossipPeriod,
      int maxPeersSize,
      int healingFactor,
      int swappingFactor) {
    bootstrapCommAddr_ = CommAddress.newRandomCommAddress();
    gossipModules_ = gossipModules;
    gossipThreads_ = new HashMap<CommAddress, Thread>();
    nGossipers_ = nGossipers;
    mediumInQueue_ = mediumInQueue;
    gossipPeriod_ = gossipPeriod;
    maxPeersSize_ = maxPeersSize;
    healingFactor_ = healingFactor;
    swappingFactor_ = swappingFactor;
  }

  public void setUp() {
    createGossiper(bootstrapCommAddr_);
    for (int i = 0; i < nGossipers_ - 1; ++i) {
      createGossiper(CommAddress.newRandomCommAddress());
    }
  }

  public void cleanUp() {
    synchronized (gossipModules_) {
      while (!gossipModules_.isEmpty()) {
        deleteGossiper(gossipModules_.keySet().iterator().next());
      }
    }
  }

  private void createGossiper(CommAddress commAddr) {
    synchronized (gossipModules_) {
      if (gossipModules_.containsKey(commAddr)) {
        logger_.warn("Trying to add gossiper with address already present " +
            "in map. Something might be wrong.");
        return;
      }
    }

    BlockingQueue<Message> gossiperInQueue = new LinkedBlockingQueue<Message>();
    PeerGossipService gossipService = new PeerGossipService(gossiperInQueue,
        mediumInQueue_, commAddr, bootstrapCommAddr_, gossipPeriod_,
        maxPeersSize_, healingFactor_, swappingFactor_);

    synchronized (gossipModules_) {
      gossipModules_.put(commAddr, gossipService);
    }

    Thread gossipThread =
      new Thread(gossipService, "Nebulostore.Communication.GossipService(" +
          commAddr + ")");
    gossipThread.setDaemon(true);
    gossipThread.start();

    gossipThreads_.put(commAddr, gossipThread);
  }

  private void deleteGossiper(CommAddress gossiperAddr) {
    synchronized (gossipModules_) {
      PeerGossipService gossiper = gossipModules_.get(gossiperAddr);
      Thread gossipThread = gossipThreads_.get(gossiperAddr);
      gossipThreads_.remove(gossiperAddr);
      gossipModules_.remove(gossiperAddr);

      logger_.debug("Ending gossiper: " + gossiperAddr);
      gossiper.endModule();
      gossipThread.interrupt();
      while (true) {
        try {
          gossipThread.join();
          break;
        } catch (InterruptedException e) {
          logger_.warn("Caught InterruptedException when joining gossiper.");
        }
      }
      logger_.debug("Ended gossiper: " + gossiperAddr);
    }
  }
}

/**
 * @author grzegorzmilka
 */
public final class GossipTest {
  private static final Map<CommAddress, PeerGossipService> GOSSIP_MODULES =
    new HashMap<CommAddress, PeerGossipService>();

  private static final String USAGE = "GossipTest N_GOSSIPERS_ " +
    "GOSSIP_PERIOD MAX_PEERS_SIZE HEALING_FACTOR SWAPPING_FACTOR " +
    "TESTING_INTERVALS...";
  private static final int MIN_ARGS = 6;

  private static int nGossipers_;
  /**
   * BiggestComponentSize/NumberOfGossipers factor threshold at which cohesive
   * test passes.
   *
   * Due to the random nature of gossiping we sometimes get hosts
   * which are not visible from the network (temporarily). If such hosts
   * constitute only a small part of network we let this test pass.
   */
  private static final double COHESIVENESS_THRESHOLD = 0.80;

  /**
   * If given a node is N_OUT_OF_MAIN_ times out of main component we regard
   * this as an error.
   */
  private static final double N_OUT_OF_MAIN = 3;
  private static Map<CommAddress, Integer> outOfMainComponentMap_ =
    new HashMap<CommAddress, Integer>();
  private static Logger logger_;

  private GossipTest() {
  }

  public static void main(String[] args) {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    logger_ = Logger.getLogger(GossipTest.class);

    if (args.length < MIN_ARGS) {
      System.out.println(USAGE);

      System.exit(1);
    }

    nGossipers_ = Integer.parseInt(args[0]);
    System.exit(runTest(args) ? 0 : 1);
  }

  /**
   * @author grzegorzmilka
   */
  private static class GossiperNode {
    public int index_;
    public boolean wasVisited_;
    public CommAddress commAddr_;
    public int age_;
    public final Collection<Integer> neighbours_ = new TreeSet<Integer>();
    public GossiperNode(int newIndex, CommAddress newCommAddr) {
      index_ = newIndex;
      commAddr_ = newCommAddr;
    }
  }

  /**
   * @author grzegorzmilka
   */
  private static class GraphCreationReport {
    public ArrayList<GossiperNode> graph_ = new ArrayList<GossiperNode>();
    public int nObsoleteEdges_;
  }

  /**
   * @author grzegorzmilka
   */
  private static class GraphCohesivenessReport {
    public Collection<Collection<GossiperNode>> components_;
    public boolean isCohesive_;
  }

  /**
   * @author grzegorzmilka
   */
  private static class CheckCohesivenessReport {
    public Collection<Collection<GossiperNode>> components_;
    public boolean isCohesive_;
    public int sizeOfLargestComponent_;
  }

  private static CheckCohesivenessReport checkCohesiveness() {
    assert GOSSIP_MODULES.size() == nGossipers_;
    logger_.info("Starting cohesiveness test.");
    GraphCreationReport result = createGossiperGraph();
    GraphCohesivenessReport cohReport = isCohesive(result.graph_);

    logger_.info("Number of components: " + cohReport.components_.size());
    int i = 0;
    Set<GossiperNode> mainComponent = new HashSet<GossiperNode>();
    for (Collection<GossiperNode> component : cohReport.components_) {
      logger_.info("Component nr " + i + ":");
      if ((double) component.size() / nGossipers_ > 0.5) {
        mainComponent.addAll(component);
      }
      logger_.info(toStringGossiperGraph(component));
      ++i;
    }

    if (nGossipers_ > 0) {
      cohReport.isCohesive_ =
        cohReport.isCohesive_ ||
        (((double) mainComponent.size() / nGossipers_) > COHESIVENESS_THRESHOLD);
    }

    for (GossiperNode node : result.graph_) {
      if (!mainComponent.contains(node)) {
        Integer nTimesOutOfMain = outOfMainComponentMap_.get(node.commAddr_);
        if (nTimesOutOfMain == null) {
          outOfMainComponentMap_.put(node.commAddr_, 1);
        } else {
          if (nTimesOutOfMain >= N_OUT_OF_MAIN) {
            cohReport.isCohesive_ = false;
          }
          outOfMainComponentMap_.put(node.commAddr_, nTimesOutOfMain + 1);
        }
      } else {
        outOfMainComponentMap_.remove(node.commAddr_);
      }
    }

    logger_.info("Graph cohesive test: " + cohReport.isCohesive_ +
        ", Number of obsolete nodes still present: " + result.nObsoleteEdges_);
    CheckCohesivenessReport cCR = new CheckCohesivenessReport();
    cCR.components_ = cohReport.components_;
    cCR.isCohesive_ = cohReport.isCohesive_;
    cCR.sizeOfLargestComponent_ = mainComponent.size();


    return cCR;
  }

  private static GraphCreationReport createGossiperGraph() {
    Map<CommAddress, Integer> indexMap = new HashMap<CommAddress, Integer>();
    GraphCreationReport result = new GraphCreationReport();
    ArrayList<GossiperNode> gossiperGraph = result.graph_;

    Method getPeersMethod;

    synchronized (GOSSIP_MODULES) {
      try {
        getPeersMethod = PeerGossipService.class.getDeclaredMethod("getPeers");
      } catch (NoSuchMethodException e) {
        logger_.error("Exception: " + e + " when trying to get private method getPeers.");
        throw new RuntimeException(e.toString());
      }
      getPeersMethod.setAccessible(true);

      ArrayList<PeerGossipService> gossiperServiceGraph = new ArrayList<PeerGossipService>();

      for (CommAddress gossiperAddr : GOSSIP_MODULES.keySet()) {
        PeerGossipService gossiper = GOSSIP_MODULES.get(gossiperAddr);
        indexMap.put(gossiperAddr, gossiperGraph.size());
        gossiperGraph.add(new GossiperNode(gossiperGraph.size(), gossiperAddr));
        gossiperServiceGraph.add(gossiper);
      }

      Iterator<PeerGossipService> iterator = gossiperServiceGraph.iterator();
      for (GossiperNode gossiperNode : gossiperGraph) {
        Collection<PeerDescriptor> peers = new HashSet<PeerDescriptor>();
        try {
          peers = (Collection<PeerDescriptor>) getPeersMethod.invoke(iterator.next());
        } catch (IllegalAccessException e) {
          logger_.error("Exception: " + e + " when trying to invoke private " +
              "method getPeers.");
          throw new RuntimeException(e.toString());
        } catch (InvocationTargetException e) {
          logger_.error("Exception: " + e + " when trying to invoke private " +
              "method getPeers.");
          throw new RuntimeException(e.toString());
        }

        for (PeerDescriptor peer : peers) {
          Integer index = indexMap.get(peer.getPeerAddress());
          if (index == null) {
            result.nObsoleteEdges_++;
          } else {
            gossiperNode.neighbours_.add(index);
          }
        }
      }
    }

    return result;
  }

  /**
   * Make DFS on graph and return dfs trees.
   */
  private static Collection<Collection<GossiperNode>> dfs(ArrayList<GossiperNode> graph) {
    Collection<Collection<GossiperNode>> components = new HashSet<Collection<GossiperNode>>();
    Deque<GossiperNode> dfsNodeStack = new LinkedList<GossiperNode>();
    Deque<Iterator<Integer>> dfsIteratorStack = new LinkedList<Iterator<Integer>>();
    int curAge = 0;
    for (GossiperNode node : graph) {
      if (node.wasVisited_) {
        continue;
      }
      Collection<GossiperNode> component = new HashSet<GossiperNode>();
      component.add(node);
      dfsNodeStack.add(node);
      dfsIteratorStack.add(node.neighbours_.iterator());
      while (!dfsNodeStack.isEmpty()) {
        assert !dfsNodeStack.isEmpty();
        GossiperNode gossiper = dfsNodeStack.pop();
        Iterator<Integer> iter = dfsIteratorStack.pop();
        while (iter.hasNext()) {
          int index = iter.next();
          GossiperNode neighbour = graph.get(index);
          if (!neighbour.wasVisited_) {
            component.add(neighbour);
            neighbour.wasVisited_ = true;
            dfsNodeStack.push(gossiper);
            dfsNodeStack.push(neighbour);
            dfsIteratorStack.push(iter);
            dfsIteratorStack.push(neighbour.neighbours_.iterator());
            break;
          }

        }
        gossiper.age_ = curAge++;
      }

      components.add(component);
    }

    return components;
  }

  /**
   * Check if given graph is strongly cohesive.
   */
  private static GraphCohesivenessReport isCohesive(ArrayList<GossiperNode> graph) {
    GraphCohesivenessReport report = new GraphCohesivenessReport();
    Collection<Collection<GossiperNode>> components = dfs(graph);

    if (components.size() != 1 && graph.size() != 0) {
      report.components_ = components;
      report.isCohesive_ = false;
      return report;
    }

    ArrayList<GossiperNode> transGraph = new ArrayList<GossiperNode>();
    for (GossiperNode gossiper : graph) {
      GossiperNode transGossiper = new GossiperNode(gossiper.index_, gossiper.commAddr_);
      transGossiper.age_ = gossiper.age_;
      transGraph.add(transGossiper);
    }

    for (int i = 0; i < graph.size(); ++i) {
      for (int index : graph.get(i).neighbours_) {
        transGraph.get(index).neighbours_.add(i);
      }
    }

    java.util.Collections.sort(transGraph, new Comparator<GossiperNode>() {
      @Override
      public int compare(GossiperNode g1, GossiperNode g2) {
        return g2.age_ - g1.age_;
      }
    });

    components = dfs(transGraph);

    if (components.size() != 1 && transGraph.size() != 0) {
      report.components_ = components;
      report.isCohesive_ = false;
      return report;
    }

    report.components_ = components;
    report.isCohesive_ = true;

    return report;
  }

  /**
   * Run test.
   */
  private static boolean runTest(String[] args) {
    BlockingQueue<Message> mediumInQueue = new LinkedBlockingQueue<Message>();

    MessageMedium medium = new MessageMedium(GOSSIP_MODULES, mediumInQueue);
    Thread mediumThread = new Thread(medium, "Nebulostore.Testing.MediumThread");
    mediumThread.setDaemon(true);
    mediumThread.start();

    ChurnFactory churn = new ChurnFactory(GOSSIP_MODULES, nGossipers_,
        mediumInQueue, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
        Integer.parseInt(args[3]), Integer.parseInt(args[4]));

    churn.setUp();
    CheckCohesivenessReport result = new CheckCohesivenessReport();
    result.isCohesive_ = true;
    int secFromStart = 0;
    double avgNOfClusters = 0.0;
    int smallestLargestCluster = -1;
    for (int i = 5; i < args.length && result.isCohesive_; ++i) {
      int period = Integer.parseInt(args[i]);
      secFromStart += period;
      try {
        Thread.sleep(period);
      } catch (InterruptedException e) {
        logger_.info("Interrupted exception.");
      }
      result = checkCohesiveness();
      avgNOfClusters += result.components_.size();
      if (smallestLargestCluster == -1) {
        smallestLargestCluster = result.sizeOfLargestComponent_;
      } else {
        smallestLargestCluster =
          java.lang.Math.min(smallestLargestCluster,
              result.sizeOfLargestComponent_);
      }
      System.out.printf("(%d) isCohesive: %b, nOfComponents %n, " +
          "size of largest component: %d.%n", secFromStart, result.isCohesive_,
          result.components_.size(), result.sizeOfLargestComponent_);
      logger_.info("Result of test nr. " + (i - 5)  + ": " + result.isCohesive_);
    }
    avgNOfClusters /= args.length - 5.;
    System.out.printf("Test finished with result : %b. avgNOfClusters: %f, " +
        "smallest largest cluster: %d", result.isCohesive_, avgNOfClusters,
        smallestLargestCluster);
    churn.cleanUp();
    mediumThread.interrupt();
    while (true) {
      try {
        mediumThread.join();
        break;
      } catch (InterruptedException e) {
        logger_.warn("Caught InterruptedException when joining: " + mediumThread);
      }
    }

    return result.isCohesive_;
  }

  /**
   * Returns nice string of given graph.
   */
  private static String toStringGossiperGraph(Collection<GossiperNode> graph) {
    StringBuilder builder = new StringBuilder();
    builder.append("Gossiper graph of size: " + graph.size() + "\n");
    for (GossiperNode node : graph)
      builder.append(String.format("Node: %3d with neighbours: %s%n",
          node.index_, java.util.Arrays.toString(node.neighbours_.toArray())));
    return builder.toString();
  }
}
