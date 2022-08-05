package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static org.junit.Assert.assertEquals;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PendingTasksTest {

  private PendingTasks pendingTasks;
  @Mock
  private final IdGenerator idGenerator = new IdGenerator();
  private final TaskID taskID = TaskID.valueOf(idGenerator.randomLong());

  @Before
  public void setUp() {
    pendingTasks = new PendingTasks();
  }

  @Test
  public void should_have_no_pending_tasks_by_default() {
    assertEquals(0, pendingTasks.getPendingTaskCount());
  }

  @Test
  public void should_have_one_pending_task_when_allocated() {
    pendingTasks.taskAllocated(taskID);

    assertEquals(1, pendingTasks.getPendingTaskCount());
  }

  @Test
  public void should_track_count_of_multiple_pending_tasks() {
    int totalTasks = 10;

    for (int i = 0; i < totalTasks; i++) {
      TaskID taskID = TaskID.valueOf(i + 1);
      pendingTasks.taskAllocated(taskID);
      assertEquals(i + 1, pendingTasks.getPendingTaskCount());
    }
    assertEquals(totalTasks, pendingTasks.getPendingTaskCount());
  }

  @Test
  public void should_track_count_of_multiple_completed_tasks() {
    int totalTasks = 10;

    for (int i = 0; i < totalTasks; i++) {
      TaskID taskID = TaskID.valueOf(i + 1);
      pendingTasks.taskAllocated(taskID);
    }
    for (int i = 0; i < totalTasks; i++) {
      TaskID taskID = TaskID.valueOf(i + 1);
      pendingTasks.taskCompleted(taskID);
    }
    assertEquals(0, pendingTasks.getPendingTaskCount());
  }

  @Test
  public void should_have_no_pending_tasks_when_allocated_task_has_completed() {
    pendingTasks.taskAllocated(taskID);
    pendingTasks.taskCompleted(taskID);

    assertEquals(0, pendingTasks.getPendingTaskCount());
  }
}