package org.nebulostore.query.language.interpreter.datatypes.values;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarLexer;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser.lambda_function_return;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
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

  public LambdaValue(String lambdaCode, Iterable<DQLFunction> functions)
      throws InterpreterException {
    super(PrivateMy.getInstance());

    CharStream charStream = new ANTLRStringStream(lambdaCode);
    DQLGrammarLexer lexer = new DQLGrammarLexer(charStream);
    TokenStream tokenStream = new CommonTokenStream(lexer);
    DQLGrammarParser parser = new DQLGrammarParser(tokenStream);

    lambda_function_return lambdaFunctionReturn = null;

    try {
      lambdaFunctionReturn = parser.lambda_function();
    } catch (RecognitionException e) {
      throw new InterpreterException(e);
    }

    expression_ = (CommonTree) ((CommonTree) lambdaFunctionReturn.getTree())
        .getChild(1);
    paramsDecls_ = new LinkedList<String>();
    CommonTree paramsDeclsTree = ((CommonTree) lambdaFunctionReturn.getTree());
    for (int i = 0; i < paramsDeclsTree.getChildCount() - 2; i++) {
      paramsDecls_.add(paramsDeclsTree.getChild(i).toString());
    }
    environment_ = new HashMap<String, IDQLValue>();
    functions_ = (Collection<DQLFunction>) functions;
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
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLLambda);
  }
}