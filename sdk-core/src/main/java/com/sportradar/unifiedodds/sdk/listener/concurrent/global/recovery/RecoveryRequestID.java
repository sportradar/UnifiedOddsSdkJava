package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class RecoveryRequestID {

  private static final RecoveryRequestID NONE = new RecoveryRequestID(-1);
  @Getter
  private final long id;

  private RecoveryRequestID(long requestID) {
    this.id = requestID;
  }

  public boolean isValid() {
    return id > NONE.id;
  }

  public static RecoveryRequestID valueOf(Long requestID) {
    return (requestID == null || requestID < 0) ? NONE : new RecoveryRequestID(requestID);
  }

  public static RecoveryRequestID none() {
    return NONE;
  }
}
