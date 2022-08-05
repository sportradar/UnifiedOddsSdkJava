package com.sportradar.unifiedodds.sdk.listener.concurrent.customer;

import static java.util.stream.Collectors.toList;

import com.sportradar.unifiedodds.sdk.listener.concurrent.ListenerType;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerListenerCallHistory {

  @Getter
  private final List<CustomerListenerCallEntry> callHistory;

  public CustomerListenerCallHistory() {
    this(new ArrayList<>());
  }

  private CustomerListenerCallHistory(List<CustomerListenerCallEntry> callHistory) {
    this.callHistory = callHistory;
  }

  public synchronized void save(CustomerListenerCallEntry.CustomerListenerCallEntryBuilder builder) {
    callHistory.add(builder.build());
  }

  public synchronized CustomerListenerCallHistory forRecoveryRequest(RecoveryContext context) {
    int producerID = context.getProducerID();
    long request = context.getRequestID();

    List<CustomerListenerCallEntry> producerCallHistory = this.callHistory
        .stream()
        .filter(call -> call.getProducerID() == producerID)
        // when we filter based on requestID we still need to include calls with requestID of 0
        // as onProducerUp() and onProducerStatusChange() calls don't contain requestID
        .filter(call -> call.getRequestID() == request || call.getRequestID() == 0)
        .collect(toList());
    return new CustomerListenerCallHistory(producerCallHistory);
  }

  public synchronized void log() {
    callHistory.forEach(call -> {
      if (call.getListenerType() == ListenerType.Global) {
        if (call.getRequestID() > 0) {
          log.info("GlobalSDKListener : Producer {} Request {} : {}",
              call.getProducerID(), call.getRequestID(), call.getMethod());
        } else {
          log.info("GlobalSDKListener : Producer {} : {}",
              call.getProducerID(), call.getMethod());
        }
      } else {
        log.info("OddsFeedListener  : Producer {} Request {} : {}",
            call.getProducerID(), call.getRequestID(), call.getMethod());
      }
    });
  }
}
