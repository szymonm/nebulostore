package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 *
 * Generic Message visitor class.
 * TODO(bolek): All methods should not be abstract and throw a meaningful
 * exception.
 *
 */
public abstract class MessageVisitor {
  public void visit(Message message) throws NebuloException { }
  public void visit(JobEndedMessage message) throws NebuloException { }
  public void visit(KillDispatcherMessage message) throws NebuloException { }
}
