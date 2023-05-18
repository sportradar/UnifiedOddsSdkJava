/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import java.io.IOException;
import java.util.Locale;

/**
 * A basic example demonstrating on how to start the SDK with multiple sessions
 */
@SuppressWarnings({ "MagicNumber" })
public class MultiSessionSetup {

    private final OddsFeed oddsFeed;

    public MultiSessionSetup(String token) {
        logEntry("Running the OddsFeed SDK Basic example - multiple session");

        logEntry("Building the configuration using the provided token");
        OddsFeedConfiguration configuration = OddsFeed
            .getOddsFeedConfigurationBuilder()
            .setAccessToken(token)
            .selectEnvironment(Environment.GlobalIntegration)
            .setSdkNodeId(SdkConstants.NODE_ID)
            .setDefaultLocale(Locale.ENGLISH)
            .build();

        logEntry("Creating a new OddsFeed instance");
        oddsFeed = new OddsFeed(new GlobalEventsListener(), configuration);
    }

    public void run() throws IOException, InitException, InterruptedException {
        logEntry("Building 3 sessions: Prematch, Liveodds, Virtuals");
        oddsFeed
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.PrematchMessagesOnly)
            .setListener(new MessageListener("PrematchMessagesOnly"))
            .build();
        oddsFeed
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.LiveMessagesOnly)
            .setListener(new MessageListener("LiveMessagesOnly"))
            .build();
        oddsFeed
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.VirtualSports)
            .setListener(new MessageListener("VirtualSports"))
            .build();

        logEntry("Opening the feed instance");
        logEntry(" ~ Feed instance will remain open for 30 minutes ~");
        oddsFeed.open();

        logEntry("Example successfully started");
        logEntry("");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        oddsFeed.close();

        logEntry("MultiSessionSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
