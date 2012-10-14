package org.nebulostore.replicator.messages;

import java.util.Set;

import com.rits.cloning.Cloner;

import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author bolek
 * This is a message containing requested object.
 */
public class SendObjectMessage extends CommMessage {
  private static final long serialVersionUID = 5852937000391705084L;

  private final EncryptedObject encryptedEntity_;

  private final Set<String> versions_;

  public SendObjectMessage(CommAddress sourceAddress, CommAddress destAddress,
      EncryptedObject encryptedObject, Set<String> versions) {
    super(sourceAddress, destAddress);
    encryptedEntity_ = encryptedObject;
    Cloner c = new Cloner();
    versions_ = c.deepClone(versions);
  }

  public SendObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, EncryptedObject encryptedObject, Set<String> versions) {
    super(jobId, sourceAddress, destAddress);
    encryptedEntity_ = encryptedObject;
    Cloner c = new Cloner();
    versions_ = c.deepClone(versions);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public EncryptedObject getEncryptedEntity() {
    return encryptedEntity_;
  }

  public Set<String> getVersions() {
    return versions_;
  }
}
