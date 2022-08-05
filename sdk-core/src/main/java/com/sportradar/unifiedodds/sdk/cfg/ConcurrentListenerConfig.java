package com.sportradar.unifiedodds.sdk.cfg;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcurrentListenerConfig {

  private final boolean enabled;
  private final int threads;
  private final int queueSize;
  private final boolean handleErrorsAsynchronously;
}
