import java.io.*;
import java.net.*;

/**
 * Usage: FirewallTestClient SERVER_HOSTNAME [SERVER_PORT]
 *
 * Returns 0 iff client has sucessfully communicated with given server
 */
class FirewallTestClient {
  private static int SERVER_PORT = 9876;
  private static int MAX_DATA_LEN = 1024;
  private static int TIMEOUT = 1000;

  public static void main(String args[]) throws Exception {
    if (args.length == 0) {
      System.err.println("Incorrect arguments\n" +
          "Usage: FirewallTestClient SERVER_HOSTNAME [SERVER_PORT]");
      System.exit(1);
    }

    String hostname = args[0];
    int port = SERVER_PORT;
    if (args.length > 1) {
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.out.println("Given arg is not a correct port number.");
        System.exit(1);
      }
    }
    sendUDPMessage(hostname, port);
    sendTCPMessage(hostname, port);
  }

  static void sendUDPMessage(String serverHostname, int port) {
    try {
      DatagramSocket clientSocket = new DatagramSocket();

      InetAddress IPAddress = InetAddress.getByName(serverHostname);
      System.out.println ("Attemping to connect to " + IPAddress +
          ") via UDP port " + port);

      byte[] sendData = (new String("EXAMPLE")).getBytes();
      byte[] receiveData = new byte[MAX_DATA_LEN];

      System.out.println ("Sending data to " + sendData.length +
          " bytes to server.");

      DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, port);

      clientSocket.send(sendPacket);

      DatagramPacket receivePacket =
        new DatagramPacket(receiveData, receiveData.length);

      System.out.println ("Waiting for return packet");
      clientSocket.setSoTimeout(1000);

      try {
        clientSocket.receive(receivePacket);
        String sentence = new String(receivePacket.getData());

        InetAddress returnIPAddress = receivePacket.getAddress();

        port = receivePacket.getPort();

        System.out.println ("From server at: " + returnIPAddress + ":" + port);
        System.out.println("Message: " + sentence);
      } catch (SocketTimeoutException ste) {
        System.out.println ("Timeout Occurred: Packet assumed lost");
        System.exit(1);
      }

      clientSocket.close();
    }
    catch (UnknownHostException ex) {
      System.err.println(ex);
      System.exit(1);
    }
    catch (IOException ex) {
      System.err.println(ex);
      System.exit(1);
    }
  }

  static void sendTCPMessage(String serverHostname, int port) throws Exception {
    System.out.println ("Attemping to connect to host " + serverHostname +
        " on port " + port);

    Socket echoSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try {
      echoSocket = new Socket(serverHostname, port);
      out = new PrintWriter(echoSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(
            echoSocket.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host: " + serverHostname);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to: " +
          serverHostname);
      System.exit(1);
    }

    out.println("EXAMPLE");
    System.out.println("echo: " + in.readLine());

    out.close();
    in.close();
    echoSocket.close();
  }
}
