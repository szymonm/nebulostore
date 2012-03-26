package org.planx.xmlstore.routing.operation;

import java.io.IOException;

import org.nebulostore.communication.address.CommAddress;
import org.planx.xmlstore.routing.Configuration;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.RoutingException;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;
import org.planx.xmlstore.routing.messaging.MessageServer;
import org.planx.xmlstore.routing.messaging.Receiver;

/**
 * Connects to an existing Kademlia network using a bootstrap node.
 **/
public class ConnectOperation extends Operation implements Receiver {
  private static final int MAX_ATTEMPTS = 5;

  private final Configuration conf;
  private final MessageServer server;
  private final Space space;
  private final Node local;
  private final CommAddress bootstrap;
  private boolean error;
  private int attempts;

  public ConnectOperation(Configuration conf, MessageServer server,
      Space space, Node local, CommAddress bootstrap) {
    if (bootstrap == null)
      throw new NullPointerException("Bootstrap node null");
    this.conf = conf;
    this.server = server;
    this.space = space;
    this.local = local;
    this.bootstrap = bootstrap;
  }

  /**
   * @return <code>null</code>
   * @throws IOException
   *           if a network error occurred
   * @throws RoutingException
   *           if the bootstrap node did not respond
   **/
  @Override
  public synchronized Object execute() throws IOException, RoutingException {
    try {
      // Contact bootstrap node
      error = true;
      attempts = 0;
      KademliaInternalMessage mess = new ConnectMessage(local);
      server.send(mess, bootstrap, this);
      wait(conf.OPERATION_TIMEOUT);
      if (error) {
        throw new RoutingException("Bootstrap node did not respond: " +
            bootstrap);
      }

      // Perform lookup operation for own id
      Operation lookup = new NodeLookupOperation(conf, server, space, local,
          local.getId());
      lookup.execute();

      // Refresh buckets
      Operation refresh = new RefreshOperation(conf, server, space, local);
      refresh.execute();

      return null;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Receives an AcknowledgeMessage from the boot strap node.
   **/
  @Override
  public synchronized void receive(KademliaInternalMessage incoming, int comm)
      throws IOException {
    AcknowledgeMessage mess = (AcknowledgeMessage) incoming;
    // Insert bootstrap node in space (id is now known)
    space.insertNode(mess.getOrigin());
    error = false;
    notify();
  }

  /**
   * Resends a ConnectMessage to the boot strap node a maximum of MAX_ATTEMPTS
   * times.
   **/
  @Override
  public synchronized void timeout(int comm) throws IOException {
    if (++attempts < MAX_ATTEMPTS) {
      KademliaInternalMessage mess = new ConnectMessage(local);
      server.send(mess, bootstrap, this);
    } else {
      notify();
    }
  }
}
