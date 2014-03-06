package org.nebulostore.appcore.messaging.visitors;

import java.util.concurrent.atomic.AtomicInteger;

import org.nebulostore.appcore.messaging.AMessage;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

import static org.junit.Assert.fail;

/**
 * A collection of simple visitors used in MessageVisitorTest. They are placed here to indicate that
 * nested visitor classes from different package than MessageVisitor should have at least protected
 * modifier.
 *
 * @author Bolek Kulbabinski
 */
public final class TestVisitors {
  public static final String EXCEPTION_MSG = "Exception from ThrowingVisitor.";

  /**
   * Handles only Message.
   */
  protected static class MVisitor extends MessageVisitor<Void> {
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
  protected static class SubclassedVisitor extends MVisitor {
    public SubclassedVisitor(AtomicInteger counter) {
      super(counter);
    }
  }

  /**
   * Throws exception in handler.
   */
  protected static class ThrowingVisitor extends MessageVisitor<Void> {
    public Void visit(Message msg) {
      throw new RuntimeException(EXCEPTION_MSG);
    }
  }

  /**
   * Handles AMessage and Message.
   */
  protected static class AVisitor extends MessageVisitor<Void> {
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
   * Handles nothing.
   */
  protected static class EmptyVisitor extends MessageVisitor<Void> {
  }



  public static MessageVisitor<Void> getMVisitor(AtomicInteger counter) {
    return new MVisitor(counter);
  }

  public static MessageVisitor<Void> getSubclassedVisitor(AtomicInteger counter) {
    return new SubclassedVisitor(counter);
  }

  public static MessageVisitor<Void> getThrowingVisitor() {
    return new ThrowingVisitor();
  }

  public static MessageVisitor<Void> getAVisitor(AtomicInteger counter) {
    return new AVisitor(counter);
  }

  public static MessageVisitor<Void> getEmptyVisitor() {
    return new EmptyVisitor();
  }

  private TestVisitors() { }
}
