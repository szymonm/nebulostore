package org.nebulostore.query;

import java.io.Serializable;

public class QueryDescription implements Serializable {

  private static final long serialVersionUID = -17157751924512620L;

  private final String query_;
  private final int maxDepth_;

  public QueryDescription(String query, int maxDepth) {
    query_ = query;
    maxDepth_ = maxDepth;
  }

  public String getQuery() {
    return query_;
  }

  public int getMaxDepth() {
    return maxDepth_;
  }

}
