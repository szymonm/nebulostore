package org.nebulostore.query.grammar;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarLexer;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser;
import org.nebulostore.query.language.interpreter.antlr.DQLGrammarParser.query_return;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;

public class Test {

  public static void main(String[] args) {
    String test = " GATHER"
        + "  LET userFriends =  FILTER("
        + "        LAMBDA friendId : (friendId != DQL_SOURCE_ID) ,"
        + "        XPATH (\".//friend/@ref\", LOAD (\"friends.xml\" ) ) )"
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = XPATH(\".//age\", LOAD(\"user-info.xml\"))  IS PRIVATE_MY AS INTEGER"
        + " FORWARD"
        + " MAX DEPTH 2"
        + " TO"
        + "  userFriends"
        + " REDUCE"
        + "  CREATE_TUPLE("
        + "    (FOLDL(LAMBDA acc, tuple: (acc+GET(tuple, 0) * GET(tuple, 1)), 0, DQL_RESULTS) + peerAge)"
        + "                  / (FOLDL(LAMBDA acc, tuple : (acc+GET(tuple, 1)), 0, DQL_RESULTS)   +1)"
        + "    IS PUBLIC_MY AS INTEGER,"
        + "        FOLDL(LAMBDA acc,tuple : (acc+GET(tuple,1)), 0, DQL_RESULTS) + 1 IS PUBLIC_MY AS INTEGER"
        + "  )";

    System.out.println("Test started");

    CharStream charStream = new ANTLRStringStream(test);
    DQLGrammarLexer lexer = new DQLGrammarLexer(charStream);
    TokenStream tokenStream = new CommonTokenStream(lexer);
    DQLGrammarParser parser = new DQLGrammarParser(tokenStream);

    System.out.println("parser init ok.");

    query_return query = null;

    try {
      query = parser.query();
    } catch (RecognitionException e) {
      e.printStackTrace();
    }

    CommonTree tree = ((CommonTree) query.getTree());

    System.out.println(((CommonTree) query.getTree()).toStringTree());

    System.out.println("parser worked on input, going to TreeWalker...");
    TreeWalker walker = new TreeWalker(
        new CommonTreeNodeStream(query.getTree()));
    try {
      walker.query();
    } catch (RecognitionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("All ok.");
  }
}
