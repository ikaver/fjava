package com.ikaver.aagarwal.fjava.stats;

import com.custardsource.parfait.MonitoredCounter;

public class DequeStats {

  public final MonitoredCounter totalSteals;
  public final MonitoredCounter successfulGetTask;
  public final MonitoredCounter dequeEmpty;
  public final MonitoredCounter dequeNotEmpty;
  public final MonitoredCounter dequeGetTask;

  public DequeStats(int idx) {
    this.totalSteals = new MonitoredCounter("deque.steals-"+idx, "# of total steals");
    this.successfulGetTask = new MonitoredCounter("deque.get-task-success-"+idx, "# of total successful get task calls");
    this.dequeEmpty = new MonitoredCounter("deque.was-empty-"+idx, "# of total times the deque was empty on getTask");
    this.dequeNotEmpty = new MonitoredCounter("deque.not-empty-"+idx, "# of total times the deque was not empty on getTask");
    this.dequeGetTask = new MonitoredCounter("deque.get-task-"+idx, "# of total times getTask is called");
  }
  
}
