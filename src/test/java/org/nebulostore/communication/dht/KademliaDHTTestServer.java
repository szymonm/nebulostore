package org.nebulostore.communication.dht;

public class KademliaDHTTestServer extends DHTTestServer {

  public KademliaDHTTestServer(int testPhases, int peersFound, int peersInTest,
      int keysMultiplier, String description) {
    super(testPhases, peersFound, peersInTest, 250, 40, keysMultiplier, "kademlia", "DHTTestClient Kademlia " + description,
        description);
  }

}
