package tests.textinterface;

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
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * This is a very simple text interface to interact with NebuloStore.
 *
 * Commands:
 *    $ end
 *          Ends application.
 *    $ get appKey/dirId1.entryId1/dirId2.entryId2/objectId fileToSave.dat
 *          Calls getNebuloFile() and writes the result to fileToSave.dat.
 *    $ put appKey
 *          Calls putKey(appKey) and prints returned NebuloKey.
 */
public final class TextInterface {
  private static Logger logger_ = Logger.getLogger(TextInterface.class);

  private TextInterface() {
  }

  public static void main(String[] args) {
    // Run NebuloStore in a separate thread.
    Thread nebuloThread = new Thread(new Runnable() {
      public void run() {
        Peer.runPeer();
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
        // End application.
        ApiFacade.quitNebuloStore();
        break;
      } else if (tokens[0].equals("put")) {
        // Execute putKey().
        if (tokens.length == 1) {
          // TESTING - default value.
          tokens = new String[2];
          tokens[1] = "appkey";
        }
        try {
          ApiFacade.putKey(new AppKey(tokens[1]));
          System.out.println("Successfully executed putKey().");
        } catch (NebuloException exception) {
          System.out.println("Got exception: " + exception.getMessage());
          continue;
        }
      } else if (tokens[0].equals("get")) {
        // Execute getNebuloFile().
        if (tokens.length == 1) {
          // TESTING - default values.
          tokens = new String[3];
          tokens[1] = "appkey/topdir.file1/fileId";
          tokens[2] = "pliczek";
        }
        NebuloFile file;
        try {
          file = (NebuloFile) ApiFacade.getNebuloFile(new NebuloAddress(new AppKey("appkey"),
              new ObjectId(new BigInteger("2"))));
        } catch (NebuloException exception) {
          System.out.println("Got exception: " + exception.getMessage());
          continue;
        }

        System.out.println("Successfully received file!");
        try {
          FileOutputStream fos = new FileOutputStream(tokens[2]);
          fos.write(file.data_);
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
