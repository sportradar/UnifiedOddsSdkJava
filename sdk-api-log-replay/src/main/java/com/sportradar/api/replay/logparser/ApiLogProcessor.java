package com.sportradar.api.replay.logparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// FIXME make AutoCloseable so reader is closed??
@Slf4j
@RequiredArgsConstructor
public class ApiLogProcessor {

  private final ApiLogLineReader reader;

  public void processAll() {
    int totalLogs = 0;
    while (next()) {
      totalLogs++;
    }
    log.info("Processed {} log entries", totalLogs);
  }

  public boolean next() {
    String line = reader.nextLogLine();
    if (line == null) {
      reader.close();
    }
    return line != null;
  }
}
