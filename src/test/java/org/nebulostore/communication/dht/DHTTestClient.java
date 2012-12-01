package org.nebulostore.communication.dht;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.ReconfigurationMessage;
import org.nebulostore.conductor.messages.StatsMessage;

/**
 * @author grzegorzmilka
 *
 */
public class DHTTestClient extends ConductorClient implements Serializable {

  private static Logger logger_ = Logger.getLogger(DHTTestClient.class);
  private static final long serialVersionUID = 3479303908516884312L;

  private final int testPhases_;
  private CommAddress[] outClients_;
  private CommAddress[] inClients_;
  private final int keysMultiplier_;

  private final String dhtProvider_;

  private final Map<KeyDHT, String> keysMapping_ = new HashMap<KeyDHT, String>();

  private final CaseStatistics stats_;

  public DHTTestClient(String serverJobId, int testPhases, String dhtProvider,
      int keysMultiplier, CommAddress[] allClients) {
    super(serverJobId);
    testPhases_ = testPhases;
    dhtProvider_ = dhtProvider;
    keysMultiplier_ = keysMultiplier;
    outClients_ = allClients;
    stats_ = new CaseStatistics();
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
    public Void visit(ReconfigurationMessage message) throws NebuloException {
      ReconfigureDHTTestMessage rdtMessage;
      try {
        rdtMessage = (ReconfigureDHTTestMessage) message;
      } catch (ClassCastException excpetion) {
        throw new NebuloException("Received wrong ReconfigurationMessage subclass!");
      }
      logger_.info("Got reconfiguration message with clients set: " +
          rdtMessage.getClients());
      outClients_ = rdtMessage.getClients().toArray(new CommAddress[0]);
      inClients_ = rdtMessage.getClientsIn().toArray(new CommAddress[0]);
      phaseFinished();
      return null;
    }
  }

  /**
   */
  final class InitializationVisitor extends EmptyInitializationVisitor {

    @Override
    public Void visit(InitMessage message) {
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
      networkQueue_.add(new StatsMessage(serverJobId_, CommunicationPeer
          .getPeerAddress(), server_, stats_));
      return null;
    }

  }

  /**
   */
  final class SenderDHTVisitor extends TestingModuleVisitor {

    private int receivedDHTAcks_;
    private int retries_;
    private static final int MAX_RETRIES = 20;

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.info("Putting DHT Messages");

      refreshVisitor();

      String src = CommunicationPeer.getPeerAddress().toString();
      for (CommAddress destination : outClients_) {
        String dest = destination.toString();
        for (int i = 0; i < keysMultiplier_; i++) {
          String keyStr = src + i + dest;
          KeyDHT key = KeyDHT.fromSerializableObject(keyStr);
          networkQueue_.add(new PutDHTMessage(jobId_, key, new ValueDHT(
              new MergeableInteger(phase_))));
          keysMapping_.put(key, keyStr);
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
      if (receivedDHTAcks_ >= outClients_.length * keysMultiplier_) {
        logger_.info("All OK finished. Phase finished.");
        /*if (phase_ % 4 == 0) {
          logger_.info(KademliaPeer.getKademliaContents());
        }*/
        /*
        try {
          // additional sleep

          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
         */

        phaseFinished();
      }
    }

    @Override
    public Void visit(OkDHTMessage message) {
      logger_.info("OK DHT Message received (" + receivedDHTAcks_ + "/" +
          outClients_.length * keysMultiplier_ + ")");

      increaseReceivedAcks();
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      if (!(message.getRequestMessage() instanceof PutDHTMessage)) {
        return null;
      }
      KeyDHT key = ((PutDHTMessage) message.getRequestMessage()).getKey();
      if (retries_ < MAX_RETRIES) {
        logger_.info("Error DHT Message received with: " +
            message.getException() + " key: " + key + " string: " +
            keysMapping_.get(key) + ". Retrying...");
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
            keysMapping_.get(key) + ". Not retrying...");
        increaseReceivedAcks();
      }
      return null;
    }
  }

  /**
   */
  final class ReceiverDHTVisitor extends TestingModuleVisitor {

    private int receivedDHTAcks_;
    private final String dest_ = CommunicationPeer.getPeerAddress().toString();
    private final int maxRetries_ = 2;
    private Map<String, Integer> retries_ = new HashMap<String, Integer>();

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.info("Getting DHT Messages");

      refreshVisitor();

      for (CommAddress destination : inClients_) {
        String src = destination.toString();
        for (int i = 0; i < keysMultiplier_; i++) {
          String keyStr = src + i + dest_;
          KeyDHT key = KeyDHT.fromSerializableObject(keyStr);
          networkQueue_.add(new GetDHTMessage(jobId_, key));
          keysMapping_.put(key, keyStr);
          retries_.put(key.toString(), 0);
        }
      }
      stats_.setDouble("all", stats_.getDouble("all") + keysMultiplier_ *
          inClients_.length);
      logger_.info("Getting DHT Messages - finished. Waiting for replies.");
      return null;
    }

    private void refreshVisitor() {
      receivedDHTAcks_ = 0;
      retries_ = new HashMap<String, Integer>();
    }

    private void increaseReceivedAcks() {
      receivedDHTAcks_++;
      if (receivedDHTAcks_ >= inClients_.length * keysMultiplier_) {
        logger_.info("All OK finished. Phase finished.");
        /*if (phase_ % 4 == 0) {
          logger_.info(KademliaPeer.getKademliaContents());
        }*/
        phaseFinished();
      }
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      Integer phaseFromDHT = ((MergeableInteger) message.getValue().getValue())
          .getValue();
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
            keysMapping_.get(key) + ". Retrying...");

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
            keysMapping_.get(key) + ". Not retrying.");
        stats_.setDouble("errors", stats_.getDouble("errors") + 1.0);

        increaseReceivedAcks();
      }
      return null;
    }
  }

}
