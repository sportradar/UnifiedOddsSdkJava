package com.sportradar.unifiedodds.sdk.listener.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtomicReadWrite {

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();

  public void read(Runnable runnable) {
    readLock.lock();
    try {
      runnable.run();
    } finally {
      readLock.unlock();
    }
  }

  public void write(Runnable runnable) {
    writeLock.lock();
    try {
      runnable.run();
    } finally {
      writeLock.unlock();
    }
  }
}
