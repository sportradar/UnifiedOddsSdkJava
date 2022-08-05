package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ProducerRandomEventFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class ProducerTaskFactory {

  private final SDKGlobalEventsListener concurrentGlobalListener;
  private final ProducerRandomEventFactory eventFactory;

  public Runnable recoveryInitiated(RecoveryContext context) {
    Producer producer = mockProducer(context.getProducerID());
    RecoveryInitiated recoveryInitiated = mock(RecoveryInitiated.class, "RecoveryInitiated");
    when(recoveryInitiated.getProducer()).thenReturn(producer);
    when(recoveryInitiated.getRequestId()).thenReturn(context.getRequestID());

    return () -> {
      log.info("Producer {} Request {} : Invoking Recovery Initiated ()...",
          context.getProducerID(), context.getRequestID());
      concurrentGlobalListener.onRecoveryInitiated(recoveryInitiated);
    };
  }

  public Runnable producerUp(ProducerID producerID) {
    ProducerStatus status = mock(ProducerStatus.class, "ProducerUp");
    when(status.getProducerStatusReason()).thenReturn(ProducerStatusReason.FirstRecoveryCompleted);

    Producer producer = mockProducer(producerID.getId());
    when(status.getProducer()).thenReturn(producer);

    return () -> {
      log.info("Producer {} : Invoking ProducerUp...", producerID.getId());
      concurrentGlobalListener.onProducerStatusChange(status);
    };
  }

  public Runnable randomEvent(RecoveryContext context) {
    return eventFactory.randomEvent(context);
  }

  private Producer mockProducer(int producerID) {
    Producer producer = mock(Producer.class, "Producer" + producerID);
    when(producer.getId()).thenReturn(producerID);
    return producer;
  }
}
