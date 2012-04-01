package org.nebulostore.testing.pingpong;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.testing.TestInitMessage;
import org.nebulostore.testing.ServerTestingModule;

/**
 * Sets up PingPong test.
 * @author szymonmatejczyk
 */
public class PingPongServer extends ServerTestingModule {
  private static Logger logger_ = Logger.getLogger(ServerTestingModule.class);

  public PingPongServer() {
    // Test consist of 2 phases and needs 2 peers.
    // We give it 5 seconds.
    // We set "PingPong client" as clients job id.
    super(2, 2, 10, "PingPong client");
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

}
