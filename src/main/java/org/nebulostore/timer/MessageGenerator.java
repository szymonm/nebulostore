package org.nebulostore.timer;

import org.nebulostore.appcore.Message;

/**
 * Generates a message.
 * @author szymonmatejczyk
 */
public interface MessageGenerator {
  Message generate();
}
