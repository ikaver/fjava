package com.ikaver.aagarwal.fjava.stats;

public class CounterStat {
    
  private final String counterID;
  private final String description;
  private long counter;
  
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

}
