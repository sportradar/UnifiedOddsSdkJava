package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TrackedTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskLifecycleTrackerTest {

  private TaskLifecycleTracker lifecycleTracker;
  @Mock
  private TrackedTask task;
  private final IdGenerator idGenerator = new IdGenerator();
  private final TaskID taskID = TaskID.valueOf(idGenerator.randomLong());
  private final int producerID = idGenerator.randomInt();
  private final long requestID = idGenerator.randomLong();
  private final RecoveryContext context = RecoveryContext.valueOf(producerID, requestID);

  @Before
  public void setUp() {
    lifecycleTracker = new TaskLifecycleTracker();
    when(task.getTaskID()).thenReturn(taskID);
    when(task.getContext()).thenReturn(context);
  }

  @Test
  public void should_register_no_allocated_tasks() {
    assertEquals(0, pendingTasksFor(context));
  }

  @Test
  public void should_register_allocated_task() {
    lifecycleTracker.taskAllocated(task);

    assertEquals(1, pendingTasksFor(context));
  }

  @Test
  public void should_do_nothing_when_task_started() {
    lifecycleTracker.taskAllocated(task);
    assertEquals(1, pendingTasksFor(context));

    lifecycleTracker.taskStarted(task);
    assertEquals(1, pendingTasksFor(context));
  }

  @Test
  public void should_unregister_completed_task() {
    lifecycleTracker.taskAllocated(task);
    lifecycleTracker.taskCompleted(task);

    assertEquals(0, pendingTasksFor(context));
  }

  @Test
  public void should_do_nothing_when_task_completed_but_never_started() {
    lifecycleTracker.taskCompleted(task);

    assertEquals(0, pendingTasksFor(context));
  }

  private long pendingTasksFor(RecoveryContext context) {
    return lifecycleTracker.pendingTasksFor(context);
  }
}