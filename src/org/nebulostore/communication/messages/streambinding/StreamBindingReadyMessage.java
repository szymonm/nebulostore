package org.nebulostore.communication.messages.streambinding;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class StreamBindingReadyMessage extends CommMessage {

  private final String streamId_;

  public StreamBindingReadyMessage(String jobId, CommAddress destAddress,
      String streamId) {
    super(jobId, null, destAddress);
    streamId_ = streamId;
  }

  public StreamBindingReadyMessage(CommAddress destAddress, String streamId) {
    super(null, destAddress);
    streamId_ = streamId;
  }

  public String getStreamId() {
    return streamId_;
  }
}
