package org.nebulostore.communication.dht;

/**
 * @author grzegorzmilka
 */
public class BdbDHTTestServer extends DHTTestServer {

  public BdbDHTTestServer(int testPhases, int peersFound, int peersInTest,
      int keysMultiplier, String description) {
    super(testPhases, peersFound, peersInTest, 450, 40, keysMultiplier, "bdb",
        "DHTTestClient Bdb" + description, description);
  }

}
