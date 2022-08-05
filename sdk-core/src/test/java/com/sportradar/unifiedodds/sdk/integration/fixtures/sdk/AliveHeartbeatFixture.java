package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.sportradar.unifiedodds.example.player.MessagePublisher;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqConfig;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AliveHeartbeatFixture {

  private final RabbitMqConfig rabbitMqConfig;

  private int frequencySecs = 10;

  private int bookmakerID;

  private MessagePublisher publisher;

  public AliveHeartbeatFixture forBookmakerID(int bookmakerID) {
    this.bookmakerID = bookmakerID;
    return this;
  }

  public AliveHeartbeatFixture withFrequencySecs(int frequencySecs) {
    this.frequencySecs = frequencySecs;
    return this;
  }

  public void isRunning() {
    String amqpHost = rabbitMqConfig.getHost();
    int port = rabbitMqConfig.getPort();
    String username = rabbitMqConfig.getUsername();
    String password = rabbitMqConfig.getPassword();

    if (publisher == null) {
      publisher = new MessagePublisher(amqpHost, port, username, password, bookmakerID);
      publisher.init();

      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

      scheduler.scheduleAtFixedRate(this::onHeartbeat, 1, frequencySecs, SECONDS);
    }
  }

  public void onHeartbeat() {
    try {
      publisher.publishMessage("-.-.-.alive.-.-.-.-", ("<alive product=\"1\" timestamp=\"" + System.currentTimeMillis() + "\" subscribed=\"1\"/>").getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
