package com.sportradar.api.replay.apiserver.responses;

import com.sportradar.api.replay.logparser.ApiLogProcessor;
import com.sportradar.api.replay.logparser.LogEntry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

// integrates between the log parsing and the stub API server handlers
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApiLogEntryProvider {

  private final ApiLogEntryCache cache;
  private final ApiLogProcessor logProcessor;

  public LogEntry nextLogEntry(ApiEndpoint apiEndpoint) {
    LogEntry logEntry = cache.nextResponseFor(apiEndpoint);
    if (logEntry != null) {
      return logEntry;
    }

    return keepReadingLogsUntilApiEndpointLogFound(apiEndpoint);
  }

  private LogEntry keepReadingLogsUntilApiEndpointLogFound(ApiEndpoint apiEndpoint) {
    LogEntry logEntry = null;
    boolean logsLeftToRead = true;

    while (logEntry == null && logsLeftToRead) {
      // keep reading logs until we have a log entry for this endpoint
      // logProcessor.next() will trigger the population of the cache using listener
      logsLeftToRead = logProcessor.next();
      logEntry = cache.nextResponseFor(apiEndpoint);
    }
    return logEntry;
  }
}
