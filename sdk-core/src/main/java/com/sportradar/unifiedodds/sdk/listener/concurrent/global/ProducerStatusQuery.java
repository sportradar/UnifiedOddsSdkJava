package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.FirstRecoveryCompleted;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ProcessingQueDelayStabilized;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason.ReturnedFromInactivity;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ProducerStatusQuery {

  private static final EnumSet<ProducerStatusReason> producerUpReasons = EnumSet.of(
      FirstRecoveryCompleted, ProcessingQueDelayStabilized, ReturnedFromInactivity);
  private final ProducerStatus status;

  public boolean isProducerUp() {
    ProducerStatusReason reason = status.getProducerStatusReason();
    return producerUpReasons.contains(reason);
  }
}
