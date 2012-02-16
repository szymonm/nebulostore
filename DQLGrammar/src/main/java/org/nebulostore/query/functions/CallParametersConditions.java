package org.nebulostore.query.functions;

import java.util.LinkedList;
import java.util.List;

import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.utils.Pair;

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

    public ConditionsBuilder parametersNumber(int i) {
      conditions_.addParametersNumber(i);
      return this;
    }
  }

  private final List<Pair<Integer, DQLType>> parameterTypeConditions_ = new LinkedList<Pair<Integer, DQLType>>();
  private final List<Integer> parametersNumber_ = new LinkedList<Integer>();

  public void check(List<IDQLValue> params) throws FunctionCallException {
    // TODO: Checking against this condidtions;
  }

  public void addParametersNumber(int i) {
    parametersNumber_.add(i);
  }

  public void addParameterCondition(int i, DQLType type) {
    parameterTypeConditions_.add(new Pair<Integer, DQLType>(i, type));
  }

  public static ConditionsBuilder newBuilder() {
    return new ConditionsBuilder();
  }

}
