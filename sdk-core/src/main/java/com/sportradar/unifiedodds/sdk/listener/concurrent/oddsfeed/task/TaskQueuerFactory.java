package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TaskQueuerFactory {

  private final ConcurrentOddsFeedListenerConfig config;
  private final ExecutorFactory executorFactory;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public TaskQueuer create(OddsFeedListener customerListener) {
    TaskAllocator taskAllocator = createTaskAllocator();
    TaskFactory taskFactory = createTaskFactory(customerListener);
    return new TaskQueuer(taskFactory, taskAllocator);
  }

  private TaskFactory createTaskFactory(OddsFeedListener customerListener) {
    EventTaskFactory eventTaskFactory = createEventTaskFactory(customerListener);
    TrackedTaskFactory trackedTaskFactory = createTrackedTaskFactory();
    return new TaskFactory(eventTaskFactory, trackedTaskFactory);
  }

  private EventTaskFactory createEventTaskFactory(OddsFeedListener customerListener) {
    return new EventTaskFactory(customerListener);
  }

  private TrackedTaskFactory createTrackedTaskFactory() {
    return new TrackedTaskFactory(taskLifecycleTracker);
  }

  private TaskAllocator createTaskAllocator() {
    TaskExecutorFactory taskExecutorFactory = new TaskExecutorFactory(executorFactory,
        taskLifecycleTracker);
    TaskAllocatorFactory factory = new TaskAllocatorFactory(config, taskExecutorFactory);
    return factory.create();
  }
}
