package com.sportradar.unifiedodds.sdk.listener.concurrent;

import java.util.Random;

public class IdGenerator {

  private final Random random = new Random();

  public long randomLong() {
    return randomInt();
  }

  public int randomInt() {
    return random.ints(1, 9999).findFirst().getAsInt();
  }
}
