package org.nebulostore.testing.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.testing.TestStatistics;

/**
 * @author szymonmatejczyk
 */
public class TestStatsMessage extends CommMessage {
  private static final long serialVersionUID = 2946080833935513302L;

  private final TestStatistics stats_;

  public TestStatsMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, TestStatistics stats) {
    super(jobId, sourceAddress, destAddress);
    stats_ = stats;
  }

  public TestStatistics getStats() {
    return stats_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
