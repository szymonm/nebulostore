package org.nebulostore.communication.address;

import java.io.Serializable;

import net.jxta.peer.PeerID;

/**
 * @author Marcin Walas
 */
public class CommAddress implements Serializable {
  private final PeerID peerId_;

  public CommAddress(PeerID peerId) {
    peerId_ = peerId;
  }

  public PeerID getPeerId() {
    return peerId_;
  }

  @Override
  public int hashCode() {
    return peerId_.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CommAddress) &&
        (peerId_.hashCode() == ((CommAddress) o).peerId_.hashCode());
  }

}
