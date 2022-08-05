package com.sportradar.api.replay.logparser;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeApiLogProcessorListener implements ApiLogProcessorListener {

  private final List<ApiLogProcessorListener> listeners;

  @Override
  public void onBookmakerID(int bookmakerID) {
    listeners.forEach(listener -> listener.onBookmakerID(bookmakerID));
  }

  @Override
  public void onLogEntry(LogEntry logEntry) {
    listeners.forEach(listener -> listener.onLogEntry(logEntry));
  }
}
