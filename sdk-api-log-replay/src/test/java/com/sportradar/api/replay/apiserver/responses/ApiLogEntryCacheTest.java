package com.sportradar.api.replay.apiserver.responses;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.sportradar.api.replay.apiserver.ApiServerStubException;
import com.sportradar.api.replay.logparser.LogEntry;
import org.junit.Test;

public class ApiLogEntryCacheTest {

  private ApiLogEntryCache apiLogEntryCache = new ApiLogEntryCache(new ApiEndpointSniffer());

  @Test(expected = ApiServerStubException.class)
  public void should_fail_on_adding_unknown_url() {
    LogEntry logEntry = url("/v1/unknown.xml");
    apiLogEntryCache.add(logEntry);
  }

  @Test
  public void should_return_next_response() {
    ApiEndpoint apiEndpoint = ApiEndpoint.DescriptionsProducers;

    LogEntry logEntry = url("/v1/descriptions/producers.xml");
    apiLogEntryCache.add(logEntry);

    assertSame(logEntry, apiLogEntryCache.nextResponseFor(apiEndpoint));
  }

  @Test
  public void should_return_null_when_no_responses_left() {
    for (ApiEndpoint apiEndpoint : ApiEndpoint.values()) {
      assertNull(apiLogEntryCache.nextResponseFor(apiEndpoint));
    }
  }

  @Test
  public void should_return_all_responses() {
    ApiEndpoint apiEndpoint = ApiEndpoint.SportsPlayerProfile;

    LogEntry logEntry1 = url("/v1/sports/en/players/sr:player:123/profile.xml");
    LogEntry logEntry2 = url("/v1/sports/en/players/sr:player:999/profile.xml");
    apiLogEntryCache.add(logEntry1);
    apiLogEntryCache.add(logEntry2);

    assertSame(logEntry1, apiLogEntryCache.nextResponseFor(apiEndpoint));
    assertSame(logEntry2, apiLogEntryCache.nextResponseFor(apiEndpoint));
    assertNull(apiLogEntryCache.nextResponseFor(apiEndpoint));
  }

  private LogEntry url(String url) {
    return LogEntry.builder().url(url).build();
  }
}