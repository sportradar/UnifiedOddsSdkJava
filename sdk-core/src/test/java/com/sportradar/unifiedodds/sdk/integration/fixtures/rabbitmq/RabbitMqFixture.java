package com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.RabbitMQContainer;

// FIXME start Rabbit using @Rule and allow each test to init a vhost/bookmaker id
@Slf4j
public class RabbitMqFixture {

  private static final String RABBITMQ_IMAGE = "rabbitmq:3.7.25-management-alpine";

  private int bookmakerId;
  private String username;
  private String password;
  private RabbitMQContainer container;

  public RabbitMqFixture forBookmakerID(int bookmakerID) {
    this.bookmakerId = bookmakerID;
    return this;
  }

  public RabbitMqFixture withUsername(String username) {
    this.username = username;
    return this;
  }

  public RabbitMqFixture withPassword(String password) {
    this.password = password;
    return this;
  }

  public RabbitMqPorts startRabbitMQ() {
    if (bookmakerId <= 0) {
      throw new IllegalStateException("Bookmaker ID is not set!");
    }
    try {
      RabbitMqPorts ports = startRabbitMqContainer();
      log.info("RabbitMQ started on port {}, http port {}", ports.getAmqpPort(),
          ports.getHttpPort());
      return ports;
    } catch (IOException e) {
      throw new RuntimeException("Error starting container!", e);
    } catch (InterruptedException e) {
      throw new RuntimeException("Error starting container!", e);
    }
  }

  private RabbitMqPorts startRabbitMqContainer()
      throws IOException, InterruptedException {
    container = new RabbitMQContainer(RABBITMQ_IMAGE);

    String vhost = "/unifiedfeed/" + bookmakerId;
    container
        .withVhost(vhost)
        .withExchange(vhost, "unifiedfeed", "topic", false, false, false,
            Collections.emptyMap())
        .withUser(username, password)
        .withPermission(vhost, username, ".*", ".*",
            ".*");

    container.start();
    verify(container);

    return new RabbitMqPorts(container.getAmqpPort(), container.getHttpPort());
  }

  private void verify(RabbitMQContainer container) throws IOException, InterruptedException {
    String out = container.execInContainer("rabbitmqadmin", "list", "exchanges")
        .getStdout();
    log.info(out);
    assertThat(out).contains("direct", "unifiedfeed");

    out = container.execInContainer("rabbitmqadmin", "list", "users")
        .getStdout();
    log.info(out);
    assertThat(out).contains(username);

    out = container.execInContainer("rabbitmqctl", "list_exchanges", "-p",
        "/unifiedfeed/" + bookmakerId).getStdout();
    log.info(out);
    assertThat(out).containsPattern("unifiedfeed\\s+topic");
  }

  public void stop() {
    if (container != null) {
      container.close();
    }
  }
}
