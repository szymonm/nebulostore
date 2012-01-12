package org.nebulostore.query.language.interpreter.datatypes;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class LambdaValue extends DQLValue {
  private final List<String> params_;
  private final CommonTree expression_;

  public LambdaValue(List<String> params, CommonTree expression) {
    params_ = new LinkedList<String>(params);
    expression_ = expression;
  }

  public IDQLValue evaluate(List<IDQLValue> params)
      throws InterpreterException, RecognitionException {
    TreeWalker walker = new TreeWalker(new CommonTreeNodeStream(expression_));
    // TODO: setting up walker environment, passing environment as a var
    return walker.expression();
  }

  @Override
  public Object toJava() {
    return this;
  }
}
