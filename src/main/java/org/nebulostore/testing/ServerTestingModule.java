package org.nebulostore.testing;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.NetworkContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.broker.NetworkContextChangedMessage;
import org.nebulostore.communication.messages.testing.ErrorTestMessage;
import org.nebulostore.communication.messages.testing.FinishTestMessage;
import org.nebulostore.communication.messages.testing.TicMessage;
import org.nebulostore.communication.messages.testing.TocMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * Testing module that acts as a Test Server.
 * @author szymonmatejczyk
 *
 * Remember to set lastPhase_ and peersNeeded_ in subclass.
 */
public abstract class ServerTestingModule extends ReturningJobModule<Void> {
  private static Logger logger_ = Logger.getLogger(ServerTestingModule.class);

  /**
   * Time(in secs) after which, if not successful, test is failed.
   */
  public final int timeout_;

  /**
   * Current phase.
   */
  private int phase_;

  /**
   * Number of Tocs received from peers. After receiving TocMessage from each peer, test advances
   * to next phase.
   */
  private int tocs_;

  private boolean successful_ = true;

  /**
   * CommAdresses of peers performing this test.
   */
  protected HashSet<CommAddress> clients_;

  /**
   * JobId that clients use on their side.
   */
  protected final String clientsJobId_;

  /**
   * Last phase of the test. If test moves to next, it is completed successfully.
   */
  private final int lastPhase_;

  /**
   * Number of peers needed to perform this test.
   */
  private final int peersNeeded_;

  private final ServerTestingModuleVisitor visitor_;

  protected ServerTestingModule(int lastPhase, int peersNeeded, int timeout, String clientsJobId) {
    clientsJobId_ = clientsJobId;
    lastPhase_ = lastPhase;
    timeout_ = timeout;
    peersNeeded_ = peersNeeded;
    visitor_ = new ServerTestingModuleVisitor();
  }

  /**
   * Sends messages to discovered peers to initialize test modules on their side.
   *
   * See: TestInitMessage, PingPongServer.
   */
  public abstract void initClients();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  protected class ServerTestingModuleVisitor extends MessageVisitor<Void> {
    private NetworkContextChangedMessage notificationMessage_;
    private NetworkContext context_ = NetworkContext.getInstance();

    @Override
    public Void visit(JobInitMessage message) {
      if (NetworkContext.getInstance().getKnownPeers().size() >= peersNeeded_) {
        clients_ = new HashSet<CommAddress>(NetworkContext.getInstance().getKnownPeers());
        initClients();
      } else {
        /* wait for enough peers to perform the test - start to listen for NetworkContext changes.*/
        context_.setDispatcherQueue(outQueue_);
        notificationMessage_ = new NetworkContextChangedMessage(message.getId());
        context_.addContextChangeMessage(notificationMessage_);
        logger_.debug("Waiting for peer discovery.");
      }
      return null;
    }

    @Override
    public Void visit(NetworkContextChangedMessage message) {
      if (NetworkContext.getInstance().getKnownPeers().size() >= peersNeeded_) {
        logger_.debug("Enough peers found, initializing test.");
        /* stop listening for notifications */
        NetworkContext.getInstance().removeContextChangeMessage(notificationMessage_);
        clients_ = new HashSet<CommAddress>(NetworkContext.getInstance().getKnownPeers());
        initClients();
      }
      return null;
    }

    @Override
    public Void visit(TocMessage message) {
      tocs_++;
      if (tocs_ == peersNeeded_) {
        phase_++;

        if (phase_ <= lastPhase_) {
          logger_.debug("Advanced to phase: " + phase_);
          tocs_ = 0;
          for (CommAddress address : clients_) {
            networkQueue_.add(new TicMessage(clientsJobId_, null, address));
          }
        } else {
          if (successful_)
            logger_.info("Test successfull.");
          else
            logger_.info("Test failed.");

          for (CommAddress address : clients_) {
            networkQueue_.add(new FinishTestMessage(clientsJobId_, null,
                address));
          }
          endWithSuccess(null);
        }
      }
      return null;
    }

    @Override
    public Void visit(ErrorTestMessage message) {
      logger_.warn("Received error, test failed: " + message.getMessage());
      successful_ = false;
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public Void getResult() throws NebuloException {
    return getResult(timeout_);
  }
}
