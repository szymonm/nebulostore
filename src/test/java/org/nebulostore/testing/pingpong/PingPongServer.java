package org.nebulostore.testing.pingpong;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.TestInitMessage;

/**
 * Sets up PingPong test.
 * @author szymonmatejczyk
 */
public class PingPongServer extends ServerTestingModule {
  private static Logger logger_ = Logger.getLogger(PingPongServer.class);

  public PingPongServer() {
    // Test consist of 2 phases and needs 2 peers.
    // We give it 5 seconds.
    // We set "PingPong client" as clients job id.
    super(2, 2, 20, "PingPong server", "PingPong server");
  }

  @Override
  public void initClients() {
    Iterator<CommAddress> i = clients_.iterator();
    CommAddress pingAddress = i.next();
    logger_.debug("Initializing ping: " + pingAddress.toString());
    CommAddress pongAddress = i.next();
    logger_.debug("Initializing pong: " + pongAddress.toString());

    networkQueue_.add(new TestInitMessage(clientsJobId_, null, pingAddress,
        new PingClient(jobId_, pongAddress)));
    networkQueue_.add(new TestInitMessage(clientsJobId_, null, pongAddress,
        new PongClient(jobId_)));
  }

  @Override
  public void configureClients() {
  }

  @Override
  public void feedStats(TestStatistics stats) {
  }

  @Override
  protected String getAdditionalStats() {
    return "";
  }

}
