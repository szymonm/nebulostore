package tests.org.nebulostore;

import java.util.LinkedList;

import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.NebuloKey.PathSegment;
import org.nebulostore.appcore.ObjectId;

/**
 * Testing utilities.
 */
public final class TestUtils {
  /**
   * Creates NebuloKey. dirIds and entryIds have to be of the same size.
   */
  public static NebuloKey createNebuloKey(String appKey, String[] dirIds, String[] entryIds,
      String objectId) {
    if (dirIds.length != entryIds.length) {
      return null;
    }
    NebuloKey nebuloKey = new NebuloKey();
    nebuloKey.appKey_ = new AppKey(appKey);
    nebuloKey.path_ = new LinkedList<PathSegment>();
    for (int i = 0; i < dirIds.length; ++i) {
      nebuloKey.addPathSegment(new ObjectId(dirIds[i]), new EntryId(entryIds[i]));
    }
    nebuloKey.objectId_ = new ObjectId(objectId);
    return nebuloKey;
  }

  private TestUtils() { }
}
