/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.managers.ProducerManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A basic example demonstrating on how to start the SDK with a single session
 */
@SuppressWarnings({ "MagicNumber" })
public class SingleSessionSetup {

    private final UofSdk uofSdk;

    public SingleSessionSetup() {
        logEntry("Running the UofSdk SDK Basic example - single session");

        logEntry("Building the configuration using the provided token");

        UofConfiguration configuration = UofSdk.getUofConfigurationBuilder().buildConfigFromSdkProperties();

        logEntry(configuration.toString());
        logEntry("Creating a new UofSdk instance");
        uofSdk = new UofSdk(new GlobalEventsListener(), configuration);
    }

    public void run(boolean doRecoveryFromTimestamp) throws IOException, InitException, InterruptedException {
        if (doRecoveryFromTimestamp) {
            setProducersRecoveryTimestamp();
        }

        logEntry("Building a simple session which will receive all messages");
        uofSdk
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.AllMessages)
            .setListener(new MessageListener("SingleSessionSetup"))
            .build();

        logEntry("Opening the feed instance");
        logEntry("Feed instance will remain open for 30 minutes");
        uofSdk.open();

        logEntry("Example successfully started");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        uofSdk.close();

        logEntry("SingleSessionSetup example finished");
        logEntry("");
    }

    private void setProducersRecoveryTimestamp() {
        logEntry(
            "Setting last message timestamp(used for recovery) for all the active producers to two hours back"
        );

        // using the timestamp from 2 hours back, in real case scenarios you need to monitor the timestamp for recovery
        // with the producerManager.getProducer(producerId).getTimestampForRecovery(); method
        long recoveryFromTimestamp =
            System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

        ProducerManager producerManager = uofSdk.getProducerManager();

        producerManager
            .getActiveProducers()
            .values()
            .forEach(p -> producerManager.setProducerRecoveryFromTimestamp(p.getId(), recoveryFromTimestamp));
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
