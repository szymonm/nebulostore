package org.nebulostore.appcore;

import java.math.BigInteger;

import org.junit.Test;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.ListMergeException;

import static org.junit.Assert.assertTrue;

/**
 * Simple unit test for NebuloList.
 */
public final class NebuloListTest {
  private NebuloAddress addr_ = new NebuloAddress(new AppKey(BigInteger.ONE),
                                                  new ObjectId(BigInteger.ONE));

  @Test
  public void testMergeOk() {
    NebuloElement a = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement b = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement c = new NebuloElement(new EncryptedObject(new byte[2]));
    NebuloElement d = new NebuloElement(new EncryptedObject(new byte[2]));

    // A - B (C removed)
    NebuloList one = new NebuloList(addr_);
    one.elements_.add(a);
    one.elements_.add(b);
    one.removedIds_.add(c.elementId_);

    // D - A - C
    NebuloList two = new NebuloList(addr_);
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
    NebuloList one = new NebuloList(addr_);
    one.elements_.add(a);
    one.elements_.add(b);
    one.elements_.add(c);

    // D - C - A - B
    NebuloList two = new NebuloList(addr_);
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
