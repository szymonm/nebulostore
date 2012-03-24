package org.nebulostore.communication.streambinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.streambinding.ErrorStreamBindingMessage;
import org.nebulostore.communication.messages.streambinding.StreamBindingMessage;
import org.nebulostore.communication.messages.streambinding.StreamBindingReadyMessage;

/**
 * @author Marcin Walas
 */
public class StreamBindingService extends Module {

  private static Logger logger_ = Logger.getLogger(StreamBindingService.class);

  private static final long TIMEOUT_ = 3000;
  private final IStreamBindingDriver bindingDriver_;

  public StreamBindingService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, IStreamBindingDriver bindingDriver) {
    super(inQueue, outQueue);
    bindingDriver_ = bindingDriver;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {

    if (message instanceof StreamBindingMessage) {
      logger_.debug("Stream binding message received");

      StreamBindingMessage casted = (StreamBindingMessage) message;
      try {
        OutputStream outStream = bindingDriver_.bindStream(
            casted.getDestAddress(), casted.getId(), casted.getStreamId(),
            TIMEOUT_);

        logger_.debug("Sending ready response");

        outQueue_.add(new StreamBindingReadyMessage(casted.getId(), 
            casted.getStreamId(), outStream));
      } catch (IOException e) {
        logger_.error(e);
        outQueue_.add(new ErrorStreamBindingMessage(casted, e));
      }
    } else {
      logger_.error("Message should not be here " + message.getClass() + " : " +
          message);
    }
  }

  public void bindStream(InputStream inStream, String jobId, String streamId) {
    logger_.debug("Socket binded. Triggering event.");
    outQueue_.add(new StreamBindingReadyMessage(jobId, streamId, inStream));
  }
}
