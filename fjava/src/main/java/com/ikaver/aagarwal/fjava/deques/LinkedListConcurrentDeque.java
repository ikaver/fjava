package com.ikaver.aagarwal.fjava.deques;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
  * Implementation of concurrent deque. Each {@code TaskRunner} 
  * maintains an a local instance of this queue; this instance can
  * also access other instances belonging to other task runners, concurrently.
  * 
  * Note that this class was created for the sole purpose of benchmarking and
  * will NOT be shipped with the production API.
 */
public class LinkedListConcurrentDeque implements TaskRunnerDeque {

  private final int dequeID;
  private ConcurrentLinkedDeque<FJavaTask> [] deques;
  private ConcurrentLinkedDeque<FJavaTask> myDeque;
  private Random random;
  

  public LinkedListConcurrentDeque(int dequeID, ConcurrentLinkedDeque<FJavaTask> [] deques) {
    this.dequeID = dequeID;
    this.deques = deques;
    this.myDeque = this.deques[this.dequeID];
    this.random = new Random();
  }
  
  @Override
  public void addTask(FJavaTask task) {
    this.myDeque.addLast(task);
  }
  
  private static final int NUM_TRIES = 1024;

  @Override
  public FJavaTask getTask(FJavaTask parentTask) {
    FJavaTask task = this.myDeque.pollLast();
    if(task != null) {
      return task;
    }
    else {
      for(int i = 0; i < NUM_TRIES; ++i) {
        if(parentTask != null && parentTask.isDone()) break;
        int victimIdx = random.nextInt(this.deques.length);
        if(victimIdx == dequeID) continue;
        FJavaTask victimTask = this.deques[victimIdx].pollFirst();
        if(victimTask != null) {
        	if (FJavaConf.shouldTrackStats()) {
        		// A task was stolen! Increment the counter.
        		StatsTracker.getInstance().onDequeSteal(this.dequeID);
        	}
        	return victimTask;
        }
      }
    } 
    return null;
  }

  @Override
  public void setupWithPool(FJavaPool pool) {
    
  }

  @Override
  public void tryLoadBalance() {
    //NOOP
  }

}
