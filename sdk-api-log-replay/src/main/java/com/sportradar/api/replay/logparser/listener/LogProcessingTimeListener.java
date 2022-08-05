package com.sportradar.api.replay.logparser.listener;

import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class LogProcessingTimeListener implements ApiLogListener {

  private long prevStartTime;
  private File currentLogfile;

  @Override
  public void onNewLogfile(File logfile) {
    currentLogfile = logfile;
    if (prevStartTime == 0) {
      prevStartTime = System.currentTimeMillis();
    } else {
      calculateProcessingTime(prevStartTime);
      prevStartTime = System.currentTimeMillis();
    }
    log.info("Processing '{}'", currentLogfile.getName());
  }

  @Override
  public void onComplete() {
    calculateProcessingTime(prevStartTime);
  }

  private long calculateProcessingTime(long start) {
    long end = System.currentTimeMillis();
    long processingTimeInMillis = end - start;
    log.info("'{}' processed in {} seconds", currentLogfile.getName(),
        processingTimeInMillis / 1000);
    return processingTimeInMillis;
  }
}
