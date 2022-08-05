package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TrackedTask implements Task {

  private final TaskLifecycleTracker lifecycleTracker;
  private final EventTask task;

  @Override
  public TaskID getTaskID() {
    return task.getTaskID();
  }

  @Override
  public RecoveryContext getContext() {
    return task.getContext();
  }

  @Override
  public void run() {
    lifecycleTracker.taskStarted(this);
    try {
      task.run();
    } finally {
      lifecycleTracker.taskCompleted(this);
    }
  }
}
