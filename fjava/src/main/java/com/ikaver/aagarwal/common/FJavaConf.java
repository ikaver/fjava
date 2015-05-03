package com.ikaver.aagarwal.common;


/**
 * Configuration file for FJava framework.
 */
public class FJavaConf {

  private static final String COLLECT_STATS = "COLLECT_STATS";
  private static final String ALGORITHM = "ALGORITHM";
  private static final String POOL_SIZE = "POOL_SIZE";

  private static boolean trackStats;
  private static StealingAlgorithm algorithm;
  private static int poolSize;

  public static void initialize() {    
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
    else if("CONCURRENT".equals(algorithmString)){
      algorithm = StealingAlgorithm.CONCURRENT;
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
    System.out.printf("Using track stats = %s algorithm = %s pool size = %d\n",
        trackStats == true ? "true" : "false",
            algorithm,
            poolSize);
  }

  public static boolean shouldTrackStats() {
    return trackStats;
  }

  public static StealingAlgorithm getStealingAlgorithm() {
    return algorithm;
  }
  
  public static int getPoolSize() {
    return poolSize;
  }

}
