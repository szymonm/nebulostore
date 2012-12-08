package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.api.DeleteNebuloObjectModule;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * NebuloObject - object that is stored in replicas and identified by NebuloAddress
 * (currently NebuloFile, NebuloList or FileChunk).
 * @author bolek
 */
public abstract class NebuloObject implements Serializable {

  private static final long serialVersionUID = 7791201890856369839L;
  private static Logger logger_ = Logger.getLogger(NebuloObject.class);

  // TODO(bolek): Is this constant more global?
  protected static final int TIMEOUT_SEC = 60;
  protected static BlockingQueue<Message> dispatcherQueue_;

  // TODO(bolek): final?
  protected NebuloAddress address_;
  protected transient CommAddress sender_;

  protected transient String lastCommittedVersion_;
  protected transient Set<String> previousVersions_ = new HashSet<String>();

  public static void initObjectApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static NebuloObject fromAddress(NebuloAddress key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloObjectModule module = new GetNebuloObjectModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }

  public static void deleteFromAddress(NebuloAddress address) {
    // Create a handler and run it through dispatcher.
    DeleteNebuloObjectModule module = new DeleteNebuloObjectModule(address, dispatcherQueue_);
    try {
      module.getResult(TIMEOUT_SEC);
    } catch (NebuloException e) {
      logger_.error("Received exception from DeleteNebuloObjectModule");
    }
  }

  protected NebuloObject(NebuloAddress address) {
    address_ = address;
  }

  public NebuloAddress getAddress() {
    return address_;
  }

  public void setSender(CommAddress sender) {
    sender_ = sender;
  }

  public Set<String> getVersions() {
    return previousVersions_;
  }

  public void setVersions(Set<String> versions) {
    previousVersions_ = versions;
  }

  public void delete() {
    // Create a handler and run it through dispatcher.
    DeleteNebuloObjectModule module = new DeleteNebuloObjectModule(address_, dispatcherQueue_);
    try {
      module.getResult(TIMEOUT_SEC);
    } catch (NebuloException e) {
      logger_.error("Received exception from DeleteNebuloObjectModule");
    }
  }

  /**
   * Commits all operations - invoked by user.
   * @throws NebuloException
   */
  public void sync() throws NebuloException {
    // TODO(bolek): return type? exception? sync/async?
    runSync();
  }

  protected abstract void runSync() throws NebuloException;


  public String getLastCommittedVersion() {
    return lastCommittedVersion_;
  }

  public void setLastCommittedVersion(String lastCommittedVersion) {
    lastCommittedVersion_ = lastCommittedVersion;
  }

  public void newVersionCommitted(String version) {
    previousVersions_.add(lastCommittedVersion_);
    lastCommittedVersion_ = version;
  }
}
