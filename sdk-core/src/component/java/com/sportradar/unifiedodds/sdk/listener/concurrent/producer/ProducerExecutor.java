package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;

/**
 * Represents a Producer which invokes methods on both SDKGlobalEventsListener and OddsFeedListener
 * from it's own thread.
 */
@RequiredArgsConstructor
class ProducerExecutor {

  private final ProducerTaskFactory taskFactory;
  private final ExecutorService executor;

  public void initiateRecovery(RecoveryContext context) {
    Runnable initiateRecoverTask = taskFactory.recoveryInitiated(context);
    executor.submit(initiateRecoverTask);
  }

  public void producerUp(ProducerID producerID) {
    Runnable producerUpTask = taskFactory.producerUp(producerID);
    executor.submit(producerUpTask);
  }

  public void sendRandomEvents(RecoveryContext context, int totalEvents) {
    for (int i = 0; i < totalEvents; i++) {
      Runnable sportEventTask = taskFactory.randomEvent(context);
      executor.submit(sportEventTask);
    }
  }

  public void sendRandomEventsAndProducerUp(RecoveryContext context, int totalEvents,
      int producerUpIndex) {
    List<Runnable> runnables = new ArrayList<>();

    for (int i = 0; i < totalEvents; i++) {
      Runnable sportEventTask = taskFactory.randomEvent(context);
      runnables.add(sportEventTask);
    }

    if (producerUpIndex >= 0) {
      ProducerID producerID = ProducerID.valueOf(context.getProducerID());
      Runnable producerUpTask = taskFactory.producerUp(producerID);
      if (producerUpIndex >= totalEvents) {
        runnables.add(producerUpTask);
      } else {
        runnables.add(producerUpIndex, producerUpTask);
      }
    }

    runnables.forEach(executor::submit);
  }
}
