package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.LogEntry;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class LogRecordedLengthListener implements ApiLogListener {

  private static final long SECOND_IN_MILLIS = 1000;
  private static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
  private static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
  private LogEntry first;
  private LogEntry last;
  private int logFileIndex;
  private int logLineIndex;

  @Override
  public void onNewLogfile(File logfile) {
    logFileIndex++;
    logLineIndex = 0;
  }

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    if (logLineIndex == 0 && logFileIndex == 1) {
      first = logEntry;
    }
    last = logEntry;
  }

  @Override
  public void onComplete() {
    if (first == null)
      return;

    long start = first.getTimestamp().getTime();
    long end = last.getTimestamp().getTime();
    long totalMillis = end - start;
    long totalHours = totalMillis / HOUR_IN_MILLIS;
    long totalMinutes = (totalMillis % HOUR_IN_MILLIS) / MINUTE_IN_MILLIS;
    long totalSeconds = (totalMillis % MINUTE_IN_MILLIS) / SECOND_IN_MILLIS;
    log.info("Logs covered {} hours, {} minutes, and {} seconds", totalHours, totalMinutes,
            totalSeconds);
  }
}
