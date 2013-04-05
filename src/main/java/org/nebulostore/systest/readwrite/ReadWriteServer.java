package org.nebulostore.systest.readwrite;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Sets up ReadWrite test.
 *
 * @author Bolek Kulbabinski
 */
public final class ReadWriteServer extends ConductorServer {
  private static Logger logger_ = Logger.getLogger(ReadWriteServer.class);
  private static final int NUM_PHASES = 3;
  private static final int INITIAL_SLEEP = 5000;
  private static final int TIMEOUT_SEC = 200;

  public ReadWriteServer() {
    super(NUM_PHASES, TIMEOUT_SEC, "ReadWriteClient_" + CryptoUtils.getRandomString(),
        "ReadWrite server");
    gatherStats_ = true;
  }

  @Override
  public void initClients() {
    sleep(INITIAL_SLEEP);
    Iterator<CommAddress> it = clients_.iterator();
    CommAddress[] clients = new CommAddress[peersNeeded_];
    for (int i = 0; i < peersNeeded_; ++i)
      clients[i] = it.next();
    for (int i = 0; i < peersNeeded_; ++i)
      networkQueue_.add(new InitMessage(clientsJobId_, null, clients[i],
          new ReadWriteClient(jobId_, commAddress_, NUM_PHASES, clients, i)));
  }

  @Override
  public void feedStats(CommAddress sender, CaseStatistics stats) {
    logger_.debug("Received statistics from " + sender);
    ReadWriteStats rwStats = (ReadWriteStats) stats;
    StringBuffer result = new StringBuffer();
    for (NebuloAddress addr : rwStats.getAddresses())
      result.append(addr + ", ");
    logger_.debug("Unavailable files: " + result);
  }

  @Override
  protected String getAdditionalStats() {
    return "";
  }
}
