package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.EventTask.newEventTask;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskAllocatorTest {

  private static final int MAX_EXECUTORS = 10;
  private final List<TaskExecutor> executors = new ArrayList<>(MAX_EXECUTORS);
  private TaskAllocator taskAllocator;
  @Mock
  private Runnable runnable;
  @Mock
  private TaskLifecycleTracker taskLifecycleTracker;
  private TrackedTaskFactory trackedTaskFactory;

  @Before
  public void setUp() {
    initExecutors();
    taskAllocator = new TaskAllocator(executors);
    trackedTaskFactory = new TrackedTaskFactory(taskLifecycleTracker);
  }

  @Test
  public void should_allocate_event_tasks_evenly_across_executors() {
    final int totalTasks = MAX_EXECUTORS * 10;
    for (int i = 0; i < totalTasks; i++) {
      EventTask task = createEventTask(i + 1);
      taskAllocator.allocate(task);
    }

    for (int i = 0; i < MAX_EXECUTORS; i++) {
      TaskExecutor executor = executors.get(i);
      verify(executor, atLeast(MAX_EXECUTORS - 1)).submit(any(EventTask.class));
    }
  }

  @Test
  public void should_allocate_tracked_tasks_evenly_across_executors() {
    final int totalTasks = MAX_EXECUTORS * 10;
    for (int i = 0; i < totalTasks; i++) {
      TrackedTask task = createTrackedTask(i + 1);
      taskAllocator.allocate(task);
    }

    for (int i = 0; i < MAX_EXECUTORS; i++) {
      TaskExecutor executor = executors.get(i);
      verify(executor, atLeast(MAX_EXECUTORS - 1)).submit(any(TrackedTask.class));
    }
  }

  private void initExecutors() {
    for (int i = 0; i < MAX_EXECUTORS; i++) {
      TaskExecutor executor = mock(TaskExecutor.class, "Executor-" + i);
      executors.add(executor);
    }
  }

  private EventTask createEventTask(long eventID) {
    RecoveryContext context = RecoveryContext.none();
    return newEventTask(TaskID.valueOf(eventID), runnable, context);
  }

  private TrackedTask createTrackedTask(long eventID) {
    return trackedTaskFactory.create(createEventTask(eventID));
  }
}