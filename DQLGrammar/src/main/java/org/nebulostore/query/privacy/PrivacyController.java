package org.nebulostore.query.privacy;

import org.nebulostore.query.privacy.level.PrivateConditionalMy;
import org.nebulostore.query.privacy.level.PrivateConditionalOthers;
import org.nebulostore.query.privacy.level.PrivateMy;
import org.nebulostore.query.privacy.level.PublicMy;
import org.nebulostore.query.privacy.level.PublicOthers;

/**
 * @author Marcin Walas
 */
public class PrivacyController {

  private static PrivacyController instance_;

  public static PrivacyController getInstance() {
    if (instance_ == null) {
      instance_ = new PrivacyController();
    }
    return instance_;
  }

  private PrivacyController() {

  }

  public boolean morePublic(PrivacyLevel start, PrivacyLevel end) {
    if (start.equals(end))
      return true;
    if (end instanceof PublicMy)
      return true;
    if (start instanceof PrivateMy)
      return true;
    if (start instanceof PrivateConditionalOthers &&
        end instanceof PrivateConditionalMy)
      return true;
    if (start instanceof PrivateConditionalOthers &&
        end instanceof PublicOthers)
      return true;

    return false;
  }

  public boolean lessPublic(PrivacyLevel start, PrivacyLevel end) {
    if (morePublic(end, start))
      return true;
    return false;
  }
}
