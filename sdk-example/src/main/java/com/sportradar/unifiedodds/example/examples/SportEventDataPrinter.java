/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.example.common.SportEntityWriter;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.utils.URN;

import java.io.IOException;
import java.util.Locale;

/**
 * A basic example which demonstrates how to access and print static spot event data
 */
public class SportEventDataPrinter {
    private final OddsFeed oddsFeed;
    private final OddsFeedConfiguration configuration;

    public SportEventDataPrinter(String token) {
        logEntry("Running the OddsFeed SDK Basic example - multiple session");

        logEntry("Building the configuration using the provided token");
        configuration = OddsFeed.getOddsFeedConfigurationBuilder()
                .setAccessToken(token)
                .selectIntegration()
                .setSdkNodeId(SdkConstants.NODE_ID)
                .setDefaultLocale(Locale.ENGLISH)
                .build();

        logEntry("Creating a new OddsFeed instance");
        oddsFeed = new OddsFeed(new GlobalEventsListener(), configuration);

        logEntry("The odds feed instance was created, the API data is now available");
    }

    public void print() {
        SportsInfoManager sportsInfoManager = oddsFeed.getSportsInfoManager();

        logEntry("");
        logEntry("Listing static sport event data");
        logEntry("");

        URN sportEventId = URN.parse("sr:match:12089842");// the example is ID is a soccer event, so it will be exposed as a Match
        SportEvent sportEvent = sportsInfoManager.getCompetition(sportEventId); // match, race, ...
        // for long term events(tournaments, seasons,..) -> sportsInfoManager.getLongTermEvent(tournamentId);

        String description = null;
        if (sportEvent != null) {
            description = SportEntityWriter.writeSportEventData(sportEvent, false, configuration.getDesiredLocales());
        }

        if (description != null) {
            logEntry(description);
        } else {
            logEntry("Sport event data was not found for id: " + sportEventId);
        }

        logEntry("");
        logEntry("Listing sport event data - END");
        logEntry("");

        logEntry("SportEventDataPrinter example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
