package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.ikaver.aagarwal.common.StealingAlgorithm;

public class FJavaPoolFactory {
  
  private static FJavaPoolFactory instance;
  
  public static FJavaPoolFactory getInstance() {
    if(instance == null) instance = new FJavaPoolFactory();
    return instance;
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
    default:
      throw new IllegalArgumentException("enum is not yet supported");
    }
  }

  private TaskRunnerDeque[] getSenderInitiatedDeques(int size) {
    TaskRunnerDeque[] deques = new TaskRunnerDeque[size];
    AtomicReference<FJavaTask> communicationCells[] = new AtomicReference[size];
    double[] dealtime = new double[size];

    for (int i = 0; i < size; ++i) {
      communicationCells[i] = new AtomicReference<FJavaTask>();
      dealtime[i] = -1.0;
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
    AtomicInteger[] requestCells = new AtomicInteger[size];
    FJavaTaskRef[] responseCells = new FJavaTaskRef[size];
    FJavaTask emptyTask = new EmptyFJavaTask(null);

    for (int i = 0; i < size; ++i) {
      status[i] = new IntRef(ReceiverInitiatedDeque.INVALID_STATUS);
      requestCells[i] = new AtomicInteger(ReceiverInitiatedDeque.EMPTY_REQUEST);
      responseCells[i] = new FJavaTaskRef(emptyTask);
    }

    for (int i = 0; i < size; ++i) {
      deques[i] = new ReceiverInitiatedDeque(status, requestCells,
          responseCells, i, emptyTask);
    }

    return deques;
  }
  
}
