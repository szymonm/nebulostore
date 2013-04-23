package org.nebulostore.communication.gossip;

import java.util.concurrent.BlockingQueue;

import com.google.inject.assistedinject.Assisted;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Grzegorz Milka
 */
public interface GossipServiceFactory {
  GossipService newGossipService(
      @Assisted("GossipServiceInQueue") BlockingQueue<Message> inQueue,
      @Assisted("GossipServiceOutQueue") BlockingQueue<Message> outQueue,
      @Assisted("BootstrapCommAddress") CommAddress bootstrapCommAddress);
}
