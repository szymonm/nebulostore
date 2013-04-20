package org.nebulostore.systest;

import org.nebulostore.appcore.AbstractPeer;
import org.nebulostore.appcore.PeerConfiguration;

/**
 * Configuration for Testing Peer.
 * @author Bolek Kulbabinski
 */
public class TestingPeerConfiguration extends PeerConfiguration {
  @Override
  protected void configurePeer() {
    bind(AbstractPeer.class).to(TestingPeer.class);
  }
}
