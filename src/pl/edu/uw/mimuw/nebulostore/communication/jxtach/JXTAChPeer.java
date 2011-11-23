package pl.edu.uw.mimuw.nebulostore.communication.jxtach;

import java.util.concurrent.BlockingQueue;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.appcore.Module;

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
