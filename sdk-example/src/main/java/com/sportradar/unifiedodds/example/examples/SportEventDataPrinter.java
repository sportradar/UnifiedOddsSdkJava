/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.example.common.SportEntityWriter;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.utils.Urn;
import java.util.Locale;

/**
 * A basic example which demonstrates how to access and print static spot event data
 */
@SuppressWarnings({ "LineLength", "VariableDeclarationUsageDistance" })
public class SportEventDataPrinter {

    private final UofSdk uofSdk;
    private final UofConfiguration configuration;

    public SportEventDataPrinter(String token) {
        logEntry("Running the UofSdk SDK Basic example - multiple session");

        logEntry("Building the configuration using the provided token");
        configuration =
            UofSdk
                .getUofConfigurationBuilder()
                .setAccessToken(token)
                .selectEnvironment(Environment.GlobalIntegration)
                .setNodeId(SdkConstants.NODE_ID)
                .setDefaultLanguage(Locale.ENGLISH)
                .build();

        logEntry("Creating a new UofSdk instance");
        uofSdk = new UofSdk(new GlobalEventsListener(), configuration);

        logEntry("The odds feed instance was created, the API data is now available");
    }

    public void print() {
        SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();

        logEntry("");
        logEntry("Listing static sport event data");
        logEntry("");

        Urn sportEventId = Urn.parse("sr:match:12089842"); // the example is ID is a soccer event, so it will be exposed as a Match
        SportEvent sportEvent = sportDataProvider.getCompetition(sportEventId); // match, race, ...
        // for long term events(tournaments, seasons,..) -> sportDataProvider.getLongTermEvent(tournamentId);

        String description = null;
        if (sportEvent != null) {
            description =
                SportEntityWriter.writeSportEventData(sportEvent, false, configuration.getLanguages());
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
