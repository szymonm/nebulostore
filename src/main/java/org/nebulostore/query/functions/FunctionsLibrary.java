package org.nebulostore.query.functions;

import java.util.Arrays;
import java.util.HashMap;

import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.dql.Append;
import org.nebulostore.query.functions.dql.CreateList;
import org.nebulostore.query.functions.dql.CreateTuple;
import org.nebulostore.query.functions.dql.Filter;
import org.nebulostore.query.functions.dql.FoldL;
import org.nebulostore.query.functions.dql.FoldR;
import org.nebulostore.query.functions.dql.Get;
import org.nebulostore.query.functions.dql.If;
import org.nebulostore.query.functions.dql.Length;
import org.nebulostore.query.functions.dql.Sum;
import org.nebulostore.query.functions.ql.xml.Load;
import org.nebulostore.query.functions.ql.xml.LoadNoise;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class FunctionsLibrary {

  private static HashMap<ExecutorContext, FunctionsLibrary> instances_ = new HashMap<ExecutorContext, FunctionsLibrary>();

  public static FunctionsLibrary getInstance(ExecutorContext context) {
    if (!instances_.containsKey(context)) {
      instances_.put(context, new FunctionsLibrary(context));
    }
    return instances_.get(context);
  }

  private FunctionsLibrary(ExecutorContext context) {
    context_ = context;

    functions_ = new DQLFunction[] { new Load(context_),
        new LoadNoise(context_), new Filter(context_), new Sum(context_),
        new FoldL(context_), new FoldR(context_), new CreateTuple(context_),
        new CreateList(context_), new Get(context_), new If(context_),
        new Append(context_), new Length(context_) };
  }

  private final DQLFunction[] functions_;
  private final ExecutorContext context_;

  public Iterable<DQLFunction> getFunctions() {
    return Arrays.asList(functions_);
  }

  public DQLFunction getFunction(String name) throws InterpreterException {
    for (DQLFunction fun : functions_) {
      if (fun.getName().toLowerCase().equals(name.toLowerCase())) {
        return fun;
      }
    }
    throw new InterpreterException("Unrecognized function " + name);
  }
}
