package com.sportradar.api.replay.apiserver.responses;

import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.api.replay.logparser.LogEntry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CachePopulatingApiLogProcessorListener implements ApiLogProcessorListener {

  private final ApiLogEntryCache cache;

  public void onLogEntry(LogEntry logEntry) {
    cache.add(logEntry);
  }
}
