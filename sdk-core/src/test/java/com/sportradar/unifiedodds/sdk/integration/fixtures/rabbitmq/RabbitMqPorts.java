package com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RabbitMqPorts {

  private final int amqpPort;
  private final int httpPort;
}
