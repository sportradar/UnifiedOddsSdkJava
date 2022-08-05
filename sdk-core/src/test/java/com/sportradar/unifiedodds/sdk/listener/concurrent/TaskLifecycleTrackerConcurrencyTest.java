package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static java.util.Collections.shuffle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryRequestID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TrackedTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskLifecycleTrackerConcurrencyTest {

  private final int totalThreads = 10;
  private final int totalRecoveryRequests = 3;
  private final int totalTasksPerRequest = 1000;
  private final int totalTaskStates = 3; // Allocated, Running, Completed
  private final int totalRunnables = totalRecoveryRequests * totalTasksPerRequest * totalTaskStates;
  private final int totalTasksToComplete = totalRecoveryRequests * totalTasksPerRequest;
  private final CountDownLatch countDownLatch = new CountDownLatch(totalTasksToComplete);
  private final List<Runnable> runnables = new ArrayList<>();
  private TaskLifecycleTracker lifecycleTracker;
  private ExecutorService executorService;
  private final IdGenerator idGenerator = new IdGenerator();
  private final int producerID = idGenerator.randomInt();

  @Before
  public void setUp() {
    lifecycleTracker = new TaskLifecycleTracker();
    executorService = Executors.newFixedThreadPool(totalThreads);
  }

  @After
  public void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  public void should_handle_many_concurrent_updates() {
    submitTaskProgressorsForEachRequestAndTaskState();

    progressAllTasksToCompletion();

    waitUntilAllTasksHaveCompleted();
  }

  private void submitTaskProgressorsForEachRequestAndTaskState() {
    requestIDs().forEach(requestID -> {
      for (int i = 0; i < totalTasksPerRequest; i++) {
        TaskProgressor taskProgressor = createTaskProgressor(requestID, i + 1);
        for (int state = 0; state < totalTaskStates; state++) {
          runnables.add(taskProgressor);
        }
      }
    });
  }

  private void progressAllTasksToCompletion() {
    assertEquals(totalRunnables, runnables.size());
    shuffle(runnables); // we don't want them all to run in perfect sequence
    runnables.forEach(task -> executorService.submit(task));
  }

  private void waitUntilAllTasksHaveCompleted() {
    try {
      boolean allTasksComplete = countDownLatch.await(30, TimeUnit.SECONDS);
      if (allTasksComplete) {
        verifyNoMorePendingTasks();
      } else {
        fail("Timed out but " + totalPendingTasks() + " tasks still pending!");
      }
    } catch (InterruptedException e) {
      fail("Interrupted but " + totalPendingTasks() + " tasks still pending!");
    }
  }

  private List<RecoveryRequestID> requestIDs() {
    List<RecoveryRequestID> requestIDs = new ArrayList<>();
    for (long i = 0; i < totalRecoveryRequests; i++) {
      requestIDs.add(RecoveryRequestID.valueOf(i + 1));
    }
    return requestIDs;
  }

  private void verifyNoMorePendingTasks() {
    assertEquals(0, totalPendingTasks());
  }

  private long totalPendingTasks() {
    final AtomicLong totalPendingTasks = new AtomicLong(0);

    requestIDs().forEach(requestID -> {
      RecoveryContext context = RecoveryContext.valueOf(producerID, requestID.getId());
      long pendingTasks = lifecycleTracker.pendingTasksFor(context);
      totalPendingTasks.getAndAdd(pendingTasks);
    });
    return totalPendingTasks.get();
  }

  private TaskProgressor createTaskProgressor(RecoveryRequestID requestID, long taskID) {
    TrackedTask task = createTask(requestID, taskID);
    return new TaskProgressor(lifecycleTracker, countDownLatch, task);
  }

  private TrackedTask createTask(RecoveryRequestID requestID, long eventTaskID) {
    RecoveryContext context = RecoveryContext.valueOf(producerID, requestID.getId());
    TaskID taskID = TaskID.valueOf(eventTaskID);
    TrackedTask trackedTask = mock(TrackedTask.class);
    when(trackedTask.getContext()).thenReturn(context);
    when(trackedTask.getTaskID()).thenReturn(taskID);
    return trackedTask;
  }

  @Slf4j
  @RequiredArgsConstructor
  private static class TaskProgressor implements Runnable {

    private enum State {Unallocated, Allocated, Running, Completed}

    private final TaskLifecycleTracker lifecycleTracker;
    private final CountDownLatch countDownLatch;
    private final TrackedTask task;
    private final AtomicReference<State> state = new AtomicReference<>(State.Unallocated);

    @Override
    public void run() {
      switch (state.get()) {
        case Unallocated:
          progressTo(State.Allocated);
          break;
        case Allocated:
          progressTo(State.Running);
          break;
        case Running:
          progressTo(State.Completed);
          break;
      }
    }

    // NOTE: this method MUST be synchronized so both state and lifecycleTracker updates
    // happen atomically
    private synchronized void progressTo(State newState) {
      state.getAndSet(newState);
      if (newState == State.Allocated) {
        lifecycleTracker.taskAllocated(task);
      } else if (newState == State.Running) {
        lifecycleTracker.taskStarted(task);
      } else if (newState == State.Completed) {
        lifecycleTracker.taskCompleted(task);
        countDownLatch.countDown();
      }
    }
  }
}