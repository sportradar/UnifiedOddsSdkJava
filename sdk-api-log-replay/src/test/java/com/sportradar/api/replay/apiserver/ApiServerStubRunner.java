package com.sportradar.api.replay.apiserver;

import static com.sportradar.api.replay.apiserver.ApiServerConfig.newApiServerConfig;
import static com.sportradar.api.replay.logparser.filesystem.TestFileResolver.logsFromTestFolder;

import com.sportradar.api.replay.apiserver.handlers.ApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.ApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.NoopApiHandlerDelay;
import com.sportradar.api.replay.apiserver.handlers.canned.CannedResponseApiHandlerFactory;
import com.sportradar.api.replay.apiserver.handlers.logbacked.LogBackedApiHandlerFactory;
import com.sportradar.api.replay.apiserver.responses.ApiLogEntryProvider;
import com.sportradar.api.replay.apiserver.responses.ApiLogEntryProviderFactory;
import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ApiServerStubRunner {

  private final ApiServerConfig serverConfig =
      newApiServerConfig().withPort(8080).withBookmaker(999).build();

  public ApiServerStub createCannedResponses() {
    ApiHandlerConfig apiHandlerConfig = new ApiHandlerConfig();
    ApiHandlerFactory apiHandlerFactory =
        new CannedResponseApiHandlerFactory(serverConfig, apiHandlerConfig);
    return new ApiServerStubFactory(serverConfig, apiHandlerFactory).create();
  }

  public ApiServerStub createLogBacked(List<File> logfiles) {
    ApiLogEntryProvider apiLogEntryProvider = createApiLogEntryProvider(logfiles);
    ApiHandlerDelay noDelay = new NoopApiHandlerDelay();
    ApiHandlerFactory apiHandlerFactory =
        new LogBackedApiHandlerFactory(apiLogEntryProvider, noDelay);
    return new ApiServerStubFactory(serverConfig, apiHandlerFactory).create();
  }

  private ApiLogEntryProvider createApiLogEntryProvider(List<File> logfiles) {
    ApiLogEntryProviderFactory providerFactory = new ApiLogEntryProviderFactory();
    ApiLogProcessorListener emptyListener = new ApiLogProcessorListener() {};
    return providerFactory.create(logfiles, emptyListener);
  }

  public void run(ApiServerStub apiServerStub) {
    apiServerStub.start();

    if (pingHost("localhost", serverConfig.getPort(), 5000)) {
      System.out.println("API Server is alive");
    } else {
      System.out.println("API Server not detected");
    }
  }

  private boolean pingHost(String host, int port, int timeout) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), timeout);
      return true;
    } catch (IOException e) {
      return false; // Either timeout or unreachable or failed DNS lookup.
    }
  }

  public static void main(String[] args) {
    boolean logBacked = false;

    ApiServerStubRunner runner = new ApiServerStubRunner();
    ApiServerStub server;

    if (logBacked) {
      List<File> files = logsFromTestFolder("restTraffic");
      server = runner.createLogBacked(files);
    } else {
      server = runner.createCannedResponses();
    }

    runner.run(server);
  }
}
