package org.nebulostore.crypto;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.Test;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.NebuloFile;

import static org.junit.Assert.assertTrue;

/**
 * CryptoUtils test class.
 */
public class CryptoUtilsTest {

  @Test
  public void testSerialization() throws CryptoException {
    ArrayList<String> list = new ArrayList<String>();
    list.add("One");
    list.add("Two");
    ArrayList<String> list2 =
      (ArrayList<String>) CryptoUtils.deserializeObject(CryptoUtils.serializeObject(list));
    assertTrue(list2.size() == 2);
    assertTrue(list2.get(0).equals("One"));
    assertTrue(list2.get(1).equals("Two"));
  }

  @Test
  public void testNebuloFileEncryption() throws CryptoException {
    NebuloFile file = new NebuloFile(new AppKey(new BigInteger("2222")),
        new ObjectId(new BigInteger("123")));
    Object object = CryptoUtils.decryptObject(CryptoUtils.encryptObject(file));
    assertTrue(object instanceof NebuloFile);
    NebuloFile file2 = (NebuloFile) object;
    assertTrue(file2.getAddress().getAppKey().getKey().equals(new BigInteger("2222")));
    assertTrue(file2.getAddress().getObjectId().getKey().equals(new BigInteger("123")));
  }
}
