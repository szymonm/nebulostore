package org.nebulostore.testing.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message send to set up a test on a client.
 * @author szymonmatejczyk
 *
 */
public class TestInitMessage extends CommMessage {
  private static final long serialVersionUID = 2556576233416223608L;

  private final JobModule handler_;

  public TestInitMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, JobModule handler) {
    super(jobId, sourceAddress, destAddress);
    handler_ = handler;
  }

  @Override
  public JobModule getHandler() {
    return handler_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
