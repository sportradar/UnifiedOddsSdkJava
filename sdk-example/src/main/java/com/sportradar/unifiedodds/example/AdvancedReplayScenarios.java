/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.example.examples.replay.ExampleReplayEvents;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.UofSdkForReplay;
import com.sportradar.unifiedodds.sdk.UofSessionBuilder;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.internal.impl.ReplayManager;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * An example that has a list of sample events that illustrate various sports and behaviours.
 */
@SuppressWarnings({ "EqualsAvoidNull", "HideUtilityClassConstructor", "LineLength", "MagicNumber" })
public class AdvancedReplayScenarios {

    public static void main(String[] args) throws InitException, IOException, InterruptedException {
        // create a new UofConfiguration, the settings should be the same as for a normal(non-replay) feed instance
        UofConfiguration config = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken("your-token-here")
            .selectReplay()
            .setNodeId(SdkConstants.NODE_ID)
            .setDefaultLanguage(Locale.ENGLISH)
            .build();

        // create new UofSdkForReplay instance
        UofSdkForReplay uofSdkForReplay = new UofSdkForReplay(new GlobalEventsListener(), config);

        // all the operations supported by the replay server can be accessed trough the ReplayManager
        ReplayManager replayManager = uofSdkForReplay.getReplayManager();

        // stop & clear the replay server, so we cleanup the "previous" state
        System.out.println("Clearing previous replay server state");
        replayManager.stop();
        replayManager.clear();

        // for testing purposes it is advised that the sessions combination is the same as you will use in non-replay SDK instances
        UofSessionBuilder sessionBuilder = uofSdkForReplay.getSessionBuilder();
        MessageListener listener = new MessageListener("AllMessages");
        sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.AllMessages).build();

        // Open the UofSdkForReplay with all the built sessions
        uofSdkForReplay.open();

        System.out.println("UofSdkForReplay opened");

        // after the feed is opened we can start adding events to the replay server and eventually play them

        // add the events that you wish to replay
        // for demonstration purposes we prepared a list of sample events which you can choose from a list
        addReplayEventsBasedOnSelection(replayManager);

        System.out.println("Playing selected replay events");
        replayManager.play();

        // The replay messages will now arrive in a separate thread to the MessageListener

        // Let's sleep awhile (30 minutes) and see what gets printed.
        Thread.sleep(1000 * 60 * 30L);

        // finally we close the UofSdkForReplay - the replay scenario will most likely finish much sooner than 30 minutes
        uofSdkForReplay.close();
    }

    private static void addReplayEventsBasedOnSelection(ReplayManager replayManager) {
        System.out.println();
        System.out.println("Sample events:");
        IntStream
            .range(0, ExampleReplayEvents.SAMPLE_EVENTS.size())
            .forEach(i ->
                System.out.println(String.format("[%2d] %s", i, ExampleReplayEvents.SAMPLE_EVENTS.get(i)))
            );

        System.out.println();

        while (true) {
            System.out.println(
                "Select an event to add or pres 'x' if you do not want to add additional events:"
            );
            String additionalConsoleInput = getConsoleInput();

            if (additionalConsoleInput.equals("x")) {
                break;
            }

            Integer additionalItemPosition = provideSelectedItemIndex(additionalConsoleInput);
            if (additionalItemPosition == null) {
                System.err.println("Invalid input, retry");
                continue;
            }

            Urn additionalEventId = ExampleReplayEvents.SAMPLE_EVENTS
                .get(additionalItemPosition)
                .getEventId();
            replayManager.addSportEventToReplay(additionalEventId);
            System.out.println(String.format("Event[%s] added to the replay server", additionalEventId));
        }
    }

    private static Integer provideSelectedItemIndex(String consoleInput) {
        int listItemPosition;
        try {
            listItemPosition = Integer.parseInt(consoleInput);
        } catch (NumberFormatException e) {
            return null;
        }

        if (listItemPosition > ExampleReplayEvents.SAMPLE_EVENTS.size() || listItemPosition < 0) {
            return null;
        }

        return listItemPosition;
    }

    private static String getConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            return getConsoleInput();
        }
    }
}
