package pl.edu.uw.mimuw.nebulostore.communication.jxta;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import net.jxta.document.AdvertisementFactory;
import net.jxta.impl.id.CBID.PipeID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;

public class SocketServer implements Runnable {

    public final static String SOCKETIDSTR = "urn:jxta:cbid-59616261646162614E5047205032503393B5C2F6CA7A41FBB0F890173088E79404";
    private transient PeerGroup netPeerGroup = null;
    
    static private Logger logger = Logger.getLogger(SocketServer.class);
    
    public SocketServer(PeerGroup netPeerGroup)
    {
    	DOMConfigurator.configure("log4j.xml");
    	this.netPeerGroup = netPeerGroup;
    }
    
    public static PipeAdvertisement createSocketAdvertisement() {
        PipeID socketID = null;

        try {
            socketID = (PipeID) PipeID.create(new URI(SOCKETIDSTR));
        } catch (URISyntaxException use) {
            logger.error(use.getStackTrace());
        }
        PipeAdvertisement advertisement = (PipeAdvertisement)
                AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

        advertisement.setPipeID(socketID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Socket tutorial");
        return advertisement;
    }
    
    /**
     * Wait for connections
     */
    public void run() {

        logger.debug("Starting ServerSocket");
        JxtaServerSocket serverSocket = null;
        try {
            serverSocket = new JxtaServerSocket(netPeerGroup, createSocketAdvertisement(), 10);
            serverSocket.setSoTimeout(0);
        } catch (IOException e) {
            logger.debug("failed to create a server socket");
            e.printStackTrace();
            System.exit(-1);
        }

        while (true) {
            try {
                logger.debug("Waiting for connections");
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    logger.debug("New socket connection accepted");
                    Thread thread = new Thread(new ConnectionHandler(socket), "Connection Handler Thread");
                    thread.start();
                }
            } catch (Exception e) {
                logger.error("Exception: ", e);
            }
        }
    }
    
    
    private class ConnectionHandler implements Runnable {
        Socket socket = null;

        ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Sends data over socket
         *
         * @param socket the socket
         */
        private void sendAndReceiveData(Socket socket) {
            try {
                long start = System.currentTimeMillis();

                // get the socket output stream
                OutputStream out = socket.getOutputStream();
                // get the socket input stream
                InputStream in = socket.getInputStream();
                DataInput dis = new DataInputStream(in);

                long iterations = dis.readLong();
                int size = dis.readInt();
                long total = iterations * size * 2L;
                long current = 0;

                logger.debug(MessageFormat.format("Sending/Receiving {0} bytes.", total));
                while (current < iterations) {
                    byte[] buf = new byte[size];
                    dis.readFully(buf);
                    out.write(buf);
                    out.flush();
                    current++;
                }

                out.close();
                in.close();

                long finish = System.currentTimeMillis();
                long elapsed = finish - start;
                logger.debug(MessageFormat.format("EOT. Received {0} bytes in {1} ms. Throughput = {2} KB/sec.", total, elapsed,
                        (total / elapsed) * 1000 / 1024));
                socket.close();
                logger.debug("Connection closed");
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }

        public void run() {
            sendAndReceiveData(socket);
        }
    }
	
}
