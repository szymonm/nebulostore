package org.nebulostore.communication.socket;

import java.util.concurrent.BlockingQueue;

import com.google.inject.assistedinject.Assisted;

import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.communication.address.CommAddressResolver;

/**
 * @author Grzegorz Milka
 */
public interface MessengerServiceFactory {
  MessengerService newMessengerService(
      @Assisted("MessengerServiceInQueue") BlockingQueue<Message> inQueue,
      @Assisted("MessengerServiceOutQueue") BlockingQueue<Message> outQueue,
      CommAddressResolver resolver);
}
