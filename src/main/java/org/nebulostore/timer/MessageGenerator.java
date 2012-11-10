package org.nebulostore.timer;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;

/**
 * Class that generates a new instance of V.
 * @author szymonmatejczyk
 * @param <V> type of message to be generated.
 */
public class MessageGenerator<V extends Message> implements IMessageGenerator {
  private static Logger logger_ = Logger.getLogger(IMessageGenerator.class);

  Class<V> type_;

  public MessageGenerator(Class<V> type) {
    type_ = type;
  }


  @Override
  public Message generate() {
    try {
      return type_.newInstance();
    } catch (InstantiationException exception) {
      logger_.error(exception.toString());
    } catch (IllegalAccessException exception) {
      logger_.error(exception.toString());
    }
    return null;
  }

}
