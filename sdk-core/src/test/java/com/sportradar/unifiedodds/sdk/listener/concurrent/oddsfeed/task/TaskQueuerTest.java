package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
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
public class TaskQueuerTest {

  private TaskQueuer taskQueuer;
  @Mock
  private TaskFactory taskFactory;
  @Mock
  private TaskAllocator taskAllocator;
  @Mock
  private OddsFeedSession session;
  @Mock
  private TrackedTask trackedTask;
  @Mock
  private EventTask eventTask;

  @Before
  public void setUp() {
    taskQueuer = new TaskQueuer(taskFactory, taskAllocator);
  }

  @Test
  public void should_queue_tracked_odds_change_task() {
    OddsChange<SportEvent> event = mock(OddsChange.class);
    when(taskFactory.createOddsChange(session, event)).thenReturn(trackedTask);

    taskQueuer.queueOddsChange(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_bet_stop_task() {
    BetStop<SportEvent> event = mock(BetStop.class);
    when(taskFactory.createBetStop(session, event)).thenReturn(trackedTask);

    taskQueuer.queueBetStop(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_bet_settlement_task() {
    BetSettlement<SportEvent> event = mock(BetSettlement.class);
    when(taskFactory.createBetSettlement(session, event)).thenReturn(trackedTask);

    taskQueuer.queueBetSettlement(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_rollback_bet_settlement_task() {
    RollbackBetSettlement<SportEvent> event = mock(RollbackBetSettlement.class);
    when(taskFactory.createRollbackBetSettlement(session, event)).thenReturn(trackedTask);

    taskQueuer.queueRollbackBetSettlement(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_bet_cancel_task() {
    BetCancel<SportEvent> event = mock(BetCancel.class);
    when(taskFactory.createBetCancel(session, event)).thenReturn(trackedTask);

    taskQueuer.queueBetCancel(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_rollback_bet_cancel_task() {
    RollbackBetCancel<SportEvent> event = mock(RollbackBetCancel.class);
    when(taskFactory.createRollbackBetCancel(session, event)).thenReturn(trackedTask);

    taskQueuer.queueRollbackBetCancel(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_tracked_fixture_change_task() {
    FixtureChange<SportEvent> event = mock(FixtureChange.class);
    when(taskFactory.createFixtureChange(session, event)).thenReturn(trackedTask);

    taskQueuer.queueFixtureChange(session, event);

    verify(taskAllocator).allocate(trackedTask);
  }

  @Test
  public void should_queue_untracked_unparsable_raw_message_task() {
    byte[] rawMessage = "unparseable".getBytes();
    SportEvent event = mock(SportEvent.class);
    when(taskFactory.createUnparsableMessage(session, rawMessage, event)).thenReturn(eventTask);

    taskQueuer.queueUnparsableMessage(session, rawMessage, event);

    verify(taskAllocator).allocate(eventTask);
  }

  @Test
  public void should_queue_untracked_unparsable_message_task() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);
    when(taskFactory.createUnparsableMessage(session, unparsableMessage)).thenReturn(eventTask);

    taskQueuer.queueUnparsableMessage(session, unparsableMessage);

    verify(taskAllocator).allocate(eventTask);
  }

  @Test
  public void should_queue_untracked_unhandled_exception_task() {
    Exception exception = mock(Exception.class);
    when(taskFactory.createUserUnhandledException(session, exception)).thenReturn(eventTask);

    taskQueuer.queueUserUnhandledException(session, exception);

    verify(taskAllocator).allocate(eventTask);
  }
}