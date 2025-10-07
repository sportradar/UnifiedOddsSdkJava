/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.examples.*;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import java.io.IOException;
import java.util.Scanner;

/**
 * The SDK basic example selector
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "HideUtilityClassConstructor",
        "JavaNCSS",
        "LineLength",
        "MethodLength",
    }
)
public class UofSdkExamples {

    public static void main(String[] args) throws IOException, InitException, InterruptedException {
        System.out.println("UofSdkExamples - example selector, START");
        System.out.println();

        doExampleSelection();
    }

    private static void doExampleSelection() throws IOException, InitException, InterruptedException {
        System.out.println("Available examples: (select the one you would like to run)");
        System.out.println("  1 - Basic SDK Setup");
        System.out.println(
            "      → Single UofSession with MessageInterest.AllMessages, full odds recovery from all producers"
        );
        System.out.println(
            "      → Demonstrates: SDK initialization, GlobalEventsListener, MessageListener patterns"
        );
        System.out.println();

        System.out.println("  2 - Multi-Session Architecture");
        System.out.println(
            "      → Three parallel sessions: Prematch, Live, Virtuals with filtered MessageInterest types"
        );
        System.out.println(
            "      → Demonstrates: Session isolation, message filtering, concurrent event handling"
        );
        System.out.println();

        System.out.println("  3 - Timestamp-Based Recovery");
        System.out.println(
            "      → Single session with ProducerManager recovery from last 2 hours via setProducersRecoveryTimestamp()"
        );
        System.out.println(
            "      → Demonstrates: Historical data recovery, producer management, timestamp-based recovery"
        );
        System.out.println();

        System.out.println("  4 - Full Message Data Access");
        System.out.println(
            "      → DataSessionSetup with DataMessageListener for complete message payload inspection"
        );
        System.out.println(
            "      → Demonstrates: Raw message data access, detailed event parsing, data structure exploration"
        );
        System.out.println();

        System.out.println("  5 - Market Descriptions API");
        System.out.println(
            "      → MarketDescriptionManager.getMarketDescriptions() - static metadata enumeration"
        );
        System.out.println(
            "      → Demonstrates: Market metadata access, static data APIs, betting market structures"
        );
        System.out.println();

        System.out.println("  6 - Market Mappings Analysis");
        System.out.println(
            "      → MarketDescriptionManager with mapping data for outcome/specifier relationships"
        );
        System.out.println(
            "      → Demonstrates: Market variant mappings, outcome translations, specifier handling"
        );
        System.out.println();

        System.out.println("  7 - Replay Server Integration");
        System.out.println("      → UofSdkForReplay with ReplayManager for historical event simulation");
        System.out.println(
            "      → Demonstrates: Replay environment setup, historical data playback, testing scenarios"
        );
        System.out.println();

        System.out.println("  8 - Sport Event Data Explorer");
        System.out.println(
            "      → SportEventDataPrinter accessing static sport hierarchy and event metadata"
        );
        System.out.println(
            "      → Demonstrates: Sport data structures, event hierarchies, tournament/competition APIs"
        );
        System.out.println();

        System.out.println("  9 - Advanced Configuration");
        System.out.println(
            "      → Custom UofConfiguration beyond default properties (timeouts, caching, locales)"
        );
        System.out.println(
            "      → Demonstrates: Configuration builder patterns, environment selection, advanced tuning"
        );
        System.out.println();

        System.out.println(" 10 - Cache Export/Import");
        System.out.println(
            "      → SDK cache serialization/deserialization for state persistence across restarts"
        );
        System.out.println(
            "      → Demonstrates: Cache management, state persistence, performance optimization"
        );

        String selection = getConsoleInput();

        boolean isReplayEnvironment = selection.equals("7");
        String environment = isReplayEnvironment ? "Replay" : "Integration";
        System.out.println("Please enter a valid Unified Feed token for '" + environment + "' environment:");
        String token = getConsoleInput();

        switch (selection) {
            case "1":
                SingleSessionSetup singleSessionSetup = new SingleSessionSetup();
                singleSessionSetup.run(false);
                break;
            case "2":
                MultiSessionSetup multiSessionSetup = new MultiSessionSetup(token);
                multiSessionSetup.run();
                break;
            case "3":
                SingleSessionSetup singleSessionSetupRecovery = new SingleSessionSetup();
                singleSessionSetupRecovery.run(true);
                break;
            case "4":
                DataSessionSetup dataSessionSetup = new DataSessionSetup(token);
                dataSessionSetup.run(true);
                break;
            case "5":
                AvailableMarketsPrinter availableMarketsPrinter = new AvailableMarketsPrinter(token);
                availableMarketsPrinter.print();
                break;
            case "6":
                AvailableMarketsPrinter availableMarketsPrinterMappings = new AvailableMarketsPrinter(token);
                availableMarketsPrinterMappings.print(true);
                break;
            case "7":
                ReplaySessionSetup replaySessionSetup = new ReplaySessionSetup();
                replaySessionSetup.run();
                break;
            case "8":
                SportEventDataPrinter sportEventDataPrinter = new SportEventDataPrinter(token);
                sportEventDataPrinter.print();
                break;
            case "9":
                AdvancedConfigurationSetup advancedConfigurationSetup = new AdvancedConfigurationSetup(token);
                advancedConfigurationSetup.run();
                break;
            case "10":
                ExportImportSetup exportImportSetup = new ExportImportSetup(token);
                exportImportSetup.run();
                break;
            default:
                System.out.println();
                System.out.println("Invalid example id selected, please select a valid example id.");
                System.out.println();
                doExampleSelection();
                break;
        }
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
