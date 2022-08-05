package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.listener.concurrent.ListenerType;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallEntry;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.utils.URN;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * This is both a SDKGlobalEventsListener and an OddsFeedListener as we need to verify that
 * Producer Up happens <bold>after</bold> all events tasks have completed.
 *
 * All calls to SDKGlobalEventsListener methods are made by a Producer.
 * All calls to OddsFeedListener methods are made by worker threads.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class CustomerSDKGlobalEventsListener extends SDKGlobalEventsListenerAdapter {

  private final CustomerListenerCallHistory callHistory;
  private final ProducerLatches producerLatches;

  @Override
  public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
    int producerID = recoveryInitiated.getProducer().getId();
    long requestID = recoveryInitiated.getRequestId();
    URN eventID = recoveryInitiated.getEventId();
    log.info("Producer {}, Request {} : onRecoveryInitiated()", producerID, requestID);

    callHistory.save(CustomerListenerCallEntry.builder()
        .listenerType(ListenerType.Global)
        .producerID(producerID)
        .requestID(requestID)
        .eventID(eventID)
        .method("onRecoveryInitiated"));

    producerLatches.recoveryInitiated(ProducerID.valueOf(producerID));
  }

  @Override
  public void onProducerUp(ProducerUp producerUp) {
    int producerID = producerUp.getProducer().getId();
    String reason = producerUp.getReason().name();
    log.info("Producer {} : onProducerUp({})", producerID, reason);

    callHistory.save(CustomerListenerCallEntry.builder()
        .listenerType(ListenerType.Global)
        .producerID(producerID)
        .method("onProducerUp")
        .producerStatusReason(reason));

    producerIsUp(producerID);
  }

  @Override
  public void onProducerStatusChange(ProducerStatus status) {
    int producerID = status.getProducer().getId();
    ProducerStatusReason reason = status.getProducerStatusReason();
    log.info("Producer {} : onProducerStatusChange({})", producerID, reason);

    callHistory.save(CustomerListenerCallEntry.builder()
        .listenerType(ListenerType.Global)
        .producerID(producerID)
        .method("onProducerStatusChange")
        .producerStatusReason(reason.name()));

    if (isStatusProducerUp(status)) {
      producerIsUp(producerID);
    }
  }

  private void producerIsUp(int producerID) {
    log.info("Producer {} : status is now UP", producerID);
    producerLatches.producerUp(ProducerID.valueOf(producerID));
  }

  private boolean isStatusProducerUp(ProducerStatus status) {
    return new ProducerStatusQuery(status).isProducerUp();
  }
}