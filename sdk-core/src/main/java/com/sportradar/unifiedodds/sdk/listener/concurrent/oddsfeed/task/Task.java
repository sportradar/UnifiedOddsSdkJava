package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;

interface Task extends Runnable {

  TaskID getTaskID();

  RecoveryContext getContext();
}
