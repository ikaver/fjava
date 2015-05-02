package com.ikaver.aagarwal.common.graph;

import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.ikaver.aagarwal.common.graph.FJavaNode.FJavaNodeState;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class FJavaGraph extends JFrame {
  
  private mxGraph graph;
  private Object graphRoot;
  

  private HashMap<Integer, FJavaNode> idToNode;
  private static final String COMPLETED_STYLE = "C";
  private static final String SYNC_STYLE = "S";
  private static final String QUEUED_STYLE = "Q";
  private static final String RUNNING_STYLE = "R";
  private static final String NONE_STYLE = "N";
  
  public FJavaGraph() {
    super("FJava Graph");

    this.graph = new mxGraph();
    this.graphRoot = graph.getDefaultParent();
    this.idToNode = new HashMap<Integer, FJavaNode>();
    
    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    getContentPane().add(graphComponent);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(800, 500);
    this.setVisible(true);
    this.createStyles();
  }

  public void addRoot(FJavaTaskInfo taskInfo) {
    graph.getModel().beginUpdate();
    try
    {
      FJavaNode node = getFJavaNodeFromTaskInfo(taskInfo, -1);
      this.idToNode.put(node.getNodeID(), node);
    }
    finally
    {
      graph.getModel().endUpdate();
    }
    
    graph.getView().validate();
    System.out.println("Added root " + taskInfo);
  }
  
  public void addChild(int parentID, FJavaTaskInfo newTaskInfo) {
    graph.getModel().beginUpdate();
    FJavaNode parentNode = this.idToNode.get(parentID);
    try
    {
      FJavaNode node = getFJavaNodeFromTaskInfo(newTaskInfo, parentID);
      graph.insertEdge(this.graphRoot, null, "", parentNode.getNodeObject(), node.getNodeObject());
    }
    finally
    {
      graph.getModel().endUpdate();
    }
    graph.getView().validate();
    System.out.println("Added child " + parentID + " " + newTaskInfo);

    new mxHierarchicalLayout(graph).execute(this.graphRoot);
    new mxParallelEdgeLayout(graph).execute(this.graphRoot);
  }
  
  public void onTaskQueued(int taskID) {
    transitionToState(taskID, FJavaNodeState.QUEUED);
  }
  
  public void onTaskRunning(int taskID) {
    transitionToState(taskID, FJavaNodeState.RUNNING);
  }
  
  public void onTaskCompleted(int taskID) { 
    transitionToState(taskID, FJavaNodeState.COMPLETED);
  }
  
  public void onTaskSync(int taskID) { 
    transitionToState(taskID, FJavaNodeState.PERFORMING_SYNC);
  }
  
  public void onTaskAssigned(int taskID, int taskRunnerID) {
    FJavaNode node = this.idToNode.get(taskID);
    node.setTaskRunnerID(taskRunnerID);
    mxCellState state = graph.getView().getState(node.getNodeObject());
    state.setLabel(node.getTaskDescription());
    this.graph.refresh();
  }
  
  
  private FJavaNode getFJavaNodeFromTaskInfo(FJavaTaskInfo taskInfo, int parentID) {
    FJavaNode node = new FJavaNode(taskInfo.taskID, parentID);
    node.setState(FJavaNode.FJavaNodeState.NONE);
    node.setTaskDescription(taskInfo.taskDescription);
    node.setTaskRunnerID(taskInfo.taskRunnerID);
    Object newVertex = graph.insertVertex(this.graphRoot, null, node.getTaskDescription(), 20, 20, 80,
        30, getStyleForNode(node));      
    node.setNodeObject(newVertex);
    this.idToNode.put(node.getNodeID(), node);
    return node;
  }
  
  public void transitionToState(int nodeID, FJavaNode.FJavaNodeState state) {
    FJavaNode node = this.idToNode.get(nodeID);
    node.setState(state);
    Object nodeObj = node.getNodeObject();
    this.graph.setCellStyle(stringForState(state), new Object[]{nodeObj}); //changes the color to red
    this.graph.refresh();
  }
  
  private String getStyleForNode(FJavaNode node) {
    return stringForState(node.getState()); 
  }
  
  private void createStyles() {
    mxStylesheet stylesheet = graph.getStylesheet();
    Hashtable<String, Object> style = new Hashtable<String, Object>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    style.put(mxConstants.STYLE_OPACITY, 50);
    style.put(mxConstants.STYLE_FILLCOLOR, "#AAAAAA");
    stylesheet.putCellStyle(NONE_STYLE, style);
    
    style = new Hashtable<String, Object>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    style.put(mxConstants.STYLE_OPACITY, 50);
    style.put(mxConstants.STYLE_FILLCOLOR, "#FF4400");
    stylesheet.putCellStyle(RUNNING_STYLE, style);
    
    style = new Hashtable<String, Object>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    style.put(mxConstants.STYLE_OPACITY, 50);
    style.put(mxConstants.STYLE_FILLCOLOR, "#ADD8E6");
    stylesheet.putCellStyle(QUEUED_STYLE, style);
    
    style = new Hashtable<String, Object>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    style.put(mxConstants.STYLE_OPACITY, 50);
    style.put(mxConstants.STYLE_FILLCOLOR, "#7FFF00");
    stylesheet.putCellStyle(COMPLETED_STYLE, style);
    
    style = new Hashtable<String, Object>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    style.put(mxConstants.STYLE_OPACITY, 50);
    style.put(mxConstants.STYLE_FILLCOLOR, "#FFFF00");
    stylesheet.putCellStyle(QUEUED_STYLE, style);
  }
  
  
  private String stringForState(FJavaNode.FJavaNodeState state) {
    if(state == FJavaNodeState.COMPLETED) {
      return COMPLETED_STYLE;
    }
    else if(state == FJavaNodeState.QUEUED) {
      return QUEUED_STYLE;
    }
    else if(state == FJavaNodeState.PERFORMING_SYNC) {
      return SYNC_STYLE;
    }
    else if(state == FJavaNodeState.RUNNING) {
      return RUNNING_STYLE;
    }
    return NONE_STYLE;
  }
    
  
  public static void main(String [] args) throws InterruptedException {
    FJavaGraph graph = new FJavaGraph();
    graph.addRoot(new FJavaTaskInfo(1, 0, "Root"));
    Thread.sleep(1000);
    graph.onTaskRunning(1);
    Thread.sleep(1000);
    graph.addChild(1, new FJavaTaskInfo(2, 1, "Child 1.1"));
    graph.onTaskQueued(2);
    Thread.sleep(1000);
    graph.addChild(1, new FJavaTaskInfo(3, 1, "Child 1.2"));
    graph.onTaskQueued(3);
    Thread.sleep(1000);
    graph.addChild(1, new FJavaTaskInfo(4, 1, "Child 1.3"));
    graph.onTaskQueued(4);
    Thread.sleep(1000);
    graph.onTaskSync(1);
    Thread.sleep(1000);
    graph.onTaskAssigned(2, 1);
    graph.onTaskRunning(2);
    Thread.sleep(1000);
    graph.addChild(2, new FJavaTaskInfo(5, 1, "Child 2.1"));
    graph.onTaskQueued(5);
    Thread.sleep(1000);
    graph.onTaskSync(2);
    Thread.sleep(1000);
    graph.onTaskAssigned(3, 2);
    graph.onTaskRunning(3);
    Thread.sleep(1000);
    graph.addChild(3, new FJavaTaskInfo(6, 3, "Child 3.1"));
    graph.onTaskQueued(6);
    Thread.sleep(1000);
    graph.addChild(3, new FJavaTaskInfo(7, 3, "Child 3.2"));
    graph.onTaskQueued(7);
    Thread.sleep(1000);
    graph.onTaskSync(3);
    Thread.sleep(1000);
    graph.onTaskRunning(6);
    Thread.sleep(1000);
    graph.onTaskCompleted(6);
    Thread.sleep(1000);
    graph.onTaskRunning(7);
    Thread.sleep(1000);
    graph.onTaskCompleted(7);
    Thread.sleep(1000);
    graph.onTaskRunning(3);
    Thread.sleep(1000);
    graph.onTaskCompleted(3);
  }

}
