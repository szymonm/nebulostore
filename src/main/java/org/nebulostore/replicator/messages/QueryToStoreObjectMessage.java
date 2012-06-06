package org.nebulostore.replicator.messages;

import java.util.Set;

import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

import com.rits.cloning.Cloner;


/**
 * @author szymonmatejczyk
 * This is a query to store a particular object.
 */
public class QueryToStoreObjectMessage extends CommMessage {
  private static final long serialVersionUID = 3283983404037381657L;

  ObjectId objectId_;
  EncryptedObject encryptedEntity_;
  Set<String> previousVersionSHAs_;
  private final String sourceJobId_;
  /*
  public StoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }
   */
  public QueryToStoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, ObjectId objectId, EncryptedObject encryptedEntity,
      Set<String> previousVersionSHAs, String sourceJobId) {
    super(jobId, sourceAddress, destAddress);
    objectId_ = objectId;
    encryptedEntity_ = encryptedEntity;
    Cloner c = new Cloner();
    previousVersionSHAs_ = c.deepClone(previousVersionSHAs);
    sourceJobId_ = sourceJobId;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public EncryptedObject getEncryptedEntity() {
    return encryptedEntity_;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  public String getSourceJobId() {
    return sourceJobId_;
  }

  public Set<String> getPreviousVersionSHAs() {
    return previousVersionSHAs_;
  }

  @Override
  public JobModule getHandler() {
    return new Replicator(jobId_, null, null);
  }
}
