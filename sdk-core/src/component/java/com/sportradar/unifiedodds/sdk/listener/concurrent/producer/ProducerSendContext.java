package com.sportradar.unifiedodds.sdk.listener.concurrent.producer;

import lombok.Getter;

@Getter
public class ProducerSendContext {

  private final int totalEvents;
  private final int producerUpIndex;

  private ProducerSendContext(Builder builder) {
    this.totalEvents = builder.totalEvents;
    this.producerUpIndex = builder.producerUpIndex;
  }

  public static Builder sending() {
    return new Builder();
  }

  public static class Builder {

    private int totalEvents = -1;
    private int producerUpIndex = -1;

    public Builder sportEvents(int totalEvents) {
      this.totalEvents = totalEvents;
      return this;
    }

    public Builder withNoProducerUpSent() {
      this.producerUpIndex = -1;
      return this;
    }

    public Builder producerUpSentFirst() {
      return producerUpSentBeforeEvent(0);
    }

    public Builder producerUpSentDuringEvents() {
      verifyTotalEventsSet();
      return producerUpSentBeforeEvent(totalEvents / 2);
    }

    public Builder producerUpSentLast() {
      verifyTotalEventsSet();
      return producerUpSentBeforeEvent(totalEvents);
    }

    public Builder producerUpSentBeforeEvent(int producerUpIndex) {
      this.producerUpIndex = producerUpIndex;
      return this;
    }

    public Builder and() {
      return this;
    }

    public Builder with() {
      return this;
    }

    public Builder followedBy() {
      return this;
    }

    public ProducerSendContext build() {
      if (totalEvents < 0) {
        throw new IllegalStateException("totalEvents must be greater than zero!");
      }
      return new ProducerSendContext(this);
    }

    private void verifyTotalEventsSet() {
      if (totalEvents < 0) {
        throw new IllegalStateException("totalEvents must be set before ProducerUp!");
      }
    }
  }
}
