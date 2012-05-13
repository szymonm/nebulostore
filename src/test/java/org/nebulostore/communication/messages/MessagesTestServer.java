package org.nebulostore.communication.messages;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;

/**
 * Testing server module of communication layer.
 * @author Marcin Walas
 */
public class MessagesTestServer extends ServerTestingModule {

  private static Logger logger_ = Logger.getLogger(MessagesTestServer.class);

  private final int peersInTest_;

  private final int messagesForPhase_;

  private final int testPhases_;

  private double lost_ = 0.0;
  private double all_ = 0.0;

  public MessagesTestServer(int testPhases, int peersFound, int peersNeeded,
      int timeout, String testDescription, int messagesForPhase) {
    super(testPhases - 1, peersFound, peersNeeded, timeout, 10, "Messages Test Client " + testDescription,
        true, testDescription);
    peersInTest_ = peersFound;
    messagesForPhase_ = messagesForPhase;
    testPhases_ = testPhases;
  }

  @Override
  public void initClients() {
    int peerNum = 0;
    List<CommAddress> clientsCopy = new LinkedList<CommAddress>();
    for (CommAddress client : clients_) {
      if (peerNum >= peersInTest_) {
        break;
      }
      if (client != null) {
        logger_.info("Copying address : " + client);
        clientsCopy.add(client);
        peerNum++;
      }
    }
    clients_ = new HashSet<CommAddress>(clientsCopy);
    if (peerNum < peersInTest_) {
      logger_.error("NULL peer addresses found. Not initializing clients");
      this.endWithError(new NebuloException(
          "NULL Peer addresses received from NetworkContext"));
    } else {
      logger_.info("Address copy done.");
      for (CommAddress client : clientsCopy) {
        logger_.info("Initializing peer at " + client.toString());
        networkQueue_.add(new TestInitMessage(clientsJobId_, null, client,
            new MessagesTestClient(jobId_, testPhases_,
                messagesForPhase_, clientsCopy.toArray(new CommAddress[0]), "Simple payload")));
      }
    }


  }

  @Override
  public void configureClients() {
    logger_.debug("Sending ReconfigureTestMessage to clients");
    for (CommAddress client : clients_) {
      networkQueue_.add(new ReconfigureTestMessage(clientsJobId_, null, client,
          clients_));
    }
  }

  @Override
  public void feedStats(TestStatistics stats) {
    all_  += stats.getDouble("all");
    lost_ += stats.getDouble("lost");
    logger_.info("Stats gathered " + stats);
  }

  @Override
  protected String getAdditionalStats() {
    return "\t" + (lost_/all_);
  }

}
