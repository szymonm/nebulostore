package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.planx.xmlstore.routing.HashCalculator;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;
import org.planx.xmlstore.routing.messaging.UnknownMessageException;

/**
 * Receives a HashRequestMessage and replies with a HashMessage containing group
 * hashes of mappings that should both be available at the local node and the
 * origin node.
 **/
public class HashRequestReceiver extends OriginReceiver {
  private final Map localMap;
  private final HashCalculator hasher;

  public HashRequestReceiver(MessageServer server, Node local, Space space,
      Map localMap) {
    super(server, local, space);
    this.localMap = localMap;
    hasher = new HashCalculator(local, space, localMap);
  }

  @Override
  public void receive(KademliaInternalMessage incoming, int comm)
      throws IOException, UnknownMessageException {
    super.receive(incoming, comm);
    HashRequestMessage mess = (HashRequestMessage) incoming;
    Node origin = mess.getOrigin();

    long now = System.currentTimeMillis();
    List hashes = hasher.logarithmicHashes(origin, now);

    if (hashes.size() > 0) {
      KademliaInternalMessage rep = new HashMessage(local, now, hashes);
      server.reply(comm, rep, origin.getAddress());
    }
  }
}
