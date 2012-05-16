package org.nebulostore.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;

abstract public class QueryTestServer extends ServerTestingModule {

  private static Logger logger_ = Logger.getLogger(QueryTestServer.class);

  protected QueryTestServer(int lastPhase, int peersFound, int peersNeeded,
      int timeout, int phaseTimeout, String clientsJobId, boolean gatherStats,
      String testDescription) {
    super(lastPhase, peersFound, peersNeeded, timeout, phaseTimeout,
        clientsJobId, gatherStats, testDescription);
    logger_.debug("Test server up!");
  }

  @Override
  public void initClients() {
    logger_.debug("initClients called");
    int peerNum = 0;
    List<CommAddress> clientsCopy = new LinkedList<CommAddress>();
    for (CommAddress client : clients_) {
      if (peerNum >= peersFound_) {
        break;
      }
      if (client != null) {
        logger_.info("Copying address : " + client);
        clientsCopy.add(client);
        peerNum++;
      }
    }
    clients_ = new HashSet<CommAddress>(clientsCopy);
    if (peerNum < peersFound_) {
      logger_.error("NULL peer addresses found. Not initializing clients");
      this.endWithError(new NebuloException(
          "NULL Peer addresses received from NetworkContext"));
    } else {
      logger_.info("Address copy done.");
      for (CommAddress client : clientsCopy) {
        logger_.info("Initializing peer at " + client.toString());
        networkQueue_.add(new TestInitMessage(clientsJobId_, null, client,
            new QueryTestClient(jobId_, peersNeeded_)));
      }
    }

  }

  private boolean writeData(int appKey, int objectId, byte[] buffer) {
    try {
      NebuloFile dataFile = new NebuloFile(new AppKey(
          BigInteger.valueOf(appKey)), new ObjectId(
              BigInteger.valueOf(objectId)));

      dataFile.write(buffer, 0);
      dataFile.sync();
      return true;
    } catch (NebuloException e) {
      logger_.error(e);
      return false;
    }
  }

  private byte[] serialize(Object o) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      return baos.toByteArray();
    } catch (IOException e) {
      logger_.error(e);
    }
    return null;
  }

  private void writeTestDataToNebulostore() {
    HashMap<String, ObjectId> mappingMap = new HashMap<String, ObjectId>();
    mappingMap.put("peerData.xml", new ObjectId(BigInteger.valueOf(2)));

    byte[] serializedMapping = serialize(mappingMap);

    logger_.debug("Writing mappings to app_keys");

    for (int appKey = 1; appKey < peersFound_ + 1; appKey++) {
      writeData(appKey, 1, serializedMapping);
      logger_.debug("Written for " + appKey);
    }

    logger_.debug("Writing files to app_keys");

    String peerData = "some peer data file..";
    for (int appKey = 1; appKey < peersFound_ + 1; appKey++) {
      writeData(appKey, 2, peerData.getBytes());
    }
  }

  @Override
  public void configureClients() {
    logger_.debug("configuring clients in progress...");
    writeTestDataToNebulostore();
    logger_.debug("Data written.");
    for (CommAddress client : clients_) {
      networkQueue_.add(new ReconfigureTestMessage(clientsJobId_, null, client,
          clients_));
    }
  }

  @Override
  public void feedStats(TestStatistics stats) {

  }

  @Override
  protected String getAdditionalStats() {
    return "asdf";
  }

}
