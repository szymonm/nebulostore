package org.nebulostore.communication.gossip;

import java.util.concurrent.BlockingQueue;

import com.google.inject.name.Named;

import org.apache.commons.configuration.XMLConfiguration;

import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.modules.Module;
import org.nebulostore.communication.address.CommAddress;

/**
 * GossipService is responsible for exchanging peer information.
 * Bootstrap's CommAddress might be needed for first enquiry.
 *
 * @author Bolek Kulbabinski
 * @author Grzegorz Milka
 */
public abstract class GossipService extends Module {
  protected static final String CONFIG_PREFIX = "communication.";

  protected XMLConfiguration config_;
  protected CommAddress commAddress_;
  protected CommAddress bootstrapCommAddress_;

  public GossipService(
      XMLConfiguration config,
      @Named("GossipQueue") BlockingQueue<Message> inQueue,
      @Named("CommunicationPeerInQueue") BlockingQueue<Message> outQueue,
      @Named("LocalCommAddress") CommAddress commAddress,
      @Named("BootstrapCommAddress") CommAddress bootstrapCommAddress) {
    config_ = config;
    inQueue_ = inQueue;
    outQueue_ = outQueue;
    commAddress_ = commAddress;
    bootstrapCommAddress_ = bootstrapCommAddress;
  }
}
