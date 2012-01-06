package org.nebulostore.api;

import org.nebulostore.appcore.Message;

/**
 * Base class for messages passed from API job module to waiting user API call.
 */
public abstract class ApiMessage extends Message {
  public ApiMessage() {
    // This messages are (currently) not passed via Dispatcher.
    super("unused_job_id");
  }
}
