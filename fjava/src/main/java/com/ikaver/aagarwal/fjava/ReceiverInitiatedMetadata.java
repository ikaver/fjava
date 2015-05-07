package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.Contended;

@Contended
public class ReceiverInitiatedMetadata {
  
  /**
   * Indicates the status of the current deque.
   * status[i] == VALID_STATUS iff deque i has some work to offer to idle threads
   * else, status[i] = INVALID_STATUS
   */
  public volatile int status;
  /**
   * responseCells[j] holds the task that task runner j stole from other
   * task runner (specifically, where j put his id in requestCells array).
   */
  public volatile FJavaTask responseCell;
  /**
   * requestCells[i] = j iff task runner j is waiting for task runner i to 
   * give him work
   */
  public AtomicInteger requestCell;
  
  public ReceiverInitiatedMetadata(int status, FJavaTask responseCell, AtomicInteger requestCell) {
    this.status = status;
    this.responseCell = responseCell;
    this.requestCell = requestCell;
  }
    
}
