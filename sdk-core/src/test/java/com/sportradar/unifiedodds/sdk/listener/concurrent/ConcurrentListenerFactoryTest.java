package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentListenerFactoryTest {

  private ConcurrentListenerFactory factory;
  @Mock
  private ConcurrentOddsFeedListenerConfig config;
  @Mock
  private ExecutorFactory executorFactory;
  @Mock
  private SDKGlobalEventsListener globalEventsListener;
  @Mock
  private OddsFeedListener oddsFeedListener;

  @Before
  public void setUp() {
    factory = new ConcurrentListenerFactory(config, executorFactory);
  }

  @Test
  public void should_create_concurrent_global_events_listener() {
    SDKGlobalEventsListener listener = factory.createGlobalEventsListener(
        factory.createGlobalEventsListener(globalEventsListener));

    assertNotNull(listener);
  }

  @Test
  public void should_create_concurrent_odds_feed_listener() {
    OddsFeedListener listener = factory.createOddsFeedListener(oddsFeedListener);

    assertNotNull(listener);
  }
}