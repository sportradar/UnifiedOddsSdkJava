package com.sportradar.api.replay.apiserver.handlers.logbacked;


import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.apiserver.responses.ApiEndpointLogEntryProvider;
import com.sportradar.api.replay.logparser.LogEntry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GenericLogBackedHandler extends AbstractLogBackedApiHandler {

  private final ApiEndpointLogEntryProvider logEntryProvider;
  private final ApiHandlerDelay apiHandlerDelay;

  @Override
  LogEntry nextLogEntry() {
    return logEntryProvider.nextLogEntry();
  }

  @Override
  ApiHandlerDelay delayer() {
    return apiHandlerDelay;
  }
}