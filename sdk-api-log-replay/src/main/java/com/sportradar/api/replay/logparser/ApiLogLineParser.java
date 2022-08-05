package com.sportradar.api.replay.logparser;

import com.sportradar.api.replay.logparser.listener.ApiLogListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class ApiLogLineParser implements Consumer<String> {

  private static final String REGEX_TIMESTAMP = "(.*)";
  private static final String REGEX_BRACKETED_STR = "\\[([\\w\\s-]*)]";
  private static final String REGEX_REQUESTER = "Request\\[(.*)]";
  private static final String REGEX_HTTP_URL = "(http.*)";
  private static final String REGEX_HTTP_RESPONSE_CODE = ", [response]?(.*\\))[:,]";
  static final Pattern REGEX_PATTERN_API_CALL = Pattern.compile(
      REGEX_TIMESTAMP
          + "\\s"
          + REGEX_BRACKETED_STR // log level
          + "\\s"
          + REGEX_BRACKETED_STR // SDK thread
          + "\\s"
          + REGEX_BRACKETED_STR  // AMQP thread
          + ".*"
          + REGEX_REQUESTER // requester
          + ".*"
          + REGEX_HTTP_URL  // url
          + REGEX_HTTP_RESPONSE_CODE  // response code & timing
  );
  static final Pattern REGEX_PATTERN_XML = Pattern.compile(".*response.*:\\s+(<.*)");
  static final Pattern REGEX_PATTERN_DURATION = Pattern.compile("\\((\\d+).*\\s([ms]+)\\)");

  static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS",
      Locale.ENGLISH);
  private final ApiLogListener listener;

  private boolean parsingException;

  @Override
  public void accept(String logLine) {
    parse(logLine);
  }

  private void parse(String logLine) {
    Matcher matcher = REGEX_PATTERN_API_CALL.matcher(logLine);
    if (matcher.find()) {
      String timestamp = matcher.group(1);
      String logLevel = matcher.group(2);
      String sdkThread = matcher.group(3);
      String amqpThread = matcher.group(4);
      String httpMethod = matcher.group(5).startsWith("POST") ? "POST" : "GET";
      String url = matcher.group(6);
      String responseCodeAndDuration = matcher.group(7);
      boolean success = responseCodeAndDuration.contains("OK");
      String xml = success ? extractXml(logLine) : null;
      int durationInMillis = extractDurationInMillis(responseCodeAndDuration);
      parsingException = !success;

      LogEntry logEntry = LogEntry.builder()
          .timestamp(toDateTime(timestamp))
          .url(url)
          .httpMethod(httpMethod)
          .success(success)
          .durationInMillis(durationInMillis)
          .xml(xml)
          .build();
      listener.onNewLogEntry(logEntry);
    } else {
      if (!parsingException) {
        log.error("Error parsing line: {}", logLine);
        throw new LogParserException("Error parsing log entry" + logLine);
      }
    }
  }

  private int extractDurationInMillis(String responseCodeAndDuration) {
    int duration;
    Matcher matcher = REGEX_PATTERN_DURATION.matcher(responseCodeAndDuration);

    if (matcher.find()) {
      String strDuration = matcher.group(1);
      String strUnits = matcher.group(2);
      duration = Integer.parseInt(strDuration);
      if ("s".equals(strUnits)) {
        duration *= 1000;
      }
    } else {
      throw new LogParserException("Duration not found:" + responseCodeAndDuration);
    }
    return duration;
  }

  private String extractXml(String logLine) {
    String xml;

    Matcher matcher = REGEX_PATTERN_XML.matcher(logLine);
    if (matcher.find()) {
      xml = matcher.group(1);
    } else {
      throw new LogParserException("XML payload not found:" + logLine);
    }
    return xml;
  }

  private Date toDateTime(String timestamp) {
    try {
      return LOG_DATE_FORMAT.parse(timestamp);
    } catch (ParseException e) {
      throw new LogParserException("Error parsing timestamp: " + timestamp, e);
    }
  }
}
