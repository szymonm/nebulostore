package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;

/**
 * Simple sender module.
 * Sends messages given to it by CommunicationPeer. If source address is null it
 * sets it to current one.
 *
 * @author Grzegorz Milka
 */
public class MessengerService extends Module {
  private static Logger logger_ = Logger.getLogger(MessengerService.class);
  private CommAddressResolver resolver_;
  private OOSDispatcher oosDispatcher_;
  private ExecutorService service_ = Executors.newCachedThreadPool();
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);
  private MessageVisitor msgVisitor_;

  @AssistedInject
  public MessengerService(
      @Assisted("MessengerServiceInQueue") BlockingQueue<Message> inQueue,
      @Assisted("MessengerServiceOutQueue") BlockingQueue<Message> outQueue,
      @Assisted CommAddressResolver resolver) {
    super(inQueue, outQueue);
    msgVisitor_ = new MessengerMsgVisitor();
    resolver_ = resolver;
    oosDispatcher_ = new CachedOOSDispatcher(resolver_);
  }

  @Inject(optional = true)
  public void setDispatcher(OOSDispatcher dispatcher) {
    oosDispatcher_ = dispatcher;
  }

  private void shutdown() {
    isEnding_.set(true);
    service_.shutdown();
    oosDispatcher_.shutdown();
    endModule();
  }

  @Override
  public void processMessage(Message msg) {
    if (isEnding_.get()) {
      logger_.warn("Can not process message, because commPeer is " +
          "shutting down.");
      return;
    }
    try {
      msg.accept(msgVisitor_);
    } catch (NebuloException e) {
      logger_.warn("NebuloException: " + e + " occured when trying to send " +
          "msg: " + msg);
    }
  }

  /**
   * Simple runnable which handles sending CommMessage over network.
   */
  private class MessageSender implements Runnable {
    private final CommMessage commMsg_;
    public MessageSender(CommMessage msg) {
      commMsg_ = msg;
    }

    @Override
    public void run() {
      logger_.debug("Executing MessageSender for " + commMsg_.getDestinationAddress());
      ObjectOutputStream oos = null;
      try {
        oos = oosDispatcher_.get(commMsg_.getDestinationAddress());
        logger_.trace("Got ObjectOutputStream from OOSDispatcher for: " +
            commMsg_.getDestinationAddress());
      } catch (IOException e) {
        logger_.warn("IOException when getting socket to: " + commMsg_ +
            ", to: " + commMsg_.getDestinationAddress() + " " + e);
        /* Tell resolver that connection to given address has failed, in case it
         * is cached */
        resolver_.reportFailure(commMsg_.getDestinationAddress());
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Message " + commMsg_ + " couldn't be sent.")));
        return;
      } catch (InterruptedException e) {
        logger_.warn("Interrupt when getting socket to: " + commMsg_ +
            ", to: " + commMsg_.getDestinationAddress() + " " + e);
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Sending of message: " + commMsg_ + ", was interrupted.")));
        return;
      }

      try {
        oos.writeObject(commMsg_);
        /* Flush in case ObjectOutputStream buffer's data to ensure that the
         * message has been sent */
        oos.flush();
        logger_.debug("Message: " + commMsg_ + " sent to: " +
            commMsg_.getDestinationAddress());
      } catch (IOException e) {
        logger_.warn("IOException when trying to send: " + commMsg_ + ", to: " +
            commMsg_.getDestinationAddress() + " " + e);
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Message " + commMsg_ + " couldn't be sent.")));
      } finally {
        oosDispatcher_.put(commMsg_.getDestinationAddress(), oos);
      }
    }
  }

  /**
   * Message Visitor for MessengerService.
   *
   * @author Grzegorz Milka
   */
  protected class MessengerMsgVisitor extends MessageVisitor<Void> {
    public Void visit(EndModuleMessage msg) {
      logger_.info("Received EndModule message");
      shutdown();
      return null;
    }

    public Void visit(CommMessage commMsg) {
      if (commMsg.getSourceAddress() == null) {
        logger_.debug("Source address set to null, changing to my address.");
        commMsg.setSourceAddress(resolver_.getMyCommAddress());
      }
      logger_.debug("Sending msg: " + commMsg + " of class: " +
          commMsg.getClass().getName() + " to: " +
          commMsg.getDestinationAddress());

      service_.execute(new MessageSender(commMsg));
      return null;
    }
  }
}
