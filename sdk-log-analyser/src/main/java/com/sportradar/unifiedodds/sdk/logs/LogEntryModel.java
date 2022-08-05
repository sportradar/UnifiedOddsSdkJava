package com.sportradar.unifiedodds.sdk.logs;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogEntryModel {
  private Date timestamp;
  private String threadName;
  private String payload;

  private String loggerName;

  // Api specific data
  private String url;

  // Customer Message Listener
  private Date messageCreationTimestamp;

  private long durationMillis;

  private String rabbitMessageType;

  private String line;

  public boolean isMqThread() {
    return threadName != null && threadName.contains("-amqp-");
  }

//  public boolean isSDKThread() {
//    return threadName != null && threadName.contains("uf-sdk-");
//  }

  public String getRestApiType() {
    if (url.endsWith("/fixture_change_fixture.xml")) {
      return "fixture_change";
    } else if (url.endsWith("/fixture.xml")) {
      return "fixture";
    } else if (url.endsWith("/summary.xml")) {
      return "summary";
    } else if (url.endsWith("/timeline.xml")) {
      return "timeline";
    } else if (url.endsWith("/profile.xml")) {
      return "profile";
    } else if (url.endsWith("/categories.xml")) {
      return "categories";
    } else if (url.endsWith("/match_status.xml")) {
      return "match_status";
    } else if (url.contains("/markets.xml")) {
      return "markets";
    } else if (url.contains("/variants.xml")) {
      return "variants";
    } else if (url.contains("/whoami.xml")) {
      return "whoami";
    } else if (url.contains("/schedule.xml")) {
      return "schedule";
    } else if (url.contains("/producers.xml")) {
      return "producers";
    }

    return "";
  }

  public boolean isApiCall() {
    // return loggerName != null && loggerName.endsWith("$UFSdkRestTrafficLog");
    return line != null && (line.contains("Request[DataFetcher]") || line.contains("LogHttpDataFetcher"));
  }

  public boolean isCustomerMessageListenerCall() {
    // Only worked with eurobet
    //return loggerName != null && loggerName.endsWith("$UFSdkClientInteractionLog");
    return line != null && line.contains("Message -");
  }

  public long getMessageDelayMillis() {
    long delay = timestamp.getTime() - messageCreationTimestamp.getTime();

    // Oh god, Daylight saving /UTC issues
    if (delay >= 3600000) {
      delay = delay -= 3600000;
    }

    return delay;
  }

  @Override
  public String toString() {
    return "LogEntryModel{" +
            "timestamp=" + timestamp +
            ", threadName='" + threadName + '\'' +
            ", loggerName='" + loggerName + '\'' +
            ", url='" + url + '\'' +
            ", messageCreationTimestamp=" + messageCreationTimestamp +
            ", durationMillis=" + durationMillis +
            ", payload='" + payload + '\'' +
            '}';
  }
}

