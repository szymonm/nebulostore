package org.nebulostore.broker;

/**
 * Implements method using only the most general one.
 * @author szymon
 */
public abstract class ContractsEvaluatorWithDefault implements ContractsEvaluator {

  @Override
  public double evaluate(ContractsSet contracts) {
    return evaluate(contracts, new ContractsEvaluator.None());
  }

  @Override
  public double evaluate(ContractsSet contracts, ContractFilter filter) {
    return evaluate(contracts, filter, new ContractsSet());
  }

  @Override
  public double evaluate(ContractsSet contracts, ContractsSet additional) {
    return evaluate(contracts, new ContractsEvaluator.None(), additional);
  }
}
