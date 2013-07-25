package org.nebulostore.networkmonitor;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.utils.Filter;

/**
 * Returns average connectionAttribute retrived from DHT for a given peer.
 * @author szymonmatejczyk
 */
public class GetPeersConnectionAverageAttributeValueModule
  extends GetPeersConnectionAverageStatisticModule {
  public GetPeersConnectionAverageAttributeValueModule(CommAddress peer,
      final ConnectionAttribute connectionAttribute) {
    super(peer, new Filter<PeerConnectionSurvey>() {
      @Override
      public boolean filter(PeerConnectionSurvey pcs) {
        return pcs.getAttribute() == connectionAttribute;
      }
    });
  }
}
