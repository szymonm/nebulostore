package org.nebulostore.query.language.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.log4j.Logger;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datasources.InjectedDataSource;
import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PublicConditionalMy;
import org.nebulostore.query.privacy.level.PublicMy;

public class DQLInterpreter {

  private static Logger logger_ = Logger.getLogger(DQLInterpreter.class);

  private final ExecutorContext context_;

  public DQLInterpreter(ExecutorContext context) {
    context_ = context;
  }

  public InterpreterState createEmptyState() {
    return new InterpreterState(context_);
  }

  public PreparedQuery prepareQuery(String query, int maxDepth, int currentDepth)
      throws InterpreterException {
    return new PreparedQuery(query, maxDepth, currentDepth);
  }

  public InterpreterState runGather(PreparedQuery query, InterpreterState state)
      throws InterpreterException {
    // TODO: Query ID here
    logger_.info("runGather called for query ");

    // TODO: Maybe introduce statefulQuery?
    DataSourcesSet set = new DataSourcesSet();
    set.add(InjectedDataSource.getInstance(InjectedDataSource.LEAF_EXECUTION));
    state.getEnvironment().put(
        "LEAF_EXECUTION",
        new BooleanValue(query.isLeafQuery() ||
            (state.getForwardResults().size() == 0), new PublicMy(set)));

    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getGatherTree())));

    try {
      walker.gather_statement();
    } catch (RecognitionException e) {
      logger_.error("Error occured in walker: ", e);
      throw new InterpreterException(e);
    } catch (Throwable t) {
      logger_.error("Error occured in walker: ", t);
      throw new InterpreterException(t);
    }

    logger_.info("runGather finished. Updateing execution state.");
    state.modify(walker.getEnvironmentContents());
    return state;
  }

  public InterpreterState runForward(PreparedQuery query, InterpreterState state)
      throws InterpreterException {

    logger_.info("runForward called for query.");
    logger_.info(state.toString());

    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getForwardTree())));

    logger_.debug("Walker created.");
    try {
      IDQLValue forward_statement = walker.forward_statement();

      logger_.info("Walker executed on forward statement. ");

      logger_.debug("Checking the returned value.");

      if (forward_statement == null) {
        String errReason = "Null returned from walker: " + forward_statement;
        logger_.error(errReason);
        throw new InterpreterException(errReason);
      }

      if (!(forward_statement instanceof ListValue)) {
        throw new InterpreterException("Forward list corrupted. " +
            forward_statement.toString());
      }

      ListValue peersToForward = (ListValue) forward_statement;
      DQLComplexType type = (DQLComplexType) peersToForward.getType();
      if (type.getComplexTypeContents().size() != 1 ||
          !(type.getComplexTypeContents().get(0) instanceof DQLPrimitiveType) ||
          ((DQLPrimitiveType) type.getComplexTypeContents().get(0))
          .getTypeEnum() != DQLPrimitiveTypeEnum.DQLInteger) {
        String errReason = "Wrong type of value returned from forward stage: " +
            peersToForward.toString();
        logger_.error(errReason);
        throw new InterpreterException(errReason);
      }
      logger_.debug("Updateing state with peers to forward.");
      state.setPeersToForward(peersToForward);
    } catch (RecognitionException e) {
      logger_.error(e);
      throw new InterpreterException(e);
    } catch (Throwable t) {
      logger_.error(t);
      throw new InterpreterException(t);
    }
    state.modify(walker.getEnvironmentContents());
    return state;

  }

  public InterpreterState runReduce(PreparedQuery query, InterpreterState state)
      throws InterpreterException {
    // TODO: Query ID here
    logger_.info("called for query ");

    logger_.info(state.toString());

    TreeWalker walker = state.prepareEnvironment(new TreeWalker(
        new CommonTreeNodeStream(query.getReduceTree())));

    Map<String, IDQLValue> dummyMap = new HashMap<String, IDQLValue>();
    dummyMap.put("DQL_RESULTS", state.getForwardResults());

    walker.setInEnvironment(dummyMap);

    try {
      IDQLValue returned = walker.reduce_statement();
      logger_.info("Returned: " + returned);
      if (!returned.getPrivacyLevel().isMorePublicThan(
          new PublicConditionalMy())) {
        throw new InterpreterException(
            "Returned value must be public, but is: " +
                returned.getPrivacyLevel());
      }
      state.setReduceResult(returned);
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
