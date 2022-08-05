package com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitMqExpectations {

  private final RabbitMqFixture fixture;
  private final RabbitMqConfig rabbitMqConfig;
  private final boolean usingTestContainers;

  public RabbitMqExpectations isConfiguredForBookmaker(int bookmakerID) {
    fixture.forBookmakerID(bookmakerID);
    return this;
  }

  public void isRunning() {
    if (usingTestContainers) {
      RabbitMqPorts rabbitMqPorts = fixture
          .withUsername(rabbitMqConfig.getUsername())
          .withPassword(rabbitMqConfig.getPassword())
          .startRabbitMQ();
      // TestContainers assigns dynamic ports to RabbitMQ each time, so we save them here
      rabbitMqConfig.setPort(rabbitMqPorts.getAmqpPort());
      rabbitMqConfig.setHttpPort(rabbitMqPorts.getHttpPort());
    }
  }
}
