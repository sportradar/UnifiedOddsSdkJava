package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import java.util.Deque;
import java.util.Map;

class DescriptionsMarketsVariantsHandler extends TemplatedResponseApiHandler {

  public DescriptionsMarketsVariantsHandler(
      TemplateFactory templateFactory,
      ApiHandlerConfig config,
      ApiServerRequestListener requestListener) {
    super(templateFactory, config, requestListener);
  }

  @Override
  void populateInput(
      Map<String, Object> input, String url, Map<String, Deque<String>> queryParameters) {
    if (requestListener != null) {
      requestListener.received(ApiEndpoint.DescriptionsMarketsVariants, url, queryParameters);
    }
  }

  @Override
  String renderXmlTemplate(Map<String, Object> input) {
    return templateFactory.descriptionsMarketsVariants(input);
  }
}
