package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorHandlerFactory {

  private final ConcurrentOddsFeedListenerConfig config;
  private final OddsFeedListener customerListener;
  private final TaskQueuer taskQueuer;

  public ErrorHandler create() {
    if (config.isHandleErrorsAsynchronously()) {
      return new AsynchronousErrorHandler(taskQueuer);
    } else {
      return new SynchronousErrorHandler(customerListener);
    }
  }
}
