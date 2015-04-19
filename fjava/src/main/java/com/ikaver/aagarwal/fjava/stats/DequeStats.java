package com.ikaver.aagarwal.fjava.stats;

import com.custardsource.parfait.MonitoredCounter;

public class DequeStats {

	public final MonitoredCounter totalSteals;
	public final MonitoredCounter successfulGetTask;
	public final MonitoredCounter dequeEmpty;
	public final MonitoredCounter dequeNotEmpty;
	public final MonitoredCounter dequeGetTask;
	public final MonitoredCounter dequeTaskDelegationSuccess;

	public final MonitoredCounter acquiredTime;

	public DequeStats(int idx) {
		this.totalSteals = new MonitoredCounter(StatsTracker.getStatisticName(
				"deque.steals", "", idx), "# of total steals");
		this.successfulGetTask = new MonitoredCounter(
				StatsTracker.getStatisticName("deque.get_task_success", "", idx),
				"# of total successful get task calls");
		this.dequeEmpty = new MonitoredCounter(StatsTracker.getStatisticName(
				"deque.was_empty", "", idx),
				"# of total times the deque was empty on getTask");
		this.dequeNotEmpty = new MonitoredCounter(StatsTracker.getStatisticName(
				"deque.not_empty", "", idx),
				"# of total times the deque was not empty on getTask");
		this.dequeGetTask = new MonitoredCounter(StatsTracker.getStatisticName(
				"deque.get_task", "", idx), "# of total times getTask is called");

		this.acquiredTime = new MonitoredCounter(StatsTracker.getStatisticName(
				"deque.time.acquire", "", idx), "Time spent on acquire");

		this.dequeTaskDelegationSuccess = new MonitoredCounter(
				"deque.task_delegation#" + idx,
				"# of total time task delegation to another worker succeeded");
	}

}
