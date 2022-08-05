package com.sportradar.api.replay.logparser.listener;

import static org.junit.Assert.assertEquals;

import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.api.replay.logparser.LogEntry;
import com.sportradar.api.replay.logparser.LogParserException;
import org.junit.Test;

public class BookmakerExtractorListenerTest {


  private final BookmakerListener bookmakerListener = new BookmakerListener();
  private final BookmakerExtractorListener listener = new BookmakerExtractorListener(
      bookmakerListener);

  @Test
  public void should_extract_bookmaker_id_from_first_log_message() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bookmaker_details response_code=\"OK\" expire_at=\"2023-02-28T13:16:44Z\" bookmaker_id=\"16281\" virtual_host=\"/unifiedfeed/16281\"/>";
    LogEntry logEntry = LogEntry.builder().xml(xml).build();

    listener.onNewLogEntry(logEntry);

    assertEquals(16281, listener.getBookmakerID());
    assertEquals(16281, bookmakerListener.bookmakerID);
  }

  @Test
  public void should_notify_listener_of_bookmaker_id() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bookmaker_details response_code=\"OK\" expire_at=\"2023-02-28T13:16:44Z\" bookmaker_id=\"16281\" virtual_host=\"/unifiedfeed/16281\"/>";
    LogEntry logEntry = LogEntry.builder().xml(xml).build();

    listener.onNewLogEntry(logEntry);

    assertEquals(16281, bookmakerListener.bookmakerID);
  }

  @Test(expected = LogParserException.class)
  public void should_fail_if_first_log_message_is_not_whoami() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><blah></blah>";
    LogEntry logEntry = LogEntry.builder().xml(xml).build();

    listener.onNewLogEntry(logEntry);
  }

  private class BookmakerListener implements ApiLogProcessorListener {

    int bookmakerID;

    public void onBookmakerID(int bookmakerID) {
      this.bookmakerID = bookmakerID;
    }
  }
}