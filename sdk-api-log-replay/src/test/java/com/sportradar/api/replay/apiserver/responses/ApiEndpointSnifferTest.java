package com.sportradar.api.replay.apiserver.responses;

import static org.junit.Assert.assertEquals;

import com.sportradar.api.replay.logparser.LogEntry;
import org.junit.Test;

public class ApiEndpointSnifferTest {

  private final ApiEndpointSniffer sniffer = new ApiEndpointSniffer();

  @Test
  public void should_not_find_match() {
    assertEquals(ApiEndpoint.Unknown, sniff("blah"));
  }

  @Test
  public void should_match_users_whoami() {
    assertEquals(ApiEndpoint.UsersWhoami, sniff("/v1/users/whoami.xml"));
  }

  @Test
  public void should_match_descriptions_producers() {
    assertEquals(ApiEndpoint.DescriptionsProducers, sniff("/v1/descriptions/producers.xml"));
  }

  @Test
  public void should_match_descriptions_bet_stop_reasons() {
    assertEquals(ApiEndpoint.DescriptionsBetStopReasons,
        sniff("/v1/descriptions/betstop_reasons.xml"));
  }

  @Test
  public void should_match_descriptions_markets() {
    assertEquals(ApiEndpoint.DescriptionsMarkets,
        sniff("/v1/descriptions/en/markets.xml?include_mappings=true"));
  }

  @Test
  public void should_match_descriptions_variants() {
    assertEquals(ApiEndpoint.DescriptionsVariants,
        sniff("/v1/descriptions/en/variants.xml?include_mappings=true"));
  }

  @Test
  public void should_match_descriptions_markets_variants() {
    assertEquals(ApiEndpoint.DescriptionsMarketsVariants,
        sniff(
            "/v1/descriptions/en/markets/533/variants/lo:snooker:bestof:19:framesbeforesd:18:4?include_mappings=true"));
  }

  @Test
  public void should_match_sports_player_profile() {
    assertEquals(ApiEndpoint.SportsPlayerProfile,
        sniff("/v1/sports/en/players/sr:player:1500083/profile.xml"));
  }

  @Test
  public void should_match_sports_schedule() {
    assertEquals(ApiEndpoint.SportsSchedule,
        sniff("/v1/sports/en/schedules/2022-04-16/schedule.xml"));
  }

  @Test
  public void should_match_sports_event_summary() {
    assertEquals(ApiEndpoint.SportsEventSummary,
        sniff("/v1/sports/en/sport_events/sr:match:28634638/summary.xml"));
  }

  @Test
  public void should_match_sports_event_fixture_change_fixture() {
    assertEquals(ApiEndpoint.SportsEventFixtureChangeFixture,
        sniff("/v1/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml"));
  }

  @Test
  public void should_match_recovery_initiation() {
    assertEquals(ApiEndpoint.RecoveryInitiateRequest,
        sniff("/v1/premium_cricket/recovery/initiate_request?node_id=1&request_id=5582"));
  }

  private ApiEndpoint sniff(String url) {
    LogEntry logEntry = LogEntry.builder().url(url).build();
    return sniffer.sniff(logEntry);
  }
}