package com.sportradar.api.replay.apiserver.responses;

import com.sportradar.api.replay.logparser.LogEntry;

// Determines which API endpoint by sniffing the request URL.
class ApiEndpointSniffer {

  public ApiEndpoint sniff(LogEntry logEntry) {
    String url = logEntry.getUrl();

    if (url.contains("/v1/descriptions")) {
      return sniffDescriptionUrl(url);
    } else if (url.contains("/v1/sports")) {
      return sniffSportsUrl(url);
    } else if (url.contains("/v1/users")) {
      return sniffUsersUrl(url);
    } else if (url.contains("/recovery")) {
      return sniffRecoveryUrl(url);
    }

    return ApiEndpoint.Unknown;
  }

  private ApiEndpoint sniffDescriptionUrl(String url) {
    if (url.contains("betstop_reasons.xml")) {
      // /v1/descriptions/betstop_reasons.xml
      return ApiEndpoint.DescriptionsBetStopReasons;
    } else if (url.contains("betting_status.xml")) {
      // /v1/descriptions/betting_status.xml
      return ApiEndpoint.DescriptionsBettingStatus;
    } else if (url.contains("markets.xml")) {
      // /v1/descriptions/en/markets.xml?include_mappings=true
      return ApiEndpoint.DescriptionsMarkets;
    } else if (url.contains("variants.xml")) {
      // /v1/descriptions/en/variants.xml?include_mappings=true
      return ApiEndpoint.DescriptionsVariants;
    } else if (url.contains("/variants/")) {
      // /v1/descriptions/en/markets/15/variants/sr:winning_margin:3+?include_mappings=true
      return ApiEndpoint.DescriptionsMarketsVariants;
    } else if (url.contains("producers.xml")) {
      // /v1/descriptions/producers.xml
      return ApiEndpoint.DescriptionsProducers;
    }
    return ApiEndpoint.Unknown;
  }

  private ApiEndpoint sniffSportsUrl(String url) {
    if (url.contains("/profile.xml")) {
      // /v1/sports/en/players/sr:player:2038675/profile.xml
      return ApiEndpoint.SportsPlayerProfile;
    } else if (url.contains("schedule.xml")) {
      // /v1/sports/en/schedules/2022-04-18/schedule.xml
      return ApiEndpoint.SportsSchedule;
    } else if (url.contains("summary.xml")) {
      // /v1/sports/en/sport_events/sr:match:28507580/summary.xml
      return ApiEndpoint.SportsEventSummary;
    } else if (url.contains("fixture_change_fixture.xml")) {
      // /v1/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml
      return ApiEndpoint.SportsEventFixtureChangeFixture;
    }
    return ApiEndpoint.Unknown;
  }

  private ApiEndpoint sniffUsersUrl(String url) {
    if (url.contains("whoami.xml")) {
      // /v1/users/whoami.xml
      return ApiEndpoint.UsersWhoami;
    }
    return ApiEndpoint.Unknown;
  }

  private ApiEndpoint sniffRecoveryUrl(String url) {
    if (url.contains("/initiate_request")) {
      // /v1/liveodds/recovery/initiate_request?node_id=1&request_id=4365
      return ApiEndpoint.RecoveryInitiateRequest;
    }
    return ApiEndpoint.Unknown;
  }

}
