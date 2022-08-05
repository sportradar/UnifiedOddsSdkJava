package com.sportradar.api.replay.logparser;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class RecordingApiLogProcessorListener implements ApiLogProcessorListener {

  @Getter
  private int bookmakerID;
  @Getter
  private int totalLogEntries;
  @Getter
  private final List<LogEntry> logEntries = new ArrayList<>();

  @Override
  public void onBookmakerID(int bookmakerID) {
    this.bookmakerID = bookmakerID;
  }

  @Override
  public void onLogEntry(LogEntry logEntry) {
    totalLogEntries++;
    logEntries.add(logEntry);
  }
}
