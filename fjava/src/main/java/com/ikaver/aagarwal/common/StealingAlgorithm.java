package com.ikaver.aagarwal.common;

/**
 * Enum indicating which stealing algorithm to use.
 */
public enum StealingAlgorithm {
	RECEIVER_INITIATED("RECEIVER_INITIATED"),
	SENDER_INITIATED("SENDER_INITIATED");
	
	private final String algorithm;

	private StealingAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public String getValue() {
		return algorithm;
	}
}
