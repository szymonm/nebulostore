package pl.edu.uw.mimuw.nebulostore.communication.jxta;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

// TODO MBW: Remove it once the timers functionality is integrated with system
public class PeriodicWorker implements Runnable {	
	
	/*
	 * TODO MBW: Configurable values of this ...
	 */
    static private long LIFETIME = 60 * 2 * 1000L;
    static private long EXPIRATION = 60 * 2 * 1000L;
    static private long WAITTIME = 3 * 1000L;	
	
	private DiscoveryService discovery;
	static private Logger logger = Logger.getLogger(PeriodicWorker.class);
	
	public PeriodicWorker(DiscoveryService discovery)
	{
		DOMConfigurator.configure("log4j.xml");
		
		this.discovery = discovery;
	}

	@Override
	public void run() {
		logger.info("Running!");
		
        discovery.getRemoteAdvertisements(// no specific peer (propagate)
                null, // Adv type
                DiscoveryService.ADV, // Attribute = any
                null, // Value = any
                null, // one advertisement response is all we are looking for
                1, // no query specific listener. we are using a global listener
                null);

		
        try {
        	while (true) {
        		logger.info("Publishing advertisement");       		
        		PipeAdvertisement pipeAdv = getPipeAdvertisement();

        		// publish the advertisement with a lifetime of 2 mintutes
        		logger.info("Publishing the following advertisement with lifetime :" + LIFETIME + " expiration :" + EXPIRATION);
        		logger.info(pipeAdv.toString());
        		discovery.publish(pipeAdv, LIFETIME, EXPIRATION);
        		discovery.remotePublish(pipeAdv, EXPIRATION);
        		try {
        			logger.info("Sleeping for :" + WAITTIME);        			
        			Thread.sleep(WAITTIME);
        		} catch (Exception e) {// ignored
        		}
        		
        		/* moving to receiving adv */
                try {
                    logger.info("Sleeping for :" + WAITTIME);
                    Thread.sleep(WAITTIME);
                } catch (Exception e) {
                    // ignored
                }
                logger.info("Sending a Discovery Message");
                // look for any peer
                discovery.getRemoteAdvertisements(
                        // no specific peer (propagate)
                        null,
                        // Adv type
                        DiscoveryService.ADV,
                        // Attribute = name
                        "Name",
                        // Value = the tutorial
                        "Nebulostore discovery",
                        // one advertisement response is all we are looking for
                        1,
                        // no query specific listener. we are using a global listener
                        null);
        		
        		
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * Creates a pipe advertisement
     *
     * @return a Pipe Advertisement
     */
    public static PipeAdvertisement getPipeAdvertisement() {
        PipeAdvertisement advertisement = (PipeAdvertisement)
                AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

        advertisement.setPipeID(IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID));
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Nebulostore discovery");
        return advertisement;
    }



}
