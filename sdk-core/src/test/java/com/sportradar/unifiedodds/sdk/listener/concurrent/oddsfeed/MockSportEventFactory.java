package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.EventMessage;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.utils.URN;

/**
 * Creates mock events with random IDs. Allows us to control the requestID for each.
 */
@SuppressWarnings("unchecked")
class MockSportEventFactory {

  public OddsChange<SportEvent> oddsChange(RecoveryContext context, int eventID) {
    OddsChange<SportEvent> event = mock(OddsChange.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public BetStop<SportEvent> betStop(RecoveryContext context, int eventID) {
    BetStop<SportEvent> event = mock(BetStop.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public BetSettlement<SportEvent> betSettlement(RecoveryContext context, int eventID) {
    BetSettlement<SportEvent> event = mock(BetSettlement.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public RollbackBetSettlement<SportEvent> rollbackBetSettlement(RecoveryContext context,
      int eventID) {
    RollbackBetSettlement<SportEvent> event = mock(RollbackBetSettlement.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public BetCancel<SportEvent> betCancel(RecoveryContext context, int eventID) {
    BetCancel<SportEvent> event = mock(BetCancel.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public RollbackBetCancel<SportEvent> rollbackBetCancel(RecoveryContext context, int eventID) {
    RollbackBetCancel<SportEvent> event = mock(RollbackBetCancel.class);
    populateEvent(event, context, eventID);
    return event;
  }

  public FixtureChange<SportEvent> fixtureChange(RecoveryContext context, int eventID) {
    FixtureChange<SportEvent> event = mock(FixtureChange.class);
    populateEvent(event, context, eventID);
    return event;
  }

  private void populateEvent(EventMessage eventMessage, RecoveryContext context, int eventID) {
    SportEvent sportEvent = mock(SportEvent.class);
    when(eventMessage.getEvent()).thenReturn(sportEvent);
    when(sportEvent.getId()).thenReturn(URN.parse("sr:match:" + eventID));

    Producer producer = mock(Producer.class);
    when(eventMessage.getProducer()).thenReturn(producer);
    when(producer.getId()).thenReturn(context.getProducerID());

    when(eventMessage.getRequestId()).thenReturn(context.getRequestID());
  }
}
