package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class ProducerID {

  @Getter
  private final int id;

  private ProducerID(int id) {
    this.id = id;
  }

  public static ProducerID valueOf(int producerID) {
    return new ProducerID(producerID);
  }
}
