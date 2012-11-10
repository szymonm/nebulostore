package org.nebulostore.communication.messages.streambinding;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class StreamBindingMessage extends Message {

  private final CommAddress destAddress_;
  private final String streamId_;

  public StreamBindingMessage(String jobId, CommAddress destAddress,
      String streamId) {
    super(jobId);
    streamId_ = streamId;
    destAddress_ = destAddress;
  }

  public StreamBindingMessage(CommAddress destAddress, String streamId) {
    super(null);
    streamId_ = streamId;
    destAddress_ = destAddress;
  }

  public String getStreamId() {
    return streamId_;
  }

  public CommAddress getDestAddress() {
    return destAddress_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
