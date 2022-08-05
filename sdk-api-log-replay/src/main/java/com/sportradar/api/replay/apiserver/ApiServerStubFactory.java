package com.sportradar.api.replay.apiserver;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.routing;

import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiServerStubFactory {

  private final ApiServerConfig serverConfig;
  private final ApiHandlerFactory handlers;

  public ApiServerStub create() {
    Undertow server = Undertow.builder()
        .addHttpListener(serverConfig.getPort(), "localhost")
        .setHandler(createHandler())
        .build();
    return new ApiServerStub(server);
  }

  private HttpHandler createHandler() {
    return path()
        .addPrefixPath("/v1/users", routing()
            .get("/whoami.xml", handlers.whoami())
        ).addPrefixPath("/v1/descriptions", routing()
            .get("/producers.xml", handlers.descriptionsProducers())
            .get("/betstop_reasons.xml", handlers.descriptionsBetStopReasons())
            .get("/betting_status.xml", handlers.descriptionsBettingStatus())
            .get("/{locale}/markets.xml", handlers.descriptionsMarkets())
            .get("/{locale}/variants.xml", handlers.descriptionsVariants())
            .get("/{locale}/markets/{marketId}/variants/{urn}",
                handlers.descriptionsMarketsVariants())
        ).addPrefixPath("/v1/sports", routing()
            .get("/{locale}/sport_events/{urn}/summary.xml", handlers.sportsEventsSummary())
            .get("/{locale}/sport_events/{urn}/fixture_change_fixture.xml",
                handlers.sportsEventsFixtureChangeFixture())
            .get("/{locale}/schedules/{date}/schedule.xml", handlers.sportsSchedulesSchedule())
            .get("/{locale}/players/{urn}/profile.xml", handlers.sportsPlayersProfile())
        ).addPrefixPath("/v1", routing()
            .post("/{product}/recovery/initiate_request", handlers.productRecoveryInitiate())
        );
  }
}
