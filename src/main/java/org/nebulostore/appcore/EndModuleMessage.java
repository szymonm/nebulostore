package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Command to end (terminate) module.
 * @author Grzegorz Milka
 */
public class EndModuleMessage extends Message {
  private static final long serialVersionUID = -1756129179954128771L;

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
