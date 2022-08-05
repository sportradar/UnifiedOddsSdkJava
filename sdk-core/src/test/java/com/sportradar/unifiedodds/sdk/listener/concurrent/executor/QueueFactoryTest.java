package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueueFactoryTest {

  private QueueFactory factory;
  @Mock
  private ConcurrentOddsFeedListenerConfig config;

  @Before
  public void setUp() {
    factory = new QueueFactory(config);
  }

  @Test
  public void should_create_fixed_size_queue() {
    when(config.isUnboundedQueues()).thenReturn(false);
    when(config.getQueueSize()).thenReturn(1000);

    BlockingQueue<Runnable> queue = factory.create();

    assertEquals(1000, queue.remainingCapacity());
    assertThat(queue, instanceOf(ArrayBlockingQueue.class));
  }

  @Test
  public void should_create_unbounded_queue() {
    when(config.isUnboundedQueues()).thenReturn(true);

    BlockingQueue<Runnable> queue = factory.create();

    assertEquals(Integer.MAX_VALUE, queue.remainingCapacity());
    assertThat(queue, instanceOf(LinkedBlockingQueue.class));
  }
}