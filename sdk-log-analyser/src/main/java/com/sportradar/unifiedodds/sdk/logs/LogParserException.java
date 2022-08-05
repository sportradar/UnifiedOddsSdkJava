package com.sportradar.unifiedodds.sdk.logs;

public class LogParserException extends RuntimeException {

  public LogParserException(String message) {
    super(message);
  }

  public LogParserException(String message, Throwable cause) {
    super(message, cause);
  }
}
