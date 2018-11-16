/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.ReplayOddsFeed;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.replay.ReplayManager;
import com.sportradar.utils.URN;

import java.io.IOException;
import java.util.Locale;

/**
 * A basic example demonstrating on how to start the SDK with a single session and perform replay operations
 */
public class ReplaySessionSetup {
    private final ReplayOddsFeed oddsFeed;

    public ReplaySessionSetup(String token) {
        logEntry("Running the OddsFeed SDK Basic example - single session with replay server");

        logEntry("Building the configuration using the provided token");
        OddsFeedConfiguration configuration = OddsFeed.getOddsFeedConfigurationBuilder().setAccessToken(token).selectStaging().setDefaultLocale(Locale.ENGLISH).build();

        logEntry("Creating a new ReplayOddsFeed instance");
        oddsFeed = new ReplayOddsFeed(new GlobalEventsListener(), configuration);
    }

    public void run() throws IOException, InitException, InterruptedException {
        logEntry("Building a simple session which will receive all messages replayed from the server");
        oddsFeed.getSessionBuilder()
                .setMessageInterest(MessageInterest.AllMessages)
                .setListener(new MessageListener("ReplaySessionSetup"))
                .build();

        ReplayManager replayManager = oddsFeed.getReplayManager();

        URN eventId = URN.parse("sr:match:12089842");
        URN eventId1 = URN.parse("sr:match:12089826");

        logEntry(String.format("Adding 2 events to the replay queue[%s,%s]", eventId, eventId1));
        replayManager.addSportEventToReplay(eventId);
        replayManager.addSportEventToReplay(eventId1);

        logEntry("Opening the feed instance & starting the replay procedure (30x faster, max delay between messages 500ms)");
        logEntry("Feed instance will remain open for 30 minutes, processing replay messages");
        oddsFeed.open();
        replayManager.play(30, 500);

        logEntry("Replay example successfully started");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        oddsFeed.close();

        logEntry("ReplaySessionSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
