package org.nebulostore.appcore;

import java.util.LinkedList;

/**
 * @author bolek
 */
public class NebuloKey {

  public NebuloKey() {
    path_ = new LinkedList<PathSegment>();
  }

  public void addPathSegment(ObjectId dirId, EntryId entryId) {
    PathSegment segment = new PathSegment();
    segment.dirId_ = dirId;
    segment.entryId_ = entryId;
    path_.add(segment);
  }

  /**
   * Pair structure to store logical path segments.
   */
  public class PathSegment {
    public ObjectId dirId_;
    public EntryId entryId_;
  }

  public AppKey appKey_;
  public LinkedList<PathSegment> path_;
  public ObjectId objectId_;
}
