package org.nebulostore.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.query.executor.DQLExecutor;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.filestorage.FileStorage;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.InterpreterState;
import org.nebulostore.query.language.interpreter.PreparedQuery;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datasources.InjectedDataSource;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PublicOthers;

public class LocalExecutor {

  private static Logger logger_ = Logger.getLogger(DQLExecutor.class);

  private final ExecutorContext executorContext_;
  private final DQLInterpreter interpreter_;
  private final Set<Integer> executedOnAppKeys_;

  private Set<Integer> availableAppKeys_;

  public LocalExecutor(String dataPath) {
    executorContext_ = new ExecutorContext(dataPath);
    interpreter_ = new DQLInterpreter(executorContext_);
    executedOnAppKeys_ = new HashSet<Integer>();
  }

  public IDQLValue executeQuery(String query, int maxDepth,
      Set<Integer> availableAppKeys) throws NebuloException {
    availableAppKeys_ = availableAppKeys;
    int startAppKey = ApiFacade.getAppKey().getKey().intValue();

    FileStorage.getInstance(executorContext_).setLocal();
    IDQLValue result = null;
    try {
      result = executeQueryOnAppKey(query, startAppKey, 0, maxDepth);
    } catch (InterpreterException e) {
      FileStorage.getInstance(executorContext_).setRemote();
      logger_.error(e);
      throw new NebuloException(e);
    }
    FileStorage.getInstance(executorContext_).setRemote();
    return result;
  }

  private IDQLValue executeQueryOnAppKey(String query, int appKey,
      int currentDepth, int maxDepth) throws InterpreterException {

    logger_.info("Executing locally query on appKey: " + appKey);

    if (!executedOnAppKeys_.contains(appKey)) {
      executedOnAppKeys_.add(appKey);
    } else {
      throw new InterpreterException("Already executed on this (" + appKey +
          ") client!");
    }

    if (!availableAppKeys_.contains(appKey)) {
      throw new InterpreterException("Unavailable appKey: " + appKey +
          " in set: " + availableAppKeys_);
    }

    executorContext_.setLocalAppKey(appKey);

    PreparedQuery preparedQuery = interpreter_.prepareQuery(query, maxDepth,
        currentDepth);

    InterpreterState state = interpreter_.createEmptyState();
    state = interpreter_.runGather(preparedQuery, state);

    if (currentDepth < maxDepth) {
      state = interpreter_.runForward(preparedQuery, state);
      ListValue peersToForward = state.getPeersToForward();

      for (IDQLValue appKeyDQLValue : peersToForward) {
        int subqueryAppKey = ((IntegerValue) appKeyDQLValue).getValue();
        try {
          IDQLValue result = executeQueryOnAppKey(query, subqueryAppKey,
              currentDepth + 1, maxDepth);
          DataSourcesSet dataSourcesSet = new DataSourcesSet();
          InjectedDataSource injected = InjectedDataSource
              .getInstance("DQL_RESULTS");
          dataSourcesSet.add(injected);
          result.setPrivacyLevel(new PublicOthers(dataSourcesSet));
          state.addFromForward(result);
        } catch (InterpreterException e) {
          logger_.error("Error in subquery from appKey: " + appKey +
              " subqueryAppKey: " + subqueryAppKey, e);
        }
      }
    }
    executorContext_.setLocalAppKey(appKey);
    state = interpreter_.runReduce(preparedQuery, state);

    IDQLValue result = state.getReduceResult();
    logger_.info("Executing locally query on appKey: " + appKey +
        " finished. Result: " + result);
    return result;

  }
}
