package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.DeferredProducerUpOrchestrator.POLL_INTERVAL;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeferredProducerUpOrchestratorTest {

  @Mock
  private ScheduledExecutorService executorService;
  @Mock
  private DeferredProducerUpTaskFactory taskFactory;
  @Mock
  private PendingRecoveries pendingRecoveries;
  @Mock
  private Producer producer;
  @Mock
  private DeferredProducerUpTask producerUpTask;
  @Mock
  private ScheduledFuture future;

  private DeferredProducerUpOrchestrator orchestrator;
  private final ProducerID producerID = ProducerID.valueOf(1);
  private final long requestID = 2;
  private final RecoveryContext context = RecoveryContext.valueOf(producerID.getId(), requestID);

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    orchestrator = new DeferredProducerUpOrchestrator(executorService, taskFactory, pendingRecoveries);

    when(producer.getId()).thenReturn(producerID.getId());
    when(executorService.scheduleWithFixedDelay(producerUpTask, POLL_INTERVAL, POLL_INTERVAL,
        TimeUnit.MILLISECONDS)).thenReturn(future);
  }

  @Test
  public void should_store_recovery_initiated() {
    orchestrator.recoveryInitiated(context);

    verify(pendingRecoveries).recoveryInitiated(context);
  }

  @Test
  public void should_defer_producer_up() {
    ProducerUp producerUp = mock(ProducerUp.class);
    givenRecoveryWasInitiated();
    givenProducerUp(producerUp);

    orchestrator.deferProducerUp(producerUp);

    verifyActionWasDeferredSuccessfully();
  }

  @Test
  public void should_not_defer_producer_up_if_no_recovery_was_initiated() {
    ProducerUp producerUp = mock(ProducerUp.class);
    when(producerUp.getProducer()).thenReturn(producer);
    givenNoRecoveryWasInitiated();

    orchestrator.deferProducerUp(producerUp);

    verifyActionWasNotDeferred();
  }

  @Test
  public void should_defer_producer_status_change() {
    ProducerStatus status = mock(ProducerStatus.class);
    givenRecoveryWasInitiated();
    givenProducerStatusChange(status);

    orchestrator.deferProducerStatusChange(status);

    verifyActionWasDeferredSuccessfully();
  }

  @Test
  public void should_not_defer_producer_status_change_if_no_recovery_was_initiated() {
    ProducerStatus status = mock(ProducerStatus.class);
    when(status.getProducer()).thenReturn(producer);
    givenNoRecoveryWasInitiated();

    orchestrator.deferProducerStatusChange(status);

    verifyActionWasNotDeferred();
  }

  private void givenRecoveryWasInitiated() {
    when(pendingRecoveries.contextFor(producerID)).thenReturn(context);
  }

  private void givenNoRecoveryWasInitiated() {
    RecoveryContext noContext = RecoveryContext.none();
    when(pendingRecoveries.contextFor(producerID)).thenReturn(noContext);
  }

  private void givenProducerUp(ProducerUp producerUp) {
    when(producerUp.getProducer()).thenReturn(producer);
    when(taskFactory.createOnProducerUp(context, producerUp)).thenReturn(producerUpTask);
  }

  private void givenProducerStatusChange(ProducerStatus status) {
    when(status.getProducer()).thenReturn(producer);
    when(taskFactory.createOnProducerStatusChange(context, status)).thenReturn(producerUpTask);
  }

  private void verifyActionWasDeferredSuccessfully() {
    InOrder inOrder = inOrder(executorService, producerUpTask, pendingRecoveries);
    inOrder.verify(executorService)
        .scheduleWithFixedDelay(producerUpTask, POLL_INTERVAL, POLL_INTERVAL,
            TimeUnit.MILLISECONDS);
    inOrder.verify(producerUpTask).setFuture(future);
    inOrder.verify(pendingRecoveries).save(context, producerUpTask);
  }

  private void verifyActionWasNotDeferred() {
    verifyNoInteractions(executorService, producerUpTask);
    verify(pendingRecoveries, never()).save(context, producerUpTask);
  }
}