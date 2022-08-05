package com.sportradar.api.replay.apiserver.handlers;

import com.sportradar.api.replay.logparser.LogEntry;

public interface ApiHandlerDelay {

  void delay(LogEntry logEntry, long apiHandlerStartInMillis);
}
