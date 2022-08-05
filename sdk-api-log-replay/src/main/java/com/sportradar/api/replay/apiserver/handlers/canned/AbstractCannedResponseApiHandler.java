package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class AbstractCannedResponseApiHandler implements HttpHandler {
  @Getter
  protected final ApiHandlerConfig config;

  public AbstractCannedResponseApiHandler(ApiHandlerConfig config) {
    this.config = config;
  }

  @Override
  public final void handleRequest(HttpServerExchange exchange) {
    String xml = createXml();
    if (xml == null) {
      exchange.setStatusCode(404);
    } else {
      sleepIfConfigured();
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/xml ");
      exchange.getResponseSender().send(xml);
    }
  }

  abstract String createXml();

  private void sleepIfConfigured() {
    if (config.getMinMessageListenerDelayMs() > 0 && config.getMaxMessageListenerDelayMs() > 0) {
      int delay = (int) (Math.random() * (config.getMaxMessageListenerDelayMs() - config.getMinMessageListenerDelayMs())) + config.getMinMessageListenerDelayMs();

      try {
        log.info("Delaying {} ms", delay);
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

}