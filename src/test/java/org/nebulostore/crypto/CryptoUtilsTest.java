package org.nebulostore.crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nebulostore.appcore.model.EncryptedObject;
import org.nebulostore.appcore.model.NebuloFile;
import org.nebulostore.appcore.model.NebuloObjectUtils;

import static org.junit.Assert.assertTrue;

/**
 * CryptoUtils test class.
 * @author Bolek Kulbabinski
 */
public class CryptoUtilsTest {
  private static final String APP_KEY = "22";
  private static final String OBJECT_ID = "123";

  @Test
  public void testSerialization() throws CryptoException {
    List<String> list = new ArrayList<String>();
    list.add("One");
    list.add("Two");

    List<?> list2 =
        (ArrayList<?>) CryptoUtils.deserializeObject(CryptoUtils.serializeObject((ArrayList) list));

    assertTrue(list2.size() == 2);
    assertTrue(list2.get(0).equals("One"));
    assertTrue(list2.get(1).equals("Two"));
  }

  @Test
  public void testNebuloFileEncryption() throws CryptoException {
    NebuloFile file = NebuloObjectUtils.getNewNebuloFile(APP_KEY, OBJECT_ID);

    Object object = CryptoUtils.decryptObject(CryptoUtils.encryptObject(file));

    assertTrue(object instanceof NebuloFile);
    NebuloFile file2 = (NebuloFile) object;
    assertTrue(file2.getAddress().getAppKey().getKey().equals(new BigInteger(APP_KEY)));
    assertTrue(file2.getAddress().getObjectId().getKey().equals(new BigInteger(OBJECT_ID)));
  }

  @Test
  public void testShaIsDeterministic() {
    byte[] seq = {1, 2, 3};
    String res1 = CryptoUtils.sha(new EncryptedObject(seq));
    String res2 = CryptoUtils.sha(new EncryptedObject(seq));
    assertTrue(res1.equals(res2));
  }
}
