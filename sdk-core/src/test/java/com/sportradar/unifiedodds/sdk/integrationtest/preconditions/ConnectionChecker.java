/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionChecker {

    public static final int TIMEOUT_IN_MILLIS = 1000;
    private final BaseUrl baseUrl;

    public ConnectionChecker(BaseUrl baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isServerUp() {
        return pingHost(TIMEOUT_IN_MILLIS);
    }

    private boolean pingHost(int timeoutInMillis) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(baseUrl.getHost(), baseUrl.getPort()), timeoutInMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
