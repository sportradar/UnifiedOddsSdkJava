package com.sportradar.unifiedodds.sdk.listener.concurrent.customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class CustomerListenerCallHistoryQuery {

  private final List<CustomerListenerCallEntry> callHistory;

  CustomerListenerCallHistoryQuery(CustomerListenerCallHistory callHistory) {
    this.callHistory = callHistory.getCallHistory();
  }

  public int findRecoveryInitiatedIndex() {
    int callIndex = 0;
    int onRecoveryInitiatedIndex = -1;

    for (CustomerListenerCallEntry call : callHistory) {
      if (call.isGlobalCall() && call.getMethod().equals("onRecoveryInitiated")) {
        onRecoveryInitiatedIndex = callIndex;
        break;
      }
      callIndex++;
    }
    return onRecoveryInitiatedIndex;
  }

  public int findProducerUpIndex() {
    int callIndex = 0;
    int producerUpIndex = -1;

    for (CustomerListenerCallEntry call : callHistory) {
      if (call.isGlobalCall() && call.isProducerUp()) {
        producerUpIndex = callIndex;
        break;
      }
      callIndex++;
    }
    return producerUpIndex;
  }

  public Set<Integer> findSportEventIndexes() {
    final AtomicInteger callIndex = new AtomicInteger(-1);
    final Set<Integer> eventIndexes = new HashSet<>();

    callHistory.forEach(call -> {
      int currentIndex = callIndex.getAndIncrement();
      if (call.isOddsFeedCall()) {
        eventIndexes.add(currentIndex);
      }
    });
    return eventIndexes;
  }

  public int totalCallsProcessed() {
    return callHistory.size() - 1;
  }
}
