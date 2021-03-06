package org.nebulostore.appcore.modules;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

/**
 * Message sent from modules to parenting object signaling its failure.
 *
 * @author Grzegorz Milka
 */
public class ModuleFailMessage extends Message {
  private static final long serialVersionUID = 4483145276056236887L;
  public Exception exception_;
  public Module module_;

  public ModuleFailMessage(Module module, Exception exception) {
    module_ = module;
    exception_ = exception;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public Exception getException() {
    return exception_;
  }

  public Module getModule() {
    return module_;
  }

  @Override
  public String toString() {
    return "ModuleFailMessage for module: " + getModule() + ", caused by: " +
        getException();
  }
}
