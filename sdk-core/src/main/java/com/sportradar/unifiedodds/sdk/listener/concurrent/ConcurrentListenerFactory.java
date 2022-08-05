package com.sportradar.unifiedodds.sdk.listener.concurrent;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.ConcurrentSDKGlobalEventsListenerFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Wraps customer provided SDKGlobalEventsListener and OddsFeedListeners with concurrent versions.
 */
@Slf4j
public class ConcurrentListenerFactory {

  private final ConcurrentOddsFeedListenerFactory oddsFeedListenerFactory;
  private final ConcurrentSDKGlobalEventsListenerFactory globalListenerFactory;

  public ConcurrentListenerFactory(ConcurrentOddsFeedListenerConfig config,
      ExecutorFactory executorFactory) {
    TaskLifecycleTracker taskLifecycleTracker = new TaskLifecycleTracker();
    oddsFeedListenerFactory = new ConcurrentOddsFeedListenerFactory(
        config, executorFactory, taskLifecycleTracker);
    globalListenerFactory = new ConcurrentSDKGlobalEventsListenerFactory(executorFactory,
        taskLifecycleTracker);
  }

  public SDKGlobalEventsListener createGlobalEventsListener(
      SDKGlobalEventsListener customerGlobalListener) {
    log.info("Creating concurrent SDKGlobalEventsListener");
    return globalListenerFactory.create(customerGlobalListener);
  }

  public OddsFeedListener createOddsFeedListener(OddsFeedListener customerListener) {
    log.info("Creating concurrent OddsFeedListener");
    return oddsFeedListenerFactory.create(customerListener);
  }
}
