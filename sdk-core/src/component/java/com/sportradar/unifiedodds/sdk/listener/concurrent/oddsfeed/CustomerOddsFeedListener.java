package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.ListenerType;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallEntry;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.EventMessage;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.utils.URN;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * This is both a SDKGlobalEventsListener and an OddsFeedListener as we need to verify that
 * Producer Up happens <bold>after</bold> all events tasks have completed.
 *
 * All calls to SDKGlobalEventsListener methods are made by a Producer.
 * All calls to OddsFeedListener methods are made by worker threads.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class CustomerOddsFeedListener implements OddsFeedListener {

  private final ProcessingDelay delayer = new ProcessingDelay();
  private final CustomerListenerCallHistory callHistory;

  @Override
  public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChanges) {
    handleEventCall("onOddsChange", oddsChanges);
  }

  @Override
  public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {
    handleEventCall("onBetStop", betStop);
  }

  @Override
  public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> betSettlement) {
    handleEventCall("onBetSettlement", betSettlement);
  }

  @Override
  public void onRollbackBetSettlement(OddsFeedSession sender,
      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
    handleEventCall("onRollbackBetSettlement", rollbackBetSettlement);
  }

  @Override
  public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {
    handleEventCall("onBetCancel", betCancel);
  }

  @Override
  public void onRollbackBetCancel(OddsFeedSession sender,
      RollbackBetCancel<SportEvent> rollbackBetCancel) {
    handleEventCall("onRollbackBetCancel", rollbackBetCancel);
  }

  @Override
  public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {
    handleEventCall("onFixtureChange", fixtureChange);
  }

  @Override
  public void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event) {
    log.info("onUnparseableMessage({})", event.getId());

    callHistory.save(CustomerListenerCallEntry.builder()
        .listenerType(ListenerType.OddsFeed)
        .eventID(event.getId())
        .method("onUnparseableMessage"));
  }

  private void handleEventCall(String method, EventMessage eventMessage) {
    int producerID = eventMessage.getProducer().getId();
    long requestID = eventMessage.getRequestId();
    URN eventID = eventMessage.getEvent().getId();
    log.info("Producer {}, Request {} : {}({})", producerID, requestID, method, eventID);

    callHistory.save(CustomerListenerCallEntry.builder()
        .listenerType(ListenerType.OddsFeed)
        .producerID(producerID)
        .requestID(requestID)
        .eventID(eventID)
        .method(method));

    delayer.delay();
  }
}