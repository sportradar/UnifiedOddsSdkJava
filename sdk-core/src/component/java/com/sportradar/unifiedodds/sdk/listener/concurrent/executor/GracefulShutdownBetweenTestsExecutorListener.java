package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GracefulShutdownBetweenTestsExecutorListener implements ExecutorListener {

  private final List<ExecutorService> executors = new ArrayList<>();
  private final List<Thread> threads = new ArrayList<>();

  public void onCreate(ExecutorService executorService) {
    executors.add(executorService);
  }

  public void onCreate(Thread thread) {
    threads.add(thread);
  }

  public void shutdownGracefully() {
    log.info("Shutting down all executors");
    executors.forEach(ExecutorService::shutdown);

    waitForAllThreadsToComplete();

    log.info("Waiting for all executors to finish");
    executors.forEach(executor -> new ExecutorServiceShutdownHook(executor).run());
  }

  private void waitForAllThreadsToComplete() {
    log.info("Waiting for all test threads to finish");
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        log.error("Error waiting on thread!", e);
      }
    });
  }
}
