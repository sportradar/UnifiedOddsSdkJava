package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ExecutorFactory {

  private final WorkerExecutorServiceFactory workerExecutorFactory;
  private final ExecutorListener listener;

  // TODO here we can control if each ConcurrentOddsFeedListener instance
  // gets it's own pool of worker threads, or if they should share one global pool
  public ExecutorService createWorkerPool() {
    ExecutorService executorService = workerExecutorFactory.create();
    addShutdownHook(executorService);
    return executorService;
  }

  public ScheduledExecutorService createProducerUpScheduler() {
    ScheduledExecutorService scheduledExecutor = newSingleThreadScheduledExecutor(
        createProducerUpThreadFactory());
    addShutdownHook(scheduledExecutor);
    listener.onCreate(scheduledExecutor);
    return scheduledExecutor;
  }

  private ThreadFactory createProducerUpThreadFactory() {
    return r -> {
      Thread thread = new Thread(r, "DeferredProducerUp");
      listener.onCreate(thread);
      return thread;
    };
  }

  private void addShutdownHook(ExecutorService executor) {
    ExecutorServiceShutdownHook shutdownHook = new ExecutorServiceShutdownHook(executor);
    Runtime.getRuntime().addShutdownHook(shutdownHook);
  }
}
