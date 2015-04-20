package com.ikaver.aagarwal.fjava.stats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.custardsource.parfait.Monitorable;
import com.custardsource.parfait.MonitorableRegistry;
import com.ikaver.aagarwal.common.FJavaConf;

public class StatsTracker {
  
  private TaskRunnerStats [] taskRunnerStats;
  private DequeStats [] dequeStats;
  private int runNumber;
  
  private Logger log;
  
  private static StatsTracker instance;
  
  public static StatsTracker getInstance() {
    if(instance == null) {
      instance = new StatsTracker();
    }
    return instance;
  }
  
  public void setup(int poolSize) {
    if(!FJavaConf.getInstance().shouldTrackStats())
      return;
    
    ++runNumber;
    MonitorableRegistry.clearDefaultRegistry();
    
    this.taskRunnerStats = new TaskRunnerStats[poolSize];
    this.dequeStats = new DequeStats[poolSize];
    for(int i = 0; i < poolSize; ++i) {
      this.taskRunnerStats[i] = new TaskRunnerStats(i);
      this.dequeStats[i] = new DequeStats(i);
    }
    this.log = LogManager.getLogger();
  }
  
  public void printStats() {
    if(!FJavaConf.getInstance().shouldTrackStats()) {
    	return;
    }
    log.warn("Stats for run #{}", this.runNumber);
    for(Monitorable<?> c : MonitorableRegistry.DEFAULT_REGISTRY.getMonitorables()) {
      log.warn("{} : {}", c.getName(), c.get());
    }
  }
  
  /* Task Runner Stats */
  public void onTaskCreated(int trIdx) {
    this.taskRunnerStats[trIdx].totalTasksCreated.inc();
  }
  
  public void onTaskCompleted(int trIdx) {
    this.taskRunnerStats[trIdx].totalTasksCompleted.inc();
  }
  
  public void onTaskAcquired(int trIdx, int numFailures) {
    this.taskRunnerStats[trIdx].totalTriesBeforeAcquireTask.inc(numFailures);
  }
  
  public void onGetTaskTime(int trIdx, long time) {
    this.taskRunnerStats[trIdx].getTaskTime.inc(time);
  }
  
  public void onRunTaskTime(int trIdx, long time) {
    this.taskRunnerStats[trIdx].runTaskTime.inc(time);
  }
  
  /* Deque stats */
  public void onSuccessfulGetTask(int dequeIdx) {
    this.dequeStats[dequeIdx].successfulGetTask.inc();
  }
  
  public void onDequeSteal(int dequeIdx) {
    this.dequeStats[dequeIdx].totalSteals.inc();
  }
  
  public void onDequeGetTask(int dequeIdx) {
    this.dequeStats[dequeIdx].dequeGetTask.inc();
  }
  
  public void onDequeNotEmpty(int dequeIdx) {
    this.dequeStats[dequeIdx].dequeNotEmpty.inc();
  }
  
  public void onDequeEmpty(int dequeIdx) {
    this.dequeStats[dequeIdx].dequeEmpty.inc();
  }
  
  public void onAcquireTime(int dequeIdx, long time) {
    this.dequeStats[dequeIdx].acquiredTime.inc(time);
  }
  
  public void onSuccessfulTaskDelegation(int dequeIdx) {
  	this.dequeStats[dequeIdx].dequeTaskDelegationSuccess.inc();
  }

  public static String getStatisticName(String id, String category, int number) {
    return String.format("%s#%s#%d", id, category, number);
  }
}
