package org.nebulostore.broker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.communication.address.CommAddress;

/**
 * All persistent data held by Broker.
 * @author szymonmatejczyk
 */
public final class BrokerContext {
  private static Logger logger_ = Logger.getLogger(BrokerContext.class);
  private static BrokerContext instance_;

  public InstanceID instanceID_;

  //TODO(szm,bolek): make fields private and create synchronized methods for accessing them.

  private HashSet<Contract> contracts_ = new HashSet<Contract>();
  private HashMap<InstanceID, Vector<Contract>> contractMap_ =
      new HashMap<InstanceID, Vector<Contract>>();

  /**
   * Available space for contract.
   */
  public HashMap<Contract, Integer> freeSpaceMap_ = new HashMap<Contract, Integer>();

  /**
   * Contract offers send to peers, waiting for response.
   */
  public HashMap<InstanceID, Contract> contractOffers_ = new HashMap<InstanceID, Contract>();

  /**
   * Peers already discovered by this instance.
   */
  public HashSet<InstanceID> knownPeers_ = new HashSet<InstanceID>();

  /* Asynchronous messages */

  /**
   * Messages waiting to be retrieved by peers.
   */
  public Map<InstanceID, LinkedList<AsynchronousMessage> > waitingAsynchronousMessagesMap_ =
      new HashMap<InstanceID, LinkedList<AsynchronousMessage> >();

  /**
   * InstanceId's of peers, that retrieved AM, but haven't sent response yet.
   */
  public Set<InstanceID> waitingForAck_ = new HashSet<InstanceID>();

  /**
   * Synchro-peers of this instance. Cached from DHT.
   */
  public List<InstanceID> myInboxHolders_ = new LinkedList<InstanceID>();

  /**
   * JobId of jobs we are waiting for response from.
   */
  public Set<String> waitingForMessages_ = new HashSet<String>();

  public InstanceID getInstanceID() {
    return instanceID_;
  }

  public void setInstanceID(InstanceID instanceID) {
    instanceID_ = instanceID;
  }

  public static BrokerContext getInstance() {
    if (instance_ == null)
      instance_ = new BrokerContext();
    return instance_;
  }

  public void addContract(Contract contract) {
    // TODO(bolek,szm): better way to synchronize?
    synchronized (contracts_) {
      synchronized (contractMap_) {
        contracts_.add(contract);
        if (contractMap_.containsKey(contract.getPeer())) {
          contractMap_.get(contract.getPeer()).add(contract);
        } else {
          Vector<Contract> vector = new Vector<Contract>();
          vector.add(contract);
          contractMap_.put(contract.getPeer(), vector);
        }
      }
    }
  }

  public Vector<Contract> getUserContracts(InstanceID id) {
    return contractMap_.get(id);
  }

  public CommAddress[] getReplicas() {
    synchronized (contracts_) {
      CommAddress[] addresses = new CommAddress[contracts_.size()];
      Iterator<Contract> iter = contracts_.iterator();
      int i = 0;
      while (iter.hasNext()) {
        addresses[i] = iter.next().getPeer().getAddress();
      }
      return addresses;
    }
  }

  private BrokerContext() { }
}
