/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.cfg.ConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A basic example demonstrating on how to start the SDK with an advanced configuration setup
 */
public class AdvancedConfigurationSetup {
    private final OddsFeed oddsFeed;

    public AdvancedConfigurationSetup(String token) {
        logEntry("Running the OddsFeed SDK Basic example - advanced configuration setup");

        logEntry("Building the configuration using the provided token");
        ConfigurationBuilder cfgBuilder = OddsFeed.getOddsFeedConfigurationBuilder().setAccessToken(token).selectStaging();

        logEntry("Setting the max recovery execution time to 3 hours");
        cfgBuilder.setMaxRecoveryExecutionTime(3, TimeUnit.HOURS);

        logEntry("Setting the default locale to German");
        cfgBuilder.setDefaultLocale(Locale.GERMAN);

        logEntry("Setting the max inactivity to 30 seconds (max interval between alive messages)");
        cfgBuilder.setMaxInactivitySeconds(30);

        logEntry("Adding additional desired locales - these locales will be directly available on the exposed entities");
        cfgBuilder.setDesiredLocales(Arrays.asList(Locale.ENGLISH, Locale.FRENCH));

        logEntry("Creating a new OddsFeed instance with the advanced config");
        oddsFeed = new OddsFeed(new GlobalEventsListener(), cfgBuilder.build());
    }

    public void run() throws IOException, InitException, InterruptedException {
        logEntry("Building a simple session which will receive all messages");
        oddsFeed.getSessionBuilder()
                .setMessageInterest(MessageInterest.AllMessages)
                .setListener(new MessageListener("AdvancedConfigurationSetup"))
                .build();

        logEntry("Opening the feed instance");
        logEntry("Feed instance will remain open for 30 minutes");
        oddsFeed.open();

        logEntry("Example successfully started");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        oddsFeed.close();

        logEntry("AdvancedConfigurationSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
