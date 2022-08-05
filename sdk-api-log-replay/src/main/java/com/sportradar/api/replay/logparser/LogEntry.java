package com.sportradar.api.replay.logparser;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogEntry {

  private Date timestamp;
  private String url;
  private String httpMethod;
  private int durationInMillis;
  private boolean success;
  private String xml;
}
