package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig.newConcurrentOddsFeedListenerConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactoryProvider;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskExecutorFactoryTest {

  private TaskExecutorFactory taskExecutorFactory;
  private final Set<String> threadNames = ConcurrentHashMap.newKeySet();
  @Mock
  private TaskLifecycleTracker taskLifecycleTracker;

  @Before
  public void setUp() {
    ConcurrentOddsFeedListenerConfig config = newConcurrentOddsFeedListenerConfig().build();
    ExecutorFactory executorFactory = new ExecutorFactoryProvider(config).create();
    taskExecutorFactory = new TaskExecutorFactory(executorFactory,
        taskLifecycleTracker);
  }

  @Test
  public void should_run_tasks_on_single_worker_thread() throws InterruptedException {
    int numberOfTasks = 10;
    CountDownLatch latch = new CountDownLatch(numberOfTasks);

    TaskExecutor taskExecutor = taskExecutorFactory.create();

    for (int i = 0; i < numberOfTasks; i++) {
      Runnable runnable = new MyRunnable(latch, threadNames);
      EventTask task = EventTask.newErrorTask(runnable);
      taskExecutor.submit(task);
    }
    latch.await();

    assertEquals(1, threadNames.size());
    assertTrue(threadNames.iterator().next().matches("Worker-\\d+"));
  }

  @Slf4j
  @RequiredArgsConstructor
  private static class MyRunnable implements Runnable {

    private final CountDownLatch latch;
    private final Set<String> threadNames;

    @Override
    public void run() {
      String name = Thread.currentThread().getName();
      log.info("Running task on thread '{}'", name);
      threadNames.add(name);
      latch.countDown();
    }
  }
}