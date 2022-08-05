package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.TestExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ProducerExecutorFactory {

  private final TestExecutorFactory executorFactory = new TestExecutorFactory();
  private final ProducerTaskFactory taskFactory;

  ProducerExecutor create(ProducerID producerID) {
    ExecutorService executor = executorFactory.createSingleThread(
        "Producer " + producerID.getId());

    return new ProducerExecutor(taskFactory, executor);
  }
}
