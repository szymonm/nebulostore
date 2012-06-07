package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.planx.xmlstore.routing.Configuration;
import org.planx.xmlstore.routing.HashCalculator;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.RoutingException;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;

/**
 * Refreshes all buckets and sends HashMessages to all nodes that are among the
 * K closest to mappings stored at this node. Also deletes any mappings that
 * this node is no longer among the K closest to.
 **/
public class RestoreOperation extends Operation {
  private final Configuration conf;
  private final MessageServer server;
  private final Space space;
  private final Node local;
  private final Map localMap;
  private final HashCalculator hasher;

  public RestoreOperation(Configuration conf, MessageServer server,
      Space space, Node local, Map localMap) {
    this.conf = conf;
    this.server = server;
    this.space = space;
    this.local = local;
    this.localMap = localMap;
    hasher = new HashCalculator(local, space, localMap);
  }

  /**
   * @return <code>null</code>
   * @throws RoutingException
   *           If any suboperation timed out
   * @throws IOException
   *           If a network occurred
   **/
  @Override
  public synchronized Object execute() throws IOException, RoutingException {
    // Refresh buckets
    Operation refresh = new RefreshOperation(conf, server, space, local);
    refresh.execute();

    // Find nodes that are among the closest to mappings stored here
    // TODO: It is ineffective since HashCalculator also goes through all
    // mappings and looks at closestNodes. This should only be done once.

    Set nodes = new HashSet();
    synchronized (localMap) {
      Iterator it = localMap.keySet().iterator();
      while (it.hasNext()) {
        Identifier key = (Identifier) it.next();
        List closest = space.getClosestNodes(key);

        // No longer closest to mapping?
        if (!closest.contains(local)) {
          it.remove();
        } else {
          nodes.addAll(closest);
        }
      }

      // Do not send to self
      nodes.remove(local);

      // Calculate hashes and send HashMessage
      long now = System.currentTimeMillis();
      it = nodes.iterator();
      while (it.hasNext()) {
        Node node = (Node) it.next();
        List hashes = hasher.logarithmicHashes(node, now);
        if (hashes.size() > 0) {
          KademliaInternalMessage mess = new HashMessage(local, now, hashes);
          server.send(mess, node.getAddress(), null);
        }
      }
    }
    return null;
  }
}