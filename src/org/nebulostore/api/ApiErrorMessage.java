package org.nebulostore.api;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Class used to pass error messages from API job module to user API call.
 */
public class ApiErrorMessage extends ApiMessage {

  protected String message_;
  protected Exception innerException_;

  public ApiErrorMessage(String errorMessage) {
    super();
    message_ = errorMessage;
  }

  public ApiErrorMessage(String errorMessage, Exception innerException) {
    super();
    message_ = errorMessage;
    innerException_ = innerException;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public String getErrorMessage() {
    return message_;
  }

  public Exception getInnerException() {
    return innerException_;
  }
}
