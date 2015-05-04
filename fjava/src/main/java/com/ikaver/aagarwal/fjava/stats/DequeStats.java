package com.ikaver.aagarwal.fjava.stats;


public class DequeStats {

	public final CounterStat totalSteals;
	public final CounterStat dequeEmpty;
	public final CounterStat dequeNotEmpty;
	public final CounterStat dequeGetTask;
	public final CounterStat dequeTaskDelegationSuccess;

	public final CounterStat acquiredTime;

	public DequeStats(int idx, CounterStatFactory factory) {
		this.totalSteals = factory.createCounter(StatsTracker.getStatisticName(
				"deque.steals", "", idx), "# of total steals");
		this.dequeEmpty = factory.createCounter(StatsTracker.getStatisticName(
				"deque.was_empty", "", idx),
				"# of total times the deque was empty on getTask");
		this.dequeNotEmpty = factory.createCounter(StatsTracker.getStatisticName(
				"deque.not_empty", "", idx),
				"# of total times the deque was not empty on getTask");
		this.dequeGetTask = factory.createCounter(StatsTracker.getStatisticName(
				"deque.get_task", "", idx), "# of total times getTask is called");

		this.acquiredTime = factory.createCounter(StatsTracker.getStatisticName(
				"deque.time.acquire", "", idx), "Time spent on acquire");

		this.dequeTaskDelegationSuccess = factory.createCounter(
				StatsTracker.getStatisticName("deque.task_delegation", "", idx),
				"# of total time task delegation to another worker succeeded");
	}

}
