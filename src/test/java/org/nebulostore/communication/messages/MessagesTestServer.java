package org.nebulostore.communication.messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.TestInitMessage;

/**
 * Testing server module of communication layer.
 *
 * @author Marcin Walas
 */
public class MessagesTestServer extends ServerTestingModule {

  private static Logger logger_ = Logger.getLogger(MessagesTestServer.class);

  private final int messagesForPhase_;

  private final int testPhases_;

  private double lost_;
  private double all_;
  private final double errorThreshold_ = 0.15;

  public MessagesTestServer(int testPhases, int peersFound, int peersNeeded,
      int timeout, String testDescription, int messagesForPhase) {
    super(testPhases - 1, peersFound, peersNeeded, timeout, 60,
        "Messages Test Client " + testDescription, true, testDescription);
    messagesForPhase_ = messagesForPhase;
    testPhases_ = testPhases;
  }

  @Override
  public void initClients() {

    List<CommAddress> clientsCopy = new LinkedList<CommAddress>();
    Random rand = new Random(System.currentTimeMillis());
    Vector<CommAddress> clientsToShuffle = new Vector<CommAddress>(clients_);

    // Remove myself:
    if (clientsToShuffle.size() >= peersFound_ + 1) {
      logger_.debug("Size before myself removal: " + clientsToShuffle.size());
      clientsToShuffle.remove(CommunicationPeer.getPeerAddress());
      logger_.debug("Size after mysqlf removal: " + clientsToShuffle.size());
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
          new MessagesTestClient(jobId_, testPhases_, messagesForPhase_,
              clientsCopy.toArray(new CommAddress[0]), "Simple payload")));
    }
  }

  @Override
  public void configureClients() {
    logger_.debug("Sending ReconfigureTestMessage to clients");

    // Randomizing connection graph topology.
    Map<CommAddress, Set<CommAddress>> clientsOut = new HashMap<CommAddress, Set<CommAddress>>();
    Map<CommAddress, Set<CommAddress>> clientsIn = new HashMap<CommAddress, Set<CommAddress>>();

    Random rand = new Random(System.currentTimeMillis());

    // TODO: Move it as a test parameter
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
      networkQueue_.add(new ReconfigureMessagesTestMessage(clientsJobId_, null,
          client, clientsOut.get(client), clientsIn.get(client).size()));
    }
  }

  @Override
  public void feedStats(TestStatistics stats) {
    all_ += stats.getDouble("all");
    lost_ += stats.getDouble("lost");
    logger_.info("Stats gathered " + stats);
  }

  @Override
  protected boolean isSuccessful() {
    return (lost_ / (all_ + lost_)) < errorThreshold_;
  }

  @Override
  protected String getAdditionalStats() {
    return "\t" + (lost_ / (all_ + lost_));
  }

}
