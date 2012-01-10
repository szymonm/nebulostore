/**
 */
package org.nebulostore.replicator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.KillModuleException;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;
import org.nebulostore.replicator.messages.UpdateObjectMessage;

/**
 * @author szymonmatejczyk
 */
public class Replicator extends JobModule {
  private MessageVisitor<Void> visitor_;

  private static Logger logger_ = Logger.getLogger(Replicator.class);

  public Replicator(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    super();
    setInQueue(inQueue);
    setOutQueue(outQueue);
    visitor_ = new ReplicatorVisitor();
  }

  /**
   * Visitor to handle different message types.
   * @author szymonmatejczyk
   */
  private class ReplicatorVisitor extends MessageVisitor<Void> {

    public Void visit(StoreObjectMessage message) throws KillModuleException {
      try {
        storeObject(message.getObjectId(), message.getEncryptedEntity());
        networkQueue_.add(new ConfirmationMessage(message.getId(), message
            .getDestinationAddress(), message.getSourceAddress()));
      } catch (SaveException exception) {
        logger_.warn(exception.toString());
        dieWithError(message.getId(), message.getDestinationAddress(),
            message.getSourceAddress(), exception.getMessage());
      }
      return null;
    }

    public Void visit(GetObjectMessage message) throws KillModuleException {
      EncryptedEntity enc = getObject(message.objectId_);

      if (enc == null) {
        dieWithError(message.getId(), message.getDestinationAddress(),
            message.getSourceAddress(), "Unable to retrieve object.");
      }

      networkQueue_.add(new SendObjectMessage(message.getId(), message
          .getDestinationAddress(), message.getSourceAddress(), enc));
      return null;
    }

    public Void visit(UpdateObjectMessage message) throws KillModuleException {
      try {
        updateObject(message.getObjectId(), message.getEncryptedEntity());
        networkQueue_.add(new ConfirmationMessage(message.getId(), message
            .getDestinationAddress(), message.getSourceAddress()));
      } catch (SaveException exception) {
        logger_.warn(exception.toString());
        dieWithError(message.getId(), message.getDestinationAddress(),
            message.getSourceAddress(), exception.getMessage());
      }
      return null;
    }

    public Void visit(DeleteObjectMessage message) throws KillModuleException {
      try {
        deleteObject(message.getObjectId());
        networkQueue_.add(new ConfirmationMessage(message.getId(), message
            .getDestinationAddress(), message.getSourceAddress()));
      } catch (DeleteObjectException exception) {
        logger_.warn(exception.toString());
        dieWithError(message.getId(), message.getDestinationAddress(),
            message.getSourceAddress(), exception.getMessage());
      }
      return null;
    }

    private void dieWithError(String jobId, CommAddress sourceAddress,
        CommAddress destinationAddress, String errorMessage)
      throws KillModuleException {
      networkQueue_.add(new ReplicatorErrorMessage(jobId, sourceAddress,
          destinationAddress, errorMessage));
      die();
    }

    private void die() throws KillModuleException {
      throw new KillModuleException();
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  private HashMap<ObjectId, String> filesLocations_ = new HashMap<ObjectId, String>();

  public void storeObject(ObjectId objectId, EncryptedEntity encryptedEntity)
    throws SaveException {
    if (filesLocations_.containsKey(objectId)) {
      throw new SaveException();
    }
    String location = getLocationPrefix() + objectId.toString();
    filesLocations_.put(objectId, location);
    updateObject(objectId, encryptedEntity);
  }

  public EncryptedEntity getObject(ObjectId objectId) {
    String location = filesLocations_.get(objectId);
    if (location == null) {
      return null;
    }

    File file = new File(location);
    FileInputStream fis = null;
    ByteArrayInputStream baos = null;
    try {
      fis = new FileInputStream(file);
      byte[] content = new byte[(int) (file.length())];

      fis.read(content);

      return new EncryptedEntity(content);
    } catch (FileNotFoundException exception) {
      logger_.warn("Object file not found.");
      return null;
    } catch (IOException e) {
      logger_.warn(e.toString());
      return null;
    }
  }

  private void updateObject(ObjectId objectId, EncryptedEntity encryptedEntity)
    throws SaveException {
    String location = filesLocations_.get(objectId);
    if (location == null)
      throw new SaveException();

    FileOutputStream fos = null;
    try {
      File f = new File(location);
      f.getParentFile().mkdirs();
      fos = new FileOutputStream(f);
      fos.write(encryptedEntity.getEncryptedData());
      fos.close();
    } catch (IOException excepion) {
      logger_.error(excepion.getMessage());
      // TODO(szm): printStackTrace?
      throw new SaveException();
    }
  }

  public void deleteObject(ObjectId objectId) throws DeleteObjectException {
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

  private String getLocationPrefix() {
    return "/tmp/nebulostore/store/";
  }
}
