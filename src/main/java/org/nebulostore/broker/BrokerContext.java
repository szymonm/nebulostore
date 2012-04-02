package org.nebulostore.broker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.async.messages.AsynchronousMessage;

/**
 * All persistent data hold by Broker.
 *  * @author szymonmatejczyk
 */
public final class BrokerContext {
  private static BrokerContext instance_;

  public static BrokerContext getInstance() {
    if (instance_ == null)
      instance_ = new BrokerContext();
    return instance_;
  }

  private BrokerContext() {
    super();
  }

  public InstanceID instanceID_;

  public InstanceID getInstanceID() {
    return instanceID_;
  }

  public void setInstanceID(InstanceID instanceID) {
    instanceID_ = instanceID;
  }

  //TODO(szm): make fields private and craete methods for accessing them.
  //TODO(szm): synchronization

  public static Logger logger_ = Logger.getLogger(BrokerContext.class);

  // TODO(szm): Contracts structure.
  public HashSet<Contract> contracts_ = new HashSet<Contract>();

  /**
   * Availabile space for contract.
   */
  public HashMap<Contract, Integer> freeSpaceMap_ = new HashMap<Contract, Integer>();

  /**
   * Contract offers send to peers, waiting for response.
   */
  public HashSet<InstanceID> contractOffers_ = new HashSet<InstanceID>();

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
}
