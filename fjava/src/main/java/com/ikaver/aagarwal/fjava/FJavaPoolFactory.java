package com.ikaver.aagarwal.fjava;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.StealingAlgorithm;
import com.ikaver.aagarwal.fjava.deques.ConcurrentArrayDeque;
import com.ikaver.aagarwal.fjava.deques.LinkedListConcurrentDeque;
import com.ikaver.aagarwal.fjava.deques.ReceiverInitiatedDeque;
import com.ikaver.aagarwal.fjava.deques.SenderInitiatedDeque;
import com.ikaver.aagarwal.fjava.deques.SharedConcurrentQueue;
import com.ikaver.aagarwal.fjava.deques.TaskRunnerDeque;

public class FJavaPoolFactory {

  private static FJavaPoolFactory instance;

  public static FJavaPoolFactory getInstance() {
    if(instance == null) instance = new FJavaPoolFactory();
    return instance;
  }

  public FJavaPool createPool() {
    return createPool(FJavaConf.getPoolSize(), FJavaConf.getStealingAlgorithm());
  }

  public FJavaPool createPool(StealingAlgorithm algorithm) {
    return createPool(Runtime.getRuntime().availableProcessors(), algorithm);
  }

  public FJavaPool createPool(int size, StealingAlgorithm algorithm) {
    switch (algorithm) {
    case RECEIVER_INITIATED:
      return new FJavaPool(size, getReceiverInitiatedDeques(size));
    case SENDER_INITIATED:
      return new FJavaPool(size, getSenderInitiatedDeques(size)); 
    case CONCURRENT_ARRAY:
      return new FJavaPool(size, getConcurrentArrayDeques(size));
    case CONCURRENT_LIST:
      return new FJavaPool(size, getConcurrentListDeques(size));
    case SHARED_CONCURRENT_QUEUE:
    	return new FJavaPool(size, getSingleSharedConcurrentQueue(size));
    default:
      throw new IllegalArgumentException("enum is not yet supported");
    }
  }
  
  private TaskRunnerDeque[] getConcurrentArrayDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    ConcurrentArrayDeque [] concurrentDeques = new ConcurrentArrayDeque[size];
    for(int i = 0; i < size; ++i) {
      concurrentDeques[i] = new ConcurrentArrayDeque(concurrentDeques, i);
      deques[i] = concurrentDeques[i];
    }
    return deques;
  }
  
  private TaskRunnerDeque[] getConcurrentListDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    ConcurrentLinkedDeque<FJavaTask> [] concurrentDeques = new ConcurrentLinkedDeque[size];
    for(int i = 0; i < size; ++i) {
      concurrentDeques[i] = new ConcurrentLinkedDeque<FJavaTask>();
    }
    for(int i = 0; i < size; ++i) {
      deques[i] = new LinkedListConcurrentDeque(i, concurrentDeques);
    }
    return deques;
  }

  private TaskRunnerDeque[] getSenderInitiatedDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    AtomicReference<FJavaTask> communicationCells[] = new AtomicReference[size];

    for (int i = 0; i < size; ++i) {
      communicationCells[i] = new AtomicReference<FJavaTask>();
    }

    for (int i = 0; i < size; ++i) {
      deques[i] = new SenderInitiatedDeque(communicationCells,
      		i,
          size);
    }

    return deques;

  }

  private TaskRunnerDeque[] getReceiverInitiatedDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    int[] status = new int[16*size];
    int[] requestCells = new int[16*size];
    FJavaTask[] responseCells = new FJavaTask[size];

    for (int i = 0; i < size; ++i) {
      status[16*i] = ReceiverInitiatedDeque.INVALID_STATUS;
      requestCells[16*i] = ReceiverInitiatedDeque.EMPTY_REQUEST;
    }

    for (int i = 0; i < size; ++i) {
      deques[i] = new ReceiverInitiatedDeque(status, requestCells,
          responseCells, i);
    }

    return deques;
  }
  
  private TaskRunnerDeque[] getSingleSharedConcurrentQueue(int size) {
  	TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
  	SharedConcurrentQueue queue = new SharedConcurrentQueue();
  	
  	for (int i = 0; i < size; i++) {
  		deques[i] = queue;
  	}
  	return deques;
  }

}
