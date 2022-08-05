package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SynchronousErrorHandlerTest {

  private SynchronousErrorHandler errorHandler;
  @Mock
  private OddsFeedListener customerListener;
  @Mock
  private OddsFeedSession session;

  @Before
  public void setUp() {
    errorHandler = new SynchronousErrorHandler(customerListener);
  }

  @Test
  public void should_handle_raw_unparsable_message() {
    byte[] rawMessage = "unparseable".getBytes();
    SportEvent event = mock(SportEvent.class);
    errorHandler.onUnparsableMessage(session, rawMessage, event);

    verify(customerListener).onUnparseableMessage(session, rawMessage, event);
  }

  @Test
  public void should_handle_unparsable_message() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);

    errorHandler.onUnparsableMessage(session, unparsableMessage);

    verify(customerListener).onUnparsableMessage(session, unparsableMessage);
  }

  @Test
  public void should_handle_exception_handling() {
    Exception exception = mock(Exception.class);

    errorHandler.onUserUnhandledException(session, exception);

    verify(customerListener).onUserUnhandledException(session, exception);
  }
}