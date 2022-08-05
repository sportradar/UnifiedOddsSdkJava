package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.DeferredProducerUpOrchestrator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.DeferredProducerUpSchedulerFactory;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ConcurrentSDKGlobalEventsListenerFactory {

  private final ExecutorFactory executorFactory;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public SDKGlobalEventsListener create(
      SDKGlobalEventsListener customerGlobalListener) {
    DeferredProducerUpOrchestrator producerUpScheduler = new DeferredProducerUpSchedulerFactory(
        customerGlobalListener, executorFactory, taskLifecycleTracker).create();
    return new ConcurrentSDKGlobalEventsListener(customerGlobalListener, producerUpScheduler);
  }
}
