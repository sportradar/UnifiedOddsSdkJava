package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.EventMessage;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EventTaskFactory {

  private final OddsFeedListener customerListener;

  public EventTask createOddsChange(OddsFeedSession session, OddsChange<SportEvent> oddsChanges) {
    Runnable runnable = () -> customerListener.onOddsChange(session, oddsChanges);
    return create(oddsChanges, runnable);
  }

  public EventTask createBetStop(OddsFeedSession session, BetStop<SportEvent> betStop) {
    Runnable runnable = () -> customerListener.onBetStop(session, betStop);
    return create(betStop, runnable);
  }

  public EventTask createBetSettlement(OddsFeedSession session,
      BetSettlement<SportEvent> betSettlement) {
    Runnable runnable = () -> customerListener.onBetSettlement(session, betSettlement);
    return create(betSettlement, runnable);
  }

  public EventTask createRollbackBetSettlement(OddsFeedSession session,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    Runnable runnable = () -> customerListener.onRollbackBetSettlement(session,
        rollbackBetSettlement);
    return create(rollbackBetSettlement, runnable);
  }

  public EventTask createBetCancel(OddsFeedSession session, BetCancel<SportEvent> betCancel) {
    Runnable runnable = () -> customerListener.onBetCancel(session, betCancel);
    return create(betCancel, runnable);
  }

  public EventTask createRollbackBetCancel(OddsFeedSession session,
      RollbackBetCancel<SportEvent> rollbackBetCancel) {
    Runnable runnable = () -> customerListener.onRollbackBetCancel(session, rollbackBetCancel);
    return create(rollbackBetCancel, runnable);
  }

  public EventTask createFixtureChange(OddsFeedSession session,
      FixtureChange<SportEvent> fixtureChange) {
    Runnable runnable = () -> customerListener.onFixtureChange(session, fixtureChange);
    return create(fixtureChange, runnable);
  }

  public EventTask createUnparsableMessage(OddsFeedSession session, byte[] rawMessage,
      SportEvent event) {
    Runnable runnable = () -> customerListener.onUnparseableMessage(session, rawMessage, event);
    return createEventError(event, runnable);
  }

  public EventTask createUnparsableMessage(OddsFeedSession session,
      UnparsableMessage unparsableMessage) {
    return createError(() -> customerListener.onUnparsableMessage(session, unparsableMessage));
  }

  public EventTask createUserUnhandledException(OddsFeedSession session, Exception exception) {
    return createError(() -> customerListener.onUserUnhandledException(session, exception));
  }

  private EventTask create(EventMessage message, Runnable runnable) {
    long eventID = message.getEvent().getId().getId();
    TaskID taskID = TaskID.valueOf(eventID);

    int producerID = message.getProducer().getId();
    long requestID = message.getRequestId();
    RecoveryContext context = RecoveryContext.valueOf(producerID, requestID);

    return EventTask.newEventTask(taskID, runnable, context);
  }

  private EventTask createEventError(SportEvent sportEvent, Runnable runnable) {
    long eventID = sportEvent.getId().getId();
    TaskID taskID = TaskID.valueOf(eventID);
    return EventTask.newEventErrorTask(taskID, runnable);
  }

  private EventTask createError(Runnable runnable) {
    return EventTask.newErrorTask(runnable);
  }
}
