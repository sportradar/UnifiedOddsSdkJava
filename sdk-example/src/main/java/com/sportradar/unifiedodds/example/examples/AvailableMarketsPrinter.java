/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.MarketDescriptionManager;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A basic example which demonstrates how to access and print static market descriptions data
 */
@SuppressWarnings({ "LambdaBodyLength", "LineLength", "MethodLength", "VariableDeclarationUsageDistance" })
public class AvailableMarketsPrinter {

    private final OddsFeed oddsFeed;

    public AvailableMarketsPrinter(String token) {
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

        logEntry("The odds feed instance was created, the API data is now available");
    }

    public void print() {
        print(false);
    }

    public void print(boolean printMappings) {
        MarketDescriptionManager marketDescriptionManager = oddsFeed.getMarketDescriptionManager();

        logEntry("");
        logEntry("Listing static market descriptions");
        logEntry("");

        marketDescriptionManager
            .getMarketDescriptions()
            .forEach(m -> {
                logEntry(
                    String.format(
                        "Id:'%s', Name:'%s', Associated groups:'%s', Specifiers:'%s'",
                        m.getId(),
                        m.getName(Locale.ENGLISH), // by default only the english locale is pre-fetched
                        m.getGroups(),
                        m.getSpecifiers() == null
                            ? ""
                            : m
                                .getSpecifiers()
                                .stream()
                                .map(Specifier::getName)
                                .collect(Collectors.joining(","))
                    )
                );

                if (printMappings && m.getMappings() != null) {
                    logEntry("\tMappingData:");
                    m
                        .getMappings()
                        .forEach(mm ->
                            logEntry(
                                String.format(
                                    "\t\tProducerIds:'%s', SportId:'%s', MappedMarketId:'%s', ValidFor:'%s', SovTemplate:'%s'",
                                    mm.getProducerIds(),
                                    mm.getSportId(),
                                    mm.getMarketId(),
                                    mm.getValidFor() == null ? "" : mm.getValidFor(),
                                    mm.getSovTemplate()
                                )
                            )
                        );
                    logEntry("");
                }
            });

        logEntry("");
        logEntry("Listing static market descriptions - END");
        logEntry("");

        logEntry("AvailableMarketsPrinter example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
