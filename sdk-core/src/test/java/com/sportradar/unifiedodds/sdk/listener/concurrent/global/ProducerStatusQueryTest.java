package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProducerStatusQueryTest {

  @Mock
  private ProducerStatus producerStatus;

  @Test
  public void should_be_producer_up_when_reason_is_FirstRecoveryCompleted() {
    assertTrue(query(ProducerStatusReason.FirstRecoveryCompleted).isProducerUp());
  }

  @Test
  public void should_be_producer_up_when_reason_is_ProcessingQueDelayStabilized() {
    assertTrue(query(ProducerStatusReason.ProcessingQueDelayStabilized).isProducerUp());
  }

  @Test
  public void should_be_producer_up_when_reason_is_ReturnedFromInactivity() {
    assertTrue(query(ProducerStatusReason.ReturnedFromInactivity).isProducerUp());
  }

  @Test
  public void should_not_be_producer_up_when_reason_is_ReturnedFromInactivity() {
    assertFalse(query(ProducerStatusReason.AliveIntervalViolation).isProducerUp());
  }

  @Test
  public void should_not_be_producer_up_when_reason_is_ProcessingQueueDelayViolation() {
    assertFalse(query(ProducerStatusReason.ProcessingQueueDelayViolation).isProducerUp());
  }

  @Test
  public void should_not_be_producer_up_when_reason_is_Other() {
    assertFalse(query(ProducerStatusReason.Other).isProducerUp());
  }

  private ProducerStatusQuery query(ProducerStatusReason reason) {
    when(producerStatus.getProducerStatusReason()).thenReturn(reason);
    return new ProducerStatusQuery(producerStatus);
  }
}