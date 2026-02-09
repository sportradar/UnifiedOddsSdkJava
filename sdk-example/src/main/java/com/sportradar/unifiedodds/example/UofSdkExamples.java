/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.examples.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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

    public static void main(String[] args) throws Exception {
        System.out.println("UofSdkExamples - example selector, START");
        System.out.println();

        doExampleSelection();
    }

    private static void doExampleSelection() throws Exception {
        System.out.println("Available examples: (select the one you would like to run)");
        System.out.println("  1a - Basic SDK Setup (Access Token)");
        System.out.println(
            "      → Single UofSession using Access Token with MessageInterest.AllMessages, full odds recovery from all producers"
        );
        System.out.println(
            "      → Demonstrates: SDK initialization, GlobalEventsListener, MessageListener patterns"
        );
        System.out.println();

        System.out.println("  1b - Basic SDK Setup (Client Authentication)");
        System.out.println(
            "      → Single UofSession using Client Authentication with MessageInterest.AllMessages, full odds recovery from all producers"
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

        String accessToken = null;
        PrivateKey privateKey = null;
        String clientId = null;
        String keyId = null;

        boolean isAccessTokenCase = selection.equals("7") || selection.equals("1a");
        if (isAccessTokenCase) {
            System.out.println("Please enter a valid Unified Feed token for 'Integration' environment:");
            accessToken = getConsoleInput();
        } else {
            System.out.println(
                "Please enter an absolute path " +
                "to a private key (RSA SHA256 PCKS#8 key in PEM format) for 'Integration' environment:"
            );
            String pemFilePath = getConsoleInput();
            Path path = Paths.get(pemFilePath);
            String pemFileContent = new String(Files.readAllBytes(path), StandardCharsets.US_ASCII).trim();
            privateKey = parsePkcs8RsaUnencryptedPrivateKey(pemFileContent);

            System.out.println(
                "Please enter a OAuth client id (Sportradar service id) for 'Integration' environment. " +
                "It was supplied by Sportradar after uploading the public key:"
            );
            clientId = getConsoleInput();

            System.out.println(
                "Please enter a signing key id for 'Integration' environment. " +
                "It was supplied by Sportradar after uploading the public key:"
            );
            keyId = getConsoleInput();
        }

        switch (selection) {
            case "1a":
                SingleSessionSetup singleSessionSetupAccessToken = new SingleSessionSetup(accessToken);
                singleSessionSetupAccessToken.run(false);
                break;
            case "1b":
                SingleSessionSetup singleSessionSetup = new SingleSessionSetup(privateKey, clientId, keyId);
                singleSessionSetup.run(false);
                break;
            case "2":
                MultiSessionSetup multiSessionSetup = new MultiSessionSetup(privateKey, clientId, keyId);
                multiSessionSetup.run();
                break;
            case "3":
                SingleSessionSetup singleSessionSetupRecovery = new SingleSessionSetup(
                    privateKey,
                    clientId,
                    keyId
                );
                singleSessionSetupRecovery.run(true);
                break;
            case "4":
                DataSessionSetup dataSessionSetup = new DataSessionSetup(privateKey, clientId, keyId);
                dataSessionSetup.run(true);
                break;
            case "5":
                AvailableMarketsPrinter availableMarketsPrinter = new AvailableMarketsPrinter(
                    privateKey,
                    clientId,
                    keyId
                );
                availableMarketsPrinter.print();
                break;
            case "6":
                AvailableMarketsPrinter availableMarketsPrinterMappings = new AvailableMarketsPrinter(
                    privateKey,
                    clientId,
                    keyId
                );
                availableMarketsPrinterMappings.print(true);
                break;
            case "7":
                ReplaySessionSetup replaySessionSetup = new ReplaySessionSetup(accessToken);
                replaySessionSetup.run();
                break;
            case "8":
                SportEventDataPrinter sportEventDataPrinter = new SportEventDataPrinter(
                    privateKey,
                    clientId,
                    keyId
                );
                sportEventDataPrinter.print();
                break;
            case "9":
                AdvancedConfigurationSetup advancedConfigurationSetup = new AdvancedConfigurationSetup(
                    privateKey,
                    clientId,
                    keyId
                );
                advancedConfigurationSetup.run();
                break;
            case "10":
                ExportImportSetup exportImportSetup = new ExportImportSetup(privateKey, clientId, keyId);
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

    public static PrivateKey parsePkcs8RsaUnencryptedPrivateKey(String pemFileContent) throws Exception {
        int start = pemFileContent.indexOf("-----BEGIN PRIVATE KEY-----");
        int end = pemFileContent.indexOf("-----END PRIVATE KEY-----");
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Not a PKCS#8 PEM: missing BEGIN/END PRIVATE KEY markers.");
        }

        String base64 = pemFileContent
            .substring(start + "-----BEGIN PRIVATE KEY-----".length(), end)
            .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
