package org.nebulostore.persistence;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * @author Bolek Kulbabinski
 */
public class FileStoreTest extends KeyValueStoreTestTemplate {

  private static final String TEST_DIR = "temp/temp_test_dir" +  new SecureRandom().nextInt(100000);

  @Override
  protected KeyValueStore getKeyValueStore() throws IOException {
    return new FileStore(TEST_DIR);
  }

}
