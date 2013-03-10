package org.nebulostore.systest.lists;

import java.util.Iterator;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Lists test.
 * @author Bolek Kulbabinski
 */
public final class ListsServer extends ConductorServer {
  private static final int NUM_PHASES = 3;
  private static final int NUM_CLIENTS = 5;
  private static final int TIMEOUT_SEC = 200;

  public ListsServer() {
    super(NUM_PHASES, NUM_CLIENTS, TIMEOUT_SEC, "ListsClient_" + CryptoUtils.getRandomString(),
        "Lists server");
    useServerAsClient_ = false;
    gatherStats_ = false;
  }

  @Override
  public void initClients() {
    Iterator<CommAddress> it = clients_.iterator();
    CommAddress[] clients = new CommAddress[NUM_CLIENTS];
    for (int i = 0; i < NUM_CLIENTS; ++i)
      clients[i] = it.next();
    for (int i = 0; i < NUM_CLIENTS; ++i)
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
