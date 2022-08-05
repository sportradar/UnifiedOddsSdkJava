package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * When using the concurrent listener, there may still be pending event tasks queued which need to
 * be completed before we can raise the ProducerUp event.
 * <p>
 * This class stores the recovery context and when onProducerUp()/onOnProducerStatusChange() we wait
 * until all tasks for that recovery context are processed. Only then do we propagate the
 * onProducerUp()/onOnProducerStatusChange() to the customer's OddsFeedListener.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DeferredProducerUpOrchestrator {

  protected static final int POLL_INTERVAL = 200;
  private final ScheduledExecutorService executorService;
  private final DeferredProducerUpTaskFactory taskFactory;
  private final PendingRecoveries pendingRecoveries;

  public void recoveryInitiated(RecoveryContext context) {
    pendingRecoveries.recoveryInitiated(context);
  }

  public void deferProducerUp(ProducerUp producerUp) {
    ProducerID producerID = ProducerID.valueOf(producerUp.getProducer().getId());
    RecoveryContext context = pendingRecoveries.contextFor(producerID);

    if (context.isValid()) {
      log.info("Producer {} Request {} : Deferring onProducerUp(reason={})",
          context.getProducerID(),
          context.getRequestID(),
          producerUp.getReason());

      DeferredProducerUpTask task = taskFactory.createOnProducerUp(context, producerUp);
      submit(context, task);
    } else {
      log.error(
          "Producer ProducerTaskFactory{} : Cannot defer onProducerUp({}) as no recovery initiated!",
          producerID.getId(), producerUp.getReason());
    }
  }

  public void deferProducerStatusChange(ProducerStatus status) {
    ProducerID producerID = ProducerID.valueOf(status.getProducer().getId());
    RecoveryContext context = pendingRecoveries.contextFor(producerID);

    if (context.isValid()) {
      log.info("Producer {} Request {} : Deferring onProducerStatusChange(status={})",
          context.getProducerID(),
          context.getRequestID(),
          status.getProducerStatusReason());

      DeferredProducerUpTask task = taskFactory.createOnProducerStatusChange(context,
          status);
      submit(context, task);
    } else {
      log.error("Producer {} : Cannot defer onProducerStatusChange({}) as no recovery initiated!",
          producerID.getId(), status.getProducerStatusReason());
    }
  }

  private void submit(RecoveryContext context, DeferredProducerUpTask task) {
    ScheduledFuture<?> future = executorService.scheduleWithFixedDelay(task,
        POLL_INTERVAL, POLL_INTERVAL, TimeUnit.MILLISECONDS);
    task.setFuture(future);
    pendingRecoveries.save(context, task);
  }
}
