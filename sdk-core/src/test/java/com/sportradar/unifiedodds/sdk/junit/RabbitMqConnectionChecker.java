package com.sportradar.unifiedodds.sdk.junit;

import com.sportradar.unifiedodds.sdk.impl.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RabbitMqConnectionChecker {

  private final int port;

  public RabbitMqConnectionChecker(int port) {
    this.port = port;
  }

  public boolean connect() {
    return pingHost(port, 1000);
  }

  private boolean pingHost(int port, int timeoutInMillis) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(Constants.RABBIT_IP, port), timeoutInMillis);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}