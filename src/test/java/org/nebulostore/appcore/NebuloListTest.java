package org.nebulostore.appcore;

import org.junit.Test;
import org.nebulostore.appcore.exceptions.ListMergeException;

import static org.junit.Assert.assertTrue;

/**
 * Simple unit test for NebuloList.
 */
public final class NebuloListTest {

  @Test
  public void testMergeOk() {
    NebuloElement a = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement b = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement c = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement d = new NebuloElement(new EncryptedObject(new byte[2]));

    // A - B (C removed)
    NebuloList one = new NebuloList();
    one.elements_.add(a);
    one.elements_.add(b);
    one.removedIds_.add(c.elementId_);

    // D - A - C
    NebuloList two = new NebuloList();
    two.elements_.add(d);
    two.elements_.add(a);
    two.elements_.add(c);

    try {
      one.mergeWith(two);
    } catch (ListMergeException e) {
      assertTrue(false);
    }
    assertTrue(one.elements_.size() == 3);
    assertTrue(one.elements_.get(0).equals(d));
    assertTrue(one.elements_.get(1).equals(a));
    assertTrue(one.elements_.get(2).equals(b));
  }

  @Test
  public void testMergeBad() {
    NebuloElement a = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement b = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement c = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement d = new NebuloElement(new EncryptedObject(new byte[2]));

    // A - B - C
    NebuloList one = new NebuloList();
    one.elements_.add(a);
    one.elements_.add(b);
    one.elements_.add(c);

    // D - C - A - B
    NebuloList two = new NebuloList();
    two.elements_.add(d);
    two.elements_.add(c);
    two.elements_.add(a);
    two.elements_.add(b);

    try {
      one.mergeWith(two);
      assertTrue(false);
    } catch (ListMergeException e) {
      assertTrue(true);
    }
  }

}
