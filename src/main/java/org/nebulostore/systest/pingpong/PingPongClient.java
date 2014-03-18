package org.nebulostore.systest.pingpong;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.UserCommMessage;
import org.nebulostore.crypto.CryptoUtils;

/**
 * ping-pong test implementation.
 * @author szymonmatejczyk, lukaszsiczek
 */
public final class PingPongClient extends ConductorClient {
  private static final long serialVersionUID = 8676871234510749533L;
  private static Logger logger_ = Logger.getLogger(PingPongClient.class);

  private CommAddress parentPongAddress_;
  private BigInteger magicNumber_;
  private final List<CommAddress> childrenPongAddress_;
  private Integer receivedPongs = 0;
  private int id_;

  public PingPongClient(String serverJobId, CommAddress serverAddress, int numPhases, 
		  List<CommAddress> childrenPongAddress, int id) {
    super(serverJobId, numPhases, serverAddress);
    childrenPongAddress_ = childrenPongAddress;
    id_ = id;
    if (id_ == 0) {
    	magicNumber_ = CryptoUtils.getRandomId();
    }
  }

  @Override
  protected void initVisitors() {
    visitors_ =  new TestingModuleVisitor[numPhases_ + 2];
    visitors_[0] = new EmptyInitializationVisitor();
    if (id_ == 0) {
        visitors_[1] = new VisitorSendPing();
        for (int i = 2; i <= 5; ++i) 
        	visitors_[i] = new EmptyVisitor();
        visitors_[6] = new VisitorReceivedPong();
    } else if (id_ >= 1 && id_ <= 2) {
    	visitors_[1] = new VisitorReceivedPing();
    	visitors_[2] = new VisitorSendPing();
        for (int i = 3; i <= 4; ++i) 
        	visitors_[i] = new EmptyVisitor();
    	visitors_[5] = new VisitorReceivedPong();
    	visitors_[6] = new VisitorSendPong();
    } else if (id_ >= 3 && id_ <= 6) {
	    visitors_[1] = new EmptyVisitor();
    	visitors_[2] = new VisitorReceivedPing();
    	visitors_[3] = new VisitorSendPing();
    	visitors_[4] = new VisitorReceivedPong();
    	visitors_[5] = new VisitorSendPong();
	    visitors_[6] = new EmptyVisitor();
    } else if (id_ >= 7 && id_ <= 14) {
        for (int i = 1; i <= 2; ++i) 
        	visitors_[i] = new EmptyVisitor();
    	visitors_[3] = new VisitorReceivedPing();
    	visitors_[4] = new VisitorSendPong();
        for (int i = 5; i <= 6; ++i) 
        	visitors_[i] = new EmptyVisitor();
    }
    visitors_[7] = new IgnoreNewPhaseVisitor();
  }

  /**
   * 
   * 
   * Phase 1 - send Ping to children.
   * @author lukaszsiczek
   */
  protected final class VisitorSendPing extends TestingModuleVisitor {

	@Override
	public Void visit(NewPhaseMessage message) {
		logger_.debug("ID("+id_+"); Phase("+phase_+")");
		for (CommAddress address : childrenPongAddress_) {
	        if (sendMessage(new UserCommMessage(jobId_, address, magicNumber_.add(BigInteger.ONE), phase_))) {
				logger_.debug("Send PingMessage to "+address.toString());
	        }
		}
		phaseFinished();
		return null;
	}
  }
  
  /**
   * Phase 2 - received Ping from parent.
   * @author lukaszsiczek
   */
  protected final class VisitorReceivedPing extends IgnoreNewPhaseVisitor {
    public Void visit(UserCommMessage message) {
		logger_.debug("ID("+id_+"); Phase("+phase_+")");
    	logger_.debug("Received PingMessage from parent.");
        magicNumber_ = (BigInteger) message.getContent();
        parentPongAddress_ = message.getSourceAddress();
        phaseFinished();
        return null;
    }
  }
  
  /**
   * Phase 3 - send Pong to parent.
   * @author lukaszsiczek
   */
  protected final class VisitorSendPong extends TestingModuleVisitor {

	@Override
	public Void visit(NewPhaseMessage message) {
		logger_.debug("ID("+id_+"); Phase("+phase_+")");
		logger_.debug("Send PongMessage to parent: "+parentPongAddress_.toString());
		sendMessage(new UserCommMessage(jobId_, parentPongAddress_, magicNumber_, phase_));
	    phaseFinished();
	    return null;
	}
	  
  }
  
  /**
   * Phase 4 - received Pong from children.
   * @author lukaszsiczek
   */
  protected final class VisitorReceivedPong extends IgnoreNewPhaseVisitor {
	    public Void visit(UserCommMessage message) {
			logger_.debug("ID("+id_+"); Phase("+phase_+")");
	        BigInteger received = (BigInteger) message.getContent();
	        logger_.debug("Received PongMessage from child: "+message.getSourceAddress().toString());
	        assertTrue(received.equals(magicNumber_.add(BigInteger.ONE)), "Correct number received.");
	        synchronized (receivedPongs) {
	        	receivedPongs++;
	        	if (receivedPongs == childrenPongAddress_.size()) {
	        		phaseFinished();
	        	}
	        }
	        return null;
	    }
  }
}
