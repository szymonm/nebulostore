package org.nebulostore.appcore.context;

import java.math.BigInteger;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.communication.address.CommAddress;

/**
 * Guice context with default values.
 * @author bolek
 */
public class DefaultTestContext extends NebuloContext {
  public DefaultTestContext() {
    super(new AppKey(new BigInteger("1")), CommAddress.newRandomCommAddress(),
        new XMLConfiguration());
  }
}
