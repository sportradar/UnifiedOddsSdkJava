package com.sportradar.unifiedodds.sdk.integration.fixtures.logreplay;

import com.sportradar.unifiedodds.sdk.integration.fixtures.logfiles.TestLogfiles;
import com.sportradar.unifiedodds.sdk.integration.fixtures.rabbitmq.RabbitMqConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogReplayFixture {

  private final RabbitMqConfig rabbitMqConfig;
  private final List<File> logFiles = new ArrayList<>();
  private int bookmakerID;

  public LogReplayFixture forBookmakerID(int bookmakerID) {
    this.bookmakerID = bookmakerID;
    return this;
  }

  public LogReplayFixture withLogfile(String testLogfile) {
    File file = TestLogfiles.logfileFromTestFolder(testLogfile);
    logFiles.add(file);
    return this;
  }

  public void withLogfilesFromFolder(String testLogfileFolder) {
    List<File> files = TestLogfiles.logsFromTestFolder(testLogfileFolder);
    logFiles.addAll(files);
  }

  public void replayLogs() {
    if (bookmakerID <= 0) {
      throw new IllegalStateException("No bookmakerID provided!");
    }
    if (logFiles.isEmpty()) {
      throw new IllegalStateException("No logs provided!");
    }
    LogReplayer logReplayer = new LogReplayer(bookmakerID, rabbitMqConfig, logFiles);
    logReplayer.replayLogs();
  }
}
