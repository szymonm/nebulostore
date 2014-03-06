package org.nebulostore.persistence;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Bolek Kulbabinski
 */
public abstract class KeyValueStoreTestTemplate {

  protected abstract KeyValueStore getKeyValueStore() throws IOException;

  @Test
  public void shouldStoreObjects() throws StoreException, IOException {
    KeyValueStore store = getKeyValueStore();
    store.putString("one", "value 1");
    store.putString("two", "value 2");

    Assert.assertEquals("value 2", store.getString("two"));
    Assert.assertEquals("value 1", store.getString("one"));
  }

  @Test(expected = StoreException.class)
  public void shouldThrowOnNonExistentKey() throws StoreException, IOException {
    KeyValueStore store = getKeyValueStore();
    store.getString("bad key");
  }

  @Test
  public void shouldConvertBetweenStringAndBytes() throws StoreException, IOException {
    KeyValueStore store = getKeyValueStore();
    byte[] array = {'a', 'b', 'c'};
    String str = "abc";
    store.putString("one", str);
    store.putBytes("two", array);

    Assert.assertArrayEquals(array, store.getBytes("one"));
    Assert.assertEquals(str, store.getString("two"));
  }
}
