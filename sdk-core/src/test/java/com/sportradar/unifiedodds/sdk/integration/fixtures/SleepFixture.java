package com.sportradar.unifiedodds.sdk.integration.fixtures;

public class SleepFixture {

  public SleepFixture forSeconds(int seconds) {
    sleep(seconds);
    return this;
  }

  public SleepFixture forMinutes(int minutes) {
    sleep(minutes * 60);
    return this;
  }

  public SleepFixture forOneMinute() {
    return forMinutes(1);
  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(1000L * seconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
