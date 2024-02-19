/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PeriodicAliveSender implements AutoCloseable {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> alivesStarted;
    private final RabbitMqProducer rabbitProducer;

    public PeriodicAliveSender(RabbitMqProducer rabbitProducer) {
        this.rabbitProducer = rabbitProducer;
    }

    public static PeriodicAliveSender periodicAliveSender(RabbitMqProducer rabbitProducer) {
        return new PeriodicAliveSender(rabbitProducer);
    }

    private static String aliveForProducer1() {
        return "<alive product=\"1\" timestamp=\"" + System.currentTimeMillis() + "\" subscribed=\"1\"/>";
    }

    @Override
    public void close() {
        if (alivesStarted != null) {
            alivesStarted.cancel(true);
        }
        executor.shutdown();
    }

    public void startSendingToLiveProducer() {
        final int frequentAlivesToSpeedUpTests = 3;
        alivesStarted =
            executor.scheduleAtFixedRate(
                () -> rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.-.-.-.-"),
                frequentAlivesToSpeedUpTests,
                frequentAlivesToSpeedUpTests,
                TimeUnit.SECONDS
            );
    }
}
