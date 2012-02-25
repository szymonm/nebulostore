package org.nebulostore.communication.jxta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.ID;
import net.jxta.impl.pipe.BlockingWireOutputPipe;
import net.jxta.impl.util.Base64;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Module responsible for sending data over JXTA Network.
 * 
 * @author Marcin Walas
 */
public class MessengerService extends Module {

  private static Logger logger_ = Logger.getLogger(MessengerService.class);

  private static final String MESSAGE_PIPE_ID_STR = "urn:jxta:uuid-" +
      "59616261646162614E504720503250338944BCED387C4A2BBD8E9411B78C284104";

  private static final int MAX_RETRIES_ = 3;

  private final DiscoveryService discovery_;
  private final PeerGroup peerGroup_;

  private MessageReceiver accepter_;

  private final Map<String, BlockingWireOutputPipe> pipes_;

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, PeerGroup peerGroup,
      DiscoveryService discovery) {
    super(inQueue, outQueue);

    discovery_ = discovery;
    peerGroup_ = peerGroup;

    pipes_ = new HashMap<String, BlockingWireOutputPipe>(256);

    try {
      accepter_ = new MessageReceiver(peerGroup.getPipeService()
          .createInputPipe(getPipeAdvertisement()), outQueue);
      (new Thread(accepter_, "Nebulostore.Communication.Accepter")).start();
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger_.info("fully initialised");
  }

  public static PipeAdvertisement getPipeAdvertisement() {
    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    advertisement.setPipeID(ID.create(URI.create(MESSAGE_PIPE_ID_STR)));
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Nebulostore messaging");
    return advertisement;
  }

  @Override
  protected void processMessage(Message msg) {
    processMessageRetry(msg, 0);
  }

  private void processMessageRetry(Message msg, int retries) {
    if (retries < MAX_RETRIES_) {
      PeerID destAddress = ((CommMessage) msg).getDestinationAddress()
          .getPeerId();
      try {

        logger_.info("Message to be sent over network to: " + destAddress);
        // new BlockingWireOutputPipe(peerGroup_, getPipeAdvertisement(),
        // destAddress).send(wrapMessage(msg));

        if ((!pipes_.containsKey(destAddress.toString())) ||
            (pipes_.get(destAddress.toString()).isClosed())) {
          logger_.info("Refreshing pipe..");
          pipes_.put(destAddress.toString(), new BlockingWireOutputPipe(
              peerGroup_, getPipeAdvertisement(), destAddress));
        }
        pipes_.get(destAddress.toString()).send(wrapMessage(msg));
        logger_.info("sended to " + destAddress);
      } catch (IOException e) {
        logger_.error(e);
        pipes_.get(destAddress.toString()).close();
        pipes_.put(destAddress.toString(), new BlockingWireOutputPipe(
            peerGroup_, getPipeAdvertisement(), destAddress));
        processMessageRetry(msg, retries + 1);
      }
    } else {
      logger_.error("Max retries elapsed, dropping message...");
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
      e1.printStackTrace();
    }
    try {
      oos.writeObject(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
    serialized = new String(Base64.encodeBase64(baos.toByteArray()));
    // logger_.info("message after encoding: " + serialized);

    jxtaMessage.addMessageElement(new StringMessageElement("serialized",
        serialized, null));
    return jxtaMessage;
  }

}
