package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.Map;

import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.TimestampedValue;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;
import org.planx.xmlstore.routing.messaging.Receiver;
import org.planx.xmlstore.routing.messaging.UnknownMessageException;

/**
 * Responds to a DataLookupMessage by sending a DataMessage containing the
 * requested mapping <i>or</i> by sending a NodeReplyMessage containing the
 * <i>K</i> closest nodes to the request key known by this node.
 **/
public class DataLookupReceiver extends OriginReceiver {
  protected Map localMap;

  public DataLookupReceiver(MessageServer server, Node local, Space space,
      Map localMap) {
    super(server, local, space);
    this.localMap = localMap;
  }

  @Override
  public void receive(KademliaInternalMessage incoming, int comm)
      throws IOException, UnknownMessageException {
    DataLookupMessage mess = (DataLookupMessage) incoming;

    // Is data located here?
    Identifier key = mess.getLookupId();
    synchronized (localMap) {
      if (localMap.containsKey(key)) {
        super.receive(incoming, comm);

        // Return mapping for key
        Node origin = mess.getOrigin();
        KademliaInternalMessage reply = new DataMessage(local, key,
            (TimestampedValue) localMap.get(key));
        server.reply(comm, reply, origin.getAddress());
      } else {
        // Return message with list of closest nodes
        Receiver recv = new NodeLookupReceiver(server, local, space);
        recv.receive(incoming, comm);
      }
    }
  }
}
