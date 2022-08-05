package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import static org.junit.Assert.fail;

import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerSequenceAnalyzer;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerExpectations;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.ProducerLatches;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.listener.concurrent.producer.ProducerSendContext.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProducerExpectations {

  private final ProducerID producerID;
  private final CustomerListenerCallHistory allCallHistory;
  private final ProducerLatches producerLatches;
  private final ProducerExecutor executor;
  private long currentRequestID;

  public ProducerExpectations initiateNewRecovery() {
    executor.initiateRecovery(nextRecoveryContext());
    return this;
  }

  public ProducerExpectations recover(Builder builder) {
    ProducerSendContext sendContext = builder.build();
    executor.sendRandomEventsAndProducerUp(currentContext(),
        sendContext.getTotalEvents(),
        sendContext.getProducerUpIndex());
    return this;
  }

  public ProducerExpectations awaitProducerUp() {
    if (!producerLatches.awaitProducerUp(producerID)) {
      fail("Producer " + producerID.getId() + " : Failed to wait for ProducerUp!");
    }
    return this;
  }

  public CustomerListenerExpectations verify() {
    return verifyFor(currentRequestID);
  }

  public CustomerListenerExpectations verifyFor(long requestID) {
    RecoveryContext context = contextForRequestID(requestID);
    CustomerListenerCallHistory producerCallHistory = allCallHistory.forRecoveryRequest(context);
    CustomerListenerSequenceAnalyzer sequenceAnalyzer = new CustomerListenerSequenceAnalyzer(
        producerCallHistory);
    return new CustomerListenerExpectations(sequenceAnalyzer);
  }

  public void dumpCallHistory() {
    log.info("Customer Listeners call history for Producer {}:", producerID.getId());
    allCallHistory.log();
  }

  public void dumpCallHistory(long requestID) {
    log.info("Customer Listeners call history for Producer {} Request {}:",
        producerID.getId(), requestID);
    RecoveryContext context = contextForRequestID(requestID);
    CustomerListenerCallHistory producerCallHistory = allCallHistory.forRecoveryRequest(context);
    producerCallHistory.log();
  }

  public ProducerExpectations and() {
    return this;
  }

  public ProducerExpectations but() {
    return this;
  }

  public ProducerExpectations then() {
    return this;
  }

  public ProducerExpectations which() {
    return this;
  }

  public ProducerExpectations should() {
    return this;
  }

  private RecoveryContext currentContext() {
    return RecoveryContext.valueOf(producerID.getId(), currentRequestID);
  }

  private RecoveryContext nextRecoveryContext() {
    return RecoveryContext.valueOf(producerID.getId(), ++currentRequestID);
  }

  private RecoveryContext contextForRequestID(long requestID) {
    return RecoveryContext.valueOf(producerID.getId(), requestID);
  }
}
