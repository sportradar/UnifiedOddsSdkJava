/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class SocketFactoryTest {

    private static final int SERVER_PORT = 48915;
    private static final int HALF_SECOND = 500;
    private final SocketFactory socketFactory = new SocketFactory();

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final CountDownLatch serverSocketCreated = new CountDownLatch(1);

    @Test
    @Timeout(value = HALF_SECOND, unit = TimeUnit.MILLISECONDS)
    public void shouldOpenSocketAtSpecifiedPort()
        throws ExecutionException, InterruptedException, TimeoutException, IOException {
        Future<?> socketOpened = executorService.submit(() -> {
            try (final val serverSocket = new ServerSocket(SERVER_PORT)) {
                serverSocketCreated.countDown();
                serverSocket.accept();
            }

            return 0;
        });

        serverSocketCreated.await();
        try (final Socket clientSocket = socketFactory.openNew("localhost", SERVER_PORT)) {
            socketOpened.get(1, TimeUnit.SECONDS);
        }
    }
}
