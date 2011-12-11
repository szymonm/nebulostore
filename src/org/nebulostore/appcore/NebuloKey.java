package org.nebulostore.appcore;

import java.util.LinkedList;

/**
 * @author bolek
 */
public class NebuloKey {

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
