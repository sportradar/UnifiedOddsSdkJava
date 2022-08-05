package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
abstract class TemplatedResponseApiHandler implements HttpHandler {

  protected final TemplateFactory templateFactory;
  protected final ApiHandlerConfig config;
  protected final ApiServerRequestListener requestListener;

  @Override
  public final void handleRequest(HttpServerExchange exchange) {
    Map<String, Object> input = new HashMap<>();
    populateInput(input, exchange.getRequestURL(), exchange.getQueryParameters());
    String xml = renderXmlTemplate(input);
    if (xml == null) {
      exchange.setStatusCode(404);
    } else {
      sleepIfConfigured();
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/xml ");
      exchange.getResponseSender().send(xml);
    }
  }

  abstract void populateInput(
      Map<String, Object> input, String url, Map<String, Deque<String>> queryParameters);

  abstract String renderXmlTemplate(Map<String, Object> input);

  private void sleepIfConfigured() {
    if (config.getMinMessageListenerDelayMs() > 0 && config.getMaxMessageListenerDelayMs() > 0) {
      int delay =
          (int)
                  (Math.random()
                      * (config.getMaxMessageListenerDelayMs()
                          - config.getMinMessageListenerDelayMs()))
              + config.getMinMessageListenerDelayMs();

      try {
        log.info("Delaying {} ms", delay);
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
