package com.ikaver.aagarwal.common.utils;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.ikaver.aagarwal.fjava.EmptyFJavaTask;
import com.ikaver.aagarwal.fjava.FJavaTaskRef;


/**
 * Fetches the size of a java object.
 */
public class ObjectSizeFetcher {

	/**
	 * Returns the size i.e. the amount of real memory which it occupies.
	 */
	public static final long getSize(Object obj) {
		return RamUsageEstimator.sizeOf(obj);
	}

	public static void main(String args[]) {
		EmptyFJavaTask task = new EmptyFJavaTask();
		FJavaTaskRef ref = new FJavaTaskRef(task);
		System.out.println(RamUsageEstimator.shallowSizeOf(ref));
	}
}
