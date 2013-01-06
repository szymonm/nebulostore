package org.nebulostore.conductor.pingpong;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorServer;
import org.nebulostore.conductor.messages.InitMessage;

/**
 * Sets up PingPong test.
 * @author szymonmatejczyk
 */
public class PingPongServer extends ConductorServer {
  private static Logger logger_ = Logger.getLogger(PingPongServer.class);

  public PingPongServer() {
    // Test consist of 2 phases and needs 2 peers.
    // We give it 60 seconds.
    // We set "PingPong client" as clients job id.
    super(2, 2, 60, "PingPong client", "PingPong server");
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
    networkQueue_.add(new InitMessage(clientsJobId_, null, pongAddress,
        new PongClient(jobId_)));
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
