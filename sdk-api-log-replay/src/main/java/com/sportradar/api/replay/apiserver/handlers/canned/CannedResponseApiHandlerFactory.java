package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerConfig;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import io.undertow.server.HttpHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CannedResponseApiHandlerFactory implements ApiHandlerFactory {

  private final TemplateFactory templateFactory = new TemplateFactory();
  private final ApiServerConfig serverConfig;
  private final ApiHandlerConfig handlerConfig;

  @Override
  public HttpHandler whoami() {
    return new WhoamiHandler(handlerConfig, serverConfig.getBookmakerID());
  }

  @Override
  public HttpHandler descriptionsProducers() {
    return new DescriptionsProducersHandler(serverConfig, handlerConfig);
  }

  @Override
  public HttpHandler descriptionsBetStopReasons() {
    return new DescriptionsBetStopReasonsHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler descriptionsBettingStatus() {
    return new DescriptionsBettingStatusHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler descriptionsMarkets() {
    return new DescriptionsMarketsHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler descriptionsVariants() {
    return new DescriptionsVariantsHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler descriptionsMarketsVariants() {
    return new DescriptionsMarketsVariantsHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler sportsEventsSummary() {
    return new SportEventsSummaryHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler sportsEventsFixtureChangeFixture() {
    return new SportEventsFixtureChangeFixtureHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler sportsSchedulesSchedule() {
    return new SportsSchedulesScheduleHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler sportsPlayersProfile() {
    return new SportsPlayersProfileHandler(
        templateFactory, handlerConfig, serverConfig.getRequestListener());
  }

  @Override
  public HttpHandler productRecoveryInitiate() {
    return new ProductRecoveryInitiateHandler(
        templateFactory,
        handlerConfig,
        serverConfig.getBookmakerID(),
        serverConfig.getRequestListener());
  }

  private HttpHandler fileBacked(String filepath) {
    return new FileBackedHandler(handlerConfig, filepath);
  }
}
