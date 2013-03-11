package org.nebulostore.communication.bootstrap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.address.PersistentAddressingPeer;
import org.nebulostore.communication.address.TomP2PClient;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.nebulostore.communication.nat.NATUtils;

/**
 * Bootstrap Client.
 * BootstrapClient makes initial contact with BootstrapServer signaling
 * its entry to the nebulostore network and getting its address for gossiping
 * It also handles persistent addressing.
 *
 * @author Grzegorz Milka
 */
public final class BootstrapClient extends BootstrapService {
  private static Logger logger_ = Logger.getLogger(BootstrapClient.class);

  private String bootstrapServerAddress_;

  //NOTE-GM Addresses for normal communication
  private CommAddress myCommAddress_;
  private CommAddress bootstrapServerCommAddress_;
  //Address of local NIC used for outside communication
  private InetAddress localInetAddress_;
  //NOTE-GM TomP2P communication
  PersistentAddressingPeer pAPeer_;

  //TODO(grzegorzmilka) Collision handling
  /**
   * commAddress - iff not equal to null then myCommAddress is set to it.
   * Otherwise it is random
   */
  public BootstrapClient(String bootstrapServerAddress,
      int commCliPort,
      int bootstrapPort,
      int tomP2PPort,
      int bootstrapTomP2PPort,
      String commAddress) throws NebuloException {
    super(commCliPort, bootstrapPort, tomP2PPort, bootstrapTomP2PPort);
    bootstrapServerAddress_ = bootstrapServerAddress;

    // Find my address both CommAddress and a real IP one
    logger_.info("Finding out my address.");
    if (commAddress != null)
        myCommAddress_ = new CommAddress(commAddress);
    else
        myCommAddress_ = CommAddress.newRandomCommAddress();

    pAPeer_ = new TomP2PClient();
    pAPeer_.setCommPort(commCliPort_);
    pAPeer_.setDHTPort(tomP2PPort_);
    pAPeer_.setBootstrapDHTPort(bootstrapTomP2PPort_);
    pAPeer_.setBootstrapServerAddress(bootstrapServerAddress_);
    pAPeer_.setMyCommAddress(myCommAddress_);
    try {
      pAPeer_.setUpAndRun();
    } catch (IOException e) {
      String errMsg = "Couldn't start TomP2PPeer.";
      logger_.error(errMsg + " " + e);
      throw new NebuloException(errMsg, e);
    }

    try {
      sendAndReceiveHelloMsg();
    } catch (IOException e) {
      logger_.warn("Error when sending hello message " + e);
    }

    /* Now that we know our local address setup UPNP */
    /* True iff upnp was successful */
    boolean upnpResult = false;
    try {
      upnpResult = setUpUpnpPortMapping();
      logger_.info("Upnp port mapping set up.");
    } catch (IOException e) {
      logger_.warn("Error when setting UPNP port mapping: " + e);
    }

    try {
      boolean foundAddress = false;
      /* iff upnp wasn't succesful check whether our internet address is bound
       * to some interface. If not throw exception */
      if (!upnpResult) {
        for (String localAddress : NATUtils.getLocalAddresses()) {
          if (pAPeer_.getCurrentInetSocketAddress().getAddress().
              getHostAddress().equals(localAddress)) {
            foundAddress = true;
            break;
          }
        }
        if (!foundAddress)
          throw new NebuloException("Discovered being behind NAT but port " +
              "forwarding has failed");
      }
    } catch (IOException e) {
      throw new NebuloException(e);
    }

    try {
      logger_.debug("Resolver resolved my address to: " +
          pAPeer_.getResolver().resolve(myCommAddress_) + ".");
    } catch (IOException e) {
      throw new NebuloException(e);
    } catch (AddressNotPresentException e) {
      //Something is really bad if this has happened
      throw new NebuloException(e);
    }
  }

  @Override
  public CommAddress getBootstrapCommAddress() {
    return bootstrapServerCommAddress_;
  }


  @Override
  //NOTE(grzegorzmilka) Do nothing for now.
  //TODO(grzegorzmilka) Ask whether it is better to start up as separate
  //function even though the object is useless otherwise
  public void startUpService() {
    return;
  }

  @Override
  public CommAddressResolver getResolver() {
    return pAPeer_.getResolver();
  }

  @Override
  public void shutdownService() {
    logger_.debug("Starting shutting down procedure");
    pAPeer_.destroy();
  }

  @Override
  public String toString() {
    return "BootstrapClient with address: " + myCommAddress_ + ", peer: " +
      pAPeer_;
  }

  /* Returns true if setting up UPNP was successful */
  private boolean setUpUpnpPortMapping() throws IOException {
    logger_.debug("Setting up upnp at address: " +
        localInetAddress_.getHostAddress());
    return NATUtils.mapUPNP(localInetAddress_.getHostAddress(), commCliPort_,
        commCliPort_);
  }

  /* Gets address of bootstrap gossiping server and get local InetAddress used
   * for accessing */
  private void sendAndReceiveHelloMsg() throws IOException {
    logger_.info("Sending Hello message to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapPort_);
    try {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(new BootstrapMessage(myCommAddress_));
      logger_.info("Sent Hello message to server.");
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      BootstrapMessage message = (BootstrapMessage) ois.readObject();
      bootstrapServerCommAddress_ = message.getPeerAddress();
      logger_.info("Received Hello message from server. His address: " +
          bootstrapServerCommAddress_);
      localInetAddress_ =
        ((InetSocketAddress) socket.getLocalSocketAddress()).getAddress();
    } catch (IOException e) {
      throw e;
    } catch (ClassNotFoundException e) {
      String errMsg = "Read object is not BootstrapMessage.";
      logger_.warn(errMsg);
      throw new IOException(errMsg, e);
    } finally {
      socket.close();
    }
  }
}
