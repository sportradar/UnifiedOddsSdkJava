package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.LogEntry;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CompositeApiLogListener implements ApiLogListener {

  private final List<ApiLogListener> listeners;

  @Override
  public void onNewLogfile(File logfile) {
    listeners.forEach(listener -> listener.onNewLogfile(logfile));
  }

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    listeners.forEach(listener -> listener.onNewLogEntry(logEntry));
  }

  @Override
  public void onComplete() {
    listeners.forEach(ApiLogListener::onComplete);
  }
}
