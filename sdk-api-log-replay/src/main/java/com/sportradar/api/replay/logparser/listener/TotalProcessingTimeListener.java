package com.sportradar.api.replay.logparser.listener;

import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class TotalProcessingTimeListener implements ApiLogListener {

  private int count;
  private long start;

  @Override
  public void onNewLogfile(File logfile) {
    count++;
    if (start == 0) {
      start = System.currentTimeMillis();
    }
  }

  @Override
  public void onComplete() {
    long end = System.currentTimeMillis();
    long totalMillis = end - start;
    log.info("{} logs processed in {} seconds", count, totalMillis / 1000);
  }
}
