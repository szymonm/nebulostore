package org.nebulostore.textinterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.context.NebuloContext;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * This is a very simple text interface to interact with NebuloStore.
 *
 * Commands:
 *    $ end
 *          Ends application.
 *    $ putkey (appKey)
 *          Executes putkey(appKey)
 *    $ write (appKey) (objectId) (content)
 *          Writes content (no spaces) to file.
 *    $ read (appKey) (objectId) (destination_path)
 *          Reads file and saves the content to destination_path.
 *    $ delete (appKey) (objectId)
 *          Deletes the file.
 *    $ subscribe (appKey) (objectId)
 *          Adds my subscription to the file.
 *
 *
 *    Default values when parameters are not given:
 *          appKey = 9999
 *          objectId = 123   (first chunk id = 124)
 *          content = "poprawna_zawartosc_pliku"
 *          destination_path = "pliczek"
 */
public final class TextInterface {
  private static Logger logger_ = Logger.getLogger(TextInterface.class);
  private static final String DEFAULT_APPKEY = "22";
  private static final String DEFAULT_OBJECT_ID = "123";
  private static final String DEFAULT_CONTENT = "poprawna zawartosc pliku\n";
  private static final String DEFAULT_FILE_NAME = "plik.txt";

  private TextInterface() {
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Provide Peer AppKey (number)!");
      return;
    }
    final Injector injector = Guice.createInjector(new NebuloContext());
    final AppKey appKey = new AppKey(new BigInteger(args[0]));
    // Run NebuloStore in a separate thread.
    Thread nebuloThread = new Thread(new Runnable() {
      public void run() {
        Peer.runPeer(appKey, injector);
      }
    });
    DOMConfigurator.configure("resources/conf/log4j.xml");
    System.out.print("Starting NebuloStore ...\n");
    nebuloThread.start();

    Scanner in = new Scanner(System.in);
    while (true) {
      System.out.print("$ ");
      String line = in.nextLine();
      String[] tokens = line.split(" ");

      if (tokens[0].equals("end")) {
        Peer.quitNebuloStore();
        break;
      } else if ("putkey".equals(tokens[0])) {
        putKey(tokens);
      } else if ("write".equals(tokens[0])) {
        write(tokens);
      } else if ("read".equals(tokens[0])) {
        read(tokens);
      } else if ("delete".equals(tokens[0])) {
        delete(tokens);
      } else if ("subscribe".equals(tokens[0])) {
        subscribe(tokens);
      } else if ("rmsub".equals(tokens[0])) {
        removeSubscription(tokens);
      } else if (tokens[0].equals("")) {
        continue;
      } else {
        System.out.println("Unknown command (type \"end\" to exit)");
      }
    }

    try {
      nebuloThread.join();
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    }
  }

  private static void removeSubscription(String[] tokens) {
    if (!validateParametersNumber(tokens, 3)) return;
    NebuloFile file = getNebuloFile(tokens[1], tokens[2]);
    if (file != null) {
      try {
        file.removeSubscription();
      } catch (NebuloException e) {
        System.out.println("Subscription removal failed.");
      }
    }
  }

  /**
   * putkey (appKey).
   */
  private static void putKey(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[2];
      tokens[1] = DEFAULT_APPKEY;
    }
    try {
      ApiFacade.putKey(new AppKey(new BigInteger(tokens[1])));
      System.out.println("Successfully executed putKey(" + tokens[1] + ").");
    } catch (NebuloException exception) {
      System.out.println("Got exception: " + exception.getMessage());
      return;
    }
  }

  /**
   * read (appkey) (objectId) (destination_path).
   */
  private static void read(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[4];
      tokens[1] = DEFAULT_APPKEY;
      tokens[2] = DEFAULT_OBJECT_ID;
      tokens[3] = DEFAULT_FILE_NAME;
    }
    NebuloFile file = getNebuloFile(tokens[1], tokens[2]);
    if (file == null) {
      return;
    }

    byte[] data;
    try {
      data = file.read(0, 100);
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'read()': " + exception.getMessage());
      exception.printStackTrace();
      return;
    }

    System.out.println("Successfully received file!");
    try {
      FileOutputStream fos = new FileOutputStream(tokens[3]);
      fos.write(data);
      fos.close();
    } catch (FileNotFoundException exception) {
      System.out.println("Cannot write file!");
    } catch (IOException exception) {
      System.out.println("Cannot write file!");
    }
  }

  /**
   * write (appkey) (objectId) (content).
   */
  private static void write(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[4];
      tokens[1] = DEFAULT_APPKEY;
      tokens[2] = DEFAULT_OBJECT_ID;
      tokens[3] = DEFAULT_CONTENT;
    }
    NebuloFile file;
    try {
      file = (NebuloFile) NebuloObject.fromAddress(
          new NebuloAddress(new AppKey(new BigInteger(tokens[1])),
              new ObjectId(new BigInteger(tokens[2]))));
      System.out.println("Successfully fetched existing file");
    } catch (NebuloException exception) {
      file = new NebuloFile(new AppKey(new BigInteger(tokens[1])),
          new ObjectId(new BigInteger(tokens[2])));
      System.out.println("Successfully created new file.");
    }
    try {
      int bytesWritten = file.write(tokens[3].getBytes(), 0);
      System.out.println("Successfully written " + String.valueOf(bytesWritten) + " bytes.");
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'write()': " + exception.getMessage());
      exception.printStackTrace();
      return;
    }
    //file.sync(); // This is currently done automatically in write().
  }

  /**
   * delete (appkey) (objectId).
   */
  private static void delete(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[3];
      tokens[1] = DEFAULT_APPKEY;
      tokens[2] = DEFAULT_OBJECT_ID;
    }
    NebuloFile file = getNebuloFile(tokens[1], tokens[2]);
    if (file == null) {
      return;
    }

    try {
      file.delete();
      System.out.println("Successfully deleted file!");
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'delete()': " + exception.getMessage());
      return;
    }
  }

  /**
   * subscribe (appkey) (objectId).
   */
  private static void subscribe(String[] tokens) {
    if (!validateParametersNumber(tokens, 3)) return;
    NebuloFile file = getNebuloFile(tokens[1], tokens[2]);
    if (file != null) {
      subscribeWithExceptionHandling(file);
    }
  }

  private static boolean validateParametersNumber(String[] tokens, int paramsNumber) {
    if (tokens.length != 3) {
      System.out.println("Invalid parameters number. Required : " + paramsNumber + " params for " +
          tokens[0] + "operation.");
      return false;
    }
    return true;
  }

  private static void subscribeWithExceptionHandling(NebuloFile file) {
    try {
      file.subscribe();
    } catch (NebuloException e) {
      System.out.println("Subscription failed.");
    }
  }

  private static NebuloFile getNebuloFile(String appKeyString, String objectIdString) {
    try {
      AppKey appKey = new AppKey(new BigInteger(appKeyString));
      ObjectId objectId = new ObjectId(new BigInteger(objectIdString));
      return (NebuloFile) NebuloObject.fromAddress(new NebuloAddress(appKey, objectId));
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'fromAddress()': " + exception.getMessage());
      return null;
    }
  }
}
