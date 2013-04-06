package org.nebulostore.appcore.model;

import java.util.Set;

import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.replicator.TransactionAnswer;

/**
 * Interface for modules capable of writing NebuloObjects.
 * @author Bolek Kulbabinski
 */
public interface ObjectWriter {
  /**
   * Write the object asynchronously.
   * @param nebuloAddress
   * @param objectToWrite
   * @param previousVersionSHAs
   */
  void writeObject(NebuloAddress nebuloAddress, NebuloObject objectToWrite,
      Set<String> previousVersionSHAs);

  // TODO(bolek): Move this logic into the module, it should not be inside NebuloFile!
  Void getSemiResult(int timeout) throws NebuloException;
  void setAnswer(TransactionAnswer answer);

  /**
   * Blocking method that waits for the end of module's execution.
   * @param timeoutSec
   * @throws NebuloException thrown if write was unsuccessful
   */
  void awaitResult(int timeoutSec) throws NebuloException;
}
