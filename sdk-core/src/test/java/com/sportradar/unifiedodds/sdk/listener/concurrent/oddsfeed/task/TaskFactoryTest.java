package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
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
public class TaskFactoryTest {

  private TaskFactory taskFactory;
  @Mock
  private EventTaskFactory eventTaskFactory;
  @Mock
  private TrackedTaskFactory trackedTaskFactory;
  @Mock
  private OddsFeedSession session;
  @Mock
  private EventTask expectedEventTask;
  @Mock
  private TrackedTask expectedTrackedTask;

  @Before
  public void setUp() {
    taskFactory = new TaskFactory(eventTaskFactory, trackedTaskFactory);
  }

  @Test
  public void should_create_trackable_odds_change_task() {
    OddsChange<SportEvent> event = mock(OddsChange.class);
    when(eventTaskFactory.createOddsChange(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createOddsChange(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_bet_stop_task() {
    BetStop<SportEvent> event = mock(BetStop.class);
    when(eventTaskFactory.createBetStop(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createBetStop(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_bet_settlement_task() {
    BetSettlement<SportEvent> event = mock(BetSettlement.class);
    when(eventTaskFactory.createBetSettlement(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createBetSettlement(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_rollback_bet_settlement_task() {
    RollbackBetSettlement<SportEvent> event = mock(RollbackBetSettlement.class);
    when(eventTaskFactory.createRollbackBetSettlement(session, event)).thenReturn(
        expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createRollbackBetSettlement(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_bet_cancel_task() {
    BetCancel<SportEvent> event = mock(BetCancel.class);
    when(eventTaskFactory.createBetCancel(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createBetCancel(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_rollback_bet_cancel_task() {
    RollbackBetCancel<SportEvent> event = mock(RollbackBetCancel.class);
    when(eventTaskFactory.createRollbackBetCancel(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createRollbackBetCancel(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_trackable_fixure_change_task() {
    FixtureChange<SportEvent> event = mock(FixtureChange.class);
    when(eventTaskFactory.createFixtureChange(session, event)).thenReturn(expectedEventTask);
    when(trackedTaskFactory.create(expectedEventTask)).thenReturn(expectedTrackedTask);

    TrackedTask actualTrackedTask = taskFactory.createFixtureChange(session, event);
    assertSame(actualTrackedTask, expectedTrackedTask);
  }

  @Test
  public void should_create_untracked_unparsable_raw_message_task() {
    SportEvent event = mock(SportEvent.class);
    byte[] rawMessage = "unparseable".getBytes();
    when(eventTaskFactory.createUnparsableMessage(session, rawMessage, event)).thenReturn(
        expectedEventTask);

    EventTask actualEventTask = taskFactory.createUnparsableMessage(session, rawMessage, event);
    assertSame(expectedEventTask, actualEventTask);
    verifyNoInteractions(trackedTaskFactory);
  }

  @Test
  public void should_create_untracked_unparsable_message_task() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);
    when(eventTaskFactory.createUnparsableMessage(session, unparsableMessage)).thenReturn(
        expectedEventTask);

    EventTask actualEventTask = taskFactory.createUnparsableMessage(session, unparsableMessage);
    assertSame(expectedEventTask, actualEventTask);
    verifyNoInteractions(trackedTaskFactory);
  }

  @Test
  public void should_create_untracked_unhandled_exception_task() {
    Exception exception = mock(Exception.class);
    when(eventTaskFactory.createUserUnhandledException(session, exception)).thenReturn(
        expectedEventTask);

    EventTask actualEventTask = taskFactory.createUserUnhandledException(session, exception);
    assertSame(expectedEventTask, actualEventTask);
    verifyNoInteractions(trackedTaskFactory);
  }
}