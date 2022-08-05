package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig.newConcurrentOddsFeedListenerConfig;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactoryProvider;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.GracefulShutdownBetweenTestsExecutorListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.CustomerSDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.ProducerLatches;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.CustomerOddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.producer.ProducerExpectations;
import com.sportradar.unifiedodds.sdk.listener.concurrent.producer.Producers;
import com.sportradar.unifiedodds.sdk.listener.concurrent.producer.ProducersFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
abstract class AbstractConcurrentListenerComponentTest {

  private final ConcurrentOddsFeedListenerConfig config = newConcurrentOddsFeedListenerConfig()
      .withNumberOfThreads(5)
      .build();
  private final GracefulShutdownBetweenTestsExecutorListener executorListener
      = new GracefulShutdownBetweenTestsExecutorListener();
  private final ExecutorFactoryProvider executorFactoryProvider = new ExecutorFactoryProvider(
      config, executorListener);
  private final ExecutorFactory executorFactory = executorFactoryProvider.create();
  private final ConcurrentListenerFactory listenerFactory = new ConcurrentListenerFactory(
      config, executorFactory);

  // shared objects
  private final CustomerListenerCallHistory callHistory = new CustomerListenerCallHistory();
  private final ProducerLatches producerLatches = new ProducerLatches();

  // these represent the listeners registered by the User, which we wrap with concurrent listeners
  private final CustomerOddsFeedListener customerOddsFeedListener
      = new CustomerOddsFeedListener(callHistory);
  private final CustomerSDKGlobalEventsListener customerSDKGlobalEventsListener
      = new CustomerSDKGlobalEventsListener(callHistory, producerLatches);

  private Producers producers;
  @Mock
  private OddsFeedSession session;

  @Before
  public void setUp() {
    SDKGlobalEventsListener concurrentGlobalListener = listenerFactory.createGlobalEventsListener(
        customerSDKGlobalEventsListener);
    OddsFeedListener concurrentOddsFeedListener = listenerFactory.createOddsFeedListener(
        customerOddsFeedListener);

    ProducersFactory producersFactory = new ProducersFactory(callHistory, session,
        concurrentGlobalListener, concurrentOddsFeedListener, producerLatches);
    producers = producersFactory.create();
  }

  @After
  public void tearDownExecutors() {
    executorListener.shutdownGracefully();
  }

  protected final ProducerExpectations producer(int producerID) {
    return producers.getOrCreate(producerID);
  }

  protected final ProducerExpectations producer1() {
    return producer(1);
  }

  protected final ProducerExpectations producer2() {
    return producer(2);
  }

  protected final void dumpCallHistory() {
    log.info("Customer Listeners call history:");
    callHistory.log();
  }

}
