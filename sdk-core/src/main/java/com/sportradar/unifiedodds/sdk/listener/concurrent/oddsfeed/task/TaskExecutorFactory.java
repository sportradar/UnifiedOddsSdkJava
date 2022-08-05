package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import java.util.concurrent.ExecutorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TaskExecutorFactory {

  private final ExecutorFactory executorFactory;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public TaskExecutor create() {
    ExecutorService executor = executorFactory.createWorkerPool();
    return new TaskExecutor(executor, taskLifecycleTracker);
  }
}
