package org.nebulostore.broker;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Set of contracts.
 */
public class ContractsSet extends HashSet<Contract> {
  private static final long serialVersionUID = 112351L;

  public ContractsSet() {
    super();
  }

  /**
   * Singleton constructor.
   */
  public ContractsSet(Contract contract) {
    super();
    add(contract);
  }

  public int realSize() {
    int size = 0;
    Iterator<Contract> it = iterator();
    while (it.hasNext()) {
      size += it.next().getSize();
    }
    return size;
  }

  @Override
  public ContractsSet clone() {
    return (ContractsSet) super.clone();
  }

}
