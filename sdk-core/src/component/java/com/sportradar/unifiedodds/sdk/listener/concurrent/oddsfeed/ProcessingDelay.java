package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ProcessingDelay {

  private final Random random = new Random();

  // introduce some delay to event processing
  public void delay() {
    long millis = 30 * random.longs(1, 11).findFirst().getAsLong();
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.error("Error sleeping!", e);
    }
  }
}
