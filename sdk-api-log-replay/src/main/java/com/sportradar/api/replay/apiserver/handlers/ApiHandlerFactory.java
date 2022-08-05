package com.sportradar.api.replay.apiserver.handlers;

import io.undertow.server.HttpHandler;

public interface ApiHandlerFactory {

  HttpHandler whoami();

  HttpHandler descriptionsProducers();

  HttpHandler descriptionsBetStopReasons();

  HttpHandler descriptionsBettingStatus();

  HttpHandler descriptionsMarkets();

  HttpHandler descriptionsVariants();

  HttpHandler descriptionsMarketsVariants();

  HttpHandler sportsEventsSummary();

  HttpHandler sportsEventsFixtureChangeFixture();

  HttpHandler sportsSchedulesSchedule();

  HttpHandler sportsPlayersProfile();

  HttpHandler productRecoveryInitiate();
}
