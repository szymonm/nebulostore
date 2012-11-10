package org.nebulostore.query.functions;

import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class InlineExecutor {

  private static HashMap<ExecutorContext, InlineExecutor> instances_ = new HashMap<ExecutorContext, InlineExecutor>();

  public static InlineExecutor getInstance(ExecutorContext context) {
    if (!instances_.containsKey(context)) {
      instances_.put(context, new InlineExecutor(context));
    }
    return instances_.get(context);
  }

  private final ExecutorContext context_;
  private final FunctionsLibrary functionsLibrary_;

  private InlineExecutor(ExecutorContext context) {
    context_ = context;
    functionsLibrary_ = FunctionsLibrary.getInstance(context);
  }

  public IDQLValue execute(String name, List<IDQLValue> params)
      throws FunctionCallException, InterpreterException, RecognitionException {
    return functionsLibrary_.getFunction(name).call(params);
  }
}
