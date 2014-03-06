package org.nebulostore.broker;

import java.util.Set;

/**
 * Calculates response for contract offer and decides which offer to send.
 * @author szymon
 */
public interface ContractsSelectionAlgorithm {
  /** Response for contract offer. */
  class OfferResponse {
    public boolean responseAnswer_;
    public Set<Contract> contractsToBreak_;

    public OfferResponse(boolean responseAnswer, Set<Contract> contractsToBreak) {
      responseAnswer_ = responseAnswer;
      contractsToBreak_ = contractsToBreak;
    }
  }

  OfferResponse responseToOffer(Contract newContract, ContractsSet currentContracts);

  Contract chooseContractToOffer(Set<Contract> possibleContracts,
      ContractsSet currentContracts);
}
