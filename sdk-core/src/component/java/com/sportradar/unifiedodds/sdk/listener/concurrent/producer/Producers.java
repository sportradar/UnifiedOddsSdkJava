package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import com.sportradar.unifiedodds.sdk.listener.concurrent.customer.CustomerListenerCallHistory;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.ProducerLatches;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Producers {

  private final Map<ProducerID, ProducerExpectations> producers = new ConcurrentHashMap<>();
  private final CustomerListenerCallHistory allCallHistory;
  private final ProducerLatches producerLatches;
  private final ProducerExecutorFactory producerExecutorFactory;

  public ProducerExpectations getOrCreate(int producerID) {
    ProducerID id = ProducerID.valueOf(producerID);

    if (!producers.containsKey(id)) {
      ProducerExecutor executor = producerExecutorFactory.create(id);
      ProducerExpectations expectations = new ProducerExpectations(
          id,
          allCallHistory,
          producerLatches,
          executor);
      producers.put(id, expectations);
    }
    return producers.get(id);
  }
}
