package com.sportradar.api.replay.apiserver.responses;

public class ApiLogEntryCacheFactory {
  private final ApiEndpointSniffer endpointSniffer = new ApiEndpointSniffer();

  public ApiLogEntryCache create() {
    return new ApiLogEntryCache(endpointSniffer);
  }
}
