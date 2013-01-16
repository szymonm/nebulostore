package org.nebulostore.appcore.context;

import java.math.BigInteger;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.addressing.AppKey;

/**
 * Guice context with default values.
 * @author bolek
 */
public class DefaultTestContext extends NebuloContext {
  public DefaultTestContext() {
    super(new AppKey(new BigInteger("1")), new XMLConfiguration());
  }
}
