package org.nebulostore.communication.jxtach;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;

/**
 * @author marcin
 */
public class JXTAChPeer extends Module {

  public JXTAChPeer(BlockingQueue<Message> jxtaChPeerIn,
      BlockingQueue<Message> jxtaPeerOut) {
    super(jxtaChPeerIn, jxtaPeerOut);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void processMessage(Message msg) {
    // TODO Auto-generated method stub

  }

}
