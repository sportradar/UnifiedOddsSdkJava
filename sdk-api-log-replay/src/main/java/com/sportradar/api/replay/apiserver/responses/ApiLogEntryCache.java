package com.sportradar.api.replay.apiserver.responses;

import static com.sportradar.api.replay.apiserver.responses.ApiEndpoint.DescriptionsProducers;
import static com.sportradar.api.replay.apiserver.responses.ApiEndpoint.UsersWhoami;

import com.sportradar.api.replay.apiserver.ApiServerStubException;
import com.sportradar.api.replay.logparser.LogEntry;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ApiLogEntryCache {

  private final ApiEndpointSniffer apiEndpointSniffer;
  private final EnumMap<ApiEndpoint, List<LogEntry>> apiResponses = new EnumMap<>(
      ApiEndpoint.class);
  private final EnumSet<ApiEndpoint> preserveFirstResponse = EnumSet.of(UsersWhoami,
      DescriptionsProducers);

  ApiLogEntryCache(ApiEndpointSniffer apiEndpointSniffer) {
    this.apiEndpointSniffer = apiEndpointSniffer;
    for (ApiEndpoint endpoint : ApiEndpoint.values()) {
      apiResponses.put(endpoint, new ArrayList<>());
    }
  }

  public void add(LogEntry logEntry) {
    ApiEndpoint apiEndpoint = apiEndpointSniffer.sniff(logEntry);
    if (apiEndpoint == ApiEndpoint.Unknown) {
      log.error("Unknown API apiEndpoint URL! : {}", logEntry.getUrl());
      throw new ApiServerStubException("Unknown API apiEndpoint URL! : " + logEntry.getUrl());
    }
    apiResponses.get(apiEndpoint).add(logEntry);
  }

  public LogEntry nextResponseFor(ApiEndpoint apiEndpoint) {
    List<LogEntry> logEntries = apiResponses.get(apiEndpoint);
    if (logEntries.isEmpty()) {
      return null;
    } else {
      if (preserveFirstResponse.contains(apiEndpoint)) {
        return logEntries.get(0);
      } else {
        return logEntries.remove(0);
      }
    }
  }
}