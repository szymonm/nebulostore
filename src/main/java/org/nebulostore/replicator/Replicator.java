package org.nebulostore.replicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;

/**
 * @author szymonmatejczyk
 */
public class Replicator extends JobModule {

  private final MessageVisitor<Void> visitor_;

  // Hashtable is synchronized.
  private static Hashtable<ObjectId, String> filesLocations_ = new Hashtable<ObjectId, String>(256);
  private static Logger logger_ = Logger.getLogger(Replicator.class);

  public Replicator(String jobId, BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    super(jobId);
    logger_.info("Replicator ctor");
    setInQueue(inQueue);
    setOutQueue(outQueue);
    visitor_ = new ReplicatorVisitor();
  }

  /**
   * Visitor to handle different message types. It calls static methods and returns
   * results via queues.
   * @author szymonmatejczyk
   */
  private class ReplicatorVisitor extends MessageVisitor<Void> {

    @Override
    public Void visit(StoreObjectMessage message) {
      logger_.info("StoreObjectMessage received");
      try {
        storeObject(message.getObjectId(), message.getEncryptedEntity());
        networkQueue_.add(new ConfirmationMessage(message.getSourceJobId(), message
            .getDestinationAddress(), message.getSourceAddress()));
        logger_.info("StoreObjectMessage finished successfully");
      } catch (SaveException exception) {
        logger_.warn(exception.toString());
        dieWithError(message.getSourceJobId(), message.getDestinationAddress(),
            message.getSourceAddress(), exception.getMessage());
      }
      return null;
    }

    @Override
    public Void visit(GetObjectMessage message) {
      EncryptedObject enc = getObject(message.objectId_);

      if (enc == null) {
        dieWithError(message.getSourceJobId(), message.getDestinationAddress(),
            message.getSourceAddress(), "Unable to retrieve object.");
      } else {
        networkQueue_.add(new SendObjectMessage(message.getSourceJobId(), message.getDestinationAddress(),
            message.getSourceAddress(), enc));
      }
      return null;
    }

    @Override
    public Void visit(DeleteObjectMessage message) {
      try {
        deleteObject(message.getObjectId());
        networkQueue_.add(new ConfirmationMessage(message.getSourceJobId(), message
            .getDestinationAddress(), message.getSourceAddress()));
      } catch (DeleteObjectException exception) {
        logger_.warn(exception.toString());
        dieWithError(message.getSourceJobId(), message.getDestinationAddress(),
            message.getSourceAddress(), exception.getMessage());
      }
      return null;
    }

    private void dieWithError(String jobId, CommAddress sourceAddress,
        CommAddress destinationAddress, String errorMessage) {
      networkQueue_.add(new ReplicatorErrorMessage(jobId, sourceAddress,
          destinationAddress, errorMessage));
      endJobModule();
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /*
   * Static methods.
   */
  public static void storeObject(ObjectId objectId, EncryptedObject encryptedEntity)
      throws SaveException {
    logger_.info("Storing object");

    String location = filesLocations_.get(objectId);
    if (location == null) {
      location = getLocationPrefix() + objectId.toString();
      filesLocations_.put(objectId, location);
    }

    FileOutputStream fos = null;
    try {
      File f = new File(location);
      f.getParentFile().mkdirs();
      fos = new FileOutputStream(f);
      fos.write(encryptedEntity.getEncryptedData());
      fos.close();
    } catch (IOException exception) {
      logger_.error(exception.getMessage());
      // TODO(szm): printStackTrace?
      throw new SaveException();
    }
  }

  public static EncryptedObject getObject(ObjectId objectId) {
    String location = filesLocations_.get(objectId);
    if (location == null) {
      return null;
    }

    File file = new File(location);
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      byte[] content = new byte[(int) (file.length())];

      fis.read(content);

      return new EncryptedObject(content);
    } catch (FileNotFoundException exception) {
      logger_.warn("Object file not found.");
      return null;
    } catch (IOException exception) {
      logger_.warn(exception.toString());
      return null;
    }
  }

  public static void deleteObject(ObjectId objectId) throws DeleteObjectException {
    String location = filesLocations_.get(objectId);
    if (location == null)
      return;
    filesLocations_.remove(objectId);

    File f = new File(location);
    if (!f.exists())
      throw new DeleteObjectException("File does not exist.");
    boolean success = f.delete();
    if (!success)
      throw new DeleteObjectException("Unable to delete file.");
  }

  private static String getLocationPrefix() {
    // TODO: Read this from config file!
    return "/tmp/nebulostore/store/";
  }
}
