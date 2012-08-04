package org.nebulostore.networkmonitor;

import java.io.Serializable;

import org.nebulostore.communication.address.CommAddress;

/**
 * Class that holds data of one peers connections survey.
 * @author szymonmatejczyk
 *
 */
public class PeerConnectionSurvey implements Serializable {
  private static final long serialVersionUID = -5595489110765197257L;

  private final CommAddress investigator_;
  private final long time_;
  private final ConnectionAttribute attribute_;
  private final double value_;

  public PeerConnectionSurvey(CommAddress investigator, long time,
      ConnectionAttribute attribute, double value) {
    investigator_ = investigator;
    time_ = time;
    attribute_ = attribute;
    value_ = value;
  }

  public CommAddress getInvestigator() {
    return investigator_;
  }

  public long getTime() {
    return time_;
  }

  public ConnectionAttribute getAttribute() {
    return attribute_;
  }

  public double getValue() {
    return value_;
  }


}
