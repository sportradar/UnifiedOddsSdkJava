package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeferredProducerUpSchedulerFactory {

  private final SDKGlobalEventsListener globalEventsListener;
  private final ExecutorFactory factory;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public DeferredProducerUpOrchestrator create() {
    ScheduledExecutorService scheduledExecutor = factory.createProducerUpScheduler();
    DeferredProducerUpTaskFactory taskFactory = new DeferredProducerUpTaskFactory(
        globalEventsListener,
        taskLifecycleTracker);
    PendingRecoveries pendingRecoveries = createPendingRecoveries();
    return new DeferredProducerUpOrchestrator(scheduledExecutor, taskFactory, pendingRecoveries);
  }

  private PendingRecoveries createPendingRecoveries() {
    Map<ProducerID, DeferredProducerUpTask> tasks = new ConcurrentHashMap<>();
    Map<ProducerID, RecoveryContext> contexts = new ConcurrentHashMap<>();
    return new PendingRecoveries(tasks, contexts);
  }
}
