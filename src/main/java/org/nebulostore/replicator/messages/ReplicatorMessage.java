package org.nebulostore.replicator.messages;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.core.Replicator;

/**
 * Base class for all replicator messages. All these messages are handled by current replicator
 * implementation.
 *
 * @see Replicator
 * @author Bolek Kulbabinski
 */
public abstract class ReplicatorMessage extends CommMessage {
  private static final long serialVersionUID = 2732807823967546590L;
  private Provider<Replicator> replicatorProvider_;

  public ReplicatorMessage(CommAddress destAddress) {
    super(null, destAddress);
  }

  public ReplicatorMessage(String jobId, CommAddress destAddress) {
    super(jobId, null, destAddress);
  }

  @Inject
  public void setReplicatorProvider(Provider<Replicator> replicatorProvider) {
    replicatorProvider_ = replicatorProvider;
  }

  @Override
  public JobModule getHandler() {
    return replicatorProvider_.get();
  }
}
