package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OddsFeedLifecycleExpectations {

  private final OddsFeedLifecycleFixture oddsFeedLifecycle;

  public OddsFeedLifecycleExpectations isStarted() {
    oddsFeedLifecycle.start();
    return this;
  }

  public OddsFeedLifecycleExpectations isStopped() {
    oddsFeedLifecycle.stop();
    return this;
  }
}
