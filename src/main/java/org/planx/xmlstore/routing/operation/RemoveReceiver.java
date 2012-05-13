package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.Map;

import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;

/**
 * Handles incoming remove messages by removing the mapping with the specified
 * key from the local map.
 **/
public class RemoveReceiver extends OriginReceiver {
  private final Map localMap;

  public RemoveReceiver(MessageServer server, Node local, Space space,
      Map localMap) {
    super(server, local, space);
    this.localMap = localMap;
  }

  @Override
  public void receive(KademliaInternalMessage incoming, int comm)
      throws IOException {
    super.receive(incoming, comm);
    RemoveMessage mess = (RemoveMessage) incoming;
    synchronized (localMap) {
      localMap.remove(mess.getKey());
    }
  }
}
