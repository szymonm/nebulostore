package org.nebulostore.query.functions;

import java.util.List;

import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLValue.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;

public class CallParametersConditions {

  public static class ConditionsBuilder {

    private final CallParametersConditions conditions_;

    protected ConditionsBuilder() {
      conditions_ = new CallParametersConditions();
    }

    public ConditionsBuilder parameter(int i, DQLType type) {
      conditions_.addParameterCondition(i, type);
      return this;
    }

    public CallParametersConditions build() {

      return conditions_;
    }

  }

  public void check(List<IDQLValue> params) throws FunctionCallException {

  }

  public void addParameterCondition(int i, DQLType type) {

  }

  public static ConditionsBuilder newBuilder() {
    return new ConditionsBuilder();
  }

}
