package org.nebulostore.query.language.interpreter.datatypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PrivateMy;

public class LambdaValue extends DQLValue {
  private final List<String> paramsDecls_;
  private final CommonTree expression_;
  private final Map<String, IDQLValue> environment_;
  private final Collection<DQLFunction> functions_;

  public LambdaValue(Collection<String> paramsDecl, CommonTree expression,
      Map<String, IDQLValue> environment, Collection<DQLFunction> functions) {
    super(PrivateMy.getInstance());
    paramsDecls_ = new LinkedList<String>(paramsDecl);
    expression_ = expression;
    environment_ = environment;
    functions_ = functions;
  }

  public IDQLValue evaluate(List<IDQLValue> params)
      throws InterpreterException, RecognitionException {

    TreeWalker walker = new TreeWalker(new CommonTreeNodeStream(expression_));
    walker.setInEnvironment(environment_);
    walker.insertFunctions(functions_);

    if (params.size() != paramsDecls_.size()) {
      throw new InterpreterException(
          "Called with wrong number of arguments expected: " +
              paramsDecls_.size() + " got: " + params.size());
    }

    Map<String, IDQLValue> parameters = new HashMap<String, IDQLValue>();
    for (int i = 0; i < params.size(); i++) {
      parameters.put(paramsDecls_.get(i), params.get(i));
    }

    walker.setInEnvironment(parameters);

    return walker.expression();
  }

  @Override
  public Object toJava() {
    return this;
  }

  @Override
  public DQLType getType() {
    return DQLType.DQLLambda;
  }
}
