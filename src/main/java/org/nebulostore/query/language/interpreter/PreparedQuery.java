package org.nebulostore.query.language.interpreter;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarLexer;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser.query_return;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class PreparedQuery {

  private final int maxDepth_;
  private final int currentDepth_;

  private final Tree gatherTree_;
  private final Tree forwardTree_;
  private final Tree reduceTree_;

  private static final int GATHER_TREE_INDEX = 0;
  private static final int FORWARD_TREE_INDEX = 1;
  private static final int REDUCE_TREE_INDEX = 2;

  public PreparedQuery(String query, int maxDepth,
      int currentDepth) throws InterpreterException {

    maxDepth_ = maxDepth;
    currentDepth_ = currentDepth;

    CharStream charStream = new ANTLRStringStream(query);
    DQLGrammarLexer lexer = new DQLGrammarLexer(charStream);
    TokenStream tokenStream = new CommonTokenStream(lexer);
    DQLGrammarParser parser = new DQLGrammarParser(tokenStream);

    query_return queryReturn = null;

    try {
      queryReturn = parser.query();
    } catch (RecognitionException e) {
      throw new InterpreterException(e);
    }

    gatherTree_ = ((CommonTree) queryReturn.getTree())
        .getChild(GATHER_TREE_INDEX);
    forwardTree_ = ((CommonTree) queryReturn.getTree())
        .getChild(FORWARD_TREE_INDEX);
    reduceTree_ = ((CommonTree) queryReturn.getTree())
        .getChild(REDUCE_TREE_INDEX);
  }

  public Tree getGatherTree() {
    return gatherTree_;
  }

  public Tree getForwardTree() {
    return forwardTree_;
  }

  public Tree getReduceTree() {
    return reduceTree_;
  }

  public int getMaxDepth() {
    return maxDepth_;
  }

  public int getCurrDepth() {
    return currentDepth_;
  }

  public boolean isLeafQuery() {
    return maxDepth_ <= currentDepth_;
  }
}
