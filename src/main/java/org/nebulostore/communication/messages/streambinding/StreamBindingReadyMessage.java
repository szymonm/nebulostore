package org.nebulostore.communication.messages.streambinding;

import java.io.InputStream;
import java.io.OutputStream;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Marcin Walas
 */
public class StreamBindingReadyMessage extends Message {

  private final String streamId_;

  private final InputStream inStream_;
  private final OutputStream outStream_;

  public StreamBindingReadyMessage(String jobId, String streamId,
      InputStream inStream) {
    super(jobId);
    streamId_ = streamId;
    inStream_ = inStream;
    outStream_ = null;
  }

  public StreamBindingReadyMessage(String jobId, String streamId,
      OutputStream outStream) {
    super(jobId);
    streamId_ = streamId;
    inStream_ = null;
    outStream_ = outStream;
  }

  public String getStreamId() {
    return streamId_;
  }

  public boolean isInput() {
    return inStream_ != null;
  }

  public boolean isOutput() {
    return outStream_ != null;
  }

  public InputStream getInput() {
    return inStream_;
  }

  public OutputStream getOutput() {
    return outStream_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
