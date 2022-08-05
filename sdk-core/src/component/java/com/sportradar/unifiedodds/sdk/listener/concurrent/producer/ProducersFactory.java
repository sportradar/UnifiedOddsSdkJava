package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.ProducerLatches;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ProducerRandomEventFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducersFactory {

  private final CustomerListenerCallHistory callHistory;
  private final OddsFeedSession session;
  private final SDKGlobalEventsListener concurrentGlobalListener;
  private final OddsFeedListener concurrentOddsFeedListener;
  private final ProducerLatches producerLatches;

  public Producers create() {
    ProducerRandomEventFactory producerRandomEventFactory = new ProducerRandomEventFactory(
        session, concurrentOddsFeedListener);
    ProducerTaskFactory producerTaskFactory = new ProducerTaskFactory(
        concurrentGlobalListener, producerRandomEventFactory);
    ProducerExecutorFactory producerExecutorFactory = new ProducerExecutorFactory(
        producerTaskFactory);
    return new Producers(callHistory,
        producerLatches,
        producerExecutorFactory);
  }
}
