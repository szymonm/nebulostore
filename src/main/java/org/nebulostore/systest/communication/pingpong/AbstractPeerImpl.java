package org.nebulostore.systest.communication.pingpong;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.PeerGossipService;

/**
 * @author grzegorzmilka
 */
public abstract class AbstractPeerImpl extends Observable implements AbstractPeer {
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

      CommAddress commAddress = new CommAddress(config.getString("communication.comm-address", ""));
      inQueue_ = new LinkedBlockingQueue<Message>();
      outQueue_ = new LinkedBlockingQueue<Message>();
      PeerGossipService gossipService = new PeerGossipService();
      gossipService.setDependencies(config, commAddress);
      communicationPeer_ = new CommunicationPeer(inQueue_, outQueue_);
      communicationPeer_.setDependencies(config, commAddress, gossipService);
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
    logger_.info("Stopping Communication Peer.");
    inQueue_.add(new EndModuleMessage());
    try {
      communicationPeerThread_.join();
      logger_.info("CommunicationPeer stopped and killed");
    } catch (InterruptedException e) {
      logger_.info("Interrupted when trying to join communicationPeerThread.");
      /* Setting interrupt flag */
      Thread.currentThread().interrupt();
    }
    communicationPeer_ = null;

    setChanged();
    notifyObservers();
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
