package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class QueueFactory {

  private final ConcurrentOddsFeedListenerConfig config;

  public BlockingQueue<Runnable> create() {
    if (config.isUnboundedQueues()) {
      return new LinkedBlockingQueue<>();
    } else {
      return new ArrayBlockingQueue<>(config.getQueueSize());
    }
  }
}