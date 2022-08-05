package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import java.util.concurrent.ScheduledFuture;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DeferredProducerUpTask implements Runnable {

  private final RecoveryContext context;
  private final TaskLifecycleTracker taskLifecycleTracker;
  private final Runnable deferredProducerUpTask;
  @Setter
  private ScheduledFuture<?> future;

  @Override
  public void run() {
    long pendingTasks = taskLifecycleTracker.pendingTasksFor(context);
    if (pendingTasks == 0) {
      log.info("Producer {} Request {} : All pending event tasks complete",
          context.getProducerID(),
          context.getRequestID());

      // invoke ProducerUp on the customer OddsFeedListener
      deferredProducerUpTask.run();
      // cancel the scheduling of this task
      cancel();
    } else {
      log.info("Producer {} Request {} : {} pending event tasks remaining",
          context.getProducerID(),
          context.getRequestID(),
          pendingTasks);
    }
  }

  public void cancel() {
    if (future == null) {
      log.error("Producer {} Request {} : Cannot kill ProducerUp task as no future set!",
          context.getProducerID(),
          context.getRequestID());
    } else {
      log.info("Producer {} Request {} : Cancelling scheduled DeferredProducerUp task",
          context.getProducerID(),
          context.getRequestID());
      future.cancel(false);
    }
  }
}
