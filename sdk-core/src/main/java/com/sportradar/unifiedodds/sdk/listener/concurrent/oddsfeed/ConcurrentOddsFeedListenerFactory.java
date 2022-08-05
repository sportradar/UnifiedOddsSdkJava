package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.executor.ExecutorFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler.ErrorHandler;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler.ErrorHandlerFactory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcurrentOddsFeedListenerFactory {

  private final ConcurrentOddsFeedListenerConfig config;
  private final ExecutorFactory executorFactory;
  private final TaskLifecycleTracker taskLifecycleTracker;

  public OddsFeedListener create(OddsFeedListener customerListener) {
    return createOddsFeedListener(customerListener);
  }

  private ConcurrentOddsFeedListener createOddsFeedListener(
      OddsFeedListener customerListener) {
    TaskQueuer taskQueuer = createTaskQueuer(customerListener);
    ErrorHandler errorHandler = createErrorHandler(customerListener, taskQueuer);
    return new ConcurrentOddsFeedListener(taskQueuer, errorHandler);
  }

  private TaskQueuer createTaskQueuer(OddsFeedListener customerListener) {
    return new TaskQueuerFactory(config, executorFactory, taskLifecycleTracker).create(
        customerListener);
  }

  private ErrorHandler createErrorHandler(OddsFeedListener customerListener,
      TaskQueuer taskQueuer) {
    ErrorHandlerFactory errorHandlerFactory = new ErrorHandlerFactory(config,
        customerListener, taskQueuer);
    return errorHandlerFactory.create();
  }
}
