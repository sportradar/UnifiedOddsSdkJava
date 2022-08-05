package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerListenerEvent {

  private final String threadName;
  private final String method;
  private final SportEvent sportEvent;
}
