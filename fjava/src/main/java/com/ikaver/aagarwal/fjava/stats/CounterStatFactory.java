package com.ikaver.aagarwal.fjava.stats;

import java.util.ArrayList;
import java.util.List;

public class CounterStatFactory {

  private List<CounterStat> counters;
  
  public CounterStatFactory() {
    this.counters = new ArrayList<CounterStat>();
  }
  
  public CounterStat createCounter(String counterID, String description) {
    CounterStat counter = new CounterStat(counterID, description);
    this.counters.add(counter);
    return counter;
  }
  
  public List<CounterStat> getCounters() {
    this.counters.sort(CounterStat.ID_COMPARATOR);
    return this.counters;
  }
  
}
