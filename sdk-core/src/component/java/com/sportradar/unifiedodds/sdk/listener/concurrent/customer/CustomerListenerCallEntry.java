package com.sportradar.unifiedodds.sdk.listener.concurrent.customer;

import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.FirstRecoveryCompleted;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ProcessingQueDelayStabilized;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ReturnedFromInactivity;
import static java.util.Arrays.asList;

import com.sportradar.unifiedodds.sdk.listener.concurrent.ListenerType;
import com.sportradar.utils.URN;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerListenerCallEntry {

  private static final Set<String> producerUpReasons = new HashSet<>(asList(
      FirstRecoveryCompleted.name(),
      ProcessingQueDelayStabilized.name(),
      ReturnedFromInactivity.name())
  );

  private final ListenerType listenerType;
  private final int producerID;
  private final long requestID;
  private final URN eventID;
  private final String method;
  private final String producerStatusReason;

  public boolean isOddsFeedCall() {
    return listenerType == ListenerType.OddsFeed;
  }

  public boolean isGlobalCall() {
    return listenerType == ListenerType.Global;
  }

  public boolean isProducerUp() {
    return isGlobalCall() && producerUpReasons.contains(producerStatusReason);
  }
}
