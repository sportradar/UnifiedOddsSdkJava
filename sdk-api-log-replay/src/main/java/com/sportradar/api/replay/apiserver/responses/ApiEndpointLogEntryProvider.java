package com.sportradar.api.replay.apiserver.responses;

import com.sportradar.api.replay.logparser.LogEntry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiEndpointLogEntryProvider {

  private final ApiLogEntryProvider apiLogEntryProvider;
  private final ApiEndpoint apiEndpoint;

  public LogEntry nextLogEntry() {
    return apiLogEntryProvider.nextLogEntry(apiEndpoint);
  }
}
