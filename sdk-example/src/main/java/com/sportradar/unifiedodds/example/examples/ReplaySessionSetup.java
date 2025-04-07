/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.UofSdkForReplay;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.internal.impl.ReplayManager;
import com.sportradar.utils.Urn;
import java.io.IOException;

/**
 * A basic example demonstrating on how to start the SDK with a single session and perform replay operations
 */
@SuppressWarnings({ "MagicNumber" })
public class ReplaySessionSetup {

    private final UofSdkForReplay uofSdkForReplay;

    public ReplaySessionSetup() {
        logEntry("Running the UofSdk SDK Basic example - single session with replay server");

        logEntry("Building the configuration using the provided token");

        UofConfiguration configuration = UofSdk
            .getUofConfigurationBuilder()
            .setAccessTokenFromApplicationYaml()
            .selectCustom() // needs to be present, it seems
            .loadConfigFromApplicationYml()
            .build();

        logEntry("Creating a new UofSdkForReplay instance");
        uofSdkForReplay = new UofSdkForReplay(new GlobalEventsListener(), configuration);
    }

    public void run() throws IOException, InitException, InterruptedException {
        logEntry("Building a simple session which will receive all messages replayed from the server");
        uofSdkForReplay
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.AllMessages)
            .setListener(new MessageListener("ReplaySessionSetup"))
            .build();

        ReplayManager replayManager = uofSdkForReplay.getReplayManager();

        Urn eventId = Urn.parse("sr:match:12089842");
        Urn eventId1 = Urn.parse("sr:match:12089826");

        logEntry(String.format("Adding 2 events to the replay queue[%s,%s]", eventId, eventId1));
        replayManager.addSportEventToReplay(eventId);
        replayManager.addSportEventToReplay(eventId1);

        logEntry(
            "Opening the feed instance & starting the replay procedure (30x faster, max delay between messages 500ms)"
        );
        logEntry("Feed instance will remain open for 30 minutes, processing replay messages");
        uofSdkForReplay.open();
        replayManager.play(30, 500);

        logEntry("Replay example successfully started");

        Thread.sleep(1000 * 60 * 30L);

        logEntry("Closing the odds feed instance (30min elapsed)");
        uofSdkForReplay.close();

        logEntry("ReplaySessionSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
