package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TrackedTaskTest {

  private TrackedTask trackedTask;
  @Mock
  private TaskLifecycleTracker lifecycleTracker;
  @Mock
  private EventTask eventTask;
  private final IdGenerator idGenerator = new IdGenerator();
  private final long expectedEventID = idGenerator.randomLong();
  private final int expectedProducerID = idGenerator.randomInt();
  private final long expectedRequestID = idGenerator.randomLong();

  @Before
  public void setUp() {
    TaskID taskID = TaskID.valueOf(expectedEventID);
    when(eventTask.getTaskID()).thenReturn(taskID);

    RecoveryContext context = RecoveryContext.valueOf(expectedProducerID, expectedRequestID);
    when(eventTask.getContext()).thenReturn(context);

    trackedTask = new TrackedTaskFactory(lifecycleTracker).create(eventTask);
  }

  @Test
  public void should_have_same_task_id() {
    assertSame(eventTask.getTaskID(), trackedTask.getTaskID());
  }

  @Test
  public void should_have_same_context() {
    assertSame(eventTask.getContext(), trackedTask.getContext());
  }

  @Test
  public void should_track_task_when_run() {
    trackedTask.run();
    verifyTaskLifecycleMethodsWereInvoked();
  }

  @Test
  public void should_track_task_fully_when_event_task_fails() {
    doThrow(new RuntimeException()).when(eventTask).run();

    try {
      trackedTask.run();
      fail("Event task should have thrown exception!");
    } catch (Throwable expected) {
      verifyTaskLifecycleMethodsWereInvoked();
    }
  }

  private void verifyTaskLifecycleMethodsWereInvoked() {
    InOrder inOrder = inOrder(lifecycleTracker, eventTask);
    inOrder.verify(lifecycleTracker).taskStarted(trackedTask);
    inOrder.verify(eventTask).run();
    inOrder.verify(lifecycleTracker).taskCompleted(trackedTask);
  }
}