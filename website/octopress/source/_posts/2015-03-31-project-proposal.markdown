---
layout: post
title: "Project Proposal"
date: 2015-03-31 17:01:13 -0400
comments: true
categories: [Key Value Store, CMU, 15618] 
---

**Title**: BLAZE: A high performance parallel key-value store

**Summary**:
We are going to implement a high performance, distributed key value store on the CMU latedays cluster that supports store-semantics and dynamic redistribution of keys using consistent hashing.

**Challenges**:
The challenges of this project are numerous. Some of them are mentioned below:

1. Building/debugging a high performance distributed system with multiple concurrent clients is never an easy task since:
   + communication between entities is asynchronous,
   + workload imbalance across nodes can adversely affect the overall system performance,
   + time taken to obtain useful performance results can take a few hours,
   + debugging is hard since the state is distributed across multiple nodes.

2. Assuring key-value pairs are evenly distributed across the nodes in the distributed system, according to key popularity. Most real world distributions of keys are highly skewed towards some keys, therefore assuring that all nodes in the system have a similar workload will be challenging. Ideally, the system should be able to rebalance and redistribute keys dynamically according to the current workload. We might use ideas such as consistent hashing to implement this.

3. To achieve high performance, we intend to take advantage of multiple cores on each node. In order to achieve maximum performance for multiple cores on a node, it is necessary that we lay out the data structure in cache efficient manner. In addition, we will need to work out a scheduling algorithm for dynamically distributing workload across the processors of a node.

4. Implement two baseline solutions for comparison; one that allows concurrent clients but implements a coarse lock on the key value store data structures. Another baseline solution that we'll implement will be a simple backend that uses MySQL database to store the key value pairs.
Generating a test environment for this platform.  We must simulate multiple concurrent clients according to different distributions like Zipf, uniform etc. This test environment should be able to talk Redis, Memcached and our key value store.

**Goals**:

Plan to achieve:

1. Implement a correct, distributed key value store that uses store-semantics deployed on the latedays cluster capable of handling several concurrent clients.
2. Implement at least one strategy for dynamic load balancing across nodes. This also involves dynamic key redistribution using ideas such as consistent hashing.
3. Implement at least one strategy for dynamic load balancing across processors of a single node. Optimize the system for a multiprocessor architecture.
4. Implement two baseline solutions for comparison; one that allows concurrent clients but implements a coarse lock on the key value store data structures. Another baseline solution that we'll implement will be a simple backend that uses MySQL database to store the key value pairs.
Generate a test suite that tests the performance of the key value store under heavy workloads, simulating different distributions such as uniform, and zipf.
5. Implement a friendly client API for the key value store in a programming language such as Java, C++ or Python.

Hope to achieve:

1. Implement cache-semantics for the distributed key value store. 
2. Provide a build script to easily deploy the key value store.
3. Implement different modes of execution, borrowed from Anderson's paper:
    + EREW mode (Exclusive Read Exclusive Write) minimizes costly inter-core communication. Good for write-only workloads.
    + CREW mode (Concurrent Read Exclusive Write) allows multiple cores to serve popular data. Much better for read-only workloads.
    + Outperform popular systems such as memcached and Redis.
4. Implement a monitoring tool that shows the business of each node of the cluster and the average latency per request through time.

**Demo description**:
We will show a live demo of the distributed key value store in late days, hopefully with a bunch of clients. If we have enough time to implement the monitoring tool, it will make the demo very cool. Else, we will simply run some scripts and report the average request latency through time.
We will show performance graphs across several distributions and workloads. Specifically, we will show the following graphs for our parallel solution, and the two baselines implementations:
Average queue length through time in the system.
Average/Nth Percentile of GET, PUT and DELETE request latency, over several distributions.
Throughput of GET, PUT and DELETE requests, over several distributions.
Also, we will include the following graphs to demonstrate the scalability of the system:
Average/Nth Percentile of GET, PUT and DELETE request latency with respect to the cluster size.
Throughput of GET, PUT and DELETE request latency with respect to the cluster size.

**Resources**:
1. Root access to latedays cluster: This is required to install and run systems like Redis, Memcache etc. In addition, we may need to install other libraries while working on our own distributed key-value store.

**Schedule**:

* Thursday, April 2nd: 
  +  Create project website.
  +  Finish project specification.
* Week of April 5th-April 11th:
  + Write test simulation program. Should be able to support the uniform and zipf distributions. Ideally, it should create several distributed clients and compile results from all of them. 
  + Write basic baseline solutions and compile basic simulation results for both of them:
  + Simple, coarse lock implementation on a single node.
  + Simple MySQL based backend.
  + Get permissions to install required software on latedays cluster.
  + Design the architecture of the distributed key value store.
  + Define potential problems we might have with concurrency in the system, race conditions, deadlocks.
  + Create deploy script to easily startup the system in latedays (make testing easier).
  + Go over research papers and blogs about key value stores.
* Week of April 12th, April 18th: Thursday, April 16th: Project checkpoint
  + Finish first working implementation of a distributed key value store. Should have implemented  at least one strategy for dynamic load balancing across nodes, even if the strategy is not optimal.
  + Compile basic test results for the current state of the system. 
  + Go over research papers and blogs about key value stores.
* Week of April 19th, April 26th:
  + Implement efficient distribution of work across the cores of a single node. Try to implement cache-friendly data structure.
  + Optimize strategy for dynamic load balancing across nodes.
* Week of April 27th, May 3rd:
  + Implement redistribution of key-values to avoid work imbalance across the nodes of the system.
  + Reevaluate design decisions, tune the work distribution across nodes, tune the work assignment within a machine.
* Week of May 4th, May 11th: Project presentation
  + Try to finish stretch goals.
  + Polish final write up.
  + Create final graphs for the presentation.
  + Setup for the demo.

