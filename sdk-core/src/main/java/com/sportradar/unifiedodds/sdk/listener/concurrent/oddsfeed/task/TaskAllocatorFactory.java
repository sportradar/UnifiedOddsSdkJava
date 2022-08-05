package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TaskAllocatorFactory {

  private final ConcurrentOddsFeedListenerConfig config;
  private final TaskExecutorFactory taskExecutorFactory;

  public TaskAllocator create() {
    List<TaskExecutor> executors = new ArrayList<>();

    for (int i = 0; i < config.getNumberOfThreads(); i++) {
      TaskExecutor executor = taskExecutorFactory.create();
      executors.add(executor);
    }
    return new TaskAllocator(executors);
  }
}
