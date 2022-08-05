package com.sportradar.unifiedodds.sdk.integration;

import static com.sportradar.api.replay.apiserver.ApiServerConfig.newApiServerConfig;
import static com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfigurationFixture.newOddsFeedConfiguration;

import com.sportradar.api.replay.apiserver.ApiServerConfig;
import com.sportradar.api.replay.apiserver.ApiServerMode;
import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.unifiedodds.example.player.MessagePublisher;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfigurationFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.SleepFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.api.ApiServerExpectations;
import com.sportradar.unifiedodds.sdk.integration.fixtures.api.ApiServerStubFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.environment.EnvironmentManagerFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.logreplay.LogReplayExpectations;
import com.sportradar.unifiedodds.sdk.integration.fixtures.logreplay.LogReplayFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqConfig;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqExpectations;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqFixture;
import com.sportradar.unifiedodds.sdk.integration.fixtures.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.BeforeClass;

/*
 * Sequence:
 * specify API logs to load using givenApi().willPlaybackLogsFrom(folder)
 * start API server using givenApi().isStarted()
 * this will pre-load the first log entry which should be the /whoami.xml
 * the processing of /whoami.xml will extract the bookmaker id and notify this classes onBookmakerID()
 * bookmakerID with then be propagated to all fixtures
 */
@Slf4j
public abstract class AbstractSdkIntegrationTest implements ApiLogProcessorListener {

  private static final int API_PORT = 8080;
  private static final String ACCESS_TOKEN = "tkIQxFhK84x4QdgPXR";
  protected int bookmakerID = 999;
  private final boolean usingTestContainers = true;
  private final ApiServerConfig apiServerConfig =
      newApiServerConfig()
          .withMode(useLogBackedApi() ? ApiServerMode.LogBacked : ApiServerMode.Canned)
          .withBookmaker(bookmakerID)
          .withPort(API_PORT)
          .build();

  private final ApiServerStubFixture apiServerStubFixture =
      new ApiServerStubFixture(apiServerConfig, this);
  private final ApiServerExpectations apiServerExpectations =
      new ApiServerExpectations(apiServerStubFixture);
  private final RabbitMqConfig rabbitMqConfigLocalDocker =
      RabbitMqConfig.builder() // Etien's local RabbitMQ
          .port(RabbitMqConfig.DEFAULT_AMQP_PORT)
          .username(ACCESS_TOKEN)
          .password(ACCESS_TOKEN)
          .build();
  private final RabbitMqConfig rabbitMqConfigTestContainers =
      RabbitMqConfig.builder() // testcontainers
          .username(ACCESS_TOKEN)
          .password(ACCESS_TOKEN)
          .build();
  private final RabbitMqConfig rabbitMqConfig =
      usingTestContainers ? rabbitMqConfigTestContainers : rabbitMqConfigLocalDocker;
  private final OddsFeedConfigurationFixture.Builder configBuilder =
      newOddsFeedConfiguration()
          .forBookmakerID(bookmakerID)
          .withAccessToken(ACCESS_TOKEN)
          .withApiPort(API_PORT)
          .withRabbitMqConfig(rabbitMqConfig);
  private final OddsFeedFixture.Builder oddsFeedFixtureBuilder =
      OddsFeedFixture.newOddsFeed().withConfig(configBuilder);
  private final OddsFeedLifecycleFixture oddsFeedLifecycle =
      new OddsFeedLifecycleFixture(oddsFeedFixtureBuilder);
  private final OddsFeedExpectations oddsFeedExpectations =
      new OddsFeedExpectations(configBuilder, oddsFeedFixtureBuilder);
  private final RabbitMqFixture rabbitMqFixture = new RabbitMqFixture();
  private final RabbitMqExpectations rabbitMqExpectations =
      new RabbitMqExpectations(rabbitMqFixture, rabbitMqConfig, usingTestContainers);

  private MessagePublisher publisher;

  @BeforeClass
  public static void overrideApiServerHostToLocalhost() {
    new EnvironmentManagerFixture().setApiServerPort(API_PORT);
  }

  @After
  public void stopOddsFeed() {
    oddsFeedLifecycle.stop();
  }

  @After
  public void stopRabbitMQ() {
    rabbitMqFixture.stop();
  }

  @After
  public void stopApiServerStop() {
    apiServerStubFixture.stop();
  }

  @Override
  public void onBookmakerID(int bookmakerID) {
    log.info("Extracted bookmaker id of {} from logs", bookmakerID);
    this.bookmakerID = bookmakerID;
    configBuilder.forBookmakerID(bookmakerID);
    rabbitMqExpectations.isConfiguredForBookmaker(bookmakerID);
    apiServerExpectations.usingBookmakerID(bookmakerID);
  }

  protected final RabbitMqExpectations givenRabbitMQ() {
    return rabbitMqExpectations.isConfiguredForBookmaker(bookmakerID);
  }

  protected final AliveHeartbeatExpectations whenAliveHeartbeat() {
    AliveHeartbeatFixture aliveHeartbeatFixture = new AliveHeartbeatFixture(rabbitMqConfig);
    AliveHeartbeatExpectations aliveHeartbeatExpectations =
        new AliveHeartbeatExpectations(aliveHeartbeatFixture);
    return aliveHeartbeatExpectations.forBookmakerID(bookmakerID).withFrequencySecs(10);
  }

  protected final OddsFeedExpectations givenOddsFeed() {
    return oddsFeedExpectations;
  }

  protected final ApiServerExpectations givenApi() {
    return apiServerExpectations;
  }

  protected final LogReplayExpectations whenLogs() {
    LogReplayFixture logReplayFixture = new LogReplayFixture(rabbitMqConfig);
    LogReplayExpectations logReplayExpectations = new LogReplayExpectations(logReplayFixture);
    return logReplayExpectations.forBookmakerID(bookmakerID);
  }

  protected final OddsFeedLifecycleExpectations whenOddsFeed() {
    return new OddsFeedLifecycleExpectations(oddsFeedLifecycle);
  }

  protected final SleepFixture sleep() {
    return new SleepFixture();
  }

  protected boolean useLogBackedApi() {
    return true;
  }

  public RabbitMqConfig getRabbitMqConfig() {
    return rabbitMqConfig;
  }

  public OddsFeed getOddsFeed() {
    return oddsFeedLifecycle.getOddsFeed();
  }

  public MessagePublisher getPublisher() {
    if (publisher == null) {
      String amqpHost = getRabbitMqConfig().getHost();
      int port = getRabbitMqConfig().getPort();
      String username = getRabbitMqConfig().getUsername();
      String password = getRabbitMqConfig().getPassword();

      this.publisher = new MessagePublisher(amqpHost, port, username, password, bookmakerID);
      publisher.init();
    }

    return publisher;
  }
}
