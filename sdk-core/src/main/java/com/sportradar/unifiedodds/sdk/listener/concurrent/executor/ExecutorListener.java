package com.sportradar.unifiedodds.sdk.listener.concurrent.executor;

import java.util.concurrent.ExecutorService;

public interface ExecutorListener {

  ExecutorListener NOP = new ExecutorListener() {
  };

  default void onCreate(ExecutorService executorService) {
  }

  default void onCreate(Thread thread) {
  }
}
