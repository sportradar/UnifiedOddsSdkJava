package com.sportradar.unifiedodds.sdk.integration.fixtures.api;

import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import com.sportradar.unifiedodds.sdk.integration.fixtures.logfiles.TestLogfiles;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiServerExpectations {

  private final ApiServerStubFixture fixture;

  public ApiServerExpectations usingPort(int port) {
    fixture.getServerConfig().setPort(port);
    return this;
  }

  public ApiServerExpectations usingBookmakerID(int bookmakerID) {
    fixture.getServerConfig().setBookmakerID(bookmakerID);
    return this;
  }

  public ApiServerExpectations replayingLogsFrom(String apiLogsFolder) {
    List<File> logfiles = TestLogfiles.logsFromTestFolder(apiLogsFolder);
    fixture.setLogfiles(logfiles);
    return this;
  }

  public ApiServerExpectations withNumberOfProducers(int totalProducers) {
    fixture.withNumberOfProducers(totalProducers);
    return this;
  }

  public ApiServerExpectations withRequestListener(ApiServerRequestListener requestListener) {
    fixture.withRequestListener(requestListener);
    return this;
  }

  public ApiServerExpectations hasRandomMessageListenerDelays(int minDelayMs, int maxDelayMs) {
    fixture.withRandomMessageListenerDelays(minDelayMs, maxDelayMs);
    return this;
  }

  public ApiServerExpectations withDelaysEnabled() {
    fixture.enableLogBasedDelays();
    return this;
  }

  public void isStarted() {
    fixture.start();
  }

  public void stop() {
    fixture.stop();
  }
}
