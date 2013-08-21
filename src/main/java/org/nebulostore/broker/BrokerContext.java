package org.nebulostore.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.communication.address.CommAddress;

/**
 * All persistent data held by Broker.
 *
 * @author szymonmatejczyk
 */
public final class BrokerContext {
  private static Logger logger_ = Logger.getLogger(BrokerContext.class);
  private static BrokerContext instance_;

  // TODO(szm,bolek): make fields private and create synchronized methods for accessing them.

  private final ContractsSet contracts_ = new ContractsSet();
  private final Map<CommAddress, List<Contract>> contractMap_ =
      new TreeMap<CommAddress, List<Contract>>();

  private final ReadWriteLock readWriteLock_ = new ReentrantReadWriteLock();
  private final Lock readLock_ = readWriteLock_.readLock();
  private final Lock writeLock_ = readWriteLock_.writeLock();

  private final BrokerConfiguration brokerConfiguration_ = new BrokerConfiguration();

  /**
   * Available space for contract.
   */
  private Map<Contract, Integer> freeSpaceMap_ = new HashMap<Contract, Integer>();

  /**
   * Contract offers send to peers, waiting for response.
   */
  private Map<CommAddress, Contract> contractOffers_ = new HashMap<CommAddress, Contract>();

  /* Asynchronous messages */
  // todo(szm): move to asynchronous messages module

  /**
   * Messages waiting to be retrieved by peers.
   */
  private Map<CommAddress, List<AsynchronousMessage>> waitingAsynchronousMessagesMap_ =
      new HashMap<CommAddress, List<AsynchronousMessage>>();

  /**
   * CommAddresses of peers, that retrieved AM, but haven't sent response yet.
   */
  private Set<CommAddress> waitingForAck_ = new HashSet<CommAddress>();

  /**
   * Synchro-peers of this instance. Cached from DHT.
   */
  private List<CommAddress> myInboxHolders_ = new LinkedList<CommAddress>();

  /**
   * JobId of jobs we are waiting for response from.
   */
  private Set<String> waitingForMessages_ = new HashSet<String>();

  public static synchronized BrokerContext getInstance() {
    if (instance_ == null) {
      instance_ = new BrokerContext();
    }
    return instance_;
  }

  public void addContract(Contract contract) {
    writeLock_.lock();
    try {
      logger_.debug("Adding contract with: " + contract.getPeer().toString());
      contracts_.add(contract);
      if (contractMap_.containsKey(contract.getPeer())) {
        contractMap_.get(contract.getPeer()).add(contract);
      } else {
        List<Contract> contractList = new ArrayList<Contract>();
        contractList.add(contract);
        contractMap_.put(contract.getPeer(), contractList);
      }
    } finally {
      writeLock_.unlock();
    }
  }

  public void remove(Contract contract) {
    writeLock_.lock();
    try {
      logger_.debug("Removing contract with: " + contract.getPeer().toString());
      contracts_.remove(contract);
      if (contractMap_.containsKey(contract.getPeer())) {
        contractMap_.get(contract.getPeer()).remove(contract);
      } else {
        logger_.warn("Removing not existing contract with: " +
            contract.getPeer().toString());
      }
    } finally {
      writeLock_.unlock();
    }
  }

  public ContractsSet acquireReadAccessToContracts() {
    readLock_.lock();
    return contracts_;
  }

  public void disposeReadAccessToContracts() {
    readLock_.unlock();
  }

  public List<CommAddress> getMyInboxHolders() {
    return myInboxHolders_;
  }

  public List<Contract> getUserContracts(CommAddress id) {
    readLock_.lock();
    try {
      return contractMap_.get(id);
    } finally {
      readLock_.unlock();
    }
  }

  public CommAddress[] getReplicas() {
    readLock_.lock();
    try {
      CommAddress[] addresses = new CommAddress[contracts_.size()];
      Iterator<Contract> iter = contracts_.iterator();
      int i = 0;
      while (iter.hasNext()) {
        addresses[i] = iter.next().getPeer();
        i++;
      }
      return addresses;
    } finally {
      readLock_.unlock();
    }
  }

  public Map<CommAddress, List<AsynchronousMessage>> getWaitingAsynchronousMessages() {
    return waitingAsynchronousMessagesMap_;
  }

  public Set<CommAddress> getWaitingForAck() {
    return waitingForAck_;
  }

  public Set<String> getWaitingForMessages() {
    return waitingForMessages_;
  }

  public void setMyInboxHolders(List<CommAddress> myInboxHolders) {
    myInboxHolders_ = myInboxHolders;
  }

  private BrokerContext() {
  }

  public BrokerConfiguration getConfiguration() {
    return brokerConfiguration_;
  }
}
