package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TaskExecutor {

  private final ExecutorService executor;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public void submit(EventTask task) {
    submitForExecution(task);
  }

  public void submit(TrackedTask task) {
    if (submitForExecution(task)) {
      taskLifecycleTracker.taskAllocated(task);
    }
  }

  private boolean submitForExecution(Task task) {
    try {
      executor.submit(task);
      return true;
    } catch (RejectedExecutionException e) {
      log.error("Error adding task '{}' to queue!", task);
      // FIXME what should we do in this scenario?
      return false;
    }
  }
}
