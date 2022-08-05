package com.sportradar.api.replay.apiserver.handlers.canned;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MatchUrnExtractorTest {

  private MatchUrnExtractor extractor = new MatchUrnExtractor();

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void should_extract_match_urn() {
    String urn = extractor.extract(
        "/v1/sports/it/sport_events/sr:match:33176369/fixture_change_fixture.xml");

    assertEquals("sr:match:33176369", urn);
  }

  @Test
  public void should_fail_to_extract_match_urn() {
    String urn = extractor.extract("blah");

    assertEquals("sr:match:UNKNOWN", urn);
  }
}