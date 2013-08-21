package org.nebulostore.broker;

/**
 * Contracts evaluator that counts only contract size.
 * @author szymon
 *
 */
public class OnlySizeContractsEvaluator extends ContractsEvaluatorWithDefault {
  /* Naive - only space sum */
  @Override
  public double evaluate(ContractsSet contracts, ContractFilter filter, ContractsSet added) {
    ContractsSet filtered = new ContractsSet();
    for (Contract c : contracts) {
      if (filter.filter(c)) {
        filtered.add(c);
      }
    }
    return (contracts.realSize() - filtered.realSize()) + added.realSize();
  }
}
