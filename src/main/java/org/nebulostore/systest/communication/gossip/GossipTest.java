package org.nebulostore.systest.communication.gossip;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Peer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.PeerDescriptor;
import org.nebulostore.communication.gossip.PeerSamplingGossipService;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author grzegorzmilka
 */
class MessageMedium implements Runnable {
  private static Logger logger_ = Logger.getLogger(MessageMedium.class);
  private final Map<CommAddress, PeerSamplingGossipService> gossipModules_;
  private final BlockingQueue<Message> inQueue_;

  MessageMedium(Map<CommAddress, PeerSamplingGossipService> gossipModules,
      BlockingQueue<Message> inQueue) {
    checkNotNull(gossipModules);
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
      PeerSamplingGossipService gossiper = gossipModules_.get(destAddress);
      if (gossiper == null && message instanceof CommMessage)  {
        inQueue_.put(new ErrorCommMessage((CommMessage) message,
              new Exception("Testing Arbitrary Exception")));
      } else if (gossiper != null) {
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
  private final Map<CommAddress, PeerSamplingGossipService> gossipModules_;
  private final Map<CommAddress, Thread> gossipThreads_;
  private final int nGossipers_;
  private final BlockingQueue<Message> mediumInQueue_;
  private final XMLConfiguration config_;
  private static Logger logger_ = Logger.getLogger(ChurnFactory.class);

  public ChurnFactory(Map<CommAddress, PeerSamplingGossipService> gossipModules,
      int nGossipers, BlockingQueue<Message> mediumInQueue,
      int gossipPeriod,
      int maxPeersSize,
      int healingFactor,
      int swappingFactor,
      XMLConfiguration config) {
    bootstrapCommAddr_ = CommAddress.newRandomCommAddress();
    gossipModules_ = gossipModules;
    gossipThreads_ = new HashMap<CommAddress, Thread>();
    nGossipers_ = nGossipers;
    mediumInQueue_ = mediumInQueue;
    gossipPeriod_ = gossipPeriod;
    maxPeersSize_ = maxPeersSize;
    healingFactor_ = healingFactor;
    swappingFactor_ = swappingFactor;
    config_ = config;
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
    PeerSamplingGossipService gossipService = new PeerSamplingGossipService();
    gossipService.setInQueue(gossiperInQueue);
    gossipService.setOutQueue(mediumInQueue_);
    gossipService.setDependencies(config_, commAddr);

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
      PeerSamplingGossipService gossiper = gossipModules_.get(gossiperAddr);
      Thread gossipThread = gossipThreads_.get(gossiperAddr);
      gossipThreads_.remove(gossiperAddr);
      gossipModules_.remove(gossiperAddr);

      logger_.debug("Ending gossiper: " + gossiperAddr);
      gossiper.shutdown();
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
 * Test whether cohesiveness is maintained during gossiping.
 * This test requires configuration file containg (in testing.gossiptest
 * namespace):
 *   num-gossipers - number of gossipers to test
 *   gossip-period - time between each gossiper sends its gossip in ms
 *   max-peers-size - number of peers a gossiper has in its view
 *   healing-factor
 *   swapping-factor
 *   cohesiveness-test-intervals - array of integers (in ms) specyfying at which
 *     points in time to test for cohesiveness
 *
 * @author grzegorzmilka
 */
public final class GossipTest extends Peer {
  private static Logger logger_ = Logger.getLogger(GossipTest.class);
  private final Map<CommAddress, PeerSamplingGossipService> gossipModules_ =
    new HashMap<CommAddress, PeerSamplingGossipService>();

  /**
   * Prefix of test related parameters used in Peer.xml configuration file.
   */
  private static final String CONFIG_PREFIX = "systest.communication.gossip.";

  private int nGossipers_;
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
  private Map<CommAddress, Integer> outOfMainComponentMap_ =
    new HashMap<CommAddress, Integer>();

  /**
   * Runs the gossip test.
   */
  @Override
  protected void runPeer() {
    nGossipers_ = Integer.parseInt(config_.getString(CONFIG_PREFIX + "num-gossipers"));
    runTest();
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

  /**
   * Runs cohesiveness test on graph and logs results.
   * */
  private CheckCohesivenessReport checkCohesiveness() {
    assert gossipModules_.size() == nGossipers_;
    logger_.info("Starting cohesiveness test.");

    /* Run the main test */
    GraphCreationReport result = createGossiperGraph();
    GraphCohesivenessReport cohReport = isCohesive(result.graph_);

    logger_.info("Number of components: " + cohReport.components_.size());
    int i = 0;
    Set<GossiperNode> mainComponent = new HashSet<GossiperNode>();

    for (Collection<GossiperNode> component : cohReport.components_) {
      logger_.info("Component nr " + i + ":");
      /* Find component that forms at least half of all gossipers */
      if ((double) component.size() / nGossipers_ > 0.5) {
        mainComponent.addAll(component);
      }
      logger_.info(toStringGossiperGraph(component));
      ++i;
    }

    /* Check if everything is ok */

    /* First of check if cohesive enough */
    if (nGossipers_ > 0) {
      cohReport.isCohesive_ =
        cohReport.isCohesive_ ||
        (((double) mainComponent.size() / nGossipers_) > COHESIVENESS_THRESHOLD);
    }

    /* Check if there is a node that has been outside the main group of nodes
     * longer than N_OUT_MAIN times */
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

  private GraphCreationReport createGossiperGraph() {
    Map<CommAddress, Integer> indexMap = new HashMap<CommAddress, Integer>();
    GraphCreationReport result = new GraphCreationReport();
    ArrayList<GossiperNode> gossiperGraph = result.graph_;

    Method getPeersMethod;

    synchronized (gossipModules_) {
      try {
        getPeersMethod = PeerSamplingGossipService.class.getDeclaredMethod("getPeers");
      } catch (NoSuchMethodException e) {
        logger_.error("Exception: " + e + " when trying to get private method getPeers.");
        throw new RuntimeException(e.toString());
      }
      getPeersMethod.setAccessible(true);

      ArrayList<PeerSamplingGossipService> gossiperServiceGraph =
        new ArrayList<PeerSamplingGossipService>();

      for (CommAddress gossiperAddr : gossipModules_.keySet()) {
        PeerSamplingGossipService gossiper = gossipModules_.get(gossiperAddr);
        indexMap.put(gossiperAddr, gossiperGraph.size());
        gossiperGraph.add(new GossiperNode(gossiperGraph.size(), gossiperAddr));
        gossiperServiceGraph.add(gossiper);
      }

      Iterator<PeerSamplingGossipService> iterator = gossiperServiceGraph.iterator();
      for (GossiperNode gossiperNode : gossiperGraph) {
        Collection<PeerDescriptor> peers;
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
  private GraphCohesivenessReport isCohesive(ArrayList<GossiperNode> graph) {
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
   * Runs the gossiping tests.
   * Initializes gossip modules and runs them with given configuration.
   * At specified intervals it tests for cohesiveness of gossipers.
   */
  private boolean runTest() {
    BlockingQueue<Message> mediumInQueue = new LinkedBlockingQueue<Message>();

    MessageMedium medium = new MessageMedium(gossipModules_, mediumInQueue);
    Thread mediumThread = new Thread(medium, "Nebulostore.Testing.MediumThread");
    mediumThread.setDaemon(true);
    mediumThread.start();

    int gossipPeriod = Integer.parseInt(config_.getString(
          "communication.gossip-period"));
    int maxPeersSize = Integer.parseInt(config_.getString(CONFIG_PREFIX + "max-peers-size"));
    int healingFactor = Integer.parseInt(config_.getString(CONFIG_PREFIX + "healing-factor"));
    int swappingFactor = Integer.parseInt(config_.getString(CONFIG_PREFIX + "swapping-factor"));

    ChurnFactory churn = new ChurnFactory(gossipModules_, nGossipers_,
        mediumInQueue, gossipPeriod, maxPeersSize,
        healingFactor, swappingFactor, config_);

    churn.setUp();
    CheckCohesivenessReport result = new CheckCohesivenessReport();
    result.isCohesive_ = true;
    int secFromStart = 0;
    double avgNOfClusters = 0.0;
    int smallestLargestCluster = -1;

    /* Set testing intervals */
    String[] intervalsStr = config_.getStringArray(
        CONFIG_PREFIX + "cohesiveness-test-intervals");
    List<Integer> intervals = new LinkedList<Integer>();
    for (String interval : intervalsStr) {
      intervals.add(Integer.parseInt(interval));
    }

    /* Run tests */
    int i = 1;
    for (Integer period : intervals) {
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
      System.out.printf("(%d) isCohesive: %b, nOfComponents %d, " +
          "size of largest component: %d.%n", secFromStart, result.isCohesive_,
          result.components_.size(), result.sizeOfLargestComponent_);
      logger_.info("Result of test nr. " + i + ": " + result.isCohesive_);
      ++i;
    }

    /* Summarize results */
    avgNOfClusters /= intervals.size() == 0 ? 1 : intervals.size();
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
