package org.nebulostore.broker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Contract selection algorithm that is a hill-climbing(greedy) algorithm.
 *
 * @author szymon
 */
public class GreedyContractsSelection implements ContractsSelectionAlgorithm {
  private static final String CONFIGURATION_PREFIX = "broker.";

  private static ContractsEvaluator contractsEvaluator_;
  private static long spaceContributedKb_;

  @Inject
  private void setDependencies(ContractsEvaluator contractsEvaluator,
      @Named(CONFIGURATION_PREFIX + "space-contributed-kb") long spaceContributedKb) {
    contractsEvaluator_ = contractsEvaluator;
    spaceContributedKb_ = spaceContributedKb;
  }

  /**
   * Calculates contractsEvaluator value of all contracts but contract.
   */
  private double withoutContractValue(Contract contract, ContractsSet contracts,
      ContractsEvaluator contractsEvaluator) {
    double value = contractsEvaluator.evaluate(contracts, new ContractsEvaluator.WithoutSet(
        new ContractsSet(contract)));
    return value;
  }

  /**
   * If there exist a contract that is strictly worse than candidate returns one of the worst. Else
   * returns candidate.
   */
  private Contract findWorstWithCandidate(ContractsSet contracts, Contract candidate,
      ContractsEvaluator contractsEvaluator) {
    Iterator<Contract> it = contracts.iterator();
    double allValue = contractsEvaluator.evaluate(contracts, new ContractsEvaluator.None());
    double worstValue = allValue - withoutContractValue(candidate, contracts, contractsEvaluator);
    Contract worstContract = candidate;
    while (it.hasNext()) {
      Contract contract = it.next();
      double marginalValue = allValue -
          withoutContractValue(contract, contracts, contractsEvaluator);
      if (marginalValue < worstValue) {
        worstValue = marginalValue;
        worstContract = contract;
      }
    }
    return worstContract;
  }

  /**
   * If accepting this contract and breaking previous to meet space constraints increases valuation,
   * returns true and a list of contracts that need to be broken. Else returns false and null.
   */
  @Override
  public OfferResponse responseToOffer(Contract newContract, ContractsSet currentContracts) {
    currentContracts.add(newContract);
    Set<Contract> toBreak = new HashSet<Contract>();
    while (currentContracts.realSize() > spaceContributedKb_) {
      Contract worst = findWorstWithCandidate(currentContracts, newContract, contractsEvaluator_);
      if (worst == newContract) {
        currentContracts.addAll(toBreak);
        return new OfferResponse(false, null);
      } else {
        toBreak.add(worst);
        currentContracts.remove(worst);
      }
    }

    return new OfferResponse(true, toBreak);
  }

  /**
   * Returns the contract that increases valuation the most.
   */
  @Override
  public Contract chooseContractToOffer(Set<Contract> possibleContracts,
      ContractsSet currentContracts) {
    Iterator<Contract> it = possibleContracts.iterator();
    double bestValue = Double.MIN_VALUE;
    Contract bestContract = null;

    while (it.hasNext()) {
      Contract contract = it.next();
      double value = contractsEvaluator_.evaluate(currentContracts, new ContractsSet(contract));
      if (value > bestValue) {
        bestValue = value;
        bestContract = contract;
      }
    }

    return bestContract;
  }

}
