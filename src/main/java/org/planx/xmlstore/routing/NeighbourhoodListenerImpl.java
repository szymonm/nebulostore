package org.planx.xmlstore.routing;

import java.io.IOException;

import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;
import org.planx.xmlstore.routing.operation.HashRequestMessage;

/**
 * When a new node is inserted into the neighbourhood of the local node a
 * HashRequestMessage is sent to it.
 **/
public class NeighbourhoodListenerImpl implements NeighbourhoodListener {
  private final Node local;
  private MessageServer server = null;

  /**
   * Note that the MessageServer must be set with {@link #setMessageServer}
   * before use.
   **/
  public NeighbourhoodListenerImpl(Node local) {
    this.local = local;
  }

  public void setMessageServer(MessageServer server) {
    this.server = server;
  }

  @Override
  public void nodeArrived(Node node) {
    try {
      if (local.equals(node))
        return;
      if (server == null)
        throw new NullPointerException("MessageServer is null");
      KademliaInternalMessage mess = new HashRequestMessage(local);
      server.send(mess, node.getAddress(), null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
