package org.nebulostore.query.functions;

import java.util.Arrays;
import java.util.HashMap;

import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.dql.CreateTuple;
import org.nebulostore.query.functions.dql.Filter;
import org.nebulostore.query.functions.dql.FoldL;
import org.nebulostore.query.functions.dql.FoldR;
import org.nebulostore.query.functions.dql.Get;
import org.nebulostore.query.functions.ql.xml.Load;
import org.nebulostore.query.functions.ql.xml.XPath;

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

    functions_ = new DQLFunction[] { new Load(context_), new XPath(),
        new Filter(), new FoldL(), new FoldR(), new CreateTuple(), new Get() };
  }

  private final DQLFunction[] functions_;
  private final ExecutorContext context_;

  public Iterable<DQLFunction> getFunctions() {
    return Arrays.asList(functions_);
  }

}
