package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class SportEventsSummaryHandler extends TemplatedResponseApiHandler {

  private final MatchUrnExtractor matchUrnExtractor = new MatchUrnExtractor();

  public SportEventsSummaryHandler(
      TemplateFactory templateFactory,
      ApiHandlerConfig config,
      ApiServerRequestListener requestListener) {
    super(templateFactory, config, requestListener);
  }

  @Override
  void populateInput(
      Map<String, Object> input, String url, Map<String, Deque<String>> queryParameters) {
    String matchUrn = matchUrnExtractor.extract(url);
    input.put("matchUrn", matchUrn);

    if (requestListener != null) {
      requestListener.received(ApiEndpoint.SportsEventSummary, url, queryParameters);
    }
  }

  @Override
  String renderXmlTemplate(Map<String, Object> input) {
    return templateFactory.sportsEventsSummary(input);
  }

  public static void main(String[] args) {
    SportEventsSummaryHandler handler =
        new SportEventsSummaryHandler(new TemplateFactory(), new ApiHandlerConfig(), null);

    Map<String, Object> input = new HashMap<>();
    handler.populateInput(
        input,
        "https://api.betradar.com/v1/sports/it/sport_events/sr:match:33176369/summary.xml",
        null);

    System.out.println(input.get("matchUrn"));
  }
}
