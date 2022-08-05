package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventTaskTest {

  private EventTask task;
  private TaskID taskID;
  @Mock
  private Runnable runnable;
  private final IdGenerator idGenerator = new IdGenerator();
  private final long expectedEventID = idGenerator.randomLong();
  private final int expectedProducerID = idGenerator.randomInt();
  private final long expectedRequestID = idGenerator.randomLong();
  private final RecoveryContext context = RecoveryContext.valueOf(expectedProducerID,
      expectedRequestID);

  @Before
  public void setUp() {
    taskID = TaskID.valueOf(expectedEventID);
    task = EventTask.newEventErrorTask(taskID, runnable);
  }

  @Test
  public void should_have_task_id_for_event_task() {
    task = EventTask.newEventTask(taskID, runnable, context);

    assertSame(taskID, task.getTaskID());
  }

  @Test
  public void should_have_task_id_for_event_error_task() {
    task = EventTask.newEventErrorTask(taskID, runnable);

    assertSame(taskID, task.getTaskID());
  }

  @Test
  public void should_assign_random_task_id_for_error_task() {
    int totalTasks = 50;
    Set<Long> taskIDs = new HashSet<>();

    for (int i = 0; i < totalTasks; i++) {
      Task task = EventTask.newErrorTask(runnable);
      taskIDs.add(task.getTaskID().getId());
    }

    assertTrue(taskIDs.size() > (totalTasks / 2));
  }

  @Test
  public void should_run_runnable_for_event_task() {
    task = EventTask.newEventTask(taskID, runnable, context);

    task.run();

    verify(runnable).run();
  }

  @Test
  public void should_run_runnable_for_event_error_task() {
    task = EventTask.newEventErrorTask(taskID, runnable);

    task.run();

    verify(runnable).run();
  }

  @Test
  public void should_run_runnable_for_error_task() {
    task = EventTask.newErrorTask(runnable);

    task.run();

    verify(runnable).run();
  }

  @Test
  public void should_have_toString() {
    task = EventTask.newEventTask(taskID, runnable, context);

    String expected = String.format("EventTask(taskID=TaskID(id=%d))", expectedEventID);
    assertEquals(expected, task.toString());
  }
}