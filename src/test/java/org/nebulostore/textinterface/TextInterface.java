package org.nebulostore.textinterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.Peer;
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
  private static AppKey myAppKey_;

  private TextInterface() {
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Provide Peer AppKey (number)!");
      return;
    }
    final AppKey appKey = new AppKey(new BigInteger(args[0]));
    // Run NebuloStore in a separate thread.
    Thread nebuloThread = new Thread(new Runnable() {
      public void run() {
        Peer.runPeer(appKey);
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
        /*
         * end
         */
        // End application.
        Peer.quitNebuloStore();
        break;
      } else if (tokens[0].equals("putkey")) {
        /*
         * putkey (appKey)
         */
        // Execute putKey().
        if (tokens.length == 1) {
          // Default value.
          tokens = new String[2];
          tokens[1] = "9999";
        }
        try {
          ApiFacade.putKey(new AppKey(new BigInteger(tokens[1])));
          System.out.println("Successfully executed putKey(" + tokens[1] + ").");
          myAppKey_ = new AppKey(new BigInteger(tokens[1]));
        } catch (NebuloException exception) {
          System.out.println("Got exception: " + exception.getMessage());
          continue;
        }
      } else if (tokens[0].equals("write")) {
        /*
         * write (appkey) (objectId) (content)
         */
        if (tokens.length == 1) {
          // Default values.
          tokens = new String[4];
          tokens[1] = "9999";
          tokens[2] = "123";
          tokens[3] = "poprawna_zawartosc_pliku";
        }
        NebuloFile file;
        try {
          file = (NebuloFile) NebuloObject.fromAddress(
              new NebuloAddress(new AppKey(new BigInteger(tokens[1])),
                  new ObjectId(new BigInteger(tokens[1]))));
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
          continue;
        }
        //file.sync(); // This is done automatically with write().
      } else if (tokens[0].equals("read")) {
        /*
         * read (appkey) (objectId) (destination_path)
         */
        if (tokens.length == 1) {
          // Default values.
          tokens = new String[4];
          tokens[1] = "9999";
          tokens[2] = "123";
          tokens[3] = "pliczek";
        }
        NebuloFile file;
        try {
          file = (NebuloFile) NebuloObject.fromAddress(
              new NebuloAddress(new AppKey(new BigInteger(tokens[1])),
                  new ObjectId(new BigInteger(tokens[2]))));
        } catch (NebuloException exception) {
          System.out.println("Got exception from 'fromAddress()': " + exception.getMessage());
          continue;
        }

        byte[] data;
        try {
          data = file.read(0, 100);
        } catch (NebuloException exception) {
          System.out.println("Got exception from 'read()': " + exception.getMessage());
          exception.printStackTrace();
          continue;
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
}
