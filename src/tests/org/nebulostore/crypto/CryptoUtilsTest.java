package tests.org.nebulostore.crypto;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.NebuloList;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;

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

  /*@Test
  public void testNebuloDirEncryption() throws CryptoException {
    byte[] byteTab = {31, 11};
    Map<EntryId, EncryptedEntity> entries = new TreeMap<EntryId, EncryptedEntity>();
    entries.put(new EntryId("entry_1"), new EncryptedEntity(byteTab));
    NebuloList dir = new NebuloList(entries);
    Object object = CryptoUtils.decryptNebuloObject(CryptoUtils.encryptNebuloObject(dir));
    assertTrue(object instanceof NebuloList);
    NebuloList dir2 = (NebuloList) object;
    byte[] resTab = dir2.getEntries().get(new EntryId("entry_1")).getEncryptedData();
    assertTrue(resTab.length == byteTab.length);
    assertTrue(resTab[0] == byteTab[0]);
    assertTrue(resTab[1] == byteTab[1]);
  }*/
}
