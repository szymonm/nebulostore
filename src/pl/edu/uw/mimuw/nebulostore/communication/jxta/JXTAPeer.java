package pl.edu.uw.mimuw.nebulostore.communication.jxta;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.security.cert.CertificateException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.appcore.Module;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommPeerFound;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommSendData;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

public class JXTAPeer extends Module implements DiscoveryListener {
	
	// TODO: probably to application configuration file
	static private String PEER_NAME = "mypeername";
	
	
	private transient PeriodicWorker periodicWorker;
	private transient SocketServer socketServer;
	
    private transient NetworkManager manager;
    private transient DiscoveryService discovery;
    
    private transient NetworkConfigurator config;
    
    static private Logger logger = Logger.getLogger(JXTAPeer.class);
    
    private List<String> knownPeers;
	
	
	public JXTAPeer(BlockingQueue<Message> jxtaPeerIn, BlockingQueue<Message> jxtaPeerOut)
	{
		super(jxtaPeerIn, jxtaPeerOut);
		
		DOMConfigurator.configure("log4j.xml");
		
		// TODO:
		// 1. uruchomić DiscoveryServer
		// 2. wczytywać konfigurację z pliku, jeśli jest już utworzony
		// 3. rozgłaszać swoje usługi
		// 4. coś poczytać w dokumentacji, jak się pipe'y robi		

        try {
			manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, PEER_NAME);
		} catch (IOException e) {
			e.printStackTrace();
			// TODO MBW: Better handle this situation
			System.exit(-1);
		}
        
        manager.setConfigPersistent(true);
        logger.info("PeerID: " + manager.getPeerID().toString());
        
        
        // Retrieving the Network Configurator
        logger.info("Retrieving the Network Configurator");
        try {
            config = manager.getConfigurator();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        logger.info("Network Configurator retrieved");
        // Does a local peer configuration exist?
        if (config.exists()) {
            logger.info("Local configuration found");
            // We load it
            File LocalConfig = new File(config.getHome(), "PlatformConfig");
            try {
                logger.info("Loading found configuration");
                config.load(LocalConfig.toURI());
                logger.info("Configuration loaded");
           } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
           } catch (CertificateException ex) {
                // An issue with the existing peer certificate has been encountered
                ex.printStackTrace();
                System.exit(-1);
           }
       } else {
           logger.info("No local configuration found");
           config.setName(PEER_NAME);
           config.setPrincipal("asdf");
           config.setPassword("asfd");
           logger.info("Principal: " + config.getPrincipal());
           logger.info("Password : " + config.getPassword());
           try {
                logger.info("Saving new configuration");
                config.save();
                logger.info("New configuration saved successfully");
           } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
           }
       }


        logger.info("PeerID: " + manager.getPeerID().toString());
        
        prepare();
        
        // socket server
        socketServer = new SocketServer(manager.getNetPeerGroup());
        new Thread(socketServer).start();
        
        
        // periodic worker prepartions
        discovery.addDiscoveryListener(this);
        
        
        
        knownPeers = new LinkedList<String>();
        
        periodicWorker = new PeriodicWorker(discovery);
        new Thread(periodicWorker).start();
	}
	

	
	
	private void prepare()
	{
        try {
			manager.startNetwork();
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        PeerGroup netPeerGroup = manager.getNetPeerGroup();

        // get the discovery service
        discovery = netPeerGroup.getDiscoveryService();		
	}

    /**
     * loop forever attempting to discover advertisements every minute
     */
    public void start() {
    }


    /*
     * (non-Javadoc)
     * @see pl.edu.uw.mimuw.nebulostore.appcore.IModule#processMessage(pl.edu.uw.mimuw.nebulostore.appcore.Message)
     */
    /**
     * number of iterations to send the payload
     */
    private final static long ITERATIONS = 10;

    /**
     * payload size
     */
    private final static int PAYLOADSIZE = 1024;

    
	@Override
	public void processMessage(Message msg) {
		if (msg instanceof MsgCommSendData)		
		{
			try {
				
				logger.info("Processing MsgCommSendData");

				long start = System.currentTimeMillis();

				JxtaSocket socket = null;

				try {
					socket = new JxtaSocket(manager.getNetPeerGroup(),
							// no specific peerid
							PeerID.create(new URI("urn:"+((MsgCommSendData)msg).address.replace("//", ""))),
							SocketServer.createSocketAdvertisement(),
							// connection timeout: 5 seconds
							5000,
							// reliable connection
							true);



				} catch (URISyntaxException e) {				
					logger.error("Exception: ", e);		
				}
				// get the socket output stream
				OutputStream out = socket.getOutputStream();
				DataOutput dos = new DataOutputStream(out);

				// get the socket input stream
				InputStream in = socket.getInputStream();
				DataInput dis = new DataInputStream(in);

				long total = ITERATIONS * (long) PAYLOADSIZE * 2;
				logger.info("Sending/Receiving " + total + " bytes.");

				dos.writeLong(ITERATIONS);
				dos.writeInt(PAYLOADSIZE);

				long current = 0;

				while (current < ITERATIONS) {
					byte[] out_buf = new byte[PAYLOADSIZE];
					byte[] in_buf = new byte[PAYLOADSIZE];

					Arrays.fill(out_buf, (byte) current);
					out.write(out_buf);
					out.flush();
					dis.readFully(in_buf);
					assert Arrays.equals(in_buf, out_buf);
					current++;
				}
				out.close();
				in.close();

				long finish = System.currentTimeMillis();
				long elapsed = finish - start;

				logger.info(MessageFormat.format("EOT. Processed {0} bytes in {1} ms. Throughput = {2} KB/sec.", total, elapsed,
						(total / elapsed) / 1024));
				socket.close();
				logger.info("Socket connection closed");

			} catch (IOException e) {				
				logger.error("Exception: ", e);				
			}			
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see net.jxta.discovery.DiscoveryListener#discoveryEvent(net.jxta.discovery.DiscoveryEvent)
	 */
	
	@Override
	public void discoveryEvent(DiscoveryEvent ev) {
        DiscoveryResponseMsg res = ev.getResponse();

        // let's get the responding peer's advertisement
        logger.info(" [  Got a Discovery Response [" + res.getResponseCount() + " elements]  from peer : " + ev.getSource() + "  ]");
        
        if (!knownPeers.contains(""+ev.getSource()))
        {
        	knownPeers.add(""+ev.getSource());            
            logger.info("known peers: " + knownPeers);      
            outQueue_.add(new MsgCommPeerFound(""+ev.getSource()));        	
        }
        

        Advertisement adv;
        Enumeration en = res.getAdvertisements();

        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                logger.info(adv);
            }
        }
        
        logger.info("known peers: " + knownPeers);
		
	}

}
