package org.nebulostore.query.dql.ql.json.functions;

import java.util.List;

import org.nebulostore.query.dql.data.functions.DQLFunction;

import com.google.gson.JsonObject;

/**
 * TODO: Każda funkcja pewnie powinna być Singletonem.
 * 
 * @author Marcin Walas
 */
public class SelectFromJsonObject extends DQLFunction<JsonObject, JsonObject> {

  public SelectFromJsonObject(String name, List supportedChanges) {
    super(supportedChanges);
  }

  @Override
  public JsonObject apply(JsonObject arg0) {
    // TODO Auto-generated method stub
    return null;
  }

}
