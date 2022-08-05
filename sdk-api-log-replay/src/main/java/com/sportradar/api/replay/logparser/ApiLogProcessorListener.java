package com.sportradar.api.replay.logparser;

public interface ApiLogProcessorListener {

  default void onBookmakerID(int bookmakerID) {
  }

  default void onLogEntry(LogEntry logEntry) {
  }
}
