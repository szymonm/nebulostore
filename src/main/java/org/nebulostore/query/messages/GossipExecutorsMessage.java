package org.nebulostore.query.messages;

import java.util.Map;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.query.executor.DQLExecutor;

public class GossipExecutorsMessage extends CommMessage {

  private static final long serialVersionUID = 4374988597597239689L;
  private final Map<AppKey, CommAddress> executorsAddresses_;
  private final Map<AppKey, String> executorsJobIds_;

  public GossipExecutorsMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Map<AppKey, CommAddress> remoteExecutorsAddresses_,
      Map<AppKey, String> remoteExecutorsJobIds_) {
    super(jobId, sourceAddress, destAddress);
    executorsAddresses_ = remoteExecutorsAddresses_;
    executorsJobIds_ = remoteExecutorsJobIds_;
  }

  public Map<AppKey, CommAddress> getExecutorsAddresses() {
    return executorsAddresses_;
  }

  public Map<AppKey, String> getExecutorsJobIds() {
    return executorsJobIds_;
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new DQLExecutor(jobId_, false);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

}
