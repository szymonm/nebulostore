package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * @author bolek
 * Class that handles API calls via static methods.
 */
public final class ApiFacade {

  private static Logger logger_ = Logger.getLogger(ApiFacade.class);
  private static BlockingQueue<Message> dispatcherQueue_;

  public static NebuloFile getNebuloFile(NebuloKey key) throws NebuloException {

    /**
     * Class to handle return messages from getNebuloFile API call.
     */
    class GetNebuloFileVisitor extends MessageVisitor<NebuloFile> {
      @Override
      public NebuloFile visit(ApiGetNebuloFileMessage message) {
        return message.getNebuloFile();
      }
      @Override
      public NebuloFile visit(ApiErrorMessage error) throws NebuloException {
        throw new NebuloException(error.getErrorMessage(), error.getInnerException());
      }
    }

    BlockingQueue<ApiMessage> resultQueue = new LinkedBlockingQueue<ApiMessage>();
    GetNebuloFileModule module = new GetNebuloFileModule(key, resultQueue);
    dispatcherQueue_.add(new JobInitMessage(CryptoUtils.getRandomName(), module));
    try {
      // TODO(bolek): Better error handling (now exception is simply passed to the user).
      return resultQueue.take().accept(new GetNebuloFileVisitor());
    } catch (InterruptedException e) {
      throw new NebuloException("Interrupt received while waiting for the result.", e);
    }
  }

  public static NebuloKey putKey(AppKey appKey) throws NebuloException {

    /**
     * Class to handle return messages from putKey API call.
     */
    class PutKeyVisitor extends MessageVisitor<ObjectId> {
      @Override
      public ObjectId visit(ApiPutKeyMessage message) {
        return message.getObjectId();
      }
      @Override
      public ObjectId visit(ApiErrorMessage error) throws NebuloException {
        throw new NebuloException(error.getErrorMessage(), error.getInnerException());
      }
    }

    BlockingQueue<ApiMessage> resultQueue = new LinkedBlockingQueue<ApiMessage>();
    PutKeyModule module = new PutKeyModule(appKey, resultQueue);
    dispatcherQueue_.add(new JobInitMessage(CryptoUtils.getRandomName(), module));
    try {
      // TODO(bolek): Better error handling (now exception is simply passed to the user).
      ObjectId dirId = resultQueue.take().accept(new PutKeyVisitor());
      NebuloKey retKey = new NebuloKey();
      retKey.appKey_ = appKey;
      retKey.objectId_ = dirId;
      return retKey;
    } catch (InterruptedException e) {
      throw new NebuloException("Interrupt received while waiting for the result.", e);
    }
  }

  public static void quitNebuloStore() {
    dispatcherQueue_.add(new KillDispatcherMessage());
  }

  public static void setDispatcherQueue(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  private ApiFacade() { }
}
