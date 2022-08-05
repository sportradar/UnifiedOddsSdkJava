package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentOddsFeedListenerFactoryTest {

  private ConcurrentOddsFeedListenerFactory factory;
  @Mock
  private ConcurrentOddsFeedListenerConfig config;
  @Mock
  private ExecutorFactory executorFactory;
  @Mock
  private TaskLifecycleTracker taskLifecycleTracker;
  @Mock
  private OddsFeedListener customerListener;

  @Before
  public void setUp() {
    factory = new ConcurrentOddsFeedListenerFactory(config, executorFactory, taskLifecycleTracker);
  }

  @Test
  public void should_create_concurrent_odds_feed_listener() {
    OddsFeedListener listener = factory.create(customerListener);

    assertThat(listener, instanceOf(ConcurrentOddsFeedListener.class));
  }
}