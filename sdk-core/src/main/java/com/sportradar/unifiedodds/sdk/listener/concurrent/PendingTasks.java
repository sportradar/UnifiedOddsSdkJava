package com.sportradar.unifiedodds.sdk.listener.concurrent;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskID;
import java.util.concurrent.atomic.AtomicLong;


class PendingTasks {

  private final AtomicLong pendingTaskCount = new AtomicLong(0);

  public long taskAllocated(TaskID taskID) {
    return pendingTaskCount.incrementAndGet();
  }

  public long taskCompleted(TaskID taskID) {
    return pendingTaskCount.decrementAndGet();
  }

  public long getPendingTaskCount() {
    return pendingTaskCount.get();
  }
}
