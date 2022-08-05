package com.sportradar.api.replay.apiserver.handlers.logbacked;

import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.logparser.LogEntry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogBasedApiHandlerDelay implements ApiHandlerDelay {

  @Override
  public void delay(LogEntry logEntry, long apiHandlerStartInMillis) {
    long durationInMillis = calculateRemainingDelay(logEntry, apiHandlerStartInMillis);
    sleep(durationInMillis);
  }

  private void sleep(long durationInMillis) {
    if (durationInMillis > 0) {
      try {
        Thread.sleep(durationInMillis);
      } catch (InterruptedException e) {
        log.error("Error sleeping!", e);
      }
    }
  }

  private long calculateRemainingDelay(LogEntry logEntry, long apiHandlerStartInMillis) {
    long now = System.currentTimeMillis();
    long timeToFindLogEntry = now - apiHandlerStartInMillis;

    long apiCallDurationTarget = logEntry.getDurationInMillis();
    long actualDelayAmount = apiCallDurationTarget - timeToFindLogEntry;

    if (actualDelayAmount > 0) {
      log.info("API Endpoint took {}ms from logs, delaying for {}ms", apiCallDurationTarget,
          actualDelayAmount);
    } else {
      log.info("API Endpoint took {}ms from logs but we took longer ({}ms)", apiCallDurationTarget,
          timeToFindLogEntry);
    }
    return actualDelayAmount;
  }
}
