package com.sportradar.api.replay.apiserver.handlers.logbacked;

import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import com.sportradar.api.replay.apiserver.responses.ApiEndpointLogEntryProvider;
import com.sportradar.api.replay.apiserver.responses.ApiLogEntryProvider;
import io.undertow.server.HttpHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogBackedApiHandlerFactory implements ApiHandlerFactory {

  private final ApiLogEntryProvider apiLogEntryProvider;
  private final ApiHandlerDelay apiHandlerDelay;

  @Override
  public HttpHandler whoami() {
    return handlerFor(ApiEndpoint.UsersWhoami);
  }

  @Override
  public HttpHandler descriptionsProducers() {
    return handlerFor(ApiEndpoint.DescriptionsProducers);
  }

  @Override
  public HttpHandler descriptionsBetStopReasons() {
    return handlerFor(ApiEndpoint.DescriptionsBetStopReasons);
  }

  @Override
  public HttpHandler descriptionsBettingStatus() {
    return handlerFor(ApiEndpoint.DescriptionsBettingStatus);
  }

  @Override
  public HttpHandler descriptionsMarkets() {
    return handlerFor(ApiEndpoint.DescriptionsMarkets);
  }

  @Override
  public HttpHandler descriptionsVariants() {
    return handlerFor(ApiEndpoint.DescriptionsVariants);
  }

  @Override
  public HttpHandler descriptionsMarketsVariants() {
    return handlerFor(ApiEndpoint.DescriptionsMarketsVariants);
  }

  @Override
  public HttpHandler sportsEventsSummary() {
    return handlerFor(ApiEndpoint.SportsEventSummary);
  }

  @Override
  public HttpHandler sportsEventsFixtureChangeFixture() {
    return handlerFor(ApiEndpoint.SportsEventFixtureChangeFixture);
  }

  @Override
  public HttpHandler sportsSchedulesSchedule() {
    return handlerFor(ApiEndpoint.SportsSchedule);
  }

  @Override
  public HttpHandler sportsPlayersProfile() {
    return handlerFor(ApiEndpoint.SportsPlayerProfile);
  }

  @Override
  public HttpHandler productRecoveryInitiate() {
    return handlerFor(ApiEndpoint.RecoveryInitiateRequest);
  }

  private HttpHandler handlerFor(ApiEndpoint apiEndpoint) {
    return new GenericLogBackedHandler(providerFor(apiEndpoint), apiHandlerDelay);
  }

  private ApiEndpointLogEntryProvider providerFor(ApiEndpoint apiEndpoint) {
    return new ApiEndpointLogEntryProvider(apiLogEntryProvider, apiEndpoint);
  }
}
