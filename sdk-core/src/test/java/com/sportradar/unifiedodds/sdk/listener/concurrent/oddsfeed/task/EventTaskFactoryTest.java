package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import com.sportradar.utils.URN;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class EventTaskFactoryTest {

  private EventTaskFactory factory;
  @Mock
  private OddsFeedListener customerListener;
  @Mock
  private OddsFeedSession session;
  @Mock
  private SportEvent sportEvent;
  @Mock
  private Producer producer;
  private final IdGenerator idGenerator = new IdGenerator();
  private final int expectedProducerID = idGenerator.randomInt();
  private final long expectedEventID = idGenerator.randomLong();

  @Before
  public void setUp() {
    factory = new EventTaskFactory(customerListener);

    when(producer.getId()).thenReturn(expectedProducerID);
    when(sportEvent.getId()).thenReturn(randomURN());
  }

  @Test
  public void should_create_odds_change_task() {
    OddsChange<SportEvent> event = mock(OddsChange.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createOddsChange(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onOddsChange(session, event);
  }

  @Test
  public void should_create_bet_stop_task() {
    BetStop<SportEvent> event = mock(BetStop.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createBetStop(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onBetStop(session, event);
  }

  @Test
  public void should_create_bet_settlement_task() {
    BetSettlement<SportEvent> event = mock(BetSettlement.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createBetSettlement(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onBetSettlement(session, event);
  }

  @Test
  public void should_create_rollback_bet_settlement_task() {
    RollbackBetSettlement<SportEvent> event = mock(RollbackBetSettlement.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createRollbackBetSettlement(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onRollbackBetSettlement(session, event);
  }

  @Test
  public void should_create_bet_cancel_task() {
    BetCancel<SportEvent> event = mock(BetCancel.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createBetCancel(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onBetCancel(session, event);
  }

  @Test
  public void should_create_rollback_bet_cancel_task() {
    RollbackBetCancel<SportEvent> event = mock(RollbackBetCancel.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createRollbackBetCancel(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onRollbackBetCancel(session, event);
  }

  @Test
  public void should_create_fixure_change_task() {
    FixtureChange<SportEvent> event = mock(FixtureChange.class);
    when(event.getProducer()).thenReturn(producer);
    when(event.getEvent()).thenReturn(sportEvent);

    Task eventTask = factory.createFixtureChange(session, event);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onFixtureChange(session, event);
  }

  @Test
  public void should_create_unparsable_raw_message_task() {
    byte[] rawMessage = "unparseable".getBytes();

    Task eventTask = factory.createUnparsableMessage(session, rawMessage, sportEvent);
    assertEquals(expectedEventID, eventTask.getTaskID().getId());

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onUnparseableMessage(session, rawMessage, sportEvent);
  }

  @Test
  public void should_create_unparsable_message_task() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);

    Task eventTask = factory.createUnparsableMessage(session, unparsableMessage);

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onUnparsableMessage(session, unparsableMessage);
  }

  @Test
  public void should_create_random_task_id_for_unparsable_message_task() {
    UnparsableMessage unparsableMessage = mock(UnparsableMessage.class);

    Set<Long> taskIDs = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      Task eventTask = factory.createUnparsableMessage(session, unparsableMessage);
      taskIDs.add(eventTask.getTaskID().getId());
    }
    assertTrue(taskIDs.size() > 50);
  }

  @Test
  public void should_create_unhandled_exception_task() {
    Exception exception = mock(Exception.class);

    Task eventTask = factory.createUserUnhandledException(session, exception);

    verifyNoInteractions(customerListener);
    eventTask.run();
    verify(customerListener).onUserUnhandledException(session, exception);
  }

  @Test
  public void should_create_random_task_id_for_unhandled_exception_task() {
    Exception exception = mock(Exception.class);

    Set<Long> taskIDs = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      Task eventTask = factory.createUserUnhandledException(session, exception);
      taskIDs.add(eventTask.getTaskID().getId());
    }
    assertTrue(taskIDs.size() > 50);
  }

  private URN randomURN() {
    String strURN = String.format("sr:match:%d", expectedEventID);
    return URN.parse(strURN);
  }
}