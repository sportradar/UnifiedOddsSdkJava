package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import lombok.Getter;

@Getter
public class ConcurrentOddsFeedListenerConfig {

  public static final int THREADS_MIN = 5;
  public static final int THREADS_MAX = 30;
  public static final int THREADS_DEFAULT = 10;
  public static final int QUEUE_SIZE_MIN = 500;
  public static final int QUEUE_SIZE_MAX = Integer.MAX_VALUE;
  public static final int QUEUE_SIZE_DEFAULT = 10000;
  private final int numberOfThreads;
  private final int queueSize;
  private final boolean handleErrorsAsynchronously;

  public static Builder newConcurrentOddsFeedListenerConfig() {
    return new Builder();
  }

  private ConcurrentOddsFeedListenerConfig(Builder builder) {
    this.numberOfThreads = builder.numberOfThreads;
    this.queueSize = builder.queueSize;
    this.handleErrorsAsynchronously = builder.handleErrorsAsynchronously;
  }

  public boolean isUnboundedQueues() {
    return queueSize == QUEUE_SIZE_MAX;
  }

  public static class Builder {

    private int numberOfThreads = THREADS_DEFAULT;
    private int queueSize = QUEUE_SIZE_DEFAULT;
    private boolean handleErrorsAsynchronously = true;

    public Builder withNumberOfThreads(int numberOfThreads) {
      this.numberOfThreads = numberOfThreads;
      return this;
    }

    public Builder withUnboundedQueues() {
      this.queueSize = QUEUE_SIZE_MAX;
      return this;
    }

    public Builder withQueueSize(int queueSize) {
      this.queueSize = queueSize;
      return this;
    }

    public Builder handleErrorsSynchronously() {
      handleErrorsAsynchronously = false;
      return this;
    }

    public Builder handleErrorsAsynchronously() {
      handleErrorsAsynchronously = true;
      return this;
    }

    public ConcurrentOddsFeedListenerConfig build() {
      if (numberOfThreads < THREADS_MIN) {
        throw new IllegalStateException("Thread count must be at least " + THREADS_MIN);
      }
      if (queueSize < QUEUE_SIZE_MIN) {
        throw new IllegalStateException("Queue size must be at least " + QUEUE_SIZE_MIN);
      }
      if (numberOfThreads > THREADS_MAX) {
        throw new IllegalStateException("Thread count cannot be greater than " + THREADS_MAX);
      }
      return new ConcurrentOddsFeedListenerConfig(this);
    }
  }
}
