package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig.newConcurrentOddsFeedListenerConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConcurrentOddsFeedListenerConfigTest {

  private ConcurrentOddsFeedListenerConfig config;

  @Test
  public void should_get_number_of_threads() {
    config = builder()
        .withNumberOfThreads(5)
        .build();

    assertEquals(5, config.getNumberOfThreads());
  }

  @Test
  public void should_have_number_of_threads_by_default() {
    config = builder().build();

    assertEquals(10, config.getNumberOfThreads());
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_thread_number_too_small() {
    config = builder()
        .withNumberOfThreads(4)
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_thread_number_too_large() {
    config = builder()
        .withNumberOfThreads(31)
        .build();
  }

  @Test
  public void should_get_queue_size() {
    config = builder()
        .withQueueSize(1000)
        .build();

    assertEquals(1000, config.getQueueSize());
  }

  @Test
  public void should_set_unbounded_queue_size() {
    config = builder()
        .withUnboundedQueues()
        .build();

    assertTrue(config.isUnboundedQueues());
  }

  @Test
  public void should_not_have_unbounded_queue_size() {
    config = builder().build();

    assertFalse(config.isUnboundedQueues());
  }

  @Test
  public void should_have_queue_size_by_default() {
    config = builder().build();

    assertEquals(10000, config.getQueueSize());
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_queue_size_too_small() {
    config = builder()
        .withQueueSize(499)
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_queue_size_overflows() {
    config = builder()
        .withQueueSize(Integer.MAX_VALUE + 1)
        .build();
  }

  @Test
  public void should_allow_max_queue_size() {
    config = builder()
        .withQueueSize(Integer.MAX_VALUE)
        .build();

    assertEquals(Integer.MAX_VALUE, config.getQueueSize());
  }

  @Test
  public void should_handle_errors_on_calling_thread() {
    config = builder()
        .handleErrorsSynchronously()
        .build();

    assertFalse(config.isHandleErrorsAsynchronously());
  }

  @Test
  public void should_handle_errors_on_worker_thread() {
    config = builder()
        .handleErrorsAsynchronously()
        .build();

    assertTrue(config.isHandleErrorsAsynchronously());
  }

  @Test
  public void should_handle_errors_on_worker_thread_by_default() {
    config = builder().build();

    assertTrue(config.isHandleErrorsAsynchronously());
  }

  private ConcurrentOddsFeedListenerConfig.Builder builder() {
    return newConcurrentOddsFeedListenerConfig();
  }
}