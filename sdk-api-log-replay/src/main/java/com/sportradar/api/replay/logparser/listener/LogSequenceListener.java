package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.LogEntry;
import com.sportradar.api.replay.logparser.LogParserException;
import java.io.File;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class LogSequenceListener implements ApiLogListener {

  private static final long MAX_MILLIS_BETWEEN_LOGFILES = 3000;
  private int index;
  private LogEntry lastLogEntry;
  private File prevLogFile;
  private File currentLogFile;

  @Override
  public void onNewLogfile(File logfile) {
    index = 0;
    prevLogFile = currentLogFile;
    currentLogFile = logfile;
  }

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    boolean firstLogEntry = index++ == 0;
    boolean haveLastLogEntryFromPreviousLogfile = lastLogEntry != null;
    boolean startOfNewLogfile = firstLogEntry && haveLastLogEntryFromPreviousLogfile;

    if (startOfNewLogfile) {
      onStartOfNextLogfile(logEntry);
    }
    lastLogEntry = logEntry;
  }

  // we are at the start of a new log file
  // compare this first log timestamp against the last log timestamp from previous logfile (if any)
  private void onStartOfNextLogfile(LogEntry logEntry) {
    Date currentTimestamp = logEntry.getTimestamp();
    long now = currentTimestamp.getTime();
    long then = lastLogEntry.getTimestamp().getTime();
    long diff = Math.abs(now - then);
    if (diff >= MAX_MILLIS_BETWEEN_LOGFILES) {
      log.error("Timestamp difference between '{}' and '{}' is {}m/s! Max allowed is {}m/s",
          prevLogFile.getName(),
          currentLogFile.getName(), diff, MAX_MILLIS_BETWEEN_LOGFILES);
      throw new LogParserException("Timestamp difference too large between logfiles!");
    } else {
      if (prevLogFile != null) {
        log.info("Timestamp difference between '{}' and '{}' is {}m/s",
            prevLogFile.getName(),
            currentLogFile.getName(), diff);
      }
    }
  }
}
