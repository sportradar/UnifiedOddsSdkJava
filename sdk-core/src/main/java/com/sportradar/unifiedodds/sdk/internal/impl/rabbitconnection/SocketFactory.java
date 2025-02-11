/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {

    public Socket openNew(final String host, final int port) throws IOException {
        return new Socket(host, port);
    }
}
