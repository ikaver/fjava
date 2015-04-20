package com.ikaver.aagarwal.common;

import org.apache.logging.log4j.LogManager;


/**
 * Configuration file for FJava framework.
 */
public class FJavaConf {

  private static final String COLLECT_STATS = "COLLECT_STATS";
  private static final String ALGORITHM = "ALGORITHM";
  private static final String POOL_SIZE = "POOL_SIZE";

  private static FJavaConf conf;

  private boolean trackStats;
  private StealingAlgorithm algorithm;
  private int poolSize;


  public static FJavaConf getInstance() {
    if (conf == null) {
      conf = new FJavaConf();
    }

    return conf;
  }

  private FJavaConf() {
    configure();
  }

  private void configure() {

    String statsString = System.getenv(COLLECT_STATS);
    if (statsString != null) {
      trackStats = Boolean.valueOf(statsString);
    }
    else {
      trackStats = false;
    }

    String algorithmString = System.getenv(ALGORITHM);
    if ("SID".equals(algorithmString)) {
      algorithm = StealingAlgorithm.SENDER_INITIATED;
    }
    else {
      algorithm = StealingAlgorithm.RECEIVER_INITIATED;
    }

    String poolSizeString = System.getenv(POOL_SIZE);
    if(poolSizeString != null) {
      poolSize = Integer.valueOf(poolSizeString);
    }
    else {
      poolSize = Runtime.getRuntime().availableProcessors();
    }

    LogManager.getLogger().warn(
        "Using track stats = {}, " +
            "algorithm = {}, " +
            "pool size = {}", trackStats, algorithm, poolSize);
  }

  public boolean shouldTrackStats() {
    return trackStats;
  }

  public StealingAlgorithm getStealingAlgorithm() {
    return algorithm;
  }
  
  public int getPoolSize() {
    return poolSize;
  }

}
