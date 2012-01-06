package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * @author bolek
 * Class that handles API calls via static methods.
 */
public final class ApiFacade {

  public static NebuloFile getNebuloFile(NebuloKey key) throws NebuloException {

    /**
     * Class to handle return messages from getNebuloFile API call.
     */
    class GetNebuloFileVisitor extends MessageVisitor<NebuloFile> {
      public NebuloFile visit(ApiGetNebuloFileMessage message) {
        return message.getNebuloFile();
      }
      public NebuloFile visit(ApiErrorMessage message) throws NebuloException {
        throw new NebuloException(message.getErrorMessage());
      }
    }

    BlockingQueue<ApiMessage> resultQueue = new LinkedBlockingQueue<ApiMessage>();
    GetNebuloFileModule module = new GetNebuloFileModule(key, resultQueue);
    dispatcherQueue_.add(new JobInitMessage(CryptoUtils.getRandomName(), module));
    // TODO(bolek): Better error handling (now exception is simply passed to the user).
    return resultQueue.poll().accept(new GetNebuloFileVisitor());
  }

  public static void setDispatcherQueue(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  private ApiFacade() { }

  private static BlockingQueue<Message> dispatcherQueue_;
}
