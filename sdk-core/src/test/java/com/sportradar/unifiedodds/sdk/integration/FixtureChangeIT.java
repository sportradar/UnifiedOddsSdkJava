package com.sportradar.unifiedodds.sdk.integration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.sportradar.uf.datamodel.UFSnapshotComplete;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Test;

public class FixtureChangeIT extends AbstractSdkIntegrationTest {

  private final SDKGlobalEventsListenerTestImpl listener = new SDKGlobalEventsListenerTestImpl();
  private final EurobetMessageListener oddsFeedListener = new EurobetMessageListener();

  private static final String LOG_SET_FOLDER = "sdk_log_sets/busy_sat";

  int offset;

  // FIXME : hard coded feed id
  private final FeedMessageBuilder feedMessageBuilder = new FeedMessageBuilder(1);

  @Override
  protected boolean useLogBackedApi() {
    return false;
  }

  @Test
  public void should_keep_odds_feed_running_using_alive_messages() {
    Instant nowUtc = Instant.now();
    ZoneId zone = ZoneId.systemDefault();//ZoneId.of(configModel.getTimezone());
    ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, zone);
    offset = nowAsiaSingapore.getOffset().getTotalSeconds();

    int frequencyMillis = 100;
    givenRabbitMQ().isRunning();
    givenApi().withNumberOfProducers(3).hasRandomMessageListenerDelays(100, 1000).isStarted();

    // Test 1 - existing listener
    //givenOddsFeed().hasListener(listener).hasOddsFeedListener(oddsFeedListener);

    // Test 2 - concurrent listener
    givenOddsFeed().usingConcurrentListener().hasListener(listener)
        .hasOddsFeedListener(oddsFeedListener);

    whenOddsFeed().isStarted();

    whenAliveHeartbeat().isRunning();
    sleep().forSeconds(10);

// Check producer
    Producer producer = getOddsFeed().getProducerManager().getProducer(1);
    assertNotNull(producer.getRecoveryInfo());
    assertTrue(producer.isFlaggedDown());
    assertTrue(producer.getRecoveryInfo().getRequestId() > 0);

    // Now send a snapshot complete message which should result in the producer up event
    UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(1,
        producer.getRecoveryInfo().getRequestId(), new Date());
    String message = Helper.serializeToJaxbXml(snapshotComplete);
    getPublisher().publishMessage("-.-.-.snapshot_complete.-.-.-.-",
        message.getBytes(StandardCharsets.UTF_8));

    await().atMost(20, SECONDS).until(listener.verifyProducerUp(1));

    //getPublisher().publishMessage("hi.-.live.fixture_change.1.sr:match.33275469.-", ("<fixture_change start_time=\"1651322400000\" product=\"1\" event_id=\"sr:match:33275469\" timestamp=\"" + System.currentTimeMillis() + "\"/>").getBytes(StandardCharsets.UTF_8));
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(this::onFixtureChange, 1, frequencyMillis, MILLISECONDS);
    //hi.-.live.odds_change.2.sr:match.33126037.-
    sleep().forSeconds(60);
  }

  public void onFixtureChange() {
    long localTime = new Date().getTime() - offset * 1000L;
    Date newDate = new Date(localTime);
    long millisUtc = newDate.getTime();

    long matchId = System.currentTimeMillis();
    try {
      getPublisher().publishMessage("hi.-.live.fixture_change.1.sr:match." + matchId + ".-",
          ("<fixture_change start_time=\"1651322400000\" product=\"1\" event_id=\"sr:match:"
              + matchId + "\" timestamp=\"" + millisUtc + "\"/>").getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
