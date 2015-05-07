package com.ikaver.aagarwal.fjava;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.StealingAlgorithm;

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
    PaddedDouble[] dealtime = new PaddedDouble[size];

    for (int i = 0; i < size; ++i) {
      communicationCells[i] = new AtomicReference<FJavaTask>();
      dealtime[i] = new PaddedDouble(-1.0);
    }

    for (int i = 0; i < size; ++i) {
      deques[i] = new SenderInitiatedDeque(communicationCells, dealtime, i,
          size);
    }

    return deques;

  }

  private TaskRunnerDeque[] getReceiverInitiatedDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    IntRef[] status = new IntRef[size];
    PaddedAtomicInteger[] requestCells = new PaddedAtomicInteger[size];
    FJavaTaskRef[] responseCells = new FJavaTaskRef[size];

    for (int i = 0; i < size; ++i) {
      status[i] = new IntRef(ReceiverInitiatedDeque.INVALID_STATUS);
      requestCells[i] = new PaddedAtomicInteger(ReceiverInitiatedDeque.EMPTY_REQUEST);
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
