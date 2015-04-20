package com.ikaver.aagarwal.common;


/**
 * Configuration file for FJava framework.
 */
public class FJavaConf {

	private static final String COLLECT_STATS = "COLLECT_STATS";
	private static final String ALGORITHM = "ALGORITHM";
	private static FJavaConf conf;
	
	private boolean trackStats;
	private StealingAlgorithm algorithm;
	
	
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
		algorithm = StealingAlgorithm.RECEIVER_INITIATED;

		String statsString = System.getenv(COLLECT_STATS);
		if (statsString != null) {
			trackStats = Boolean.valueOf(statsString);
		}
		
		String algorithmString = System.getenv(ALGORITHM);
		if (algorithmString != null) {
			algorithm = StealingAlgorithm.valueOf(algorithmString);
		}
	}

	public boolean shouldTrackStats() {
		return trackStats;
	}
	
	public StealingAlgorithm getStealingAlgorithm() {
		return algorithm;
	}
}
