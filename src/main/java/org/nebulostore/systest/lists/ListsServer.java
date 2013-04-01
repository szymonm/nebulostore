package org.nebulostore.systest.lists;

import java.util.Iterator;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Lists test.
 *
 * @author Bolek Kulbabinski
 */
public final class ListsServer extends ConductorServer {
  private static final int NUM_PHASES = 3;
  private static final int TIMEOUT_SEC = 400;
  private static final int INITIAL_SLEEP = 5000;

  public ListsServer() {
    super(NUM_PHASES, TIMEOUT_SEC, "ListsClient_" + CryptoUtils.getRandomString(),
        "Lists server");
    gatherStats_ = false;
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
          new ListsClient(jobId_, NUM_PHASES, clients, i)));
  }

  @Override
  public void feedStats(CommAddress sender, CaseStatistics stats) { }

  @Override
  protected String getAdditionalStats() {
    return "";
  }
}
