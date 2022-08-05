package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.TaskLifecycleTracker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TrackedTaskFactory {

  private final TaskLifecycleTracker lifecycleTracker;

  public TrackedTask create(EventTask eventTask) {
    return new TrackedTask(lifecycleTracker, eventTask);
  }
}
