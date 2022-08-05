package com.sportradar.api.replay.apiserver.responses;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.sportradar.api.replay.logparser.ApiLogProcessor;
import com.sportradar.api.replay.logparser.LogEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiLogEntryProviderTest {

  private ApiLogEntryProvider logEntryProvider;
  @Mock
  private ApiLogEntryCache cache;
  @Mock
  private ApiLogProcessor processor;
  @Mock
  private LogEntry logEntry;

  @Before
  public void setUp() throws Exception {
    logEntryProvider = new ApiLogEntryProvider(cache, processor);
  }

  @Test
  public void should_have_next_log_entry_in_cache() {
    when(cache.nextResponseFor(ApiEndpoint.UsersWhoami)).thenReturn(logEntry);

    LogEntry logEntry = logEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);

    assertNotNull(logEntry);
  }

  @Test
  public void should_load_next_log_entry_from_logs() {
    when(cache.nextResponseFor(ApiEndpoint.UsersWhoami)).thenReturn(null, logEntry);
    when(processor.next()).thenReturn(true);

    LogEntry logEntry = logEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);

    assertNotNull(logEntry);
  }

  @Test
  public void should_load_multiple_log_entries_until_correct_one_is_found() {
    when(cache.nextResponseFor(ApiEndpoint.UsersWhoami)).thenReturn(null, null, logEntry);
    when(processor.next()).thenReturn(true);

    LogEntry logEntry = logEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);

    assertNotNull(logEntry);
  }

  @Test
  public void should_load_multiple_log_entries_until_none_are_found() {
    when(cache.nextResponseFor(ApiEndpoint.UsersWhoami)).thenReturn(null, null, null);
    when(processor.next()).thenReturn(true, true, false);

    LogEntry logEntry = logEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);

    assertNull(logEntry);
  }

  @Test
  public void should_not_have_any_more_log_entries() {
    when(cache.nextResponseFor(ApiEndpoint.UsersWhoami)).thenReturn(null);
    when(processor.next()).thenReturn(false);

    LogEntry logEntry = logEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);

    assertNull(logEntry);
  }
}