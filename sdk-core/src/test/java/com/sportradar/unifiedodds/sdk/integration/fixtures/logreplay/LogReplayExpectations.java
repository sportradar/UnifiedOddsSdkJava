package com.sportradar.unifiedodds.sdk.integration.fixtures.logreplay;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogReplayExpectations {

  private final LogReplayFixture fixture;

  public LogReplayExpectations logfile(String logfile) {
    fixture.withLogfile(logfile);
    return this;
  }

  public LogReplayExpectations logsFromFolder(String testLogfileFolder) {
    fixture.withLogfilesFromFolder(testLogfileFolder);
    return this;
  }

  public LogReplayExpectations forBookmakerID(int bookmakerID) {
    fixture.forBookmakerID(bookmakerID);
    return this;
  }

  public void isPlayedBack() {
    arePlayedBack();
  }

  public void arePlayedBack() {
    fixture.replayLogs();
  }
}
