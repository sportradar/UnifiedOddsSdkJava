package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class RecoveryContext {

  private static final RecoveryContext NONE = new RecoveryContext(-1, -1);

  @Getter
  private final int producerID;

  @Getter
  private final long requestID;

  private RecoveryContext(int producerID, long requestID) {
    this.producerID = producerID;
    this.requestID = requestID;
  }

  public boolean isValid() {
    return producerID > 0 && requestID > 0;
  }

  public static RecoveryContext valueOf(int producerID, long requestID) {
    return (producerID > 0 && requestID > 0)
        ? new RecoveryContext(producerID, requestID)
        : NONE;
  }

  public static RecoveryContext none() {
    return NONE;
  }
}
