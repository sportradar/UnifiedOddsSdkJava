package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestExecutorFactory {

  public ExecutorService createSingleThread(String name) {
    int threadCount = 1;
    ThreadFactory threadFactory = r -> new Thread(r, name);
    ExecutorService executorService = new ThreadPoolExecutor(threadCount, threadCount,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(1000),
        threadFactory);
    ExecutorServiceShutdownHook shutdownHook = new ExecutorServiceShutdownHook(executorService);
    Runtime.getRuntime().addShutdownHook(shutdownHook);
    return executorService;
  }
}
