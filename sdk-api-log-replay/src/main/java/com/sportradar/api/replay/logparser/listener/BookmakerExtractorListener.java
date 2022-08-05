package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.api.replay.logparser.LogEntry;
import com.sportradar.api.replay.logparser.LogParserException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class BookmakerExtractorListener implements ApiLogListener {

  private static final Pattern REGEX_BOOKMAKER_ID = Pattern.compile(".*bookmaker_id=\"(\\d+)\"");
  private final ApiLogProcessorListener apiLogProcessorListener;

  private int index;
  @Getter
  private int bookmakerID;

  @Override
  public void onNewLogEntry(LogEntry logEntry) {
    if (bookmakerID == 0 && index++ == 0) {
      // log entry should be the /users/whoami.xml call and response contains the bookmaker id
      String xml = logEntry.getXml();
      Matcher matcher = REGEX_BOOKMAKER_ID.matcher(xml);
      if (matcher.find()) {
        String value = matcher.group(1);
        bookmakerID = Integer.parseInt(value);
        apiLogProcessorListener.onBookmakerID(bookmakerID);
      } else {
        log.error("First log entry wasn't /users/whoami.xml call!");
        throw new LogParserException("First log entry wasn't /users/whoami.xml call!");
      }
    }
  }
}
