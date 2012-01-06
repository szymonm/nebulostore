package org.nebulostore.api;

/**
 * Class used to pass error messages from API job module to user API call.
 */
public class ApiErrorMessage extends ApiMessage {
  public ApiErrorMessage(String errorMessage) {
    super();
    message_ = errorMessage;
  }

  public String getErrorMessage() {
    return message_;
  }

  private String message_;
}
