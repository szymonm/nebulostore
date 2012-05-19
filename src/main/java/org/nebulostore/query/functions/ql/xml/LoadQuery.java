package org.nebulostore.query.functions.ql.xml;

import org.nebulostore.query.functions.exceptions.FunctionCallException;

public class LoadQuery {

  private final String[] queryParsed;
  private final String queryPath_;

  public LoadQuery(String queryPath) throws FunctionCallException {
    this.queryPath_ = queryPath;
    queryParsed = queryPath.split("/");

  }

  public String toXPath() {
    return queryPath_;
  }

}
