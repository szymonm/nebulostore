package org.nebulostore.systest.newcommunication.functionaltest;

import java.io.IOException;
import java.rmi.RemoteException;

import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.newcommunication.CommunicationFacade;

/**
 * Implementation of {@link RemoteCommunicationFacadeAdapter}.
 *
 * @author Grzegorz Milka
 *
 */
public class RemoteCommunicationFacadeAdapterImpl implements RemoteCommunicationFacadeAdapter {
  private CommunicationFacade facade_;

  public RemoteCommunicationFacadeAdapterImpl(CommunicationFacade facade) {
    facade_ = facade;
  }

  @Override
  public void sendMessage(CommMessage message) throws RemoteException {
    facade_.sendMessage(message);
  }

  @Override
  public void startUp() throws IOException {
    facade_.startUp();
  }

  @Override
  public void shutDown() throws InterruptedException, RemoteException {
    facade_.shutDown();
  }

}
