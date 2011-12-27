package org.nebulostore.replicator.tests;

import java.util.Collection;

import org.junit.Test;

import org.nebulostore.appcore.DirectoryEntry;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.replicator.Replicator;
import org.nebulostore.replicator.SaveException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 * @author szymonmatejczyk
 */

public class ReplicatorTest {
  private SimpleStringFile file1_ = new SimpleStringFile("Test string");
  private SimpleIntegerFile file2_ = new SimpleIntegerFile(273);

  private Replicator replicator_ = new Replicator(null, null);

  /**
   * Tries to store and restore different types of objects using Replicators storeObject and
   * getObject methods.
   * @throws SaveException
   */
  @Test
  public void testStoreGet() throws SaveException {
    replicator_.storeObject(new ObjectId("key1"), file1_);
    SimpleStringFile local = new SimpleStringFile("Test string");
    SimpleStringFile stored = (SimpleStringFile) replicator_.getObject(new ObjectId("key1"));
    assertEquals("String store-get failed.", local, stored);

    replicator_.storeObject(new ObjectId("key2"), file2_);
    SimpleIntegerFile localInteger = new SimpleIntegerFile(273);
    SimpleIntegerFile storedInteger =
        (SimpleIntegerFile) replicator_.getObject(new ObjectId("key2"));
    assertEquals("Integer store-get failed.", localInteger, storedInteger);

    Object obj3 = replicator_.getObject(new ObjectId("key3"));
    assertNull(obj3);
  }

  /**
   * Tries to create simple Directory, append to it 2 objects and retrieve the directory.
   * @throws SaveException
   */
  @Test
  public void testDirectoryOperations() throws SaveException {
    ObjectId dirKey1 = new ObjectId("dirKey1");
    replicator_.createEmptyDirectory(dirKey1);
    HardLink de1 = new HardLink("entry 1", new ObjectId("entry1key"),
                                           new CommAddress[1]);
    replicator_.appendToDirectory(dirKey1, de1);

    HardLink de2 = new HardLink("entry 2", new ObjectId("entry2key"),
        new CommAddress[1]);
    replicator_.appendToDirectory(dirKey1, de2);

    Collection<DirectoryEntry> col = replicator_.listDirectory(new ObjectId("dirKey1"));
    assertTrue(col.contains(de1));
    assertTrue(col.contains(de2));
    assertTrue(col.size() == 2);
  }
}
