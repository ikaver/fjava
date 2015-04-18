package com.ikaver.aagarwal.fjava.stats;

import org.apache.logging.log4j.LogManager;

import com.custardsource.parfait.Monitorable;
import com.custardsource.parfait.MonitorableRegistry;
import com.ikaver.aagarwal.common.Definitions;

public class StatsTracker {
  
  private TaskRunnerStats [] taskRunnerStats;
  private DequeStats [] dequeStats;
  private int runNumber;
  
  private static StatsTracker instance;
  
  public static StatsTracker getInstance() {
    if(instance == null) {
      instance = new StatsTracker();
    }
    return instance;
  }
  
  public void setup(int poolSize) {
    ++runNumber;
    MonitorableRegistry.clearDefaultRegistry();
    
    this.taskRunnerStats = new TaskRunnerStats[poolSize];
    this.dequeStats = new DequeStats[poolSize];
    for(int i = 0; i < poolSize; ++i) {
      this.taskRunnerStats[i] = new TaskRunnerStats(i);
      this.dequeStats[i] = new DequeStats(i);
    }
  }
  
  public void printStats() {
    if(!Definitions.TRACK_STATS) return;
    LogManager.getLogger().warn("Stats for run #{}", this.runNumber);
    for(Monitorable<?> c : MonitorableRegistry.DEFAULT_REGISTRY.getMonitorables()) {
      LogManager.getLogger().warn("{} : {}", c.getName(), c.get());
    }
  }
  
  /* Task Runner Stats */
  public void onTaskCreated(int trIdx) {
    this.taskRunnerStats[trIdx].totalTasksCreated.inc();
  }
  
  public void onTaskCompleted(int trIdx) {
    this.taskRunnerStats[trIdx].totalTasksCompleted.inc();
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

}
