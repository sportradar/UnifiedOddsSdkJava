package com.sportradar.api.replay.apiserver;

import io.undertow.Undertow;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApiServerStub {

  private final Undertow server;

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop();
  }
}
