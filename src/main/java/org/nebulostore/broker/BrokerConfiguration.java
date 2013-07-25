package org.nebulostore.broker;


/**
 * Reads broker configuration from file and provides Broker Module with it.
 *
 * @author szymon
 */
public class BrokerConfiguration {
//
  //  private static final String CONFIG_PREFIX = "broker.";
//  private static Logger logger_ = Logger.getLogger(BrokerConfiguration.class);
//
//  private int defaultContractSizeKb_;
//  private int spaceContributedKb_;
//  private ContractsEvaluator contractsEvaluator_;
//  private ContractsSelectionAlgorithm contractsSelectionAlgorithm_;
//  private int replicationGroupUpdateTimeout_;
//  private long contractImprovementPeriodSec_;
//
//  private XMLConfiguration config_;
//
//  @Inject
//  public void setConfig(XMLConfiguration config) {
//    config_ = config;
//    defaultContractSizeKb_ = config_.getInt(CONFIG_PREFIX + "default-contract-size-kb", 1024);
//    spaceContributedKb_ = config_.getInt(CONFIG_PREFIX + "size-contributed-kb", 30720);
//    if (config_.getString(CONFIG_PREFIX + "contracts-evaluator", "default").equals("default")) {
//      contractsEvaluator_ = new OnlySizeContractsEvaluator();
//    }
//    if (config_.getString(CONFIG_PREFIX + "contracts-selection-algorithm", "greedy")
//        .equals("greedy")) {
//      contractsSelectionAlgorithm_ = new GreedyContractsSelection();
//    }
//    replicationGroupUpdateTimeout_ = config_.getInt(CONFIG_PREFIX +
//        "replication-group-update-timeout", 5);
//    contractImprovementPeriodSec_ = config_.getLong(CONFIG_PREFIX +
//        "contract-improvement-period-sec", 3L);
//  }
//
//  public void readFromFile(String configurationPath) {
//    XMLConfiguration config = null;
//    try {
//      config = new XMLConfiguration(configurationPath);
//      setConfig(config);
//    } catch (ConfigurationException cex) {
//      logger_.error("Configuration read error in: " + configurationPath);
//    }
//  }
//
//  public ContractsSelectionAlgorithm getContractsSelectionAlgorithm() {
//    return contractsSelectionAlgorithm_;
//  }
//
//  public int getDefaultContractSizeKb() {
//    return defaultContractSizeKb_;
//  }
//
//  public void setDefaultContractSizeKb(int defaultContractSizeKb) {
//    defaultContractSizeKb_ = defaultContractSizeKb;
//  }
//
//  public int getSpaceContributedKb() {
//    return spaceContributedKb_;
//  }
//
//  public void setSpaceContributedKb(int spaceContributedKb) {
//    spaceContributedKb_ = spaceContributedKb;
//  }
//
//  public ContractsEvaluator getContractsEvaluator() {
//    return contractsEvaluator_;
//  }
//
//  public void setContractsEvaluator(ContractsEvaluator contractsEvaluator) {
//    contractsEvaluator_ = contractsEvaluator;
//  }
//
//  public int getReplicationGroupUpdateTimeout() {
//    return replicationGroupUpdateTimeout_;
//  }
//
//  public long getContractImprovementPeriodSec() {
//    return contractImprovementPeriodSec_;
//  }
//
}
