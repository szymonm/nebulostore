package org.nebulostore.communication.nat;

import java.net.InetAddress;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Interface to NAT-PMP functionality on the network.
 *
 * Each client is bound to only one gateway address during its lifetime. To
 * change gateway one must create new client.
 *
 * @author Grzegorz Milka
 */
public class NatPMPClient extends Module {
  /* TODO(grzegorzmilka) This class is only a frame for future iterations. */
  /* TODO(grzegorzmilka) This client apart from maintaining leases will also
   * send information about change in external address if any */
  public NatPMPClient(InetAddress gateway) {
    throw new UnsupportedOperationException();
  }

  /**
   * Synchronously queries gateway device for current external address.
   *
   * Returns null iff unsuccessful.
   *
   * @author Grzegorz Milka
   */
  public InetAddress getCurrentExternalAddress() {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets up a mapping lease on a device and updates it when nearing
   * expiration.
   *
   * Returns true if given mapping was successful.
   *
   * On unsuccessful mapping module will send a PMPMappingExceptionMessage.
   *
   * @author Grzegorz Milka
   */
  public boolean setUpMapping(int internalPortTCP, int externalPortTCP) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
  }
}
