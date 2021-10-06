/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MultithreadedMessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.ProducerManager;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A advanced example demonstrating on how to start the SDK with a single session while parsing messages in new thread
 */
public class MultithreadedSessionSetup {
    private final OddsFeed oddsFeed;

    public MultithreadedSessionSetup(String token) {
        logEntry("Running the OddsFeed SDK Advanced example - multithreaded session");

        logEntry("Building the configuration using the provided token");
        OddsFeedConfiguration configuration = OddsFeed.getOddsFeedConfigurationBuilder()
                .setAccessToken(token)
                .selectEnvironment(Environment.GlobalIntegration)
                .setSdkNodeId(SdkConstants.NODE_ID)
                .setDefaultLocale(Locale.ENGLISH)
                .build();

        logEntry("Creating a new OddsFeed instance");
        oddsFeed = new OddsFeed(new GlobalEventsListener(), configuration);
    }

    public void run(boolean doRecoveryFromTimestamp) throws IOException, InitException, InterruptedException {
        if (doRecoveryFromTimestamp) {
            setProducersRecoveryTimestamp();
        }

        logEntry("Building a simple session which will receive all messages");
        MultithreadedMessageListener messageListener = new MultithreadedMessageListener("MultithreadedSessionSetup");
        oddsFeed.getSessionBuilder()
                .setMessageInterest(MessageInterest.AllMessages)
                .setListener(messageListener)
                .build();

        logEntry("Opening the feed instance");
        logEntry("Feed instance will remain open for 30 minutes");
        oddsFeed.open();

        logEntry("Example successfully started");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        oddsFeed.close();
        messageListener.close();

        logEntry("MultithreadedSessionSetup example finished");
        logEntry("");
    }

    private void setProducersRecoveryTimestamp() {
        logEntry("Setting last message timestamp(used for recovery) for all the active producers to two hours back");

        // using the timestamp from 2 hours back, in real case scenarios you need to monitor the timestamp for recovery
        // with the producerManager.getProducer(producerId).getTimestampForRecovery(); method
        long recoveryFromTimestamp = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

        ProducerManager producerManager = oddsFeed.getProducerManager();

        producerManager.getActiveProducers().values().forEach(p -> producerManager.setProducerRecoveryFromTimestamp(p.getId(), recoveryFromTimestamp));
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
