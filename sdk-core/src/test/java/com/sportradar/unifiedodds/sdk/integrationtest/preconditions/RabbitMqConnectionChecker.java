package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

import com.sportradar.unifiedodds.sdk.impl.Constants;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RabbitMqConnectionChecker {

    public static final int TIMEOUT_IN_MILLIS = 1000;
    private final int port;

    public RabbitMqConnectionChecker(int port) {
        this.port = port;
    }

    public boolean isServerUp() {
        return pingHost(TIMEOUT_IN_MILLIS);
    }

    private boolean pingHost(int timeoutInMillis) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(Constants.RABBIT_IP, port), timeoutInMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
