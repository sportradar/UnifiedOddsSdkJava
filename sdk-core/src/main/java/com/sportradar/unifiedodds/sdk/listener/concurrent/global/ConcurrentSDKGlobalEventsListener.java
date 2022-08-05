package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.DeferredProducerUpOrchestrator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConcurrentSDKGlobalEventsListener extends DelegatingSDKGlobalEventsListener {

  private final DeferredProducerUpOrchestrator producerUpScheduler;

  ConcurrentSDKGlobalEventsListener(SDKGlobalEventsListener listener,
      DeferredProducerUpOrchestrator producerUpScheduler) {
    super(listener);
    this.producerUpScheduler = producerUpScheduler;
  }

  @Override
  public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
    int producerID = recoveryInitiated.getProducer().getId();
    long requestID = recoveryInitiated.getRequestId();
    log.info("Producer {} Request {} : onRecoveryInitiated()", producerID, requestID);

    RecoveryContext context = RecoveryContext.valueOf(producerID, requestID);
    producerUpScheduler.recoveryInitiated(context);
    super.onRecoveryInitiated(recoveryInitiated);
  }

  @Override
  public void onProducerUp(ProducerUp producerUp) {
    int producerID = producerUp.getProducer().getId();
    String reason = producerUp.getReason().name();
    log.info("Producer {} : onProducerUp(reason={})", producerID, reason);

    producerUpScheduler.deferProducerUp(producerUp);
  }

  @Override
  public void onProducerStatusChange(ProducerStatus status) {
    int producerID = status.getProducer().getId();
    String statusName = status.getProducerStatusReason().name();
    log.info("Producer {} : onProducerStatusChange(status={})", producerID, statusName);

    if (isProducerUp(status)) {
      producerUpScheduler.deferProducerStatusChange(status);
    } else {
      super.onProducerStatusChange(status);
    }
  }

  private boolean isProducerUp(ProducerStatus status) {
    return new ProducerStatusQuery(status).isProducerUp();
  }
}
