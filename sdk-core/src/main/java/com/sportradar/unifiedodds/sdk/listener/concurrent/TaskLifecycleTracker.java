package com.sportradar.unifiedodds.sdk.listener.concurrent;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TrackedTask;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskLifecycleTracker {

  private static final PendingTasks NO_PENDING_TASKS = new PendingTasks();
  private final Map<RecoveryContext, PendingTasks> tasks = new ConcurrentHashMap<>();
  private final AtomicReadWrite lock = new AtomicReadWrite();

  public void taskAllocated(TrackedTask task) {
    RecoveryContext context = task.getContext();

    lock.write(() -> {
      tasks.putIfAbsent(context, new PendingTasks());
      tasks.get(context).taskAllocated(task.getTaskID());
    });
  }

  public void taskStarted(TrackedTask task) {
  }

  public void taskCompleted(TrackedTask task) {
    RecoveryContext context = task.getContext();

    lock.write(() -> {
      PendingTasks pendingTasks = tasks.get(context);
      if (pendingTasks == null) {
        log.error("Request {} Task {} completed but was never started!", context.getRequestID(),
            task.getTaskID().getId());
      } else {
        pendingTasks.taskCompleted(task.getTaskID());
      }
    });
  }

  public long pendingTasksFor(RecoveryContext context) {
    final AtomicLong ref = new AtomicLong();

    lock.read(() -> {
      long pendingTasks = tasks.getOrDefault(context, NO_PENDING_TASKS).getPendingTaskCount();
      ref.set(pendingTasks);
    });
    return ref.get();
  }
}
