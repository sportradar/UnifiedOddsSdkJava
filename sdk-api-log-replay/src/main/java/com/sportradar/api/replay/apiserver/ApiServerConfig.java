package com.sportradar.api.replay.apiserver;

import lombok.Getter;
import lombok.Setter;

public class ApiServerConfig {

  @Getter @Setter private int port;
  @Getter @Setter private int bookmakerID;
  private ApiServerMode mode;
  @Getter @Setter private ApiServerRequestListener requestListener;

  public static Builder newApiServerConfig() {
    return new Builder();
  }

  public boolean isCanned() {
    return mode == ApiServerMode.Canned;
  }

  public boolean isLogBacked() {
    return mode == ApiServerMode.LogBacked;
  }

  private ApiServerConfig(Builder builder) {
    this.port = builder.port;
    this.bookmakerID = builder.bookmakerID;
    this.mode = builder.mode;
    this.requestListener = builder.requestListener;
  }

  public static class Builder {

    private int port = 8080;
    private int bookmakerID = -1;
    private ApiServerMode mode = ApiServerMode.LogBacked;

    private ApiServerRequestListener requestListener;

    public Builder withPort(int port) {
      this.port = port;
      return this;
    }

    public Builder withBookmaker(int bookmakerID) {
      this.bookmakerID = bookmakerID;
      return this;
    }

    public Builder withMode(ApiServerMode mode) {
      this.mode = mode;
      return this;
    }

    public Builder withRequestListener(ApiServerRequestListener requestListener) {
      this.requestListener = requestListener;
      return this;
    }

    public ApiServerConfig build() {
      return new ApiServerConfig(this);
    }
  }
}
