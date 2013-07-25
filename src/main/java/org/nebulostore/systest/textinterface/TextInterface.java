package org.nebulostore.systest.textinterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Scanner;

import com.google.inject.Inject;

import org.nebulostore.appcore.addressing.AppKey;
import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.appcore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.NebuloFile;
import org.nebulostore.appcore.model.NebuloObjectFactory;
import org.nebulostore.peers.Peer;

/**
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
 *
 * @author Bolek Kulbabinski
 */
public final class TextInterface extends Peer {
  private static final String DEFAULT_APPKEY = "22";
  private static final String DEFAULT_OBJECT_ID = "123";
  private static final String DEFAULT_CONTENT = "poprawna zawartosc pliku\n";
  private static final String DEFAULT_FILE_NAME = "plik.txt";

  private NebuloObjectFactory objectFactory_;

  public TextInterface() {
    objectFactory_ = null;
  }

  @Inject
  public void setDependencies(NebuloObjectFactory objectFactory) {
    objectFactory_ = objectFactory;
  }

  @Override
  protected void initializeModules() {
    System.out.print("Starting NebuloStore ...\n");
    runNetworkMonitor();
    runBroker();
  }

  @Override
  protected void runActively() {
    register(appKey_);
    inputLoop();
  }

  protected void inputLoop() {
    try (Scanner in = new Scanner(System.in, "UTF-8")) {
      while (true) {
        System.out.print("$ ");
        String line = in.nextLine();
        String[] tokens = line.split(" ");
        if (tokens[0].equals("end")) {
          quitNebuloStore();
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void removeSubscription(String[] tokens) {
    if (!validateParametersNumber(tokens, 3)) {
      return;
    }
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
  private void putKey(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[2];
      tokens[1] = DEFAULT_APPKEY;
    }
    super.register(new AppKey(new BigInteger(tokens[1])));
  }

  /**
   * = read (appkey) (objectId) (destination_path).
   *
   * @throws IOException
   */
  private void read(String[] input) throws IOException {
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
      return;
    }

    System.out.println("Successfully received file!");
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(tokens[3]);
      fos.write(data);
    } catch (FileNotFoundException exception) {
      System.out.println("Cannot write file!");
    } catch (IOException exception) {
      System.out.println("Cannot write file!");
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }

  /**
   * write (appkey) (objectId) (content).
   */
  private void write(String[] input) {
    String[] tokens = input;
    if (tokens.length == 1) {
      tokens = new String[4];
      tokens[1] = DEFAULT_APPKEY;
      tokens[2] = DEFAULT_OBJECT_ID;
      tokens[3] = DEFAULT_CONTENT;
    }
    NebuloFile file;
    try {
      file = (NebuloFile) objectFactory_.fetchExistingNebuloObject(new NebuloAddress(new AppKey(
          new BigInteger(tokens[1])), new ObjectId(new BigInteger(tokens[2]))));
      System.out.println("Successfully fetched existing file");
    } catch (NebuloException exception) {
      file = objectFactory_.createNewNebuloFile(new NebuloAddress(new AppKey(new BigInteger(
          tokens[1])), new ObjectId(new BigInteger(tokens[2]))));
      System.out.println("Successfully created new file.");
    }
    try {
      int bytesWritten = file.write(tokens[3].getBytes("UTF-8"), 0);
      System.out.println("Successfully written " + bytesWritten + " bytes.");
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'write()': " + exception.getMessage());
      return;
    } catch (UnsupportedEncodingException exception) {
      System.out.println("Got UnsupportedEncodingException from 'write()': " +
          exception.getMessage());
      return;
    }
    // file.sync(); // This is currently done automatically in write().
  }

  /**
   * delete (appkey) (objectId).
   */
  private void delete(String[] input) {
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
  private void subscribe(String[] tokens) {
    if (!validateParametersNumber(tokens, 3)) {
      return;
    }
    NebuloFile file = getNebuloFile(tokens[1], tokens[2]);
    if (file != null) {
      subscribeWithExceptionHandling(file);
    }
  }

  private boolean validateParametersNumber(String[] tokens, int paramsNumber) {
    if (tokens.length != 3) {
      System.out.println("Invalid parameters number. Required : " + paramsNumber + " params for " +
          tokens[0] + "operation.");
      return false;
    }
    return true;
  }

  private void subscribeWithExceptionHandling(NebuloFile file) {
    try {
      file.subscribe();
    } catch (NebuloException e) {
      System.out.println("Subscription failed.");
    }
  }

  private NebuloFile getNebuloFile(String appKeyString, String objectIdString) {
    try {
      AppKey appKey = new AppKey(new BigInteger(appKeyString));
      ObjectId objectId = new ObjectId(new BigInteger(objectIdString));
      return (NebuloFile) objectFactory_.fetchExistingNebuloObject(new NebuloAddress(appKey,
          objectId));
    } catch (NebuloException exception) {
      System.out.println("Got exception from 'fromAddress()': " + exception.getMessage());
      return null;
    }
  }
}
