package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class Concat extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(Concat.class);

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      // .parameter(1, new DQLComplexType(DQLComplexTypeEnum.DQLList,
      // contentTypes) -- TODO: Jakiś wildcard na typy powinien być
      .parametersNumber(2).build();

  public Concat(ExecutorContext context) {
    super("Concat", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {
    ListValue l = (ListValue) params.get(0);
    ListValue l2 = (ListValue) params.get(1);
    for (IDQLValue elt : l2) {
      l.add(elt);
    }
    return l;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }

}
