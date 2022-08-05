package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DeferredProducerUpTaskFactory {

  private final SDKGlobalEventsListener customerGlobalListener;
  private final TaskLifecycleTracker taskLifecycleTracker;

  DeferredProducerUpTask createOnProducerUp(RecoveryContext context, ProducerUp producerUp) {
    Runnable runnable = () -> {
      log.info("Producer {} Request {} : Invoking deferred onProducerUp({})",
          context.getProducerID(),
          context.getRequestID(),
          producerUp.getReason());
      customerGlobalListener.onProducerUp(producerUp);
    };
    return create(context, runnable);
  }

  DeferredProducerUpTask createOnProducerStatusChange(RecoveryContext context,
      ProducerStatus status) {
    Runnable runnable = () -> {
      log.info("Producer {} Request {} : Invoking deferred onProducerStatusChange({})",
          context.getProducerID(),
          context.getRequestID(),
          status.getProducerStatusReason());
      customerGlobalListener.onProducerStatusChange(status);
    };
    return create(context, runnable);
  }

  private DeferredProducerUpTask create(RecoveryContext context, Runnable runnable) {
    return new DeferredProducerUpTask(context, taskLifecycleTracker, runnable);
  }
}
