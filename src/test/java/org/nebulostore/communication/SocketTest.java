package org.nebulostore.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class SocketTest {

  private static Logger logger_ = Logger.getLogger(SocketTest.class);

  private final int port_ = 9789;
  private final int fallbackPort = 9790;

  private final int bootstrapPort_ = 9788;
  //private final String bootstrapAddress_ = "127.0.0.1";
  private final String bootstrapAddress_ = "130.83.166.245";


  private boolean isBootstrap_ = false;
  private final Set<String> remoteHosts = new HashSet<String>();

  private final Map<String, Socket> remoteConnections = new HashMap<String, Socket>();

  public SocketTest(boolean isBootstrap) {
    isBootstrap_ = isBootstrap;
  }

  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    boolean isBootstrap = false;
    if (args.length == 1)
      isBootstrap = true;

    SocketTest test = new SocketTest(isBootstrap);
    test.run();
  }

  class BootstrapServer implements Runnable {
    ServerSocket server;

    public BootstrapServer() throws IOException {
      logger_.info("Init on bootstrap server");
      try {
        server = new ServerSocket(bootstrapPort_);
      } catch (IOException e) {
        logger_.error(e);
        throw e;
      }

      logger_.info("Bootstrap server opened.");
    }

    @Override
    public void run() {
      logger_.info("Bootstrap server started.");

      while (true) {

        try {
          Socket socket = server.accept();

          synchronized (remoteHosts) {
            String addr = socket.getRemoteSocketAddress().toString()
                .replaceAll("/", "").replaceAll(":.*", "");
            logger_.info("Feeding host: " + addr);
            remoteHosts.add(addr);

            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(outputStream));
            for (String host : remoteHosts) {
              bufferedWriter.write(host + "\n");
            }
            bufferedWriter.write("END\n");
            bufferedWriter.close();
            socket.close();
            logger_.info("Finished for host: " +
                socket.getRemoteSocketAddress().toString());
          }
        } catch (IOException e) {
          logger_.error(e);
        }
      }
    }

  }

  class BootstrapClient extends TimerTask {

    @Override
    public void run() {

      logger_.info("Running bootstarp client");
      try {
        Socket socket = new Socket(bootstrapAddress_, bootstrapPort_);

        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream));
        boolean readed = false;
        while (!readed) {
          String line = bufferedReader.readLine().trim();
          if (line.equals("END")) {
            readed = true;
          } else {
            logger_.info("Readed address: " + line);
            synchronized (remoteHosts) {
              remoteHosts.add(line);
            }
          }
        }
        logger_.info("All readed. closing");
        bufferedReader.close();
        socket.close();

      } catch (UnknownHostException e) {
        logger_.error(e);
      } catch (IOException e) {
        logger_.error(e);
      }

    }

  }

  class OutputHandler extends TimerTask {

    @Override
    public void run() {

      synchronized (remoteHosts) {
        for (String address : remoteHosts) {
          if (!remoteConnections.containsKey(address)) {
            try {
              logger_.info("Opening new connection to : " + address);
              remoteConnections.put(address, new Socket(address, port_));
            } catch (UnknownHostException e) {
              logger_.error(e);

            } catch (IOException e) {
              logger_.error(e);
            }
          }
        }
      }

      String payload = "012345678901";
      for (int i = 0; i < 7; i++) {
        payload += payload;
      }
      int messagesPerPeer = 5;

      for (Socket socket : remoteConnections.values()) {

        logger_.info("Trying to write data to host: " +
            socket.getRemoteSocketAddress().toString());
        try {

          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              socket.getOutputStream()));

          for (int i = 0; i < messagesPerPeer; i++) {
            String data = "Hello world!" + payload + " \n";
            bytesSent += data.length();
            count++;
            writer.write(data);
          }
          writer.flush();

        } catch (IOException e) {
          logger_.error(e);
        }
      }

    }

  }

  class IncomingHandler implements Runnable {

    private final Socket socket_;

    public IncomingHandler(Socket socket) {
      logger_.info("Accepting connection from remote host: " +
          socket.getRemoteSocketAddress().toString());
      socket_ = socket;
    }

    @Override
    public void run() {

      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new InputStreamReader(
            socket_.getInputStream()));
      } catch (IOException e) {
        logger_.error("Unable to create reader. Closing thread.");
        return;
      }

      while (true) {
        try {
          String data = reader.readLine();
          countR++;
          bytesR += data.length();
          logger_.info("Readed line of length: " + data.length());

        } catch (IOException e) {
          logger_.error("Unable to readline...", e);
        }

      }

    }

  }

  private long count = 0;
  private long lastCount = 0;

  private long bytesSent = 0;
  private long lastBytes = 0;

  private long lastCountR = 0;
  private long countR = 0;

  private long bytesR = 0;
  private long lastBytesR = 0;

  private long lastTime;

  class StatTask extends TimerTask {

    @Override
    public void run() {
      long now = System.currentTimeMillis();
      int peers = 0;
      synchronized (remoteHosts) {
        peers = remoteHosts.size();
      }

      logger_.info("STATS: peers: " + peers + " sent msg : " +
          ((count - lastCount) * 1000 / (now - lastTime)) + " bytes: " +
          ((bytesSent - lastBytes) * 1000 / (now - lastTime)) + " recv msg: " +
          ((countR - lastCountR) * 1000 / (now - lastTime)) + " bytes: " +
          ((bytesR - lastBytesR) * 1000 / (now - lastTime)));
      lastTime = now;
      lastCount = count;
      lastBytes = bytesSent;
      lastCountR = countR;
      lastBytesR = bytesR;
    }

  }

  private void run() {

    logger_.info("Initializing...");

    Timer timer = new Timer();

    if (isBootstrap_) {
      logger_.info("In bootstrap mode, starting bootstrap server");

      try {
        (new Thread(new BootstrapServer(), "BootstrapServer")).start();
      } catch (IOException e) {
        logger_.error("Unable to start bootstrap server due to: ", e);
      }
    }
    logger_.info("Starting bootstrap client.");
    timer.schedule(new BootstrapClient(), 5000, 5000);

    logger_.info("Creating output handler. ");
    timer.schedule(new OutputHandler(), 250, 250);

    logger_.info("Creating stats handler. ");
    timer.schedule(new StatTask(), 10000, 10000);

    logger_.info("Creating a socket server for incoming connections");

    ServerSocket server = null;

    try {
      server = new ServerSocket(port_);
    } catch (IOException e) {
      logger_.info("Failed to bind to " + port_ + " retrying on " +
          fallbackPort);
      try {
        server = new ServerSocket(fallbackPort);
      } catch (IOException e1) {
        logger_.error("Unable to bind to fallback port. Closing...", e);
        return;
      }

    }

    ExecutorService pool = Executors.newFixedThreadPool(30);

    while (true) {
      try {
        pool.execute(new IncomingHandler(server.accept()));
      } catch (IOException e) {
        logger_.error(e);
      }
    }

  }

}
