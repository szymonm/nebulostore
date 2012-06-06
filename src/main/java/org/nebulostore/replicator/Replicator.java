package org.nebulostore.replicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.api.GetEncryptedObjectModule;
import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ObjectOutdatedMessage;
import org.nebulostore.replicator.messages.QueryToStoreObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.TransactionResultMessage;
import org.nebulostore.replicator.messages.UpdateRejectMessage;
import org.nebulostore.replicator.messages.UpdateWithholdMessage;
import org.nebulostore.replicator.messages.UpdateWithholdMessage.Reason;
import org.nebulostore.utils.Pair;

/*
 * TODO: cloning object before send. java cloning library?
 */

/**
 * @author szymonmatejczyk
 */
public class Replicator extends JobModule {

  private final MessageVisitor<Void> visitor_;

  private static final int UPDATE_TIMEOUT_SEC = 10;

  private static final int LOCK_TIMEOUT_SEC = 2;

  private static final int GET_OBJECT_TIMEOUT_SEC = 10;

  // Hashtable is synchronized.
  private static Hashtable<ObjectId, String> filesLocations_ = new Hashtable<ObjectId, String>(256);
  private static Hashtable<ObjectId, Set<String>> previousVersions_ = new Hashtable<ObjectId,
      Set<String>>();
  private static Hashtable<ObjectId, Boolean> freshnessMap_ =
      new Hashtable<ObjectId, Boolean>(256);

  private static Hashtable<ObjectId, Semaphore> locksMap_ = new Hashtable<ObjectId, Semaphore>();

  private static Logger logger_ = Logger.getLogger(Replicator.class);

  public Replicator(String jobId, BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    super(jobId);
    logger_.info("Replicator ctor");
    setInQueue(inQueue);
    setOutQueue(outQueue);
    visitor_ = new ReplicatorVisitor();
  }

  /**
   * Result of queryToStore.
   */
  private enum QueryToStoreResult { OK, OBJECT_OUT_OF_DATE, INVALID_VERSION, SAVE_FAILED, TIMEOUT }

  /**
   * Visitor to handle different message types. It calls static methods and returns
   * results via queues.
   * @author szymonmatejczyk
   */
  private class ReplicatorVisitor extends MessageVisitor<Void> {
    private QueryToStoreObjectMessage storeWaitingForCommit_;

    @Override
    public Void visit(QueryToStoreObjectMessage message) throws NebuloException {
      logger_.info("StoreObjectMessage received");
      QueryToStoreResult result = queryToUpdateObject(message.getObjectId(),
          message.getEncryptedEntity(), message.getPreviousVersionSHAs());
      switch (result) {
        case OK:
          networkQueue_.add(new ConfirmationMessage(message.getSourceJobId(), message
              .getDestinationAddress(), message.getSourceAddress()));
          storeWaitingForCommit_ = message;
          try {
            TransactionResultMessage m = (TransactionResultMessage) inQueue_.poll(LOCK_TIMEOUT_SEC,
                TimeUnit.SECONDS);
            if (m == null) {
              abortUpdateObject(message.getObjectId());
              logger_.warn("Transaction aborted - timeout.");
            } else {
              processMessage(m);
            }
          } catch (InterruptedException exception) {
            abortUpdateObject(message.getObjectId());
            throw new NebuloException("Timeout", exception);
          } catch (ClassCastException exception) {
            abortUpdateObject(message.getObjectId());
            throw new NebuloException("Wrong message type received.", exception);
          }
          break;
        case OBJECT_OUT_OF_DATE:
          networkQueue_.add(new UpdateWithholdMessage(message.getSourceJobId(),
              message.getDestinationAddress(), message.getSourceAddress(),
              Reason.OBJECT_OUT_OF_DATE));
          endJobModule();
          break;
        case INVALID_VERSION:
          networkQueue_.add(new UpdateRejectMessage(message.getSourceJobId(),
              message.getDestinationAddress(), message.getSourceAddress()));
          endJobModule();
          break;
        case SAVE_FAILED:
          networkQueue_.add(new UpdateWithholdMessage(message.getSourceJobId(),
              message.getDestinationAddress(), message.getSourceAddress(), Reason.SAVE_FAILURE));
          break;
        case TIMEOUT:
          networkQueue_.add(new UpdateWithholdMessage(message.getSourceJobId(),
              message.getDestinationAddress(), message.getSourceAddress(), Reason.TIMEOUT));
          endJobModule();
          break;
        default:
          break;
      }
      return null;
    }

    @Override
    public Void visit(TransactionResultMessage message) {
      logger_.info("CommitObjectMessage received");
      if (storeWaitingForCommit_ == null) {
        //TODO(szm): ignore late abort transaction messages send by timer.
        logger_.warn("Unexpected commit message received.");
        endJobModule();
        return null;
      }
      if (message.getResult() == TransactionAnswer.COMMIT) {
        commitUpdateObject(storeWaitingForCommit_.getObjectId(),
                           storeWaitingForCommit_.getPreviousVersionSHAs(),
                           CryptoUtils.sha(storeWaitingForCommit_.getEncryptedEntity()));
      } else {
        abortUpdateObject(storeWaitingForCommit_.getObjectId());
      }
      endJobModule();
      return null;
    }

    @Override
    public Void visit(GetObjectMessage message) {
      EncryptedObject enc;
      Set<String> versions;
      try {
        enc = getObject(message.getObjectId());
        versions = previousVersions_.get(message.getObjectId());
      } catch (OutOfDateFileException exception) {
        networkQueue_.add(new ReplicatorErrorMessage(message.getSourceJobId(),
            message.getDestinationAddress(), message.getDestinationAddress(),
            "object out of date"));
        return null;
      }

      if (enc == null) {
        dieWithError(message.getSourceJobId(), message.getDestinationAddress(),
            message.getSourceAddress(), "Unable to retrieve object.");
      } else {
        networkQueue_.add(new SendObjectMessage(message.getSourceJobId(),
            message.getDestinationAddress(), message.getSourceAddress(), enc,
            versions));
      }
      endJobModule();
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
      endJobModule();
      return null;
    }

    @Override
    public Void visit(ObjectOutdatedMessage message) {
      freshnessMap_.put(message.getAddress().getObjectId(), false);
      try {
        GetEncryptedObjectModule getModule = new GetEncryptedObjectModule(message.getAddress(),
            outQueue_);
        Pair<EncryptedObject, Set<String>> res = getModule.getResult(GET_OBJECT_TIMEOUT_SEC);
        EncryptedObject encryptedObject = res.getFirst();
        try {
          deleteObject(message.getAddress().getObjectId());
        } catch (DeleteObjectException exception) {
          logger_.warn("Error deleting file.");
        }

        QueryToStoreResult query = queryToUpdateObject(message.getAddress().getObjectId(),
            encryptedObject, res.getSecond());
        if (query == QueryToStoreResult.OK || query == QueryToStoreResult.OBJECT_OUT_OF_DATE) {
          commitUpdateObject(message.getAddress().getObjectId(), res.getSecond(),
              CryptoUtils.sha(encryptedObject));
          freshnessMap_.put(message.getAddress().getObjectId(), true);
        } else
          throw new NebuloException("Unable to fetch new version of file.");
      } catch (NebuloException exception) {
        logger_.warn(exception);
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


  /**
   * Begins transaction: tries to store object to temporal location.
   */
  public static QueryToStoreResult queryToUpdateObject(ObjectId objectId,
      EncryptedObject encryptedObject, Set<String> previousVersions) {
    logger_.info("Checking store consistency");

    String currentObjectVersion = null;

    String location = filesLocations_.get(objectId);
    if (location != null) {
      /* checking whether local file is up to date */
      try {
        currentObjectVersion = CryptoUtils.sha(getObject(objectId));
      } catch (OutOfDateFileException exception) {
        return QueryToStoreResult.OBJECT_OUT_OF_DATE;
      }

      /* checking remote file's previous versions */
      if (!previousVersionsMatch(objectId, currentObjectVersion, previousVersions)) {
        return QueryToStoreResult.INVALID_VERSION;
      }
    } else {
      location = getLocationPrefix() + objectId.toString();
      locksMap_.put(objectId, new Semaphore(1));
    }

    try {
      locksMap_.get(objectId).tryAcquire(UPDATE_TIMEOUT_SEC, TimeUnit.SECONDS);
    } catch (InterruptedException exception) {
      logger_.warn("Object " + objectId.toString() + " lock timeout.");
      return QueryToStoreResult.TIMEOUT;
    }

    String tmpLocation = location + ".tmp";
    FileOutputStream fos = null;
    try {
      File f = new File(tmpLocation);
      f.getParentFile().mkdirs();
      fos = new FileOutputStream(f);
      fos.write(encryptedObject.getEncryptedData());
      fos.close();
    } catch (IOException exception) {
      logger_.error(exception.getMessage());
      return QueryToStoreResult.SAVE_FAILED;
    }

    return QueryToStoreResult.OK;
  }

  public static void commitUpdateObject(ObjectId objectId, Set<String> previousVersions,
      String currentVersion) {
    logger_.info("Commit storing object");

    String location = filesLocations_.get(objectId);

    if (location == null) {
      location = getLocationPrefix() + objectId.toString();
    }

    File previous = new File(location);
    previous.delete();

    File tmp = new File(location + ".tmp");

    tmp.renameTo(previous);


    if (filesLocations_.get(objectId) == null) {
      previousVersions_.put(objectId, new HashSet<String>(previousVersions));
      previousVersions_.get(objectId).addAll(previousVersions);
      freshnessMap_.put(objectId, true);
      filesLocations_.put(objectId, location);
    } else {
      previousVersions_.get(objectId).addAll(previousVersions);

    }


    locksMap_.get(objectId).release();
  }

  public static void abortUpdateObject(ObjectId objectId) {
    String location = filesLocations_.get(objectId);
    boolean newObjectTransaction = false;
    if (location == null) {
      newObjectTransaction = true;
      location = getLocationPrefix() + objectId.toString();
    }

    File file = new File(location + ".tmp");
    file.delete();
    locksMap_.get(objectId).release();
    if (newObjectTransaction) {
      // New lcoal object wasn't created.
      locksMap_.remove(objectId);
    }
  }

  /**
   * Returns true only if every previous version stored in previousVersions_ is contained by
   * previousVersions and also this version - current is in previousVersions_.
   */
  private static boolean previousVersionsMatch(ObjectId objectId, String current,
      Set<String> previousVersions) {
    for (String s : previousVersions_.get(objectId))
      if (!previousVersions.contains(s))
        return false;
    if (!previousVersions.contains(current))
      return false;
    return false;
  }

  /**
   * Retrieves object from disk.
   * @return Encrypted object or null if and only if object can't be read from disk(either because
   * it wasn't stored or there was a problem reading file).
   *
   * @throws OutOfDateFileException if object is stored but out of date.
   */
  public static EncryptedObject getObject(ObjectId objectId) throws OutOfDateFileException {
    String location = filesLocations_.get(objectId);
    if (location == null) {
      return null;
    }

    if (!freshnessMap_.get(objectId))
      throw new OutOfDateFileException();

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
    Semaphore mutex = locksMap_.get(objectId);
    try {
      mutex.tryAcquire(UPDATE_TIMEOUT_SEC, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new DeleteObjectException("Timeout.");
    }

    filesLocations_.remove(objectId);
    freshnessMap_.remove(objectId);
    previousVersions_.remove(objectId);
    locksMap_.remove(objectId);
    mutex.release();

    File f = new File(location);
    if (!f.exists())
      throw new DeleteObjectException("File does not exist.");
    boolean success = f.delete();
    if (!success)
      throw new DeleteObjectException("Unable to delete file.");
  }

  private static String getLocationPrefix() {
    // TODO: Read this from config file!
    return "/tmp/nebulostore/store/" + ApiFacade.getAppKey().getKey().intValue() + "/";
  }
}
