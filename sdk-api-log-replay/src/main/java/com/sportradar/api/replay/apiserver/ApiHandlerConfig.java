package com.sportradar.api.replay.apiserver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiHandlerConfig {

  private int totalProducers = 3;

  private int minMessageListenerDelayMs = 0;
  
  private int maxMessageListenerDelayMs = 0;

}
