package com.sportradar.unifiedodds.sdk.integration;

import static org.junit.Assert.fail;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerConfig;
import com.sportradar.api.replay.apiserver.ApiServerStub;
import com.sportradar.api.replay.apiserver.ApiServerStubFactory;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.NoopApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.canned.CannedResponseApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.logbacked.LogBackedApiHandlerFactory;
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
import org.junit.rules.ExternalResource;


@Deprecated
@RequiredArgsConstructor
public class ApiServerStubRule extends ExternalResource {

  @Getter
  private final ApiServerConfig serverConfig;
  private final ApiLogProcessorListener bookmakerListener;
  @Getter
  private final ApiHandlerConfig handlerConfig = new ApiHandlerConfig();
  private ApiServerStub serverStub;
  @Setter
  private List<File> logfiles;
  @Setter
  private boolean backedByLogs = true;

  protected void before() {
    if (logfiles == null || logfiles.isEmpty()) {
      throw new IllegalStateException("No logfiles provided for API server!");
    }
    ApiHandlerFactory handlerFactory = backedByLogs ? createLogBacked() : createCannedResponses();
    serverStub = new ApiServerStubFactory(serverConfig, handlerFactory).create();
    serverStub.start();
    verifyServerRunning();
  }

  @Deprecated
  private ApiHandlerFactory createCannedResponses() {
    return new CannedResponseApiHandlerFactory(serverConfig, handlerConfig);
  }

  private ApiHandlerFactory createLogBacked() {
    ApiLogEntryProvider apiLogEntryProvider = createApiLogEntryProvider(logfiles);
    ApiHandlerDelay noDelay = new NoopApiHandlerDelay();
    return new LogBackedApiHandlerFactory(apiLogEntryProvider, noDelay);
  }

  private ApiLogEntryProvider createApiLogEntryProvider(List<File> logfiles) {
    ApiLogEntryProviderFactory providerFactory = new ApiLogEntryProviderFactory();
    ApiLogEntryProvider apiLogEntryProvider = providerFactory.create(logfiles, bookmakerListener);
    // read the /whoami.xml log entry to trigger the extraction of bookmaker id to the listener
    apiLogEntryProvider.nextLogEntry(ApiEndpoint.UsersWhoami);
    return apiLogEntryProvider;
  }

  protected void after() {
    if (serverStub != null) {
      serverStub.stop();
    }
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
