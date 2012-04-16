package org.nebulostore.appcore;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Vector;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;

/**
 * @author bolek
 * File (only metadata).
 */

public class NebuloFile extends NebuloObject {

  /**
   * File chunk meta data.
   */
  protected class FileChunkWrapper implements Serializable {

    private static final long serialVersionUID = -6723808968821818811L;

    public int startByte_;
    public int endByte_;
    public NebuloAddress address_;

    private transient FileChunk chunk_;
    private transient boolean isChanged_;

    public FileChunkWrapper(int startByte, int endByte, NebuloAddress address) {
      startByte_ = startByte;
      endByte_ = endByte;
      address_ = address;
      chunk_ = new FileChunk();
    }

    public byte[] getData() throws NebuloException {
      if (chunk_ == null) {
        // Use metadata holder for chunk query.
        // TODO(bolek): Retry with full address if unsuccessful.
        GetNebuloObjectModule module =
            new GetNebuloObjectModule(address_, sender_, dispatcherQueue_);
        // Exception from getResult() is simply passed to the user.
        chunk_ = (FileChunk) module.getResult(TIMEOUT_SEC);
      }
      return chunk_.getData();
    }

    public void setData(byte[] data) {
      chunk_.setData(data);
      isChanged_ = true;
    }

    public WriteNebuloObjectModule sync() {
      if (isChanged_) {
        WriteNebuloObjectModule syncModule = new WriteNebuloObjectModule(address_, chunk_,
            dispatcherQueue_);
        return syncModule;
      } else {
        return null;
      }
    }

    public void setSynced() {
      isChanged_ = false;
    }
  }

  private static final long serialVersionUID = -1687075358113579488L;

  // Is this file new, i.e. its ObjectId is not yet in DHT (false by default).
  protected transient boolean isNew_;

  protected int size_;
  protected Vector<FileChunkWrapper> chunks_;

  /**
   * New, empty file.
   */
  public NebuloFile(AppKey appKey) {
    // TODO(bolek): Here should come more sophisticated ID generation method to account for
    //   (probably) fixed replication groups with ID intervals. (ask Broker? what size?)
    address_ = new NebuloAddress(appKey, new ObjectId(CryptoUtils.getRandomId()));
    initNewFile();
  }

  public NebuloFile(AppKey appKey, ObjectId objectId) {
    address_ = new NebuloAddress(appKey, objectId);
    initNewFile();
  }

  public byte[] read(int pos, int len) throws NebuloException {
    int trueLen = Math.min(len, size_ - pos);
    int truePos = pos;
    byte[] ret = new byte[trueLen];
    int retPos = 0;

    FileChunkWrapper chunk = null;
    for (int i = 0; i < chunks_.size() && trueLen > 0; ++i) {
      chunk = chunks_.get(i);
      if (chunk.startByte_ <= truePos && chunk.endByte_ > truePos) {
        int currLen = Math.min(trueLen, chunk.endByte_ - truePos);
        try {
          System.arraycopy(chunk.getData(), truePos - chunk.startByte_, ret, retPos, currLen);
        } catch (NebuloException exception) {
          throw new NebuloException("Unable to fetch file", exception);
        }
        trueLen -= currLen;
        truePos += currLen;
      }
    }
    return ret;
  }

  public int write(byte[] buffer, int pos) throws NebuloException {
    FileChunkWrapper chunk = null;
    // TODO(bolek): What if buffer is very large?
    for (int i = 0; i < chunks_.size(); ++i) {
      chunk = chunks_.get(i);
      if (chunk.startByte_ <= pos) {
        break;
      }
    }
    int maxPos = pos + buffer.length;
    try {
      byte[] newDataArray = chunk.getData();
      if (maxPos > chunk.endByte_) {
        // Resize chunk.
        newDataArray = new byte[maxPos - chunk.startByte_];
        System.arraycopy(chunk.getData(), 0, newDataArray, 0, chunk.endByte_ - chunk.startByte_);
        chunk.endByte_ = maxPos;
        size_ = Math.max(size_, maxPos);
      }
      System.arraycopy(buffer, 0, newDataArray, pos - chunk.startByte_, buffer.length);
      chunk.setData(newDataArray);
    } catch (NebuloException exception) {
      throw new NebuloException("Unable to fetch file", exception);
    }
    runSync();
    return buffer.length;
  }

  private void initNewFile() {
    isNew_ = true;
    chunks_ = new Vector<FileChunkWrapper>();
    // TODO(bolek): How to assign IDs for new chunks?
    ObjectId chunkId = new ObjectId(address_.getObjectId().getKey().add(BigInteger.ONE));
    chunks_.add(new FileChunkWrapper(0, 0, new NebuloAddress(address_.getAppKey(), chunkId)));
  }

  @Override
  protected void runSync() throws NebuloException {
    Vector<WriteNebuloObjectModule> updateModules = new Vector<WriteNebuloObjectModule>();
    // Run sync for all chunks in parallel.
    for (int i = 0; i < chunks_.size(); ++i) {
      updateModules.add(chunks_.get(i).sync());
    }
    // Run sync for NebuloFile (metadata).
    updateModules.add(new WriteNebuloObjectModule(address_, this, dispatcherQueue_));
    // Wait for all results.
    NebuloException caughtException = null;
    for (int i = 0; i < updateModules.size(); ++i) {
      try {
        if (updateModules.get(i) != null) {
          updateModules.get(i).getResult(TIMEOUT_SEC);
          if (i < chunks_.size()) {
            chunks_.get(i).setSynced();
          }
        }
      } catch (NebuloException exception) {
        caughtException = exception;
      }
    }
    if (caughtException != null) {
      throw caughtException;
    }
  }
}

