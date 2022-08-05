package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfigurationFixture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OddsFeedExpectations {

  private final OddsFeedConfigurationFixture.Builder configBuilder;
  private final OddsFeedFixture.Builder oddsFeedBuilder;

  public OddsFeedExpectations hasListener(SDKGlobalEventsListener listener) {
    oddsFeedBuilder.withGlobalEventsListener(listener);
    return this;
  }

  public OddsFeedExpectations hasRandomMessageListenerDelays(int minDelayMs, int maxDelayMs) {
    oddsFeedBuilder.withRandomMessageListenerDelays(minDelayMs, maxDelayMs);
    return this;
  }

  public OddsFeedExpectations hasOddsFeedListener(OddsFeedListener listener) {
    oddsFeedBuilder.withOddsFeedListener(listener);
    return this;
  }

  public OddsFeedExpectations usingConcurrentListener() {
    configBuilder.withConcurrentListenerEnabled();
    return this;
  }

  public OddsFeedExpectations withConcurrentListenerThreads(int threadCount) {
    configBuilder.withConcurrentListenerThreads(threadCount);
    return this;
  }

  public OddsFeedExpectations withConcurrentListenerQueueSize(int queueSize) {
    configBuilder.withConcurrentListenerQueueSize(queueSize);
    return this;
  }

  public OddsFeedExpectations handlesConcurrentListenerErrorsOnRabbitThread() {
    configBuilder.withConcurrentListenerHandleErrorsOnRabbitThread();
    return this;
  }
}
