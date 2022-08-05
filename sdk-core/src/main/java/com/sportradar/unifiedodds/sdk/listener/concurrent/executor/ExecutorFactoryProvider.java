package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;

public class ExecutorFactoryProvider {

  private final ConcurrentOddsFeedListenerConfig config;
  private final ExecutorListener listener;

  public ExecutorFactoryProvider(ConcurrentOddsFeedListenerConfig config) {
    this(config, ExecutorListener.NOP);
  }

  public ExecutorFactoryProvider(ConcurrentOddsFeedListenerConfig config,
      ExecutorListener listener) {
    this.config = config;
    this.listener = listener;
  }

  public ExecutorFactory create() {
    QueueFactory queueFactory = new QueueFactory(config);
    WorkerExecutorServiceFactory workerExecutorFactory = new WorkerExecutorServiceFactory(
        queueFactory, listener);
    return new ExecutorFactory(workerExecutorFactory, listener);
  }
}
