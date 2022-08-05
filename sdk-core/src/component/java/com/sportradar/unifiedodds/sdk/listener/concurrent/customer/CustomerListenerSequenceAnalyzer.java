package com.sportradar.unifiedodds.sdk.listener.concurrent.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

public class CustomerListenerSequenceAnalyzer {

  private final List<CustomerListenerCallEntry> callHistory;
  private final CustomerListenerCallHistoryQuery query;
  private final int onRecoveryInitiatedIndex;
  private final int onProducerUpIndex;
  private final Set<Integer> eventIndexes;

  public CustomerListenerSequenceAnalyzer(CustomerListenerCallHistory callHistory) {
    this.callHistory = callHistory.getCallHistory();
    this.query = new CustomerListenerCallHistoryQuery(callHistory);
    this.onRecoveryInitiatedIndex = query.findRecoveryInitiatedIndex();
    this.onProducerUpIndex = query.findProducerUpIndex();
    this.eventIndexes = query.findSportEventIndexes();
  }

  public void verifyRecoveryInitiatedWasFirstCall() {
    assertEquals("Expected onRecoveryInitiated to be called first!", 0,
        onRecoveryInitiatedIndex);
  }

  public void verifyProducerUpWasCalledLast() {
    int expected = query.totalCallsProcessed();
    assertEquals("Expected Producer Up to be called last!", expected, onProducerUpIndex);
  }

  public void verifyAllSportEventsHappenedBetweenRecoveryInitiatedAndProducerUp() {
    eventIndexes.forEach(index -> {
      if (index < onRecoveryInitiatedIndex || index > onProducerUpIndex) {
        fail("Expected all events to be called between onRecoveryInitiated and Producer Up!");
      }
    });
  }

  public void verifyAllCallsHappened(int eventsSent) {
    int totalEventsExpected = eventsSent;
    totalEventsExpected += onRecoveryInitiatedIndex >= 0 ? 1 : 0;
    totalEventsExpected += onProducerUpIndex >= 0 ? 1 : 0;

    int totalEvents = callHistory.size();

    assertEquals("Expected all listener calls happened", totalEventsExpected,
        totalEvents);
  }
}
