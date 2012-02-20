package org.nebulostore.addressing;

/**
 * (AppKey, ObjectId) pair uniquely identifying object.
 */
public class NebuloAddress {
  private AppKey appKey_;
  private ObjectId objectId_;

  public NebuloAddress(AppKey appKey, ObjectId objectId) {
    appKey_ = appKey;
    objectId_ = objectId;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  public AppKey getGroupId() {
    return appKey_;
  }
}
