package org.nebulostore.systest.lists;

import java.math.BigInteger;
import java.util.Vector;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.NebuloElement;
import org.nebulostore.appcore.model.NebuloList;
import org.nebulostore.appcore.model.NebuloList.ListIterator;
import org.nebulostore.appcore.model.NebuloObjectFactory;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Lists client.
 *
 * @author Bolek Kulbabinski
 */
public final class ListsClient extends ConductorClient {
  private static final long serialVersionUID = -7238750658102427676L;
  private static Logger logger_ = Logger.getLogger(ListsClient.class);
  private static final int MAX_ITER = 10;
  private static final int INITIAL_SLEEP = 5000;
  private static final int ITER_SLEEP = 500;
  private static final int N_CASES = 3;

  private CommAddress[] clients_;
  private Vector<NebuloAddress> addresses_;
  private int clientId_;
  private NebuloList myList_;
  private transient NebuloObjectFactory objectFactory_;

  public ListsClient(String serverJobId, int numPhases, CommAddress[] clients, int clientId) {
    super(serverJobId, numPhases);
    clients_ = clients;
    clientId_ = clientId;
    addresses_ = new Vector<NebuloAddress>();
  }

  @Inject
  public void setNebuloObjectFactory(NebuloObjectFactory objectFactory) {
    objectFactory_ = objectFactory;
  }

  @Override
  protected void initVisitors() {
    visitors_ =  new TestingModuleVisitor[numPhases_ + 2];
    visitors_[0] = new EmptyInitializationVisitor();
    myList_ = createList();
    visitors_[1] = new AddressExchangeVisitor(clients_, addresses_, clientId_, myList_.getAddress(),
        INITIAL_SLEEP);
    visitors_[2] = new ReadFilesVisitor();
    visitors_[3] = new DeleteFileVisitor();
    visitors_[4] = new IgnoreNewPhaseVisitor();
  }

  private NebuloList createList() {
    NebuloList list = objectFactory_.createNewNebuloList(
        new ObjectId(new BigInteger((clientId_ + 1) + "000")));
    try {
      for (int i = 0; i < N_CASES; ++i) {
        BigInteger value = list.getAddress().getObjectId().getKey().add(BigInteger.valueOf(i));
        list.append(new NebuloElement(CryptoUtils.encryptObject(value)));
      }
      list.sync();
      return list;
    } catch (CryptoException e) {
      endWithError("Exception while encrypting object: " + e.getMessage());
      return null;
    } catch (NebuloException e) {
      endWithError("Exception in list append: " + e.getMessage());
      return null;
    }
  }

  /**
   * Phase 2 - read all the files and verify.
   */
  final class ReadFilesVisitor extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      for (NebuloAddress address : addresses_) {
        // Try to fetch each file at most MAX_ITER times.
        for (int iter = 1; iter <= MAX_ITER; ++iter) {
          try {
            NebuloList list = (NebuloList) objectFactory_.fetchExistingNebuloObject(address);
            ListIterator iterator = list.iterator();
            for (int i = 0; i < N_CASES; ++i) {
              BigInteger elem = (BigInteger) CryptoUtils.decryptObject(iterator.next().getData());
              BigInteger good = list.getAddress().getObjectId().getKey().add(BigInteger.valueOf(i));
              if (!good.equals(elem)) {
                endWithError("List content is incorrect (" + elem + " != " + good + ")");
                return null;
              }
            }
            logger_.debug("Received correct list from address " + address);
            break;
          } catch (NebuloException e) {
            logger_.debug("Unable to fetch list with address " + address + " in iteration " + iter);
          }
          sleep(ITER_SLEEP);
        }
      }
      phaseFinished();
      return null;
    }
  }

  /**
   * Phase 3 - delete my file.
   */
  final class DeleteFileVisitor extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      try {
        myList_.delete();
      } catch (NebuloException e) {
        endWithError("Unable to delete file (" + e.getMessage() + ")");
        return null;
      }
      phaseFinished();
      return null;
    }
  }
}