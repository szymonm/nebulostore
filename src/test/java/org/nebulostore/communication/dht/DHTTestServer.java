package org.nebulostore.communication.dht;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;

public class DHTTestServer extends ServerTestingModule {
  private static Logger logger_ = Logger.getLogger(DHTTestServer.class);

  private final int peersInTest_;
  private final int testPhases_;
  private final String dhtProvider_;

  private final DHTTestServerVisitor visitor_;

  private final int keysMultiplier_;

  private double all_ = 0.0;
  private double errors_ = 0.0;

  public DHTTestServer(int testPhases, int peersFound, int peersInTest,
      int timeout, int phaseTimeout, int keysMultiplier, String dhtProvider, String clientsJobId,
      String testDescription) {
    super(testPhases - 1, peersFound, peersInTest, timeout, phaseTimeout, clientsJobId, true,
        testDescription);
    keysMultiplier_ = keysMultiplier;
    dhtProvider_ = dhtProvider;
    peersInTest_ = peersFound;
    testPhases_ = testPhases;
    visitor_ = new DHTTestServerVisitor();
  }

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
            new DHTTestClient(jobId_, testPhases_, dhtProvider_,
                keysMultiplier_, clientsCopy.toArray(new CommAddress[1]))));
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
    all_ += stats.getDouble("all");
    errors_ += stats.getDouble("errors");
    logger_.info("Stats gathered " + stats);
  }

  @Override
  protected String getAdditionalStats() {
    return "\t" + (errors_/all_);
  }
}
