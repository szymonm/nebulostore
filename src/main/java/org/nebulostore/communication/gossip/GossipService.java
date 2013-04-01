package org.nebulostore.communication.gossip;

import com.google.inject.Inject;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.address.CommAddress;

/**
 * GossipService is responsible for exchanging peer information. Bootstrap's CommAddress might be
 * needed for first enquiry.
 *
 * @author Bolek Kulbabinski
 */
public abstract class GossipService extends Module {
  protected static final String CONFIG_PREFIX = "communication.";

  protected XMLConfiguration config_;
  protected CommAddress commAddress_;
  protected CommAddress bootstrapCommAddress_;

  public GossipService() { }

  @Inject
  public void setDependencies(XMLConfiguration config, CommAddress commAddress) {
    config_ = config;
    commAddress_ = commAddress;
  }

  public void setBootstrapCommAddress(CommAddress bootstrapCommAddress) {
    bootstrapCommAddress_ = bootstrapCommAddress;
  }
}
