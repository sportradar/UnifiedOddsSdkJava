package com.sportradar.api.replay.apiserver.handlers.logbacked;

import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.logparser.LogEntry;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
abstract class AbstractLogBackedApiHandler implements HttpHandler {

  @Override
  public final void handleRequest(HttpServerExchange exchange) {
    long apiHandlerStartInMillis = System.currentTimeMillis();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/xml");

    LogEntry logEntry = nextLogEntry();
    if (logEntry == null) {
      log.error("Failing call on purpose as no more logs for this endpoint!");
      exchange.setStatusCode(404);
      exchange.getResponseSender()
          .send("<error message=\"No more API logs for this endpoint!\"/>");
    } else {
      delayer().delay(logEntry, apiHandlerStartInMillis);

      if (logEntry.isSuccess()) {
        exchange.getResponseSender().send(logEntry.getXml());
      } else {
        log.error("Failing call to '{}' on purpose as per log entry at {}", logEntry.getUrl(),
            logEntry.getTimestamp());
        exchange.setStatusCode(500);
        exchange.getResponseSender()
            .send("<error message=\"This API call failed according to logs.\"/>");
      }
    }
  }

  abstract LogEntry nextLogEntry();

  abstract ApiHandlerDelay delayer();
}