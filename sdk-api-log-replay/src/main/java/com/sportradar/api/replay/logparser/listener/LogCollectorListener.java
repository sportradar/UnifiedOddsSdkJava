package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.api.replay.logparser.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // FIXME rename to LogNotificationListener? or remove?
public class LogCollectorListener implements ApiLogListener {

  private final ApiLogProcessorListener apiLogProcessorListener;

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    apiLogProcessorListener.onLogEntry(logEntry);
  }
}
