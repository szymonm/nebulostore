package org.nebulostore.communication.bootstrap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.ICommAddressResolver;
import org.nebulostore.communication.address.IPersistentAddressingPeer;
import org.nebulostore.communication.address.TomP2PClient;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

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
  //NOTE-GM TomP2P communication
  IPersistentAddressingPeer pAPeer_;

  private Boolean isEnding_ = false;

  //TODO(grzegorzmilka) Collision handling
  public BootstrapClient(String bootstrapServerAddress,
      int commCliPort,
      int bootstrapPort,
      int tomP2PPort,
      int bootstrapTomP2PPort) throws NebuloException {
    super(commCliPort, bootstrapPort, tomP2PPort, bootstrapTomP2PPort);
    bootstrapServerAddress_ = bootstrapServerAddress;

    // Find my address
    logger_.info("Finding out my address.");
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

    //Set up Upnp port mapping
    try {
      setUpUpnpPortMapping();
      logger_.info("Upnp port mapping set up.");
    } catch (IOException e) {
      logger_.error("Couldn't set up Upnp port mapping: " + e);
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

    // Send hello keep alive
    while (true) {
      try {
        sendAndReceiveHelloMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending hello message " + e);
      }
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
  public ICommAddressResolver getResolver() {
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

  private void setUpUpnpPortMapping() throws IOException {
    InetAddress myLocalAddr = InetAddress.getLocalHost();
    PortMapping desiredMapping = new PortMapping(commCliPort_,
        myLocalAddr.toString(),
        PortMapping.Protocol.TCP);
    desiredMapping.setExternalPort(new UnsignedIntegerTwoBytes(commCliPort_));
    desiredMapping.setInternalPort(new UnsignedIntegerTwoBytes(commCliPort_));

    UpnpService upnpService =
      new UpnpServiceImpl(new PortMappingListener(desiredMapping));

    upnpService.getControlPoint().search();
  }

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
    } catch (IOException e) {
      throw e;
    } catch (ClassNotFoundException e) {
      String errMsg = "Read object is not BootstrapMessage.";
      logger_.error("errMsg");
      throw new IOException(errMsg, e);
    } finally {
      socket.close();
    }
  }
}
