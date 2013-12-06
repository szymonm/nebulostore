package org.nebulostore.newcommunication.routing;

import org.junit.Before;

/**
 *
 * @author Grzegorz Milka
 */
public class CachedOOSDispatcherTest extends AbstractOOSDispatcherTest {
  @Before
  @Override
  public void setUp() {
    dispatcher_ = new CachedOOSDispatcher();
    super.setUp();
  }

}
