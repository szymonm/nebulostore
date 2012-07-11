package org.nebulostore.communication.jxta;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.jxta.document.AdvertisementFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.jxta.utils.IDFactory;
import org.nebulostore.communication.streambinding.IStreamBindingDriver;
import org.nebulostore.communication.streambinding.StreamBindingService;

/**
 * @author Marcin Walas
 */
public class SocketServer implements Runnable, IStreamBindingDriver {

  private static Logger logger_ = Logger.getLogger(SocketServer.class);

  public static final String SOCKETIDSTR = "socketServerAdv";
  private transient PeerGroup netPeerGroup_;

  private StreamBindingService streamBindingService_;

  public SocketServer(PeerGroup netPeerGroup) {
    netPeerGroup_ = netPeerGroup;
  }

  public PipeAdvertisement createSocketAdvertisement() {
    PipeID socketID = IDFactory.createPipeID(netPeerGroup_.getPeerGroupID(),
        SOCKETIDSTR);

    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    advertisement.setPipeID(socketID);
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Nebulostore socket.");

    return advertisement;
  }

  /**
   * Wait for connections.
   */
  @Override
  public void run() {

    logger_.debug("Starting ServerSocket");
    JxtaServerSocket serverSocket = null;
    try {
      serverSocket = new JxtaServerSocket(netPeerGroup_,
          createSocketAdvertisement(), 10);
      serverSocket.setSoTimeout(0);
    } catch (IOException e) {
      logger_.error(e);
      return;
    }

    while (streamBindingService_ == null) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logger_.debug("Sleep interrupted");
      }
    }

    while (true) {
      try {
        logger_.debug("Waiting for connections");
        Socket socket = serverSocket.accept();
        logger_.debug("Connection established!");

        InputStream inStream = socket.getInputStream();
        BufferedInputStream buffStream = new BufferedInputStream(inStream);

        byte[] jobIdByte = new byte[buffStream.read()];
        buffStream.read(jobIdByte);
        String jobId = new String(jobIdByte);

        byte[] streamIdByte = new byte[buffStream.read()];
        buffStream.read(streamIdByte);
        String streamId = new String(streamIdByte);

        logger_.debug("Description of socket fully read.");

        if (jobId != null && jobId.equals("")) {
          jobId = null;
        }

        if (jobId != null && streamId.equals("")) {
          streamId = null;
        }
        streamBindingService_.bindStream(inStream, jobId, streamId);

      } catch (IOException e) {
        logger_.error("Exception: ", e);
      }
    }
  }


  /*
   * (non-Javadoc)
   * @see
   * org.nebulostore.communication.streambinding.IStreamBindingDriver#bindStream
   * (org.nebulostore.communication.address.CommAddress, java.lang.String,
   * java.lang.String, long)
   */
  @Override
  public OutputStream bindStream(CommAddress address, String jobId,
      String streamId, long timeout) throws IOException {
    logger_.error("Trying to bindStream in Socket server. JXTA is obsolete");
    throw new IOException("JXTA is obsolete.");
  }

  public void setStreamBindingService(StreamBindingService s) {
    streamBindingService_ = s;
  }
}
