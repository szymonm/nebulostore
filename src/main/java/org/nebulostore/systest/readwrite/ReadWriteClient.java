package org.nebulostore.systest.readwrite;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Vector;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.NebuloFile;
import org.nebulostore.appcore.model.NebuloObject;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.UserCommMessage;

/**
 * ReadWrite client.
 * @author bolek
 */
public class ReadWriteClient extends ConductorClient {
  private static final long serialVersionUID = -7238750658102427676L;
  private static Logger logger_ = Logger.getLogger(ReadWriteClient.class);
  private static final int MAX_ITER = 10;
  private static final int INITIAL_SLEEP = 10000;
  private static final int ITER_SLEEP = 500;

  private CommAddress[] clients_;
  private Vector<NebuloAddress> files_;
  private AppKey myAppKey_;
  private int clientId_;
  private NebuloFile myFile_;

  public ReadWriteClient(String serverJobId, CommAddress[] clients, int clientId) {
    super(serverJobId);
    clients_ = clients;
    clientId_ = clientId;
    files_ = new Vector<NebuloAddress>();
  }

  @Inject
  public void setAppKey(AppKey appKey) {
    myAppKey_ = appKey;
  }

  @Override
  protected void initVisitors() {
    visitors_ =  new TestingModuleVisitor[4];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new Visitor1();
    visitors_[2] = new Visitor2();
    visitors_[3] = new Visitor3();
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e1) {
      logger_.debug("Interrupted while sleeping.");
    }
  }

  /**
   * Phase 1 - Create new file, send its address to everyone and receive their information.
   */
  final class Visitor1 extends TestingModuleVisitor  {
    @Override
    public Void visit(NewPhaseMessage message) {
      sleep(INITIAL_SLEEP);
      myFile_ = new NebuloFile(myAppKey_, new ObjectId(new BigInteger((clientId_ + 1) + "000")));
      try {
        myFile_.write(myAppKey_.getKey().toString().getBytes("UTF-8"), 0);
      } catch (NebuloException exception) {
        endWithError("Unable to write NebuloFile (" + exception.getMessage() + ")");
        return null;
      } catch (UnsupportedEncodingException e) {
        endWithError("Unable to encode string in UTF-8.");
        return null;
      }
      logger_.debug("Sending NebuloAddress to " + clients_.length + " peers.");
      for (int i = 0; i < clients_.length; ++i)
        if (i != clientId_)
          networkQueue_.add(new UserCommMessage(jobId_, clients_[i], myFile_.getAddress()));
      tryFinishPhase();
      return null;
    }

    @Override
    public Void visit(UserCommMessage message) {
      NebuloAddress receivedAddr = (NebuloAddress) message.getContent();
      logger_.debug("Received NebuloAddress: " + receivedAddr);
      files_.add(receivedAddr);
      tryFinishPhase();
      return null;
    }

    private void tryFinishPhase() {
      if (files_.size() == clients_.length - 1)
        phaseFinished();
    }
  }

  /**
   * Phase 2 - read all the files and verify.
   */
  final class Visitor2 extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      for (NebuloAddress address : files_) {
        boolean fetched = false;
        // Try to fetch each file at most MAX_ITER times.
        for (int iter = 1; iter <= MAX_ITER; ++iter) {
          try {
            NebuloFile file = (NebuloFile) NebuloObject.fromAddress(address);
            byte[] content = file.read(0, 1000);
            if (!Arrays.equals(content,
                address.getAppKey().getKey().toString().getBytes("UTF-8"))) {
              endWithError("File content is incorrect (" + new String(content, "UTF-8") + ")");
              return null;
            } else {
              logger_.debug("Received correct file from address " + address);
              fetched = true;
              break;
            }
          } catch (NebuloException e) {
            logger_.debug("Unable to fetch file with address " + address + " in iteration " + iter);
          } catch (UnsupportedEncodingException e) {
            endWithError("Unable to decode received string in UTF-8.");
          }
          sleep(ITER_SLEEP);
        }
        if (!fetched) {
          endWithError("Unable to fetch file with address " + address);
          break;
        }
      }
      phaseFinished();
      return null;
    }
  }

  /**
   * Phase 3 - delete my file.
   */
  final class Visitor3 extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      try {
        myFile_.delete();
      } catch (NebuloException e) {
        endWithError("Unable to delete file (" + e.getMessage() + ")");
        return null;
      }
      phaseFinished();
      return null;
    }
  }
}
