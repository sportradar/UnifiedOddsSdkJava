package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqConfig;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.ConcurrentOddsFeedListenerConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OddsFeedConfigurationFixture {

  private final OddsFeedConfiguration configuration;

  public static OddsFeedConfigurationFixture.Builder newOddsFeedConfiguration() {
    return new Builder();
  }

  public OddsFeedConfiguration getConfiguration() {
    return configuration;
  }

  private OddsFeedConfigurationFixture(Builder builder) {
    this.configuration = builder.configuration;
  }

  public static class Builder {

    private static final int HTTP_CLIENT_TIMEOUT = 30; // from OddsFeedConfiguration
    private static final int HTTP_CLIENT_MAX_CONN_TOTAL = 20; // from OddsFeedConfiguration
    private static final int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 15; // from OddsFeedConfiguration
    private static final int RECOVERY_HTTP_CLIENT_TIMEOUT = 30; // from OddsFeedConfiguration
    private static final int RECOVERY_HTTP_CLIENT_MAX_CONN_TOTAL = 20; // from OddsFeedConfiguration
    private static final int RECOVERY_HTTP_CLIENT_MAX_CONN_PER_ROUTE = 15; // from OddsFeedConfiguration

    private OddsFeedConfiguration configuration;
    private RabbitMqConfig rabbitMqConfig;

    private int bookmakerID;
    private String accessToken;
    private final Locale defaultLocale = Locale.ENGLISH;
    private final List<Locale> desiredLocales = new ArrayList<>();
    private final boolean useApiSsl = false;
    private final String apiHost = "localhost"; // localhost is Wiremock server
    private int apiPort = 8080;
    private final int inactivitySeconds = 20; // 20 is minimum from OddsFeedConfigurationBuilderImpl
    private final int maxRecoveryExecutionMinutes = 60; // from test/resources/application.yml
    private final int minIntervalBetweenRecoveryRequests = 35; // from test/resources/application.yml
    private final Integer sdkNodeId = 1;
    private final boolean useIntegrationEnvironment = false;
    private final List<Integer> disabledProducers = new ArrayList<>();
    private final ExceptionHandlingStrategy exceptionHandlingStrategy = ExceptionHandlingStrategy.Throw;
    boolean concurrentListenerEnabled = false; // TODO enable by default?
    int concurrentListenerThreads = ConcurrentOddsFeedListenerConfig.THREADS_DEFAULT;
    int concurrentListenerQueueSize = ConcurrentOddsFeedListenerConfig.QUEUE_SIZE_DEFAULT;
    boolean concurrentListenerHandleErrorsAsynchronously = true;

    public Builder forBookmakerID(int bookmakerID) {
      this.bookmakerID = bookmakerID;
      return this;
    }

    public Builder withApiPort(int apiPort) {
      this.apiPort = apiPort;
      return this;
    }

    public Builder withAccessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    public Builder withRabbitMqConfig(RabbitMqConfig rabbitMqConfig) {
      this.rabbitMqConfig = rabbitMqConfig;
      return this;
    }

    public Builder withConcurrentListenerEnabled() {
      this.concurrentListenerEnabled = true;
      return this;
    }

    public Builder withConcurrentListenerThreads(int threadCount) {
      this.concurrentListenerThreads = threadCount;
      return this;
    }

    public Builder withConcurrentListenerQueueSize(int queueSize) {
      this.concurrentListenerQueueSize = queueSize;
      return this;
    }

    public Builder withConcurrentListenerHandleErrorsOnRabbitThread( ) {
      this.concurrentListenerHandleErrorsAsynchronously = false;
      return this;
    }

    public OddsFeedConfigurationFixture build() {
      if (bookmakerID <= 0) {
        throw new IllegalArgumentException("Bookmaker ID is required!");
      }
      if (accessToken == null) {
        throw new IllegalArgumentException("Access Token is required!");
      }
      configuration = new OddsFeedConfiguration(accessToken,
          defaultLocale,
          desiredLocales,
          rabbitMqConfig.getHost(),
          apiHost,
          apiPort,
          inactivitySeconds,
          maxRecoveryExecutionMinutes,
          minIntervalBetweenRecoveryRequests,
          rabbitMqConfig.isUseSSL(),
          useApiSsl,
          rabbitMqConfig.getPort(),
          rabbitMqConfig.getUsername(),
          rabbitMqConfig.getPassword(),
          sdkNodeId,
          useIntegrationEnvironment,
          disabledProducers,
          exceptionHandlingStrategy,
          Environment.GlobalProduction,
          rabbitMqConfig.getVirtualHost(bookmakerID),
          HTTP_CLIENT_TIMEOUT,
          HTTP_CLIENT_MAX_CONN_TOTAL,
          HTTP_CLIENT_MAX_CONN_PER_ROUTE,
          RECOVERY_HTTP_CLIENT_TIMEOUT,
          RECOVERY_HTTP_CLIENT_MAX_CONN_TOTAL,
          RECOVERY_HTTP_CLIENT_MAX_CONN_PER_ROUTE,
          concurrentListenerConfig());
      return new OddsFeedConfigurationFixture(this);
    }

    public final ConcurrentListenerConfig concurrentListenerConfig() {
      return ConcurrentListenerConfig.builder()
          .enabled(concurrentListenerEnabled)
          .threads(concurrentListenerThreads)
          .queueSize(concurrentListenerQueueSize)
          .handleErrorsAsynchronously(concurrentListenerHandleErrorsAsynchronously)
          .build();
    }
  }
}
