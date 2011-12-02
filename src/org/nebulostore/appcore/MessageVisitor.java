package org.nebulostore.appcore;

import org.nebulostore.appcore.messages.JobEndedMessage;

/**
 *
 * Generic Message visitor class.
 * TODO(bolek): All methods should not be abstract and throw a meaningful
 * exception.
 *
 */
public abstract class MessageVisitor {
  public void visit(JobEndedMessage message) { }
  public void visit(Message message) { }
}
