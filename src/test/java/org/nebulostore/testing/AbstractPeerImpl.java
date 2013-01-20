package org.nebulostore.testing;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author grzegorzmilka
 */
public abstract class AbstractPeerImpl implements AbstractPeer {
  private static final String CONFIGURATION_PATH = "resources/conf/Peer.xml";
  protected final Logger logger_;
  protected BlockingQueue<Message> inQueue_;
  protected BlockingQueue<Message> outQueue_;
  protected CommunicationPeer communicationPeer_;
  protected Thread communicationPeerThread_;
  protected int peerId_;

  public AbstractPeerImpl(int peerId) throws NebuloException {
    logger_ = Logger.getLogger(this.getClass());
    peerId_ = peerId;

    try {
      startCommPeer();
    } catch (RemoteException e) {
      logger_.error("RemoteException in constructor. Unthinkable happened");
      throw new NebuloException("RemoteException in constructor. Unthinkable happened", e);
    }

    Thread listener = new Thread(
        new Listener(), "Nebulostore.Testing.AbstractPeer$Listener");
    listener.setDaemon(true);
    listener.start();
  }

  public int getId() throws RemoteException {
    return peerId_;
  }

  public void startCommPeer() throws NebuloException, RemoteException {
    if (communicationPeer_ == null) {
      XMLConfiguration config = null;

      try {
        config = new XMLConfiguration(CONFIGURATION_PATH);
      } catch (ConfigurationException cex) {
        throw new NebuloException("Configuration read error in: " + CONFIGURATION_PATH);
      }

      inQueue_ = new LinkedBlockingQueue<Message>();
      outQueue_ = new LinkedBlockingQueue<Message>();
      communicationPeer_ =
        new CommunicationPeer(inQueue_, outQueue_, config);
      communicationPeerThread_ = new Thread(communicationPeer_,
          "Nebulostore.Communication.CommunicationPeer");
      communicationPeerThread_.setDaemon(true);
      communicationPeerThread_.start();
      logger_.info("CommunicationPeer started.");
    }
  }

  public void stopCommPeer() throws RemoteException {
    if (communicationPeer_ == null) {
      throw new IllegalStateException("Can not stop non existent commPeer.");
    }
    communicationPeer_.endModule();
    communicationPeerThread_.interrupt();
    while (true) {
      try {
        communicationPeerThread_.join();
        break;
      } catch (InterruptedException e) {
        logger_.warn("Interrupted when trying to join communicationPeerThread.");
      }
    }
    communicationPeer_ = null;
    logger_.info("CommunicationPeer stopped and killed");
  }

  @Override
  public CommAddress getCommAddress() throws RemoteException {
    if (communicationPeer_ == null) {
      throw new IllegalStateException("Can not get address from non existent commPeer.");
    }
    return CommunicationPeer.getPeerAddress();
  }

  public boolean isActive() throws RemoteException {
    return communicationPeer_ != null;
  }

  protected abstract void processMessage(Message msg);

  /**
   * @author grzegorzmilka
   */
  private class Listener implements Runnable {
    public void run() {
      while (true) {
        Message msg = null;
        try {
          msg = outQueue_.take();
        } catch (InterruptedException e) {
          logger_.warn("Interrupt when trying to take message.");
          continue;
        }
        //try {
        processMessage(msg);
        /*} catch (RuntimeException e) {
          logger_.error("Caught exception: " + e + " when processing: " + msg);
          continue;
        }*/
      }
    }
  }

}
