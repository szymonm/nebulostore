package org.nebulostore.systest;

import com.google.inject.Singleton;
import org.nebulostore.peers.AbstractPeer;
import org.nebulostore.peers.PeerConfiguration;
import org.nebulostore.systest.readwrite.ReadWriteClientFactory;
import org.nebulostore.systest.readwrite.ReadWriteClientFactoryDefaultImpl;

/**
 * Configuration for Testing Peer.
 * @author Bolek Kulbabinski
 */
public class TestingPeerConfiguration extends PeerConfiguration {

  @Override
  protected void configurePeer() {
    bind(AbstractPeer.class).to(TestingPeer.class);
  }

  @Override
  protected void configureAdditional() {
    bind(ReadWriteClientFactory.class).to(ReadWriteClientFactoryDefaultImpl.class).in(
        Singleton.class);
  }


}
