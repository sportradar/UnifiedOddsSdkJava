package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskExecutorTest {

  private TaskExecutor taskExecutor;
  @Mock
  private ExecutorService executorService;
  @Mock
  private TaskLifecycleTracker lifecycleTracker;
  @Mock
  private EventTask eventTask;

  @Mock
  private TrackedTask trackedTask;

  @Before
  public void setUp() {
    taskExecutor = new TaskExecutor(executorService, lifecycleTracker);
  }

  @Test
  public void should_submit_event_task_to_executor_service() {
    taskExecutor.submit(eventTask);

    verify(executorService).submit(eventTask);
  }

  @Test
  public void should_not_track_event_tasks() {
    taskExecutor.submit(eventTask);

    verifyNoInteractions(lifecycleTracker);
  }

  @Test
  public void should_fail_to_submit_event_task_to_executor_service() {
    when(executorService.submit(eventTask)).thenThrow(new RejectedExecutionException());

    taskExecutor.submit(eventTask);
  }

  @Test
  public void should_submit_tracked_task_to_executor_service() {
    taskExecutor.submit(trackedTask);

    InOrder inOrder = inOrder(executorService, lifecycleTracker);
    inOrder.verify(executorService).submit(trackedTask);
    inOrder.verify(lifecycleTracker).taskAllocated(trackedTask);
  }

  @Test
  public void should_not_track_tracked_task_which_failed_submission() {
    when(executorService.submit(trackedTask)).thenThrow(new RejectedExecutionException());

    taskExecutor.submit(trackedTask);

    verifyNoInteractions(lifecycleTracker);
  }
}