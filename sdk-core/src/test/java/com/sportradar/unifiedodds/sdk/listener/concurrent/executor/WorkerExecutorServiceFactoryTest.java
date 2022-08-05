package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig.newConcurrentOddsFeedListenerConfig;
import static org.junit.Assert.assertNotNull;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.concurrent.ExecutorService;
import org.junit.Before;
import org.junit.Test;

public class WorkerExecutorServiceFactoryTest {

  private WorkerExecutorServiceFactory factory;

  @Before
  public void setUp() {
    ConcurrentOddsFeedListenerConfig config = newConcurrentOddsFeedListenerConfig().build();
    factory = new WorkerExecutorServiceFactory(new QueueFactory(config), ExecutorListener.NOP);
  }

  @Test
  public void should_create_executor_service() {
    ExecutorService executorService = factory.create();

    assertNotNull(executorService);
  }
}