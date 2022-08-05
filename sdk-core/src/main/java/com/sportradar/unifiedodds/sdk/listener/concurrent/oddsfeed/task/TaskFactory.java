package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TaskFactory {

  private final EventTaskFactory eventTaskFactory;
  private final TrackedTaskFactory trackedTaskFactory;

  public TrackedTask createOddsChange(OddsFeedSession session, OddsChange<SportEvent> oddsChanges) {
    return track(eventTaskFactory.createOddsChange(session, oddsChanges));
  }

  public TrackedTask createBetStop(OddsFeedSession session, BetStop<SportEvent> betStop) {
    return track(eventTaskFactory.createBetStop(session, betStop));
  }

  public TrackedTask createBetSettlement(OddsFeedSession session,
      BetSettlement<SportEvent> betSettlement) {
    return track(eventTaskFactory.createBetSettlement(session, betSettlement));
  }

  public TrackedTask createRollbackBetSettlement(OddsFeedSession session,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    return track(eventTaskFactory.createRollbackBetSettlement(session, rollbackBetSettlement));
  }

  public TrackedTask createBetCancel(OddsFeedSession session, BetCancel<SportEvent> betCancel) {
    return track(eventTaskFactory.createBetCancel(session, betCancel));
  }

  public TrackedTask createRollbackBetCancel(OddsFeedSession session,
      RollbackBetCancel<SportEvent> rollbackBetCancel) {
    return track(eventTaskFactory.createRollbackBetCancel(session, rollbackBetCancel));
  }

  public TrackedTask createFixtureChange(OddsFeedSession session,
      FixtureChange<SportEvent> fixtureChange) {
    return track(eventTaskFactory.createFixtureChange(session, fixtureChange));
  }

  public EventTask createUnparsableMessage(OddsFeedSession session, byte[] rawMessage,
      SportEvent event) {
    return eventTaskFactory.createUnparsableMessage(session, rawMessage, event);
  }

  public EventTask createUnparsableMessage(OddsFeedSession session,
      UnparsableMessage unparsableMessage) {
    return eventTaskFactory.createUnparsableMessage(session, unparsableMessage);
  }

  public EventTask createUserUnhandledException(OddsFeedSession session, Exception exception) {
    return eventTaskFactory.createUserUnhandledException(session, exception);
  }

  private TrackedTask track(EventTask eventTask) {
    return trackedTaskFactory.create(eventTask);
  }
}
