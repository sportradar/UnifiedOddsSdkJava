package com.sportradar.unifiedodds.sdk.integration.fixtures.api;

import static org.junit.Assert.fail;

import com.sportradar.api.replay.apiserver.*;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.NoopApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.canned.CannedResponseApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.logbacked.LogBackedApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.logbacked.LogBasedApiHandlerDelay;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import com.sportradar.api.replay.apiserver.responses.ApiLogEntryProvider;
import com.sportradar.api.replay.apiserver.responses.ApiLogEntryProviderFactory;
import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ApiServerStubFixture {

  @Getter private final ApiServerConfig serverConfig;
  private final ApiLogProcessorListener bookmakerListener;
  private final ApiHandlerConfig apiHandlerConfig = new ApiHandlerConfig();
  private ApiServerStub serverStub;
  @Setter private List<File> logfiles;
  @Setter private ApiHandlerDelay apiDelayStrategy = new NoopApiHandlerDelay();

  public void start() {
    if (serverConfig.isLogBacked()) {
      if (logfiles == null || logfiles.isEmpty()) {
        throw new IllegalStateException("No logfiles provided for API server!");
      }
    }
    serverStub = createApiServerStub();
    serverStub.start();
    verifyServerRunning();
  }

  public void stop() {
    if (serverStub != null) {
      serverStub.stop();
    }
  }

  public void enableLogBasedDelays() {
    if (serverConfig.isCanned()) {
      throw new IllegalStateException("Delays not implemented for canned api yet!");
    }
    apiDelayStrategy = new LogBasedApiHandlerDelay();
  }

  public void withNumberOfProducers(int totalProducers) {
    if (serverConfig.isCanned()) {
      apiHandlerConfig.setTotalProducers(totalProducers);
    } else {
      throw new IllegalStateException("Can only set number of Producers in Canned API mode!");
    }
  }

  public void withRandomMessageListenerDelays(int minDelayMs, int maxDelayMs) {
    apiHandlerConfig.setMinMessageListenerDelayMs(minDelayMs);
    apiHandlerConfig.setMaxMessageListenerDelayMs(maxDelayMs);
  }

  public void withRequestListener(ApiServerRequestListener requestListener) {
    serverConfig.setRequestListener(requestListener);
  }

  private ApiServerStub createApiServerStub() {
    ApiHandlerFactory apiHandlerFactory;
    if (serverConfig.isCanned()) {
      apiHandlerFactory = createCannedApiHandlerFactory();
    } else {
      apiHandlerFactory = createLogBackedApiHandlerFactory();
    }
    return new ApiServerStubFactory(serverConfig, apiHandlerFactory).create();
  }

  private ApiHandlerFactory createLogBackedApiHandlerFactory() {
    ApiLogEntryProvider apiLogEntryProvider = createApiLogEntryProvider(logfiles);
    return new LogBackedApiHandlerFactory(apiLogEntryProvider, apiDelayStrategy);
  }

  private ApiHandlerFactory createCannedApiHandlerFactory() {
    return new CannedResponseApiHandlerFactory(serverConfig, apiHandlerConfig);
  }

  private ApiLogEntryProvider createApiLogEntryProvider(List<File> logfiles) {
    ApiLogEntryProviderFactory providerFactory = new ApiLogEntryProviderFactory();
    ApiLogEntryProvider apiLogEntryProvider = providerFactory.create(logfiles, bookmakerListener);
    // read the /whoami.xml log entry to trigger the extraction of bookmaker id to the listener
    apiLogEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);
    return apiLogEntryProvider;
  }

  private void verifyServerRunning() {
    if (!pingHost()) {
      fail("API server not started!");
    }
  }

  private boolean pingHost() {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress("localhost", serverConfig.getPort()), 5000);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
