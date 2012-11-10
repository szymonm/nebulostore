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
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.query.executor.DQLExecutor;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;

abstract public class QueryTestServer extends ServerTestingModule {

  private static Logger logger_ = Logger.getLogger(QueryTestServer.class);

  private Map<AppKey, Map<Integer, QueryDescription>> queryTests_;
  private final int queryTimeout_;

  protected QueryTestServer(int lastPhase, int peersFound, int peersNeeded,
      int timeout, int phaseTimeout, String clientsJobId, boolean gatherStats,
      String testDescription,
      Map<AppKey, Map<Integer, QueryDescription>> queryTests, int queryTimeout) {
    super(lastPhase, peersFound, peersNeeded, timeout, phaseTimeout,
        clientsJobId, gatherStats, testDescription);
    queryTests_ = queryTests;
    queryTimeout_ = queryTimeout;
    logger_.debug("Test server up!");
  }

  public void setQueryTests(
      Map<AppKey, Map<Integer, QueryDescription>> queryTests) {
    queryTests_ = queryTests;
  }

  @Override
  public void initClients() {
    logger_.debug("initClients called");

    Random rand = new Random(System.currentTimeMillis());
    List<CommAddress> clientsCopy = new LinkedList<CommAddress>();
    Vector<CommAddress> clientsToShuffle = new Vector<CommAddress>(clients_);

    for (int i = 0; i < peersFound_; i++) {
      clientsCopy.add(clientsToShuffle.remove(rand.nextInt(clientsToShuffle
          .size())));
    }

    clients_ = new HashSet<CommAddress>(clientsCopy);

    logger_.info("Address copy done.");
    for (CommAddress client : clientsCopy) {
      logger_.info("Initializing peer at " + client.toString());
      AppKey appKey = DQLExecutor.getInstance().getExecutorAppKey(client);
      networkQueue_.add(new TestInitMessage(clientsJobId_, null, client,
          new QueryTestClient(jobId_, lastPhase_ + 2, queryTests_.get(appKey),
              queryTimeout_)));
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
    logger_.info("called writeFilesForAppKey(" + testName + ", " + appKeyInt +
        ")");
    int testFoldersNum = 4;

    String folderPath = "resources/test/query/" + testName + "/" +
        (((appKeyInt - 1) % testFoldersNum) + 1);

    logger_.debug("folderPath = " + folderPath);
    File folder = new File(folderPath);
    File[] listOfFiles = folder.listFiles();

    logger_.debug("listOfFiles = " + listOfFiles);

    List<String> filesToFetch = new LinkedList<String>();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        filesToFetch.add(listOfFiles[i].getName());
      }
    }

    logger_.debug("filesToFetch = " + filesToFetch);

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
        logger_.error("Unable to read file from local drive: " + folderPath +
            "/" + filename, e);
      }
      objectId += stepObjectId;
    }

    logger_.debug("mappingMap = " + mappingMap);
    writeData(appKeyInt, mappingObjectId, serialize(mappingMap));

    logger_.info("writeFilesForAppKey finished.");

  }

  private void writeTestDataToNebulostore() {
    for (CommAddress client : clients_) {
      writeFilesForAppKey("pajek-40", DQLExecutor.getInstance()
          .getExecutorAppKey(client).getKey().intValue());
      logger_.debug("Data written for " +
          DQLExecutor.getInstance().getExecutorAppKey(client).getKey()
          .intValue());
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
    return "\tNO ADDITIONAL STATS DEFINED";
  }

}
