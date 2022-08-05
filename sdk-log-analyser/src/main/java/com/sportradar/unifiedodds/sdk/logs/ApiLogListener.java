package com.sportradar.unifiedodds.sdk.logs;

import java.nio.file.Path;

public interface ApiLogListener {

  default void onNewLogfile(Path logfile) {
  }

  default void onNewLogEntry(LogEntry logEntry) {
  }

  default void onComplete() {
  }
}
