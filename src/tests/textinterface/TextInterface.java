package tests.textinterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.exceptions.NebuloException;

import tests.org.nebulostore.TestUtils;

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
          NebuloKey retKey = ApiFacade.putKey(new AppKey(tokens[1]));
          System.out.println("Successfully received key (" + retKey.appKey_.appKey_ + ")");
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
        NebuloKey nebuloKey = parseNebuloKey(tokens[1]);
        NebuloFile file;
        try {
          file = ApiFacade.getNebuloFile(nebuloKey);
        } catch (NebuloException exception) {
          System.out.println("Got exception: " + exception.getMessage());
          continue;
        }

        System.out.println("Successfully received file!");
        try {
          FileOutputStream fos = new FileOutputStream(tokens[2]);
          fos.write(file.data_);
          fos.close();
        } catch (FileNotFoundException eexception) {
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

  private static NebuloKey parseNebuloKey(String key) {
    String[] segments = key.split("/");
    String[] dirIds = new String[segments.length - 2];
    String[] entryIds = new String[segments.length - 2];
    for (int i = 1; i < segments.length - 1; ++i) {
      String[] parts = segments[i].split("\\.");
      dirIds[i - 1] = parts[0];
      entryIds[i - 1] = parts[1];
    }
    return TestUtils.createNebuloKey(segments[0], dirIds, entryIds, segments[segments.length - 1]);
  }
}
