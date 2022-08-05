package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import static java.lang.String.format;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class WorkerExecutorServiceFactory {

  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final QueueFactory queueFactory;
  private final ExecutorListener listener;

  public ExecutorService create() {
    ThreadFactory threadFactory = createThreadFactory();
    BlockingQueue<Runnable> queue = queueFactory.create();
    ExecutorService executorService = new ThreadPoolExecutor(1, 1,
        0L, TimeUnit.MILLISECONDS,
        queue,
        threadFactory);
    listener.onCreate(executorService);
    return executorService;
  }

  private ThreadFactory createThreadFactory() {
    String threadName = format("Worker-%02d", threadNumber.getAndIncrement());
    return r -> {
      Thread thread = new Thread(r, threadName);
      listener.onCreate(thread);
      return thread;
    };
  }
}
