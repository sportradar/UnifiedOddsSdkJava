package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.LogEntry;
import java.io.File;

public interface ApiLogListener {

  default void onNewLogfile(File logfile) {
  }

  default void onNewLogEntry(LogEntry logEntry) {
  }

  default void onComplete() {
  }
}
