package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerFactoryTest {

  private ErrorHandlerFactory factory;
  @Mock
  private ConcurrentOddsFeedListenerConfig config;
  @Mock
  private OddsFeedListener customerListener;
  @Mock
  private TaskQueuer taskQueuer;

  @Before
  public void setUp() {
    factory = new ErrorHandlerFactory(config, customerListener, taskQueuer);
  }

  @Test
  public void should_create_synchronous_error_handler() {
    when(config.isHandleErrorsAsynchronously()).thenReturn(true);

    ErrorHandler errorHandler = factory.create();

    assertThat(errorHandler, instanceOf(AsynchronousErrorHandler.class));
  }

  @Test
  public void should_create_asynchronous_error_handler() {
    when(config.isHandleErrorsAsynchronously()).thenReturn(false);

    ErrorHandler errorHandler = factory.create();

    assertThat(errorHandler, instanceOf(SynchronousErrorHandler.class));
  }
}