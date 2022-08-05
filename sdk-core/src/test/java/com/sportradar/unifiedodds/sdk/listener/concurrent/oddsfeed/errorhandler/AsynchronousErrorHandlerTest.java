package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AsynchronousErrorHandlerTest {

  private AsynchronousErrorHandler errorHandler;
  @Mock
  private TaskQueuer taskQueuer;
  @Mock
  private OddsFeedSession session;

  @Before
  public void setUp() {
    errorHandler = new AsynchronousErrorHandler(taskQueuer);
  }

  @Test
  public void should_queue_raw_unparsable_message() {
    byte[] rawMessage = "unparseable".getBytes();
    SportEvent sportEvent = Mockito.mock(SportEvent.class);
    errorHandler.onUnparsableMessage(session, rawMessage, sportEvent);

    verify(taskQueuer).queueUnparsableMessage(session, rawMessage, sportEvent);
  }

  @Test
  public void should_queue_unparsable_message() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);

    errorHandler.onUnparsableMessage(session, unparsableMessage);

    verify(taskQueuer).queueUnparsableMessage(session, unparsableMessage);
  }

  @Test
  public void should_queue_exception_handling() {
    Exception exception = mock(Exception.class);

    errorHandler.onUserUnhandledException(session, exception);

    verify(taskQueuer).queueUserUnhandledException(session, exception);
  }
}