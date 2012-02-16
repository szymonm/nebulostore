package org.nebulostore.query.language.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class DQLInterpreter {

  private static Log log = LogFactory.getLog(DQLInterpreter.class);
  private final ExecutorContext context_;

  public DQLInterpreter(ExecutorContext context) {
    context_ = context;
  }

  public InterpreterState createEmptyState() {
    return new InterpreterState(context_);
  }

  public PreparedQuery prepareQuery(String query, String sender)
      throws InterpreterException {
    return new PreparedQuery(query, sender);
  }

  public InterpreterState runGather(PreparedQuery query, InterpreterState state)
      throws InterpreterException {
    // TODO: Query ID here
    log.info("called for query ");
    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getGatherTree())));

    try {
      walker.gather_statement();
    } catch (RecognitionException e) {
      throw new InterpreterException(e);
    } catch (Throwable t) {
      throw new InterpreterException(t);
    }
    state.modify(walker.getEnvironmentContents());
    return state;
  }

  public InterpreterState runForward(PreparedQuery query, InterpreterState state)
      throws InterpreterException {
    // TODO: Query ID here
    log.info("called for query ");

    log.info(state.toString());

    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getForwardTree())));
    try {
      walker.forward_statement();
    } catch (RecognitionException e) {
      throw new InterpreterException(e);
    } catch (Throwable t) {
      throw new InterpreterException(t);
    }
    state.modify(walker.getEnvironmentContents());
    return state;
    // Tutaj pewnie bierzemy jakąś funckję klasy, którą dostaliśmy w
    // konstruktorze, budujemy
    // message sieciowy i wysyłamy
    // return state;
  }

  public InterpreterState runReduce(PreparedQuery query, InterpreterState state)
      throws InterpreterException {
    // TODO: Query ID here
    log.info("called for query ");

    log.info(state.toString());

    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getReduceTree())));

    // TODO : move it to interpreterState once we have good FORWARD support
    Map<String, IDQLValue> dummyMap = new HashMap<String, IDQLValue>();
    dummyMap.put("DQL_RESULTS", state.getForwardResults());

    walker.setInEnvironment(dummyMap);

    try {
      IDQLValue returned = walker.reduce_statement();
      log.info("Returned: " + returned);
    } catch (RecognitionException e) {
      throw new InterpreterException(e);
    } catch (Throwable t) {
      throw new InterpreterException(t);
    }
    state.modify(walker.getEnvironmentContents());
    return state;
    // Tutaj wysyłamy do ziomka, który nam odpowiedział, kiedy tylko state się
    // updateuje, że wszystkie odpowiedzi doszły, lub zaliczyliśmy timeout
    // return state;
  }
}
