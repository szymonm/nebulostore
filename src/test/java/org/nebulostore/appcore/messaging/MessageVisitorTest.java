package org.nebulostore.appcore.messaging;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.nebulostore.appcore.exceptions.NebuloException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Bolek Kulbabinski
 */
public class MessageVisitorTest {
  private static final String EXCEPTION_MSG = "Exception from ThrowingVisitor.";

  /**
   * User-defined message type.
   */
  static class AMessage extends Message {
    private static final long serialVersionUID = -5071230337314289632L;
  }

  /**
   * Handles AMessage and Message.
   */
  static class AVisitor extends MessageVisitor<Void> {
    private final AtomicInteger counter_;
    public AVisitor(AtomicInteger counter) {
      counter_ = counter;
    }
    public Void visit(AMessage msg) {
      counter_.incrementAndGet();
      return null;
    }
    public Void visit(Message msg) {
      fail("Should not enter visit(Message) method in AVisitor.");
      return null;
    }
  }

  /**
   * Handles only Message.
   */
  static class MVisitor extends MessageVisitor<Void> {
    private final AtomicInteger counter_;
    public MVisitor(AtomicInteger counter) {
      counter_ = counter;
    }
    public Void visit(Message msg) {
      counter_.incrementAndGet();
      return null;
    }
  }

  /**
   * Inherits handlers from parent.
   */
  static class SubclassedVisitor extends MVisitor {
    public SubclassedVisitor(AtomicInteger counter) {
      super(counter);
    }
  }

  /**
   * Handles nothing.
   */
  static class EmptyVisitor extends MessageVisitor<Void> {
  }

  /**
   * Throws exception in handler.
   */
  static class ThrowingVisitor extends MessageVisitor<Void> {
    public Void visit(Message msg) {
      throw new RuntimeException(EXCEPTION_MSG);
    }
  }



  @Test
  public void testHandleSpecificMessageType() throws NebuloException {
    Message message = new AMessage();
    AtomicInteger counter = new AtomicInteger(0);
    MessageVisitor<Void> visitor = new AVisitor(counter);

    message.accept(visitor);

    assertEquals("Handler call counter should be equal to one.", 1, counter.get());
  }

  @Test
  public void testHandleAllMessages() throws NebuloException {
    Message message = new AMessage();
    AtomicInteger counter = new AtomicInteger(0);
    MessageVisitor<Void> visitor = new SubclassedVisitor(counter);

    message.accept(visitor);

    assertEquals("Handler call counter should be equal to one.", 1, counter.get());
  }

  @Test(expected = UnsupportedMessageException.class)
  public void testHandleUnsupportedMessageType() throws NebuloException {
    Message message = new AMessage();
    MessageVisitor<Void> visitor = new EmptyVisitor();

    // Should throw UnsupportedMessageException.
    message.accept(visitor);
  }

  @Test(expected = UnsupportedMessageException.class)
  public void testHandleNotMessageSubclass() throws NebuloException {
    MessageVisitor<Void> visitor = new MVisitor(null);

    // Should throw UnsupportedMessageException.
    visitor.visit(new Integer(123));
  }

  @Test
  public void testExceptionFromVisitMethod() throws NebuloException {
    Message message = new AMessage();
    MessageVisitor<Void> visitor = new ThrowingVisitor();

    try {
      message.accept(visitor);
      fail("ThrowingVisitor handler should throw exception.");
    } catch (NebuloException e) {
      assertNotNull("Exception should contain the real cause.", e.getCause());
      assertEquals("Cause message should be correct.", EXCEPTION_MSG, e.getCause().getMessage());
    }
  }
}
