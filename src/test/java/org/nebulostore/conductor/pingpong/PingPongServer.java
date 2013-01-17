package org.nebulostore.conductor.pingpong;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Sets up PingPong test.
 * @author szymonmatejczyk
 */
public class PingPongServer extends ConductorServer {
  private static Logger logger_ = Logger.getLogger(PingPongServer.class);
  private static final int NUM_PHASES = 2;
  private static final int NUM_CLIENTS = 2;
  private static final int TIMEOUT_SEC = 60;

  public PingPongServer() {
    super(NUM_PHASES, NUM_CLIENTS, TIMEOUT_SEC, "PingPongClient_" + CryptoUtils.getRandomString(),
        "PingPong server");
    useServerAsClient_ = false;
  }

  @Override
  public void initClients() {
    Iterator<CommAddress> i = clients_.iterator();
    CommAddress pingAddress = i.next();
    logger_.debug("Initializing ping: " + pingAddress.toString());
    CommAddress pongAddress = i.next();
    logger_.debug("Initializing pong: " + pongAddress.toString());

    networkQueue_.add(new InitMessage(clientsJobId_, null, pingAddress,
        new PingClient(jobId_, pongAddress)));
    networkQueue_.add(new InitMessage(clientsJobId_, null, pongAddress, new PongClient(jobId_)));
  }

  @Override
  public void configureClients() {
  }

  @Override
  public void feedStats(CaseStatistics stats) {
  }

  @Override
  protected String getAdditionalStats() {
    return "";
  }
}
