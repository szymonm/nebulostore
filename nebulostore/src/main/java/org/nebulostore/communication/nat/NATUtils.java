package org.nebulostore.communication.nat;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/** Class containing basic utilities for nat traversal and port forwarding.
 * @author Grzegorz Milka
 */
public final class NATUtils {

  private NATUtils() {
    throw new UnsupportedOperationException();
  }

  /* return ip addresses of local NICs */
  public static Collection<String> getLocalAddresses() throws IOException {
    Collection<String> ipAddresses = new LinkedList<String>();
    Enumeration<NetworkInterface> intfList =
      NetworkInterface.getNetworkInterfaces();
    List<InterfaceAddress> addrList = null;
    NetworkInterface intf = null;
    while (intfList.hasMoreElements()) {
      intf = intfList.nextElement();
      addrList = intf.getInterfaceAddresses();
      for (InterfaceAddress intAddr : addrList) {
        ipAddresses.add(intAddr.getAddress().getHostAddress());
      }
    }
    return ipAddresses;
  }

  public static boolean mapPMP(int internalPortTCP, int externalPortTCP) {
    return false;
    /*
    try {
      InetAddress gateway = Gateway.getIP();
      NatPmpDevice pmpDevice = new NatPmpDevice(gateway);
      MapRequestMessage mapTCP = new MapRequestMessage(true, internalPortTCP,
          externalPortTCP, Integer.MAX_VALUE, null);
      pmpDevice.enqueueMessage(mapTCP);
      pmpDevice.waitUntilQueueEmpty();
      return mapTCP.getResultCode() == ResultCode.Success;
    } catch (NatPmpException e) {
      return false;
    }
    */
  }

  public static boolean mapUPNP(String internalHost, int internalPortTCP, int
      externalPortTCP) throws IOException {
    return false;
    /*
    return (new net.tomp2p.connection.NATUtils()).mapUPNP(internalHost, -1,
        internalPortTCP, -1, externalPortTCP);
    */
  }
}
