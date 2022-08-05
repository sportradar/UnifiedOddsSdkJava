package com.sportradar.unifiedodds.sdk.integration;

import org.junit.Ignore;
import org.junit.Test;

// STEPS:
// rename log dir so fresh logs will be captured
// run this test
// run SdkLogReplay (can add more logs or change duration between messages)
// check uf-sdk-rest-traffic.log for any FAILED rest calls and ProducerDown
//  - search for "FAILED" in the uf-sdk-rest-traffic.log for any missing calls
public class BasicSdkIT extends AbstractSdkIntegrationTest {

  private static final String LOG_SET_FOLDER = "sdk_log_sets/busy_sat";

  @Ignore
  @Test
  public void should_play_back_api_and_rabbit_logs() {
    givenApi()
        .replayingLogsFrom(LOG_SET_FOLDER + "/restTraffic")
        .withDelaysEnabled()
        .isStarted();
    givenRabbitMQ().isRunning();

    whenOddsFeed().isStarted();
    // FIXME add a way to play back RabbitMQ messages at 1x, 5, 10x speed
    whenLogs().logsFromFolder(LOG_SET_FOLDER + "/traffic").arePlayedBack();

    // FIXME use awaitility to wait until all rabbit messages sent and all api responses have been sent
    sleep().forMinutes(30);
  }

  @Ignore
  @Test
  public void should_start_api_server_lazily_without_delays() {
    givenApi()
        .replayingLogsFrom(LOG_SET_FOLDER + "/restTraffic")
        .isStarted();

    sleep().forMinutes(30);
  }

  @Ignore
  @Test
  public void should_start_api_server_lazily_with_delays() {
    givenApi()
        .replayingLogsFrom(LOG_SET_FOLDER + "/restTraffic")
        .withDelaysEnabled()
        .isStarted();

    sleep().forMinutes(30);
  }

  @Ignore
  @Test
  public void should_load_api_logs_from_test_folder() {
    givenApi()
        .replayingLogsFrom(LOG_SET_FOLDER + "/restTraffic");
  }

  @Ignore
  @Test
  public void should_load_rabbitmq_logs_from_test_folder() {
    whenLogs()
        .logsFromFolder(LOG_SET_FOLDER + "/traffic");
  }
}
