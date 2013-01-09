package org.nebulostore.communication.messages.performance;

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
import org.nebulostore.communication.messages.ReconfigureMessagesTestMessage;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;

/**
 */
public class PerformanceMessagesTestServer extends ConductorServer {

  private static Logger logger_ = Logger
      .getLogger(PerformanceMessagesTestServer.class);

  private final long shortPhaseTimeout_;
  private final int messagesNumber_;
  private final int shortPhases_;

  private static final double ERROR_THRESHOLD = 0.5;

  private double received_;

  private Double shouldReceive_;

  public PerformanceMessagesTestServer(int peersFound, int peersNeeded,
      int timeout, String testDescription, int messagesNumber, int shortPhases,
      long shortPhaseTimeout) {
    super(2, peersFound, peersNeeded, timeout, timeout, "PerfMsgTestClient " +
        testDescription, true, testDescription);
    messagesNumber_ = messagesNumber;
    shortPhases_ = shortPhases;
    shortPhaseTimeout_ = shortPhaseTimeout;
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
      networkQueue_.add(new InitMessage(clientsJobId_, null, client,
          new PerformanceMessagesTestClient(jobId_, messagesNumber_,
              shortPhases_, shortPhaseTimeout_)));
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
  public void feedStats(CaseStatistics stats) {
    received_ += stats.getDouble("received");
    shouldReceive_ += stats.getDouble("shouldReceive");
    logger_.info("Stats gathered " + stats);
  }

  @Override
  protected boolean isSuccessful() {
    return (received_ / shouldReceive_) < ERROR_THRESHOLD;
  }

  @Override
  protected String getAdditionalStats() {
    return "\t" + (received_ / shouldReceive_);
  }

}
