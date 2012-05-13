package org.nebulostore.communication.dht;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.kademlia.KademliaPeer;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.TestingModule;
import org.nebulostore.testing.messages.GatherStatsMessage;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;
import org.nebulostore.testing.messages.TestStatsMessage;

public class DHTTestClient extends TestingModule implements Serializable {

  private static Logger logger_ = Logger.getLogger(DHTTestClient.class);
  private static final long serialVersionUID = 3479303908516884312L;

  private final int testPhases_;
  CommAddress[] allClients_;
  private final int keysMultiplier_;

  private final String dhtProvider_;

  private final Map<KeyDHT, String> keysMapping = new HashMap<KeyDHT, String>();

  private final TestStatistics stats_;

  public DHTTestClient(String serverJobId, int testPhases, String dhtProvider,
      int keysMultiplier, CommAddress[] allClients) {
    super(serverJobId);
    testPhases_ = testPhases;
    dhtProvider_ = dhtProvider;
    keysMultiplier_ = keysMultiplier;
    allClients_ = allClients;
    stats_ = new TestStatistics();
    stats_.setDouble("errors", 0.0);
    stats_.setDouble("all", 0.0);
  }


  @Override
  protected void initVisitors() {

    visitors_ = new TestingModuleVisitor[testPhases_ + 1];

    TestingModuleVisitor senderVisitor = new SenderDHTVisitor();
    TestingModuleVisitor receiverVisitor = new ReceiverDHTVisitor();
    visitors_[0] = new InitializationVisitor();
    visitors_[1] = new ConfigurationVisitor();
    for (int i = 2; i < testPhases_; i++) {
      if (i % 2 == 0) {
        visitors_[i] = senderVisitor;
      } else {
        visitors_[i] = receiverVisitor;
      }
    }

    visitors_[testPhases_] = new StatisticsGatherVisitor();

  }

  /**
   * @author Marcin Walas
   */
  final class ConfigurationVisitor extends EmptyInitializationVisitor {
    @Override
    public Void visit(ReconfigureTestMessage message) {
      logger_.info("Got reconfiguration message with clients set: " +
          message.getClients());
      allClients_ = message.getClients().toArray(new CommAddress[0]);
      phaseFinished();
      return null;
    }
  }

  final class InitializationVisitor extends EmptyInitializationVisitor {

    @Override
    public Void visit(TestInitMessage message) {
      logger_.info("Reconfiguring DHT to " + dhtProvider_);
      jobId_ = message.getId();
      networkQueue_.add(new ReconfigureDHTMessage(jobId_, dhtProvider_));
      return null;
    }

    @Override
    public Void visit(ReconfigureDHTAckMessage message) {
      logger_.info("Reconfiguring DHT to " + dhtProvider_ + " finished");
      phaseFinished();
      return null;
    }
  }

  /**
   * @author Marcin Walas
   */
  final class StatisticsGatherVisitor extends TestingModuleVisitor {

    @Override
    public Void visit(NewPhaseMessage message) {
      return null;
    }

    @Override
    public Void visit(GatherStatsMessage message) {
      logger_.debug("Returning stats on request...");
      networkQueue_.add(new TestStatsMessage(serverJobId_, CommunicationPeer
          .getPeerAddress(), server_, stats_));
      return null;
    }

  }

  final class SenderDHTVisitor extends TestingModuleVisitor {

    private int receivedDHTAcks_;
    private int retries_;
    private static final int maxRetries_ = 20;

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.info("Putting DHT Messages");

      refreshVisitor();

      String src = CommunicationPeer.getPeerAddress().toString();
      for (CommAddress destination : allClients_) {
        String dest = destination.toString();
        for (int i = 0; i < keysMultiplier_; i++) {
          String keyStr = src + i + dest;
          KeyDHT key = KeyDHT.fromSerializableObject(keyStr);
          networkQueue_
          .add(new PutDHTMessage(jobId_, key, new ValueDHT(phase_)));
          keysMapping.put(key, keyStr);
        }
      }
      logger_.info("Putting DHT Messages - finished. Waiting for replies.");

      return null;
    }

    private void refreshVisitor() {
      receivedDHTAcks_ = 0;
      retries_ = 0;
    }

    private void increaseReceivedAcks() {
      receivedDHTAcks_++;
      if (receivedDHTAcks_ >= allClients_.length * keysMultiplier_) {
        logger_.info("All OK finished. Phase finished.");
        if (phase_ % 4 == 0) {
          logger_.info(KademliaPeer.getKademliaContents());
        }
        try {
          // additional sleep
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        phaseFinished();
      }
    }

    @Override
    public Void visit(OkDHTMessage message) {
      logger_.info("OK DHT Message received (" + receivedDHTAcks_ + "/" +
          allClients_.length * keysMultiplier_ + ")");

      increaseReceivedAcks();
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      if (!(message.getRequestMessage() instanceof PutDHTMessage)) {
        return null;
      }
      KeyDHT key = ((PutDHTMessage) message.getRequestMessage()).getKey();
      if (retries_ < maxRetries_) {
        logger_.info("Error DHT Message received with: " +
            message.getException() + " key: " + key + " string: " +
            keysMapping.get(key) + ". Retrying...");
        retries_++;
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          logger_.error(e);
        }

        networkQueue_.add(message.getRequestMessage());
      } else {
        logger_.info("Error DHT Message received with: " +
            message.getException() + " key: " + key + " string: " +
            keysMapping.get(key) + ". Not retrying...");
        increaseReceivedAcks();
      }
      return null;
    }
  }

  final class ReceiverDHTVisitor extends TestingModuleVisitor {

    private int receivedDHTAcks_;
    private final String src_ = CommunicationPeer.getPeerAddress().toString();
    private final int maxRetries_ = 2;
    private Map<String, Integer> retries_ = new HashMap<String, Integer>();

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.info("Getting DHT Messages");

      refreshVisitor();

      for (CommAddress destination : allClients_) {
        String dest = destination.toString();
        for (int i = 0; i < keysMultiplier_; i++) {
          String keyStr = dest + i + src_;
          KeyDHT key = KeyDHT.fromSerializableObject(keyStr);
          networkQueue_.add(new GetDHTMessage(jobId_, key));
          keysMapping.put(key, keyStr);
          retries_.put(key.toString(), 0);
        }
      }
      stats_.setDouble("all", stats_.getDouble("all") + keysMultiplier_ *
          allClients_.length);
      logger_.info("Getting DHT Messages - finished. Waiting for replies.");
      return null;
    }

    private void refreshVisitor() {
      receivedDHTAcks_ = 0;
      retries_ = new HashMap<String, Integer>();
    }

    private void increaseReceivedAcks() {
      receivedDHTAcks_++;
      if (receivedDHTAcks_ >= allClients_.length * keysMultiplier_) {
        logger_.info("All OK finished. Phase finished.");
        if (phase_ % 4 == 0) {
          logger_.info(KademliaPeer.getKademliaContents());
        }
        phaseFinished();
      }
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      Integer phaseFromDHT = (Integer) message.getValue().getValue();
      logger_.info("Value DHT Message received with " + phaseFromDHT +
          " (phase " + phase_ + ")");
      if (!retries_.containsKey(message.getKey().toString()))
        retries_.put(message.getKey().toString(), 0);

      if (phaseFromDHT != phase_ - 1 &&
          retries_.get(message.getKey().toString()) < maxRetries_) {
        logger_.error("Phase from DHT = " + phaseFromDHT + " should be " +
            phase_ + " retrying on key : " + message.getKey());

        retries_.put(message.getKey().toString(),
            retries_.get(message.getKey().toString()) + 1);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          logger_.error(e);
        }
        networkQueue_.add(new GetDHTMessage(jobId_, message.getKey()));
      } else {
        increaseReceivedAcks();
      }
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      logger_.debug("Error DHT Message start processing");
      if (!(message.getRequestMessage() instanceof GetDHTMessage)) {
        return null;
      }
      logger_.debug("Error DHT Message before key...");
      KeyDHT key = ((GetDHTMessage) message.getRequestMessage()).getKey();
      logger_.debug("Error DHT Message after key...");

      if (!retries_.containsKey(key.toString()))
        retries_.put(key.toString(), 0);

      logger_.debug("Error DHT Message after retries check...");

      if (retries_.get(key.toString()) < maxRetries_) {
        logger_.info("Error DHT Message received with: " +
            message.getException() + " key: " + key + " string: " +
            keysMapping.get(key) + ". Retrying...");

        retries_.put(key.toString(), retries_.get(key.toString()) + 1);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          logger_.error(e);
        }

        networkQueue_.add(message.getRequestMessage());
      } else {
        logger_.info("Error DHT Message received with: " +
            message.getException() + " key: " + key + " string: " +
            keysMapping.get(key) + ". Not retrying.");
        stats_.setDouble("errors", stats_.getDouble("errors") + 1.0);

        increaseReceivedAcks();
      }
      return null;
    }
  }

}
