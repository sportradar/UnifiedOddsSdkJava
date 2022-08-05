package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class PendingRecoveriesConcurrencyTest {

  private final Map<ProducerID, DeferredProducerUpTask> tasks = new ConcurrentHashMap<>();
  private final Map<ProducerID, RecoveryContext> contexts = new ConcurrentHashMap<>();
  private PendingRecoveries pendingRecoveries;
  private final int totalThreads = 5;
  private final int totalProducers = 100;
  private final List<Runnable> runnables = new ArrayList<>();
  private final CountDownLatch startLatch = new CountDownLatch(1);
  private final CountDownLatch countDownLatch = new CountDownLatch(totalProducers);
  private ExecutorService executorService;

  @Before
  public void setUp() throws Exception {
    pendingRecoveries = new PendingRecoveries(tasks, contexts);
    executorService = Executors.newFixedThreadPool(totalThreads);
  }

  @After
  public void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  public void should_handle_concurrent_updates_from_multiple_producers() {
    executeAllProducerInteractionsAtSameTime();

    waitUntilAllTasksHaveCompleted();

    verifyResults();
  }

  private void verifyResults() {
    assertEquals(totalProducers, contexts.size());
    assertEquals(totalProducers, tasks.size());
  }

  private void executeAllProducerInteractionsAtSameTime() {
    recoveryContexts().forEach(context -> runnables.add(producerInteractions(context)));

    assertEquals(totalProducers, runnables.size());
    runnables.forEach(task -> executorService.submit(task));
    startLatch.countDown(); // now all Producer tasks will start at exact same time
  }

  private void waitUntilAllTasksHaveCompleted() {
    try {
      boolean allTasksComplete = countDownLatch.await(30, TimeUnit.SECONDS);
      if (!allTasksComplete) {
        fail("Timed out waiting for all tasks!");
      }
    } catch (InterruptedException e) {
      fail("Interrupted waiting for all tasks!");
    }
  }

  private Runnable producerInteractions(RecoveryContext context) {
    return () -> {
      ProducerID producerID = ProducerID.valueOf(context.getProducerID());

      try {
        startLatch.await();
        pendingRecoveries.contextFor(producerID);
        pendingRecoveries.recoveryInitiated(context);
        pendingRecoveries.save(context, createTask(context));
        pendingRecoveries.recoveryInitiated(context);
        pendingRecoveries.contextFor(producerID);
        pendingRecoveries.recoveryInitiated(context);
         pendingRecoveries.save(context, createTask(context));
        pendingRecoveries.contextFor(producerID);
      } catch (InterruptedException e) {
        log.error("Interrupted waiting to start!", e);
      } finally {
        countDownLatch.countDown();
      }
    };
  }

  private List<RecoveryContext> recoveryContexts() {
    List<RecoveryContext> contexts = new ArrayList<>();
    producerIDs().forEach(producerID -> contexts.add(createContext(producerID)));
    return contexts;
  }

  private RecoveryContext createContext(ProducerID producerID) {
    return RecoveryContext.valueOf(producerID.getId(), 1);
  }

  private List<ProducerID> producerIDs() {
    List<ProducerID> ids = new ArrayList<>();
    for (int i = 0; i < totalProducers; i++) {
      ids.add(ProducerID.valueOf(i + 1));
    }
    return ids;
  }

  private DeferredProducerUpTask createTask(RecoveryContext context) {
    DeferredProducerUpTask task = mock(DeferredProducerUpTask.class,
        "DeferredProducerUpTask " + context.getProducerID());
    return task;
  }
}