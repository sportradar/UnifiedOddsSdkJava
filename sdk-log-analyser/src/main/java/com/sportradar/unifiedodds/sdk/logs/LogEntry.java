package com.sportradar.unifiedodds.sdk.logs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;

public class LogEntry {
  @Getter
  @Setter
  private Date timestamp;

  @Getter
  @Setter
  private LogEntryType type;

  @Getter
  @Setter
  private TreeSet<String> producers = new TreeSet<>();
  @Getter
  @Setter
  private TreeSet<String> reasons = new TreeSet<>();

  @Getter
  @Setter
  private int exceptionCount;
  @Getter
  @Setter
  private int channelRecoveryExceptionCount = 0;
  @Getter
  @Setter
  private int connectionRecoveryExceptionCount = 0;
  @Getter
  @Setter
  private int topologyRecoveryExceptionCount = 0;
  @Getter
  @Setter
  private int connectionDriverExceptionCount = 0;

  @Getter
  @Setter
  private TreeMap<String, Integer> exceptionBreakdown = new TreeMap<>();

  @Getter
  @Setter
  private long durationMillis;

  @Getter
  @Setter
  private long messageDelayMillis;

  @Getter
  @Setter
  private Date messageCreationTimestamp;

  @Getter
  @Setter
  private String rabbitMessageType;

  @Getter
  @Setter
  private LogEntryModel logEntryModel;

  private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

//  @Override
//  public String toString() {
//    return "LogEntry{" +
//            "timestamp=" + timestamp +
//            ", type=" + type +
//            ", producers=" + producers +
//            ", reasons=" + reasons +
//            '}';
//  }

  public void incrementChannelRecoveryExceptionCount() {
    Integer val = exceptionBreakdown.get("Channel Recovery");
    if (val == null) {
      exceptionBreakdown.put("Channel Recovery", 1);
    } else {
      exceptionBreakdown.put("Channel Recovery", val + 1);
    }
  }

  public void incrementConnectionDriverExceptionCount() {
    Integer val = exceptionBreakdown.get("Connection Driver");
    if (val == null) {
      exceptionBreakdown.put("Connection Driver", 1);
    } else {
      exceptionBreakdown.put("Connection Driver", val + 1);
    }
  }

  public void incrementTopologyRecoveryExceptionCount() {
    Integer val = exceptionBreakdown.get("Topology Recovery");
    if (val == null) {
      exceptionBreakdown.put("Topology Recovery", 1);
    } else {
      exceptionBreakdown.put("Topology Recovery", val + 1);
    }
  }

  public void incrementConnectionRecoveryExceptionCount() {
    Integer val = exceptionBreakdown.get("Connection Recovery");
    if (val == null) {
      exceptionBreakdown.put("Connection Recovery", 1);
    } else {
      exceptionBreakdown.put("Connection Recovery", val + 1);
    }
  }


  @Override
  public String toString() {
    return dateTimeFormat.format(timestamp) + " " + producers + " " + reasons;
  }

  public String getExceptionDetails() {
    TreeMap<String, Integer> tm = new TreeMap<>();
    if (connectionDriverExceptionCount > 0) tm.put("Connection Driver", connectionDriverExceptionCount);
    if (connectionRecoveryExceptionCount > 0) tm.put("Connection Recovery", connectionRecoveryExceptionCount);
    if (channelRecoveryExceptionCount > 0) tm.put("Channel Recovery", channelRecoveryExceptionCount);
    if (topologyRecoveryExceptionCount > 0) tm.put("Topology Recovery", topologyRecoveryExceptionCount);
    if (tm.size() == 0) {
      return "";
    } else {
      return tm.toString().replace("{", "(").replace("}", ")");
    }
  }
}
