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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TaskQueuer {

  private final TaskFactory taskFactory;
  private final TaskAllocator taskAllocator;

  public void queueOddsChange(OddsFeedSession session, OddsChange<SportEvent> oddsChanges) {
    allocateToQueue(taskFactory.createOddsChange(session, oddsChanges));
  }

  public void queueBetStop(OddsFeedSession session, BetStop<SportEvent> betStop) {
    allocateToQueue(taskFactory.createBetStop(session, betStop));
  }

  public void queueBetSettlement(OddsFeedSession session, BetSettlement<SportEvent> betSettlement) {
    allocateToQueue(taskFactory.createBetSettlement(session, betSettlement));
  }

  public void queueRollbackBetSettlement(OddsFeedSession session,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    allocateToQueue(taskFactory.createRollbackBetSettlement(session, rollbackBetSettlement));
  }

  public void queueBetCancel(OddsFeedSession session, BetCancel<SportEvent> betCancel) {
    allocateToQueue(taskFactory.createBetCancel(session, betCancel));
  }

  public void queueRollbackBetCancel(OddsFeedSession session,
      RollbackBetCancel<SportEvent> rollbackBetCancel) {
    allocateToQueue(taskFactory.createRollbackBetCancel(session, rollbackBetCancel));
  }

  public void queueFixtureChange(OddsFeedSession session, FixtureChange<SportEvent> fixtureChange) {
    allocateToQueue(taskFactory.createFixtureChange(session, fixtureChange));
  }

  public void queueUnparsableMessage(OddsFeedSession session, byte[] rawMessage,
      SportEvent event) {
    allocateToQueue(taskFactory.createUnparsableMessage(session, rawMessage, event));
  }

  public void queueUnparsableMessage(OddsFeedSession session,
      UnparsableMessage unparsableMessage) {
    allocateToQueue(taskFactory.createUnparsableMessage(session, unparsableMessage));
  }

  public void queueUserUnhandledException(OddsFeedSession session, Exception exception) {
    allocateToQueue(taskFactory.createUserUnhandledException(session, exception));
  }

  private void allocateToQueue(EventTask task) {
    taskAllocator.allocate(task);
  }

  private void allocateToQueue(TrackedTask task) {
    taskAllocator.allocate(task);
  }
}
