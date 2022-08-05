package com.sportradar.unifiedodds.sdk.listener.concurrent.customer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerListenerExpectations {

  private final CustomerListenerSequenceAnalyzer analyzer;

  public CustomerListenerExpectations recoveryInitiatedWasInvokedFirst() {
    analyzer.verifyRecoveryInitiatedWasFirstCall();
    return this;
  }

  public CustomerListenerExpectations allEventsWereProcessed(int eventsToProcess) {
    analyzer.verifyAllCallsHappened(eventsToProcess);
    return this;
  }

  public CustomerListenerExpectations producerUpWasDeferredSuccessfully() {
    analyzer.verifyProducerUpWasCalledLast();
    analyzer.verifyAllSportEventsHappenedBetweenRecoveryInitiatedAndProducerUp();
    return this;
  }

  public CustomerListenerExpectations that() {
    return this;
  }

  public CustomerListenerExpectations and() {
    return this;
  }
}
