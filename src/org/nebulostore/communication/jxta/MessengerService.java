package org.nebulostore.communication.jxta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.concurrent.BlockingQueue;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.ID;
import net.jxta.impl.pipe.BlockingWireOutputPipe;
import net.jxta.impl.util.Base64;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class MessengerService extends Module {

  private final DiscoveryService discovery_;
  private final PeerGroup peerGroup_;
  private MessageReceiver accepter_;

  private final PeerDiscoveryService resolverThread_;

  private static final String MESSAGE_PIPE_ID_STR = "urn:jxta:uuid-"
      + "59616261646162614E504720503250338944BCED387C4A2BBD8E9411B78C284104";

  private static Logger logger_ = Logger.getLogger(MessengerService.class);

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, PeerGroup peerGroup,
      DiscoveryService discovery) {

    super(inQueue, outQueue);

    discovery_ = discovery;
    peerGroup_ = peerGroup;

    try {
      accepter_ = new MessageReceiver(peerGroup.getPipeService()
          .createInputPipe(getPipeAdvertisement()), outQueue);
      (new Thread(accepter_, "Nebulostore.Communication.Accepter")).start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    resolverThread_ = new PeerDiscoveryService(discovery_);
    (new Thread(resolverThread_, "Nebulostore.Communication.ResolverThread"))
        .start();

    logger_.info("fully initialised");
  }

  public static PipeAdvertisement getPipeAdvertisement() {
    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    // / advertisement.setPipeID(IDFactory
    // / .newPipeID(PeerGroupID.defaultNetPeerGroupID));

    advertisement.setPipeID(ID.create(URI.create(MESSAGE_PIPE_ID_STR)));
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Nebulostore messaging");
    return advertisement;
  }

  @Override
  protected void processMessage(Message msg) {
    try {
      logger_.info("Message to be sent over network!");
      BlockingWireOutputPipe p = new BlockingWireOutputPipe(peerGroup_,
          getPipeAdvertisement(), ((CommMessage) msg).getDestinationAddress()
              .getPeerId());

      p.send(wrapMessage(msg));
      logger_.info("sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private net.jxta.endpoint.Message wrapMessage(Message msg) {
    net.jxta.endpoint.Message jxtaMessage = new net.jxta.endpoint.Message();
    String serialized = new String();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      oos.writeObject(msg);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    serialized = new String(Base64.encodeBase64(baos.toByteArray()));
    logger_.info("message after encoding: " + serialized);

    jxtaMessage.addMessageElement(new StringMessageElement("serialized",
        serialized, null));
    return jxtaMessage;
  }

}
