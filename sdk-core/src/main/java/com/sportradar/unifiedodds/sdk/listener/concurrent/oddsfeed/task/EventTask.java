package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import lombok.Getter;
import lombok.ToString;
import lombok.ToString.Exclude;


@ToString
class EventTask implements Task {

  @Getter
  private final TaskID taskID;
  @Getter
  @Exclude
  private final RecoveryContext context;
  @Exclude
  private final Runnable runnable;

  private EventTask(TaskID taskID, Runnable runnable, RecoveryContext context) {
    this.taskID = taskID == null ? TaskID.none() : taskID;
    this.runnable = runnable;
    this.context = context == null ? RecoveryContext.none() : context;
  }

  public static EventTask newEventTask(TaskID taskID, Runnable runnable,
      RecoveryContext context) {
    return new EventTask(taskID, runnable, context);
  }

  public static EventTask newEventErrorTask(TaskID taskID, Runnable runnable) {
    return new EventTask(taskID, runnable, null);
  }

  public static EventTask newErrorTask(Runnable runnable) {
    return new EventTask(null, runnable, null);
  }

  @Override
  public void run() {
    runnable.run();
  }
}
