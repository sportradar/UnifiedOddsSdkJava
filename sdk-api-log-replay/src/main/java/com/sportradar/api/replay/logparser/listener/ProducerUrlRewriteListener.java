package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class ProducerUrlRewriteListener implements ApiLogListener {

  private int index;

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    if (index++ == 1) {
      // the second log entry should be the /descriptions/producers.xml response
      String xml = logEntry.getXml();
      // FIXME rewrite URLs to http://localhost:8080 so Producers will go to our Undertow stub server
    }
  }
}
