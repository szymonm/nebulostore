package org.nebulostore.appcore;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.replicator.TransactionAnswer;

/**
 * @author bolek
 * File (only metadata).
 */

public class NebuloFile extends NebuloObject {

  /**
   * File chunk metadata.
   */
  protected class FileChunkWrapper implements Serializable {

    private static final long serialVersionUID = -6723808968821818811L;

    // FileChunk stores (endByte_ - startByte_) bytes from interval [startByte_, endByte_).
    public int startByte_;
    public int endByte_;
    public NebuloAddress address_;

    private transient FileChunk chunk_;
    private transient boolean isChanged_;

    public FileChunkWrapper(int startByte, int endByte, NebuloAddress address) {
      startByte_ = startByte;
      endByte_ = endByte;
      address_ = address;
      chunk_ = new FileChunk(address, endByte_ - startByte_);
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

    public void deleteChunk() {
      if (chunk_ == null) {
        NebuloObject.deleteFromAddress(address_);
      } else {
        chunk_.delete();
      }
    }

    public WriteNebuloObjectModule sync() {
      if (isChanged_) {
        WriteNebuloObjectModule syncModule = new WriteNebuloObjectModule(address_, chunk_,
            dispatcherQueue_, chunk_.getVersions());
        return syncModule;
      } else {
        return null;
      }
    }

    public void setSynced() {
      isChanged_ = false;
    }
  }



  private static Logger logger_ = Logger.getLogger(NebuloFile.class);
  private static final long serialVersionUID = -1687075358113579488L;
  public static final int DEFAULT_CHUNK_SIZE_BYTES = 1024 * 1024;

  // Is this file new, i.e. its ObjectId is not yet in DHT (false by default).
  protected transient boolean isNew_;

  protected int size_;
  protected Vector<FileChunkWrapper> chunks_;
  protected int chunkSize_ = DEFAULT_CHUNK_SIZE_BYTES;

  /**
   * New, empty file.
   */
  public NebuloFile(AppKey appKey) {
    // TODO(bolek): Here should come more sophisticated ID generation method to account for
    //   (probably) fixed replication groups with ID intervals. (ask Broker? what size?)
    this(appKey, new ObjectId(CryptoUtils.getRandomId()), DEFAULT_CHUNK_SIZE_BYTES);
  }

  public NebuloFile(AppKey appKey, ObjectId objectId) {
    this(appKey, objectId, DEFAULT_CHUNK_SIZE_BYTES);
  }

  public NebuloFile(AppKey appKey, ObjectId objectId, int chunkSize) {
    super(new NebuloAddress(appKey, objectId));
    chunkSize_ = chunkSize;
    initNewFile();
  }

  public int getSize() {
    return size_;
  }

  /**
   * Read at most len bytes starting from position pos.
   * @param pos
   * @param len
   * @return bytes read
   * @throws NebuloException
   */
  public byte[] read(int pos, int len) throws NebuloException {
    logger_.info("read called");

    int trueLen = Math.min(len, size_ - pos);
    int truePos = pos;
    byte[] ret = new byte[trueLen];
    int retPos = 0;

    FileChunkWrapper chunk = null;
    for (int i = 0; i < chunks_.size() && trueLen > 0; ++i) {
      logger_.debug("Fetching chunk... " + i);
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
        retPos += currLen;
      }
    }
    return ret;
  }

 /**
  * Write bytes from buffer starting at position pos.
  * @param buffer
  * @param pos
  * @return bytes written
  * @throws NebuloException
  */
  public int write(byte[] buffer, int pos) throws NebuloException {
    if (pos > size_) {
      throw new NebuloException("Write attempt after the end of the file!");
    }
    int chunkIndex = pos / chunkSize_;
    int currPos = pos;
    int currBufPos = 0;
    while (currPos < pos + buffer.length) {
      int currChunkPos = currPos - chunkIndex * chunkSize_;
      int currLen = Math.min(chunkSize_ - currChunkPos, buffer.length + pos - currPos);
      writeSingleChunk(chunkIndex, buffer, currBufPos, currChunkPos, currLen);
      currBufPos += currLen;
      currPos += currLen;
      ++chunkIndex;
    }
    size_ = Math.max(size_, pos + buffer.length);
    runSync();
    return buffer.length;
  }

  private void writeSingleChunk(int chunkIdx, byte[] buffer, int bufPos, int chunkPos, int len)
    throws NebuloException {
    if (chunkIdx >= chunks_.size()) {
      // Create new chunk at the end.
      // TODO(bolek): Better ID generation!
      ObjectId chunkId = new ObjectId(
          chunks_.lastElement().address_.getObjectId().getKey().add(BigInteger.ONE));
      chunks_.add(new FileChunkWrapper(chunkIdx * chunkSize_, chunkIdx * chunkSize_ + len,
          new NebuloAddress(address_.getAppKey(), chunkId)));
    }
    FileChunkWrapper chunk = chunks_.get(chunkIdx);
    try {
      byte[] newDataArray = chunk.getData();
      int endPos = chunkIdx * chunkSize_ + (chunkPos + len);
      if (endPos > chunk.endByte_) {
        // Resize chunk.
        newDataArray = new byte[endPos - chunk.startByte_];
        System.arraycopy(chunk.getData(), 0, newDataArray, 0, chunk.endByte_ - chunk.startByte_);
        chunk.endByte_ = endPos;
      }
      System.arraycopy(buffer, bufPos, newDataArray, chunkPos, len);
      chunk.setData(newDataArray);
    } catch (NebuloException exception) {
      throw new NebuloException("Unable to fetch file", exception);
    }
  }

  /**
   * Truncates the file to newSize.
   * @param newSize
   * @throws NebuloException The exception is thrown when newSize is greater than current size.
   */
  public void truncate(int newSize) throws NebuloException {
    if (newSize > size_) {
      throw new NebuloException("Cannot truncate file to greater size!");
    }
    int nChunks = (newSize + (chunkSize_ - 1)) / chunkSize_;
    /// Remove unneeded chunks.
    while (chunks_.size() > nChunks) {
      chunks_.lastElement().deleteChunk();
      chunks_.remove(chunks_.size() - 1);
    }
    // Truncate last chunk if necessary.
    FileChunkWrapper chunk = chunks_.lastElement();
    if (chunk.endByte_ > newSize) {
      byte[] newData = new byte[newSize - chunk.startByte_];
      System.arraycopy(chunk.getData(), 0, newData, 0, newSize - chunk.startByte_);
      chunk.setData(newData);
      chunk.endByte_ = newSize;
    }
    // Set file size.
    size_ = newSize;
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
    logger_.info("Running sync on file ");
    Vector<WriteNebuloObjectModule> updateModules = new Vector<WriteNebuloObjectModule>();
    // Run sync for all chunks in parallel.
    for (int i = 0; i < chunks_.size(); ++i) {
      logger_.debug("Creating update module for chunk " + i);
      updateModules.add(chunks_.get(i).sync());
    }
    // Run sync for NebuloFile (metadata).
    updateModules.add(new WriteNebuloObjectModule(address_, this, dispatcherQueue_,
        previousVersions_));

    notifySubscribers();

    // Wait for all results.
    NebuloException caughtException = null;
    for (int i = 0; i < updateModules.size(); ++i) {
      try {
        if (updateModules.get(i) != null) {
          updateModules.get(i).getSemiResult(TIMEOUT_SEC);
        }
      } catch (NebuloException exception) {
        caughtException = exception;
      }
    }
    if (caughtException != null) {
      // aborting the transaction
      for (WriteNebuloObjectModule update : updateModules) {
        update.setAnswer(TransactionAnswer.ABORT);
      }
      throw caughtException;
    } else {
      logger_.debug("Commiting transaction");
      for (WriteNebuloObjectModule update : updateModules) {
        if (update != null) {
          update.setAnswer(TransactionAnswer.COMMIT);
        }
      }
      for (FileChunkWrapper chunk : chunks_) {
        chunk.setSynced();
      }
    }
    for (WriteNebuloObjectModule update : updateModules) {
      if (update != null) {
        update.getResult(TIMEOUT_SEC);
      }
    }
  }
}

