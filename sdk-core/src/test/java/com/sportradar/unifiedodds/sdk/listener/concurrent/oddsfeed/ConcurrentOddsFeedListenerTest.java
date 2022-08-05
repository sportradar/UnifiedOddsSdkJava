package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler.ErrorHandler;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ConcurrentOddsFeedListenerTest {

  private ConcurrentOddsFeedListener listener;
  @Mock
  private TaskQueuer taskQueuer;
  @Mock
  private ErrorHandler errorHandler;
  @Mock
  private OddsFeedSession session;


  @Before
  public void setUp() {
    listener = new ConcurrentOddsFeedListener(taskQueuer, errorHandler);
  }

  @Test
  public void should_queue_odds_change() {
    OddsChange<SportEvent> event = mock(OddsChange.class);

    listener.onOddsChange(session, event);

    verify(taskQueuer).queueOddsChange(session, event);
  }

  @Test
  public void should_queue_bet_stop() {
    BetStop<SportEvent> event = mock(BetStop.class);

    listener.onBetStop(session, event);

    verify(taskQueuer).queueBetStop(session, event);
  }

  @Test
  public void should_queue_bet_settlement() {
    BetSettlement<SportEvent> event = mock(BetSettlement.class);

    listener.onBetSettlement(session, event);

    verify(taskQueuer).queueBetSettlement(session, event);
  }

  @Test
  public void should_queue_rollback_bet_settlement() {
    RollbackBetSettlement<SportEvent> event = mock(RollbackBetSettlement.class);

    listener.onRollbackBetSettlement(session, event);

    verify(taskQueuer).queueRollbackBetSettlement(session, event);
  }

  @Test
  public void should_queue_bet_cancel() {
    BetCancel<SportEvent> event = mock(BetCancel.class);

    listener.onBetCancel(session, event);

    verify(taskQueuer).queueBetCancel(session, event);
  }

  @Test
  public void should_queue_rollback_bet_cancel() {
    RollbackBetCancel<SportEvent> event = mock(RollbackBetCancel.class);

    listener.onRollbackBetCancel(session, event);

    verify(taskQueuer).queueRollbackBetCancel(session, event);
  }

  @Test
  public void should_queue_fixture_change() {
    FixtureChange<SportEvent> event = mock(FixtureChange.class);

    listener.onFixtureChange(session, event);

    verify(taskQueuer).queueFixtureChange(session, event);
  }

  @Test
  public void should_queue_raw_unparseable_message() {
    byte[] rawMessage = "test".getBytes();
    SportEvent event = mock(SportEvent.class);

    listener.onUnparseableMessage(session, rawMessage, event);
  }

  @Test
  public void should_queue_unparseable_message() {
    UnparsableMessage message = mock(UnparsableMessage.class);

    listener.onUnparsableMessage(session, message);
  }

  @Test
  public void should_queue_unhandled_exception() {
    Exception exception = mock(Exception.class);

    listener.onUserUnhandledException(session, exception);
  }
}