package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import static com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfigurationFixture.newOddsFeedConfiguration;

import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfigurationFixture;
import com.sportradar.unifiedodds.sdk.conn.SdkConnListener;
import lombok.Getter;

public class OddsFeedFixture {

  @Getter
  private final OddsFeed oddsFeed;
  @Getter
  private final OddsFeedConfiguration configuration;

  @Getter
  private int minMessageListenerDelayMs;

  @Getter
  private int maxMessageListenerDelayMs;

  @Getter
  private final OddsFeedListener oddsFeedListener;

  public static OddsFeedFixture.Builder newOddsFeed() {
    return new OddsFeedFixture.Builder();
  }

  private OddsFeedFixture(Builder builder) {
    this.oddsFeed = builder.oddsFeed;
    this.configuration = builder.configuration;
    this.minMessageListenerDelayMs = builder.minMessageListenerDelayMs;
    this.maxMessageListenerDelayMs = builder.maxMessageListenerDelayMs;
    this.oddsFeedListener = builder.oddsFeedListener;
  }

  public static class Builder {

    private OddsFeed oddsFeed;
    private SDKGlobalEventsListener globalEventsListener = new SdkConnListener();
    private OddsFeedConfiguration configuration;
    private OddsFeedConfigurationFixture.Builder configurationBuilder = newOddsFeedConfiguration();

    private int minMessageListenerDelayMs = 0;

    private int maxMessageListenerDelayMs = 0;

    private OddsFeedListener oddsFeedListener;

    public Builder withConfig(OddsFeedConfigurationFixture.Builder configurationBuilder) {
      this.configurationBuilder = configurationBuilder;
      return this;
    }

    public Builder withGlobalEventsListener(SDKGlobalEventsListener listener) {
      this.globalEventsListener = listener;
      return this;
    }

    public Builder withOddsFeedListener(OddsFeedListener listener) {
      this.oddsFeedListener = listener;
      return this;
    }

    public Builder withRandomMessageListenerDelays(int minDelayMs, int maxDelayMs) {
      this.minMessageListenerDelayMs = minDelayMs;
      this.maxMessageListenerDelayMs = maxDelayMs;
      return this;
    }

    public OddsFeedFixture build() {
      OddsFeedConfigurationFixture configurationFixture = configurationBuilder.build();
      configuration = configurationFixture.getConfiguration();
      oddsFeed = new OddsFeed(globalEventsListener, configuration);
      return new OddsFeedFixture(this);
    }
  }
}
