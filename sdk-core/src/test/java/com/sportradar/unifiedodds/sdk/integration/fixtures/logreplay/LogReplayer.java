package com.sportradar.unifiedodds.sdk.integration.fixtures.logreplay;

import com.sportradar.unifiedodds.example.player.MessagePlayer;
import com.sportradar.unifiedodds.example.player.exceptions.LogFileNotFound;
import com.sportradar.unifiedodds.example.player.exceptions.MalformedLogEntry;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqConfig;
import java.io.File;
import java.util.List;

public class LogReplayer {

  private final int bookmakerId;
  private final RabbitMqConfig rabbitMqConfig;
  private final List<File> logFiles;

  public LogReplayer(int bookmakerId, RabbitMqConfig rabbitMqConfig, List<File> logFiles) {
    this.bookmakerId = bookmakerId;
    this.rabbitMqConfig = rabbitMqConfig;
    this.logFiles = logFiles;
  }

  public void replayLogs() {
    new Thread(this::run, "LogReplay").start();
  }

  private void run() {
    String amqpHost = rabbitMqConfig.getHost();
    int port = rabbitMqConfig.getPort();
    String username = rabbitMqConfig.getUsername();
    String password = rabbitMqConfig.getPassword();

    MessagePlayer player = new MessagePlayer(logFiles, amqpHost, port, bookmakerId, username,
        password);
    try {
      player.publishLogs();
    } catch (LogFileNotFound | MalformedLogEntry e) {
      throw new RuntimeException("Error replaying logs!", e);
    }
  }
}
