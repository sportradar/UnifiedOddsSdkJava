package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TaskID {

  private static final Random random = new Random();
  @Getter
  private final long id;

  private TaskID(long id) {
    this.id = id;
  }

  public static TaskID valueOf(long eventID) {
    return new TaskID(eventID);
  }

  public static TaskID none() {
    long randomID = random.nextLong();
    return new TaskID(randomID);
  }
}
