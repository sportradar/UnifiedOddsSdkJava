package com.sportradar.api.replay.apiserver;

public class ApiServerStubException extends RuntimeException {

  public ApiServerStubException(String message) {
    super(message);
  }

  public ApiServerStubException(String message, Throwable cause) {
    super(message, cause);
  }
}
