package org.nebulostore.addressing;

import java.io.Serializable;

/**
 * (AppKey, ObjectId) pair uniquely identifying object.
 */
public class NebuloAddress implements Serializable {
  private static final long serialVersionUID = 673007553164628096L;
  private final AppKey appKey_;
  private final ObjectId objectId_;

  public NebuloAddress(AppKey appKey, ObjectId objectId) {
    appKey_ = appKey;
    objectId_ = objectId;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  public AppKey getAppKey() {
    return appKey_;
  }
}
