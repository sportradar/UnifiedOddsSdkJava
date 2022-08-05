package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import java.util.List;

class TaskAllocator {

  private final List<TaskExecutor> executors;
  private final int totalExecutors;

  TaskAllocator(List<TaskExecutor> executors) {
    this.executors = executors;
    this.totalExecutors = executors.size();
  }

  public void allocate(EventTask task) {
    allocatedExecutor(task).submit(task);
  }

  public void allocate(TrackedTask task) {
    allocatedExecutor(task).submit(task);
  }

  private TaskExecutor allocatedExecutor(Task task) {
    int index = calculatePartition(task);
    return executors.get(index);
  }

  private int calculatePartition(Task task) {
    int hashCode = task.getTaskID().hashCode();
    return hashCode % totalExecutors;
  }
}
