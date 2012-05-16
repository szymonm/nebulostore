package org.nebulostore.query;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
            new QueryTestClient(jobId_, lastPhase_ + 2)));
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

  private static String readFile(String path) throws IOException {
    FileInputStream stream = new FileInputStream(new File(path));
    try {
      FileChannel fc = stream.getChannel();
      MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      /* Instead of using default, pass in a decoder. */
      return Charset.defaultCharset().decode(bb).toString();
    } finally {
      stream.close();
    }
  }

  private void writeFilesForAppKey(String testName, int appKeyInt) {

    String folderPath = "resources/test/query/" + testName + "/" + appKeyInt;
    File folder = new File(folderPath);
    File[] listOfFiles = folder.listFiles();

    List<String> filesToFetch = new LinkedList<String>();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        filesToFetch.add(listOfFiles[i].getName());
      }
    }

    int mappingObjectId = 1;
    HashMap<String, ObjectId> mappingMap = new HashMap<String, ObjectId>();

    int startObjectId = 100;
    int stepObjectId = 100;

    int objectId = startObjectId;
    for (String filename : filesToFetch) {
      logger_.debug("fetching filename: " + filename);
      mappingMap.put(filename, new ObjectId(BigInteger.valueOf(objectId)));

      try {
        writeData(appKeyInt, objectId, readFile(folderPath + "/" + filename)
            .getBytes());
      } catch (IOException e) {
        logger_.error("Unable to read file from local drive: " + folderPath + "/" + filename, e);
      }
      objectId += stepObjectId;
    }
    writeData(appKeyInt, mappingObjectId, serialize(mappingMap));

  }

  private void writeTestDataToNebulostore() {
    for (int appKey = 1; appKey < peersFound_ + 1; appKey++) {
      writeFilesForAppKey("trivialTest", appKey);
      logger_.debug("Data written for " + appKey);
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
