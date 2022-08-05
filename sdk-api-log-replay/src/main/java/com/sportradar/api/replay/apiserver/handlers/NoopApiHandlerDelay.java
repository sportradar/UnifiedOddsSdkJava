package com.sportradar.api.replay.apiserver.handlers;

import com.sportradar.api.replay.logparser.LogEntry;

public class NoopApiHandlerDelay implements ApiHandlerDelay {

  @Override
  public void delay(LogEntry logEntry, long apiHandlerStartInMillis) {
  }
}
