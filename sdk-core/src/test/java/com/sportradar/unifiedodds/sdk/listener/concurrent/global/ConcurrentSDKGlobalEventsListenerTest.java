package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ProcessingQueDelayStabilized;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ProcessingQueueDelayViolation;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ReturnedFromInactivity;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.DeferredProducerUpOrchestrator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUpReason;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentSDKGlobalEventsListenerTest {

  private ConcurrentSDKGlobalEventsListener listener;
  @Mock
  private SDKGlobalEventsListener globalEventsListener;
  @Mock
  private DeferredProducerUpOrchestrator producerUpScheduler;
  @Mock
  private Producer producer;
  private final IdGenerator idGenerator = new IdGenerator();
  private final int producerID = idGenerator.randomInt();
  private final long requestID = idGenerator.randomLong();

  @Before
  public void setUp() {
    listener = new ConcurrentSDKGlobalEventsListener(globalEventsListener, producerUpScheduler);

    when(producer.getId()).thenReturn(producerID);
  }

  @Test
  public void should_store_context_when_recovery_initiated() {
    RecoveryInitiated recoveryInitiated = mock(RecoveryInitiated.class);
    when(recoveryInitiated.getProducer()).thenReturn(producer);
    when(recoveryInitiated.getRequestId()).thenReturn(requestID);
    ArgumentCaptor<RecoveryContext> captor = ArgumentCaptor.forClass(RecoveryContext.class);

    listener.onRecoveryInitiated(recoveryInitiated);

    InOrder inOrder = inOrder(producerUpScheduler, globalEventsListener);
    inOrder.verify(producerUpScheduler).recoveryInitiated(captor.capture());
    inOrder.verify(globalEventsListener).onRecoveryInitiated(recoveryInitiated);
    assertEquals(producerID, captor.getValue().getProducerID());
    assertEquals(requestID, captor.getValue().getRequestID());
  }

  @Test
  public void should_defer_producer_up_until_pending_tasks_are_completed() {
    ProducerUp producerUp = mock(ProducerUp.class);
    when(producerUp.getProducer()).thenReturn(producer);
    when(producerUp.getReason()).thenReturn(ProducerUpReason.FirstRecoveryCompleted);

    listener.onProducerUp(producerUp);

    verify(producerUpScheduler).deferProducerUp(producerUp);
    verifyNoInteractions(globalEventsListener);
  }

  @Test
  public void should_defer_producer_up_until_pending_tasks_are_completed_when_producer_status_is_ProcessingQueDelayStabilized() {
    verifyProducerUpIsDeferred(ProcessingQueDelayStabilized);
  }

  @Test
  public void should_defer_producer_up_until_pending_tasks_are_completed_when_producer_status_is_ReturnedFromInactivity() {
    verifyProducerUpIsDeferred(ReturnedFromInactivity);
  }

  @Test
  public void should_defer_producer_up_until_pending_tasks_are_completed_when_producer_status_is_AliveIntervalViolation() {
    verifyCustomerListenerIsInvokedImmediately(ProducerStatusReason.AliveIntervalViolation);
  }

  @Test
  public void should_not_defer_when_producer_status_is_ProcessingQueueDelayViolation() {
    verifyCustomerListenerIsInvokedImmediately(ProcessingQueueDelayViolation);
  }

  @Test
  public void should_not_defer_when_producer_status_is_Other() {
    verifyCustomerListenerIsInvokedImmediately(ProducerStatusReason.Other);
  }

  private void verifyProducerUpIsDeferred(ProducerStatusReason reason) {
    ProducerStatus status = createStatus(reason);

    listener.onProducerStatusChange(status);

    verify(producerUpScheduler).deferProducerStatusChange(status);
    verifyNoInteractions(globalEventsListener);
  }

  private void verifyCustomerListenerIsInvokedImmediately(ProducerStatusReason reason) {
    ProducerStatus status = createStatus(reason);

    listener.onProducerStatusChange(status);

    verify(globalEventsListener).onProducerStatusChange(status);
    verifyNoInteractions(producerUpScheduler);
  }

  private ProducerStatus createStatus(ProducerStatusReason reason) {
    ProducerStatus status = mock(ProducerStatus.class);
    when(status.getProducer()).thenReturn(producer);
    when(status.getProducerStatusReason()).thenReturn(reason);
    return status;
  }
}