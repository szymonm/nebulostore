package org.nebulostore.systest.performance;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.OneTimeUniformGossipService;
import org.nebulostore.systest.TestingPeer;

/**
 * Testing peer for performance tests. It uses OneTimeUniformGossip gossiping service to
 * quickly establish uniform replication contracts among peers. Apart from that, it behaves like
 * TestingPeer.
 *
 * @author Bolek Kulbabinski
 */
public class PerfTestingPeer extends TestingPeer {
  @Override
  protected Injector createInjector() {
    return Guice.createInjector(new PerfGuiceModule());
  }

  /**
   * @author Bolek Kulbabinski
   */
  protected class PerfGuiceModule extends PeerGuiceModule {
    @Override
    protected void configureCommunicationPeer() {
      // Empty override not to bind anything here.
    }

    @Provides
    protected GossipService provideGossipService() {
      return new OneTimeUniformGossipService(nTestParticipants_, 3);
    }
  }

  @Override
  protected void runBroker() {
    // No broker needed for server.
    if (!isTestServer_)
      super.runBroker();
  }
}
