package com.ikaver.aagarwal.fjava.stats;

import java.util.Comparator;

public class CounterStat implements Comparable<CounterStat> {
    
  private final String counterID;
  private final String description;
  private long counter;
  
  public static final Comparator<CounterStat> ID_COMPARATOR = new Comparator<CounterStat>() {

    @Override
    public int compare(CounterStat o1, CounterStat o2) {
      return o1.compareTo(o2);
    }

  };
  
  public CounterStat(String counterID) {
    this(counterID, "");
  }
  
  public CounterStat(String counterID, String description) {
    this.counterID = counterID;
    this.description = description;
    this.counter = 0;
  }
  
  public long get() {
    return this.counter;
  }
  
  public void inc() {
    ++this.counter;
  }
  
  public void inc(long value) {
    this.counter += value;
  }
  
  public String getCounterID() {
    return this.counterID;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String toString() {
    return String.format("%s : %d", this.counterID, this.counter);
  }

  @Override
  public int compareTo(CounterStat o) {
    return this.getCounterID().compareTo(o.getCounterID());
  }

}
