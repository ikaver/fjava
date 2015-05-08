package com.ikaver.aagarwal.common.graph;

public class FJavaNode {
  
  public enum FJavaNodeState {
    COMPLETED,
    PERFORMING_SYNC,
    QUEUED,
    RUNNING,
    NONE
  }
  
  private final int nodeID;
  private final int parentNodeID;
  private int taskRunnerID;
  private String taskDescription;
  private FJavaNodeState state;
  private Object nodeObject;
  
  public FJavaNode(int nodeID, int parentNodeID) {
    this.taskRunnerID = -1;
    this.nodeID = nodeID;
    this.parentNodeID = parentNodeID;
    this.taskDescription = "";
    this.state = FJavaNodeState.NONE;
  }
  
  public int getNodeID() {
    return this.nodeID;
  }
  
  public int getParentNodeID() {
    return this.parentNodeID;
  }
  
  public int getTaskRunnerID() {
    return taskRunnerID;
  }

  public void setTaskRunnerID(int taskRunnerID) {
    this.taskRunnerID = taskRunnerID;
  }

  public String getTaskDescription() {
    return String.format("%s [%d]", taskDescription, taskRunnerID);
  }

  public void setTaskDescription(String taskDescription) {
    this.taskDescription = taskDescription;
  }

  public FJavaNodeState getState() {
    return state;
  }

  public void setState(FJavaNodeState state) {
    this.state = state;
  }
  
  public Object getNodeObject() {
    return this.nodeObject;
  }
  
  public void setNodeObject(Object nodeObject) {
    this.nodeObject = nodeObject;
  }
  
  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if(!(other instanceof FJavaNode)) return false;
    FJavaNode otherNode = (FJavaNode)other;
    return this.nodeID == otherNode.nodeID;
  }
  
  @Override
  public int hashCode() {
    return Integer.hashCode(this.nodeID);
  }
      
}