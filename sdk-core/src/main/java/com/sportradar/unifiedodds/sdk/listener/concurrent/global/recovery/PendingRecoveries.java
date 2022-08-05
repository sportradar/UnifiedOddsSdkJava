package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import com.sportradar.unifiedodds.sdk.listener.concurrent.AtomicReadWrite;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class PendingRecoveries {

  private final Map<ProducerID, DeferredProducerUpTask> tasks;
  private final Map<ProducerID, RecoveryContext> contexts;
  private final AtomicReadWrite lock = new AtomicReadWrite();

  public void recoveryInitiated(RecoveryContext context) {
    ProducerID producerID = ProducerID.valueOf(context.getProducerID());

    lock.write(() -> {
      killExistingTaskfNecessary(context);
      contexts.put(producerID, context);
    });
  }

  public void save(RecoveryContext context, DeferredProducerUpTask task) {
    ProducerID producerID = ProducerID.valueOf(context.getProducerID());

    lock.write(() -> {
      killExistingTaskfNecessary(context);
      tasks.put(producerID, task);
    });
  }

  public RecoveryContext contextFor(ProducerID producerID) {
    final AtomicReference<RecoveryContext> ref = new AtomicReference<>();

    lock.read(() -> ref.set(contexts.getOrDefault(producerID, RecoveryContext.none())));
    return ref.get();
  }

  private void killExistingTaskfNecessary(RecoveryContext context) {
    ProducerID producerID = ProducerID.valueOf(context.getProducerID());

    if (tasks.containsKey(producerID)) {
      // if there is an existing DeferredProducerUpTask then this Producer is already
      // in recovery, so we need to kill the currently scheduled task
      killPendingTaskFor(producerID);
    }
  }

  private void killPendingTaskFor(ProducerID producerID) {
    RecoveryContext context = contexts.get(producerID);
    log.info(
        "Producer {} Request {} : Recovery initiated while recovery already pending, cancelling existing task",
        context.getProducerID(),
        context.getRequestID());

    DeferredProducerUpTask pendingTask = tasks.remove(producerID);
    pendingTask.cancel();
  }
}
