package org.nebulostore.systest;

import org.nebulostore.networkmonitor.NetworkMonitorImpl;
import org.nebulostore.systest.messages.ChangeTestMessageHandlerMessage;

/**
 * NetworkMonitor used for testing - handling additional messages.
 * @author szymonmatejczyk
 *
 */
public class NetworkMonitorForTesting extends NetworkMonitorImpl {

  public NetworkMonitorForTesting() {
    visitor_ = new NetworkMonitorForTestingVisitor();
  }

  /**
   * Visitor.
   */
  public class NetworkMonitorForTestingVisitor extends NetworkMonitorVisitor {

    public Void visit(ChangeTestMessageHandlerMessage message) {
      connectionTestMessageHandlerProvider_ = message.getProvider();
      return null;
    }

  }

}
