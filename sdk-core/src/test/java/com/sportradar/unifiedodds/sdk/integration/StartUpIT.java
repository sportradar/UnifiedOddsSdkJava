package com.sportradar.unifiedodds.sdk.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import com.sportradar.uf.datamodel.UFSnapshotComplete;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.Test;

/**
 * A simple test that starts the
 */
public class StartUpIT extends AbstractSdkIntegrationTest {

  private final SDKGlobalEventsListenerTestImpl listener = new SDKGlobalEventsListenerTestImpl();
  private final EurobetMessageListener oddsFeedListener = new EurobetMessageListener();

  private final FeedMessageBuilder feedMessageBuilder = new FeedMessageBuilder(1);

  int offset;

  @Override
  protected boolean useLogBackedApi() {
    return false;
  }

  @Test
  public void should_start_running_using_alive_messages() {
    Instant nowUtc = Instant.now();
    ZoneId zone = ZoneId.systemDefault();
    ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, zone);
    offset = nowAsiaSingapore.getOffset().getTotalSeconds();

    ApiServerRequestListener requestListener =
        (endpoint, path, queryParameters) -> {
          if (endpoint == ApiEndpoint.RecoveryInitiateRequest) {
            int producerId = 1;
            int startIndex = path.indexOf("producer");
            if (startIndex > 0) {
              int endIndex = path.indexOf("/", startIndex);
              String producerIdStr = path.substring(startIndex + 8, endIndex);
              producerId = Integer.parseInt(producerIdStr);
            }

            long requestId = 0;
            if (queryParameters.containsKey("request_id")) {
              String requestIdStr = queryParameters.get("request_id").getFirst();
              if (requestIdStr != null) {
                requestId = Long.parseLong(requestIdStr);
              }
            }

            int finalProducerId = producerId;
            long finalRequestId = requestId;

            Thread asyncSnapshotComplete =
                new Thread(
                    () -> {
                      // wait 10 secs as don't want to start too fast!
                      sleep().forSeconds(10);
                      UFSnapshotComplete snapshotComplete =
                          feedMessageBuilder.buildSnapshotComplete(
                              finalProducerId, finalRequestId, new Date());
                      String message = Helper.serializeToJaxbXml(snapshotComplete);
                      getPublisher()
                          .publishMessage(
                              "-.-.-.snapshot_complete.-.-.-.-",
                              message.getBytes(StandardCharsets.UTF_8));
                    });

            asyncSnapshotComplete.start();
          }
        };

    givenRabbitMQ().isRunning();
    givenApi()
        .withNumberOfProducers(3)
        .hasRandomMessageListenerDelays(100, 1000)
        .withRequestListener(requestListener)
        .isStarted();

    // Test 1 - existing listener
    givenOddsFeed().hasListener(listener).hasOddsFeedListener(oddsFeedListener);

    whenOddsFeed().isStarted();

    whenAliveHeartbeat().isRunning();
    sleep().forSeconds(10);

    // Check producer
    Producer producer = getOddsFeed().getProducerManager().getProducer(1);
    assertNotNull(producer.getRecoveryInfo());
    assertTrue(producer.isFlaggedDown());
    assertTrue(producer.getRecoveryInfo().getRequestId() > 0);

    await().atMost(20, SECONDS).until(listener.verifyProducerUp(1));
  }
}
