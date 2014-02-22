package org.nebulostore.persistence;

import java.io.IOException;

/**
 * @author Bolek Kulbabinski
 */
public class InMemoryStoreTest extends KeyValueStoreTestTemplate {

  @Override
  protected KeyValueStore getKeyValueStore() throws IOException {
    return new InMemoryStore();
  }

}
