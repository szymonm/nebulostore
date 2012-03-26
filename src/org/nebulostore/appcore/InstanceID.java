package org.nebulostore.appcore;

import java.io.Serializable;

import org.nebulostore.communication.address.CommAddress;

/**
 * Application instance ID.
 * @author szymonmatejczyk
 */
public class InstanceID implements Serializable {
  private static final long serialVersionUID = 77L;

  private final CommAddress address_;

  public InstanceID(CommAddress address) {
    address_ = address;
  }

  public CommAddress getAddress() {
    return address_;
  }
}
