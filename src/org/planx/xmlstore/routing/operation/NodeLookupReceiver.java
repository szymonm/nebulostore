package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.List;

import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;
import org.planx.xmlstore.routing.messaging.UnknownMessageException;

/**
 * Handles incoming LookupMessages by sending a NodeReplyMessage containing the
 * <i>K</i> closest nodes to the requested identifier.
 **/
public class NodeLookupReceiver extends OriginReceiver {
  public NodeLookupReceiver(MessageServer server, Node local, Space space) {
    super(server, local, space);
  }

  @Override
  public void receive(KademliaInternalMessage incoming, int comm)
      throws IOException, UnknownMessageException {
    super.receive(incoming, comm);

    LookupMessage mess = (LookupMessage) incoming;
    Node origin = mess.getOrigin();
    List nodes = space.getClosestNodes(mess.getLookupId());

    KademliaInternalMessage reply = new NodeReplyMessage(local, nodes);
    server.reply(comm, reply, origin.getAddress());
  }
}
