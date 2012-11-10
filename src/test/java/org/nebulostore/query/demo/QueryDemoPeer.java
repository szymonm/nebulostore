package org.nebulostore.query.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.communication.address.CommAddress;
//import org.nebulostore.communication.jxta.JXTAPeer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.query.client.DQLClient;
import org.nebulostore.query.executor.DQLExecutor;

public class QueryDemoPeer extends Peer {

  private static Logger logger_ = Logger.getLogger(QueryDemoPeer.class);

  enum ConsoleState {
    Exiting, WaitingForResults, WaitingForQuery
  };


  public static void main(String[] args) {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    BigInteger appKey = BigInteger.ZERO;
    if (args.length < 1) {
      // Random AppKey if not provided.
      appKey = CryptoUtils.getRandomId();
      logger_.debug("Random appKey generateds = " + appKey);
    } else {
      appKey = new BigInteger(args[0]);
    }
    logger_.info("Starting testing peer with appKey = " + appKey);
    startPeer(new AppKey(appKey));

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
    }

    try {
      ApiFacade.putKey(new AppKey(appKey));
    } catch (NebuloException e) {
      logger_.error(e);
    }



    String dqlJobId = CryptoUtils.getRandomId().toString();
    dispatcherInQueue_.add(new JobInitMessage(dqlJobId, new DQLExecutor(
        dqlJobId, true)));

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
    }

    writeFilesForAppKey("pajek-40", appKey.intValue());

    //JXTAPeer.startFeeding_ = true;


    ConsoleState actualState = ConsoleState.WaitingForQuery;

    InputStreamReader inputReader = new InputStreamReader(System.in);
    BufferedReader input = new BufferedReader(inputReader);

    System.out.println("Welcome to DQL console! (type 'help')");

    while (actualState != ConsoleState.Exiting) {

      System.out.print("> ");

      String command = null;
      try {
        command = input.readLine().toLowerCase().trim();
      } catch (IOException e) {
        actualState = ConsoleState.Exiting;
      }

      if (command != null) {

        if (command.equals("help")) {
          printHelp();
        }
        if (command.equals("query")) {
          try {
            issueQuery(input);
          } catch (IOException e) {
            actualState = ConsoleState.Exiting;
          }
        }

        if (command.equals("ls")) {
          listPeers();
        }

        if (command.equals("write-data")) {
          writeData();
        }


        /*
         * TODO:
         *  -> show known peers --- DONE?
         *  -> and the DHT contents
         *  -> refresh data written to distributed fs
         * 
         */

        if (command.equals("exit")) {
          actualState = ConsoleState.Exiting;
        }

      }
    }

    System.out.println("\nEXITING!");
    System.exit(0);
  }

  private static void listPeers() {
    Vector<CommAddress> knownPeers = NetworkContext.getInstance().getKnownPeers();

    System.out.println("visible " + knownPeers.size() + " peers:");
    for (CommAddress knownPeer: knownPeers) {
      System.out.print("\t" + knownPeer.toString() + "\t");
      AppKey executorAppKey = DQLExecutor.getInstance().getExecutorAppKey(knownPeer);
      if (executorAppKey == null) {
        System.out.println("UKNOWN APP KEY");
      } else {
        System.out.println(executorAppKey.toString());
      }
    }
    System.out.println("\n");
  }


  private static void issueQuery(BufferedReader input) throws IOException {

    String query = "";
    int maxDepth = 1;

    boolean queryEnd = false;
    while (!queryEnd) {

      String line;

      line = input.readLine();
      if (line.trim().length() == 0) {
        queryEnd = true;
      } else {
        query += line;
      }
    }

    if (query.trim().length() == 0) {
      System.out.println("ERROR: empty query");
      return;
    }

    // TODO: Read maximum depth

    System.out.println("Please provide maximum query depth...");
    boolean readMaxDepth = false;
    while (!readMaxDepth) {
      String line = input.readLine().trim();
      try {
        maxDepth = Integer.valueOf(line);
        readMaxDepth = true;
      } catch (NumberFormatException exception) {
      }
    }

    if (maxDepth < 0 || maxDepth > 3) {
      System.out.println("ERROR: depth out of bounds [0;3]");
      return;
    }

    DQLClient dqlClient = new DQLClient(query, maxDepth);
  }

  private static void printHelp() {
    System.out.println("Available commands:");
    System.out.println("\thelp\t - displays this help");
    System.out.println("\tquery\t - issues query defined lines following,");
    System.out.println("\t\t waits for results. Query input mode ends with an");
    System.out.println("\t\t empty line. Then maximum query depth have");
    System.out.println("\t\t to be specified.");
    System.out.println("\tls\t - shows a list of connected peers");
    //System.out.println("\tdht-get\t - get value from DHT under a key specified");
    System.out.println("\twrite-data\t - writes down test cases");
    System.out.println("\texit\t - exit from the console");
  }


  private static boolean writeData(int appKey, int objectId, byte[] buffer) {
    System.out.println("\twriting for appKey " + appKey + " objectId " + objectId);
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

  private static byte[] serialize(Object o) {
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

  private static void writeFilesForAppKey(String testName, int appKeyInt) {
    logger_.info("called writeFilesForAppKey(" + testName + ", " + appKeyInt +
        ")");
    int testFoldersNum = 40;

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

  static private void writeData() {
    Vector<CommAddress> knownClients = NetworkContext.getInstance().getKnownPeers();
    System.out.println("writing data for " + knownClients.size() + " clients");
    for (CommAddress client : knownClients) {
      if (DQLExecutor.getInstance().getExecutorAppKey(client) != null) {
        System.out.println("writing data for client\t" + client.toString() + "\t...");
        writeFilesForAppKey("pajek-40", DQLExecutor.getInstance()
            .getExecutorAppKey(client).getKey().intValue());
        logger_.debug("Data written for " +
            DQLExecutor.getInstance().getExecutorAppKey(client).getKey()
            .intValue());
      } else {
        System.out.println("ommitting client\t" + client.toString() + "\t unknown app key");
      }



    }
  }

}
