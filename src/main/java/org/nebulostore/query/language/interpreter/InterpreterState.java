package org.nebulostore.query.language.interpreter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.FunctionsLibrary;
import org.nebulostore.query.language.interpreter.antlr.TreeWalker;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datasources.InjectedDataSource;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PublicOthers;

public class InterpreterState {

  private final Map<String, IDQLValue> environment_;
  private final List<IDQLValue> fromForward_;
  private Iterable<DQLFunction> libraryFunctions_;
  private IDQLValue reduceReturn_;
  private ListValue peersToForward_;

  public InterpreterState(ExecutorContext context) {
    environment_ = new HashMap<String, IDQLValue>(32);
    fromForward_ = new LinkedList<IDQLValue>();
    libraryFunctions_ = FunctionsLibrary.getInstance(context).getFunctions();
  }

  public void modify(Map<String, IDQLValue> environmentContents) {
    environment_.putAll(environmentContents);
  }

  public Map<String, IDQLValue> getEnvironment() {
    return environment_;
  }

  public void addFromForward(IDQLValue value) {
    fromForward_.add(value);
  }

  public ListValue getForwardResults() {
    // TODO: Types proper support here

    DataSourcesSet set = new DataSourcesSet();
    set.add(InjectedDataSource.getInstance(InjectedDataSource.DQL_RESULTS));
    return new ListValue(fromForward_, new DQLPrimitiveType(
        DQLPrimitiveTypeEnum.DQLInteger), new PublicOthers(set));
  }

  @Override
  public String toString() {
    String repr = "InterpreterState: \n";
    for (String key : environment_.keySet()) {
      repr += "\t" + key + " : \t" + environment_.get(key) + "\n";
    }
    return repr;
  }

  public TreeWalker prepareEnvironment(TreeWalker treeWalker)
      throws InterpreterException {
    treeWalker.insertFunctions(libraryFunctions_);
    treeWalker.setInEnvironment(getEnvironment());
    return treeWalker;
  }

  public void mockFunction(DQLFunction mockFunction) {
    LinkedList<DQLFunction> newLibraryFunctions = new LinkedList<DQLFunction>();
    boolean shaded = false;
    for (DQLFunction function : libraryFunctions_) {
      if (function.getName().equals(mockFunction.getName())) {
        newLibraryFunctions.add(mockFunction);
        shaded = true;
      } else {
        newLibraryFunctions.add(function);
      }
    }
    if (!shaded) {
      newLibraryFunctions.add(mockFunction);
    }
    libraryFunctions_ = newLibraryFunctions;
  }

  public void setReduceResult(IDQLValue returned) {
    reduceReturn_ = returned;
  }

  public IDQLValue getReduceResult() {
    return reduceReturn_;
  }

  public ListValue getPeersToForward() {
    return peersToForward_;
  }

  public void setPeersToForward(ListValue peersToForward) {
    peersToForward_ = peersToForward;
  }
}
