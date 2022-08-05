package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUpReason;
import java.util.concurrent.ScheduledFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeferredProducerUpTaskTest {

  @Mock
  private SDKGlobalEventsListener customerGlobalListener;
  @Mock
  private TaskLifecycleTracker taskLifecycleTracker;
  @Mock
  private ScheduledFuture<?> future;

  private final RecoveryContext context = RecoveryContext.valueOf(1, 2);
  private DeferredProducerUpTaskFactory factory;

  @Before
  public void setUp() {
    factory = new DeferredProducerUpTaskFactory(
        customerGlobalListener, taskLifecycleTracker);
  }

  @Test
  public void should_not_invoke_listener_when_there_are_still_pending_tasks_for_producer_up_task() {
    ProducerUp producerUp = mock(ProducerUp.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(1L);

    DeferredProducerUpTask task = createProducerUpTask(producerUp);
    task.run();

    verifyNoInteractions(customerGlobalListener);
  }

  @Test
  public void should_invoke_listener_when_there_are_no_more_pending_tasks_for_producer_up_task() {
    ProducerUp producerUp = mock(ProducerUp.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(0L);

    DeferredProducerUpTask task = createProducerUpTask(producerUp);
    task.run();

    verify(customerGlobalListener).onProducerUp(producerUp);
  }

  @Test
  public void should_cancel_producer_up_task_when_listener_invoked() {
    ProducerUp producerUp = mock(ProducerUp.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(0L);

    DeferredProducerUpTask task = createProducerUpTask(producerUp);
    task.setFuture(future);
    task.run();

    verify(customerGlobalListener).onProducerUp(producerUp);
    verify(future).cancel(false);
  }

  @Test
  public void should_not_invoke_listener_when_there_are_still_pending_tasks_for_producer_status_change_task() {
    ProducerStatus status = mock(ProducerStatus.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(1L);

    DeferredProducerUpTask task = createProducerStatusChangeTask(status);
    task.run();

    verifyNoInteractions(customerGlobalListener);
  }

  @Test
  public void should_invoke_listener_when_there_are_no_more_pending_tasks_for_producer_status_change_task() {
    ProducerStatus status = mock(ProducerStatus.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(0L);

    DeferredProducerUpTask task = createProducerStatusChangeTask(status);
    task.run();

    verify(customerGlobalListener).onProducerStatusChange(status);
  }

  @Test
  public void should_cancel_producer_status_change_task_when_listener_invoked() {
    ProducerStatus status = mock(ProducerStatus.class);
    when(taskLifecycleTracker.pendingTasksFor(context)).thenReturn(0L);

    DeferredProducerUpTask task = createProducerStatusChangeTask(status);
    task.setFuture(future);
    task.run();

    verify(customerGlobalListener).onProducerStatusChange(status);
    verify(future).cancel(false);
  }

  @Test
  public void should_cancel_task_if_future_set() {
    ProducerStatus status = mock(ProducerStatus.class);

    DeferredProducerUpTask task = createProducerStatusChangeTask(status);
    task.setFuture(future);
    task.cancel();

    verify(future).cancel(false);
  }

  @Test
  public void should_not_cancel_task_if_no_future_set() {
    ProducerStatus status = mock(ProducerStatus.class);

    DeferredProducerUpTask task = createProducerStatusChangeTask(status);
    task.cancel();

    verifyNoInteractions(future);
  }

  private DeferredProducerUpTask createProducerUpTask(ProducerUp producerUp) {
    when(producerUp.getReason()).thenReturn(ProducerUpReason.FirstRecoveryCompleted);

    return factory.createOnProducerUp(context, producerUp);
  }

  private DeferredProducerUpTask createProducerStatusChangeTask(ProducerStatus status) {
    when(status.getProducerStatusReason())
        .thenReturn(ProducerStatusReason.FirstRecoveryCompleted);

    return factory.createOnProducerStatusChange(context, status);
  }
}