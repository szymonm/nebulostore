package tests.org.nebulostore.crypto;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.nebulostore.crypto.CryptoUtils;

import static org.junit.Assert.assertTrue;

/**
 * CryptoUtils test class.
 */
public class CryptoUtilsTest {

  @Test
  public void testSerialization() throws IOException, ClassNotFoundException {
    ArrayList<String> list = new ArrayList<String>();
    list.add("One");
    list.add("Two");
    ArrayList<String> list2 =
      (ArrayList<String>) CryptoUtils.deserializeObject(CryptoUtils.serializeObject(list));
    assertTrue(list2.size() == 2);
    assertTrue(list2.get(0).equals("One"));
    assertTrue(list2.get(1).equals("Two"));
  }
}
