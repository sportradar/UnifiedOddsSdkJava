package com.sportradar.unifiedodds.sdk.logs.config;

import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigModel {
  private String contentEncoding = StandardCharsets.UTF_8.displayName();
  private String customer;
  private String timezone;

  private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSS";

  // bit offsets
  private Integer logOffsetThreadName;

  private Integer logOffsetLoggerName;
}
