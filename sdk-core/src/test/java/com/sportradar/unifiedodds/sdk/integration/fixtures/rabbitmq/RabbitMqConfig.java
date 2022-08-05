package com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class RabbitMqConfig {

  public static final int DEFAULT_AMQPS_PORT = 5671; // AMQP (SSL)
  public static final int DEFAULT_AMQP_PORT = 5672; // AMQP
  public static final int DEFAULT_HTTPS_PORT = 15671; // HTTP (SSL)
  public static final int DEFAULT_HTTP_PORT = 15672; // HTTP

  private final boolean useSSL = false;
  @Builder.Default
  private String host = "localhost";
  @Builder.Default
  private String username = "guest";
  @Builder.Default
  private String password = "guest";
  @Setter
  private int port;
  @Setter
  private int httpPort;

  public String getVirtualHost(int bookmakerID) {
    return "/unifiedfeed/" + bookmakerID;
  }
}
