package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RecordingCustomerListener implements OddsFeedListener {

  private final CountDownLatch latch;

  @Getter
  private final Queue<CustomerListenerEvent> activityLog = new ConcurrentLinkedQueue<>();

  @Override
  public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChanges) {
    record("onOddsChange", oddsChanges.getEvent());
  }

  @Override
  public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {
    record("onBetStop", betStop.getEvent());
  }

  @Override
  public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> clearBets) {
    record("onBetSettlement", clearBets.getEvent());
  }

  @Override
  public void onRollbackBetSettlement(OddsFeedSession sender,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    record("onRollbackBetSettlement", rollbackBetSettlement.getEvent());
  }

  @Override
  public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {
    record("onBetCancel", betCancel.getEvent());
  }

  @Override
  public void onRollbackBetCancel(OddsFeedSession sender,
      RollbackBetCancel<SportEvent> rbBetCancel) {
    record("onRollbackBetCancel", rbBetCancel.getEvent());
  }

  @Override
  public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {
    record("onFixtureChange", fixtureChange.getEvent());
  }

  @Override
  public void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event) {
    record("onUnparseableMessage", event);
  }

  @Override
  public void onUnparsableMessage(OddsFeedSession sender, UnparsableMessage unparsableMessage) {
    record("onUnparsableMessage", unparsableMessage.getEvent());
  }

  @Override
  public void onUserUnhandledException(OddsFeedSession sender, Exception exception) {
    record("onUserUnhandledException", null);
  }

  private void record(String method, SportEvent sportEvent) {
    String threadName = Thread.currentThread().getName();
    CustomerListenerEvent event = new CustomerListenerEvent(threadName, method, sportEvent);
    activityLog.add(event);

    if (sportEvent == null) {
      log.info("{} : {}()", threadName, method);
    } else {
      log.info("{} : {}({})", threadName, method, sportEvent.getId());
    }
    latch.countDown();
  }
}
