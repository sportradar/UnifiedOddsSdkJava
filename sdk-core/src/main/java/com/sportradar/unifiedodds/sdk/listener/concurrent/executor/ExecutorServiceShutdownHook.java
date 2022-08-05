package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ExecutorServiceShutdownHook extends Thread {

  private static final long TIMEOUT_MILLIS = 200;
  private final ExecutorService executor;

  @Override
  public void run() {
    int executorID = executor.hashCode();

    try {
      if (executor.isTerminated()) {
        log.debug("Executor '{}' already terminated", executorID);
      } else {
        executor.shutdown();
        if (executor.awaitTermination(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
          log.debug("Executor '{}' terminated successfully", executorID);
        } else {
          log.debug("Executor still not terminated after {}ms...", TIMEOUT_MILLIS);
          List<Runnable> notExecuted = executor.shutdownNow();
          log.debug("Executor shut down. There were {} unexecuted tasks", notExecuted.size());
        }
      }
    } catch (InterruptedException e) {
      log.error("Interrupted while shutting down executor!", e);
    }
  }
}
