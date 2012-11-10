package org.nebulostore.communication.dht;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.TestInitMessage;

/**
 * @author grzegorzmilka
 */
public class DHTTestServer extends ServerTestingModule {
  private static Logger logger_ = Logger.getLogger(DHTTestServer.class);

  private final int peersInTest_;
  private final int testPhases_;
  private final String dhtProvider_;

  private final DHTTestServerVisitor visitor_;

  private final int keysMultiplier_;

  private double all_;
  private double errors_;

  public DHTTestServer(int testPhases, int peersFound, int peersInTest,
      int timeout, int phaseTimeout, int keysMultiplier, String dhtProvider,
      String clientsJobId, String testDescription) {
    super(testPhases - 1, peersFound, peersInTest, timeout, phaseTimeout,
        clientsJobId, true, testDescription);
    keysMultiplier_ = keysMultiplier;
    dhtProvider_ = dhtProvider;
    peersInTest_ = peersFound;
    testPhases_ = testPhases;
    visitor_ = new DHTTestServerVisitor();
  }

  /**
   */
  class DHTTestServerVisitor extends ServerTestingModuleVisitor {
    @Override
    public Void visit(ReconfigureDHTAckMessage message) {
      logger_.info("Got reconfigure DHTAck Message");
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  @Override
  public void initClients() {
    logger_.info("Initializing DHT on server...");
    networkQueue_.add(new ReconfigureDHTMessage(jobId_, dhtProvider_));
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    logger_.info("Initializing peers (number of clients: " + clients_.size() +
        ")");

    List<CommAddress> clientsCopy = new LinkedList<CommAddress>();
    Random rand = new Random(System.currentTimeMillis());
    Vector<CommAddress> clientsToShuffle = new Vector<CommAddress>(clients_);

    // Remove myself:
    if (clientsToShuffle.size() >= peersFound_ + 1) {
      logger_.debug("Size before myself removal: " + clientsToShuffle.size());
      clientsToShuffle.remove(CommunicationPeer.getPeerAddress());
      logger_.debug("Size after myself removal: " + clientsToShuffle.size());
    }
    for (int i = 0; i < peersFound_; i++) {
      clientsCopy.add(clientsToShuffle.remove(rand.nextInt(clientsToShuffle
          .size())));
    }

    clients_ = new HashSet<CommAddress>(clientsCopy);
    logger_.info("Address copy done.");
    for (CommAddress client : clientsCopy) {
      logger_.info("Initializing peer at " + client.toString());
      networkQueue_.add(new TestInitMessage(clientsJobId_, null, client,
          new DHTTestClient(jobId_, testPhases_, dhtProvider_, keysMultiplier_,
              clientsCopy.toArray(new CommAddress[0]))));
    }

  }

  @Override
  public void configureClients() {
    logger_.debug("Sending ReconfigureTestMessage to clients");

    Map<CommAddress, Set<CommAddress>> clientsOut = new HashMap<CommAddress, Set<CommAddress>>();
    Map<CommAddress, Set<CommAddress>> clientsIn = new HashMap<CommAddress, Set<CommAddress>>();

    Random rand = new Random(System.currentTimeMillis());

    // TODO(grzegorzmilka): Move it as a test parameter
    int outDegree = 5;

    for (CommAddress client : clients_) {
      Vector<CommAddress> shuffle = new Vector<CommAddress>(clients_);
      clientsOut.put(client, new HashSet<CommAddress>());
      if (!clientsIn.containsKey(client)) {
        clientsIn.put(client, new HashSet<CommAddress>());
      }
      for (int i = 0; i < outDegree; i++) {
        CommAddress drawn = shuffle.remove(rand.nextInt(shuffle.size()));
        clientsOut.get(client).add(drawn);
        if (!clientsIn.containsKey(drawn)) {
          clientsIn.put(drawn, new HashSet<CommAddress>());
        }
        clientsIn.get(drawn).add(client);
      }
    }

    for (CommAddress client : clients_) {
      networkQueue_.add(new ReconfigureDHTTestMessage(clientsJobId_, null,
          client, clientsOut.get(client), clientsIn.get(client)));
    }

  }

  @Override
  public void feedStats(TestStatistics stats) {
    all_ += stats.getDouble("all");
    errors_ += stats.getDouble("errors");
    logger_.info("Stats gathered " + stats);
  }

  @Override
  protected String getAdditionalStats() {
    return "\t" + (errors_ / (all_ + errors_));
  }
}
