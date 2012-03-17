package org.nebulostore.communication.messages.streambinding;

import java.io.InputStream;
import java.io.OutputStream;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class StreamBindingMessage extends CommMessage {

  private InputStream inStream_;
  private OutputStream outStream_;
  private final String streamId_;

  public StreamBindingMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, InputStream inStream, String streamId) {
    super(jobId, sourceAddress, destAddress);
    inStream_ = inStream;
    streamId_ = streamId;
  }

  public StreamBindingMessage(CommAddress sourceAddress,
      CommAddress destAddress, InputStream inStream, String streamId) {
    super(sourceAddress, destAddress);
    inStream_ = inStream;
    streamId_ = streamId;
  }

  public String getStreamId() {
    return streamId_;
  }

  public InputStream getInputStream() {
    return inStream_;
  }

  public OutputStream getOutputStream() {
    return outStream_;
  }

  public void setOutputStream(OutputStream outStream) {
    outStream_ = outStream;
  }


  @Override
  public void prepareToSend() {
    inStream_ = null;
  }

}
