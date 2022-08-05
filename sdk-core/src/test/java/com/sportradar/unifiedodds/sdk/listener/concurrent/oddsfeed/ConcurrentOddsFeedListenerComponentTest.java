package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig.newConcurrentOddsFeedListenerConfig;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactoryProvider;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

// Silent prevents Mockito failing on unnecessary stubbings
@RunWith(MockitoJUnitRunner.Silent.class)
public class ConcurrentOddsFeedListenerComponentTest {

  private static final int MAX_THREADS = 10;
  private static final int MAX_EVENTS = 100;
  private RecordingCustomerListener customerListener;
  private OddsFeedListener concurrentListener;
  private final MockSportEventFactory eventFactory = new MockSportEventFactory();
  private CountDownLatch latch;
  @Mock
  private OddsFeedSession session;
  private final TaskLifecycleTracker taskLifecycleTracker = new TaskLifecycleTracker();

  @Before
  public void setUp() {
    latch = new CountDownLatch(MAX_EVENTS);
    customerListener = new RecordingCustomerListener(latch);
    ConcurrentOddsFeedListenerConfig config = newConcurrentOddsFeedListenerConfig()
        .withNumberOfThreads(MAX_THREADS)
        .build();
    ExecutorFactory executorFactory = new ExecutorFactoryProvider(config).create();
    concurrentListener = new ConcurrentOddsFeedListenerFactory(config,
        executorFactory,
        taskLifecycleTracker)
        .create(customerListener);
  }

  @Test
  public void should_invoke_customer_listener_across_multiple_threads()
      throws InterruptedException {
    RecoveryContext context = RecoveryContext.valueOf(1, 123);
    for (int i = 0; i < MAX_EVENTS; i++) {
      int eventID = i + 1;
      concurrentListener.onOddsChange(session, eventFactory.oddsChange(context, eventID));
      concurrentListener.onBetStop(session, eventFactory.betStop(context, eventID));
      concurrentListener.onBetSettlement(session, eventFactory.betSettlement(context, eventID));
      concurrentListener.onRollbackBetSettlement(session,
          eventFactory.rollbackBetSettlement(context, eventID));
      concurrentListener.onBetCancel(session, eventFactory.betCancel(context, eventID));
      concurrentListener.onRollbackBetCancel(session,
          eventFactory.rollbackBetCancel(context, eventID));
      concurrentListener.onFixtureChange(session, eventFactory.fixtureChange(context, eventID));
    }
    latch.await();

    Queue<CustomerListenerEvent> log = customerListener.getActivityLog();
    verifySameEventIdsWereExecutedBySameThread(log);
    verifyListenerMethodsWereinvokedAcrossAllThreads(log);
  }

  private void verifySameEventIdsWereExecutedBySameThread(Queue<CustomerListenerEvent> log) {
    for (int i = 0; i < MAX_EVENTS; i++) {
      int eventID = i + 1;
      Set<String> threadNames = collectThreadNamesForEventID(log, eventID);
      assertEquals(1, threadNames.size());
    }
  }

  private void verifyListenerMethodsWereinvokedAcrossAllThreads(
      Queue<CustomerListenerEvent> log) {
    verifyMethodWasInvokedAcrossAllThreads(log, "onOddsChange");
    verifyMethodWasInvokedAcrossAllThreads(log, "onBetStop");
    verifyMethodWasInvokedAcrossAllThreads(log, "onBetSettlement");
    verifyMethodWasInvokedAcrossAllThreads(log, "onRollbackBetSettlement");
    verifyMethodWasInvokedAcrossAllThreads(log, "onBetCancel");
    verifyMethodWasInvokedAcrossAllThreads(log, "onRollbackBetCancel");
    verifyMethodWasInvokedAcrossAllThreads(log, "onFixtureChange");
  }

  private void verifyMethodWasInvokedAcrossAllThreads(Queue<CustomerListenerEvent> log,
      String method) {
    Set<String> threadNames = threadNamesCustomerListenerMethodWasInvokedOn(log, method);
    verifyMethodInvokedAcrossAllThreads(threadNames);
  }

  private Set<String> threadNamesCustomerListenerMethodWasInvokedOn(
      Queue<CustomerListenerEvent> log, String method) {
    Set<String> threadNames = log.stream()
        .filter(entry -> method.equals(entry.getMethod()))
        .map(CustomerListenerEvent::getThreadName)
        .collect(Collectors.toSet());
    assertEquals(MAX_THREADS, threadNames.size());
    return threadNames;
  }

  private Set<String> collectThreadNamesForEventID(Queue<CustomerListenerEvent> log, int eventID) {
    return log.stream()
        .filter(entry -> eventID == entry.getSportEvent().getId().getId())
        .map(CustomerListenerEvent::getThreadName)
        .collect(Collectors.toSet());
  }

  private void verifyMethodInvokedAcrossAllThreads(Set<String> threadNames) {
    assertEquals(MAX_THREADS, threadNames.size());
    for (int i = 0; i < MAX_THREADS; i++) {
      String threadName = format("Worker-%02d", i + 1);
      assertTrue(threadNames.contains(threadName));
    }
  }
}