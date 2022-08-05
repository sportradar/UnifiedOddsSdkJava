package com.sportradar.unifiedodds.sdk.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.sportradar.uf.datamodel.UFSnapshotComplete;
import com.sportradar.unifiedodds.example.player.MessagePublisher;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.Test;

public class OneProducerNoMessagesIT extends AbstractSdkIntegrationTest {

  private final SDKGlobalEventsListenerTestImpl listener = new SDKGlobalEventsListenerTestImpl();

  // FIXME : hard coded feed id
  private final FeedMessageBuilder feedMessageBuilder = new FeedMessageBuilder(1);

  @Override
  protected boolean useLogBackedApi() {
    return false;
  }

  // @Ignore
  @Test
  public void should_keep_odds_feed_running_using_alive_messages() {
    givenRabbitMQ().isRunning();
    givenApi().withNumberOfProducers(3).isStarted();

    givenOddsFeed().hasListener(listener).hasRandomMessageListenerDelays(100, 4000);

    whenOddsFeed().isStarted();

    whenAliveHeartbeat().isRunning();
    sleep().forSeconds(10);

    /*
    FIXME Publish a message to the rabbit q. This should be tidied up
     */
    String amqpHost = getRabbitMqConfig().getHost();
    int port = getRabbitMqConfig().getPort();
    String username = getRabbitMqConfig().getUsername();
    String password = getRabbitMqConfig().getPassword();

    MessagePublisher publisher = new MessagePublisher(amqpHost, port, username, password,
        bookmakerID);
    publisher.init();

    Producer producer = getOddsFeed().getProducerManager().getProducer(1);
    assertNotNull(producer.getRecoveryInfo());
    assertTrue(producer.isFlaggedDown());
    assertTrue(producer.getRecoveryInfo().getRequestId() > 0);

    // Now send a snapshot complete message which should result in the producer up event
    UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(1,
        producer.getRecoveryInfo().getRequestId(), new Date());
    String message = Helper.serializeToJaxbXml(snapshotComplete);
    publisher.publishMessage("-.-.-.snapshot_complete.-.-.-.-",
        message.getBytes(StandardCharsets.UTF_8));

    await().atMost(20, SECONDS).until(listener.verifyProducerUp(1));

    sleep().forSeconds(10);
  }
}
