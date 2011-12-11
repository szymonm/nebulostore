package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.exceptions.UnsupportedMessageException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.DelDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.InDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;

/**
 *
 * Generic Message visitor class.
 * TODO(bolek): All methods should not be abstract and throw a meaningful
 * exception.
 *
 */
public abstract class MessageVisitor {
  /* Common action for all messages that are not handled. */
  private void action(String name) throws NebuloException {
    throw new UnsupportedMessageException(name);
  }

  /* Base class. */
  public void visit(Message message) throws NebuloException { action("Message"); }

  /* Dispatcher messages. */
  public void visit(JobEndedMessage message) throws NebuloException { action("JobEndedMessage"); }
  public void visit(JobInitMessage message) throws NebuloException { action("JobInitMessage"); }
  public void visit(KillDispatcherMessage message) throws NebuloException {
    action("KillDispatcherMessage");
  }

  /* Replicator messages. */
  public void visit(GetObjectMessage message) throws NebuloException { action("GetObjectMessage"); }
  public void visit(DeleteObjectMessage message) throws NebuloException {
    action("DeleteObjectMessage");
  }
  public void visit(SendObjectMessage message) throws NebuloException {
    action("SendObjectMessage");
  }
  public void visit(StoreObjectMessage message) throws NebuloException {
    action("StoreObjectMessage");
  }

  /* Network messages. */
  public void visit(CommMessage message) throws NebuloException { action("CommMessage"); }
  public void visit(CommPeerFoundMessage message) throws NebuloException {
    action("CommPeerFoundMessage");
  }
  public void visit(CommSendDataMessage message) throws NebuloException {
    action("CommSendDataMessage");
  }
  public void visit(DiscoveryMessage message) throws NebuloException { action("DiscoveryMessage"); }

  /* DHT messages. */
  public void visit(DHTMessage message) throws NebuloException { action("DHTMessage"); }
  public void visit(DelDHTMessage message) throws NebuloException { action("DelDHTMessage"); }
  public void visit(ErrorDHTMessage message) throws NebuloException { action("ErrorDHTMessage"); }
  public void visit(GetDHTMessage message) throws NebuloException { action("GetDHTMessage"); }
  public void visit(InDHTMessage message) throws NebuloException { action("InDHTMessage"); }
  public void visit(OkDHTMessage message) throws NebuloException { action("OkDHTMessage"); }
  public void visit(OutDHTMessage message) throws NebuloException { action("OutDHTMessage"); }
  public void visit(PutDHTMessage message) throws NebuloException { action("PutDHTMessage"); }
  public void visit(ValueDHTMessage message) throws NebuloException { action("ValueDHTMessage"); }
}
