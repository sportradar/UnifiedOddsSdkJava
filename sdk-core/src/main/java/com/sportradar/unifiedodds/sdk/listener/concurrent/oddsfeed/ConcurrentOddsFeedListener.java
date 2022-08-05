package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ConcurrentOddsFeedListener implements OddsFeedListener {

  private final TaskQueuer taskQueuer;
  private final ErrorHandler errorHandler;

  @Override
  public void onOddsChange(OddsFeedSession session, OddsChange<SportEvent> oddsChanges) {
    taskQueuer.queueOddsChange(session, oddsChanges);
  }

  @Override
  public void onBetStop(OddsFeedSession session, BetStop<SportEvent> betStop) {
    taskQueuer.queueBetStop(session, betStop);
  }

  @Override
  public void onBetSettlement(OddsFeedSession session, BetSettlement<SportEvent> clearBets) {
    taskQueuer.queueBetSettlement(session, clearBets);
  }

  @Override
  public void onRollbackBetSettlement(OddsFeedSession session,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    taskQueuer.queueRollbackBetSettlement(session, rollbackBetSettlement);
  }

  @Override
  public void onBetCancel(OddsFeedSession session, BetCancel<SportEvent> betCancel) {
    taskQueuer.queueBetCancel(session, betCancel);
  }

  @Override
  public void onRollbackBetCancel(OddsFeedSession session,
      RollbackBetCancel<SportEvent> rollbackBetCancel) {
    taskQueuer.queueRollbackBetCancel(session, rollbackBetCancel);
  }

  @Override
  public void onFixtureChange(OddsFeedSession session, FixtureChange<SportEvent> fixtureChange) {
    taskQueuer.queueFixtureChange(session, fixtureChange);
  }

  @Override
  public void onUnparseableMessage(OddsFeedSession session, byte[] rawMessage, SportEvent event) {
    errorHandler.onUnparsableMessage(session, rawMessage, event);
  }

  @Override
  public void onUnparsableMessage(OddsFeedSession session, UnparsableMessage unparsableMessage) {
    errorHandler.onUnparsableMessage(session, unparsableMessage);
  }

  @Override
  public void onUserUnhandledException(OddsFeedSession session, Exception exception) {
    errorHandler.onUserUnhandledException(session, exception);
  }
}
