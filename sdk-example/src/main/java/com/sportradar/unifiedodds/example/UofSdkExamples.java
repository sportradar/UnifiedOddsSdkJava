/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.examples.*;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import java.io.IOException;
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
        if (isReplayEnvironment) {
            //also exemplifies yml file based configuration
            System.out.println(
                "Please confirm that application.yml file is configured in the src/main/resources folder:"
            );
            getConsoleInput();

            ReplaySessionSetup replaySessionSetup = new ReplaySessionSetup();
            replaySessionSetup.run();
        } else {
            System.out.println(
                "Please enter an absolute path " +
                "to a private key (RSA SHA256 PCKS#8 key in PEM format) for 'Integration' environment:"
            );
            String pemFilePath = getConsoleInput();
            Path path = Paths.get(pemFilePath);
            String pemFileContent = new String(Files.readAllBytes(path), StandardCharsets.US_ASCII).trim();
            PrivateKey privateKey = parsePkcs8RsaUnencryptedPrivateKey(pemFileContent);

            System.out.println(
                "Please enter a OAuth client id (Sportradar service id) for 'Integration' environment. " +
                "It was supplied by Sportradar after uploading the public key:"
            );
            String clientId = getConsoleInput();

            System.out.println(
                "Please enter a signing key id for 'Integration' environment. " +
                "It was supplied by Sportradar after uploading the public key:"
            );
            String keyId = getConsoleInput();

            String token;
            switch (selection) {
                case "1":
                    // exemplifies properties file based configuration
                    System.out.println(
                        "Please confirm that UFSdkConfiguration.properties file is configured in the src/main/resources folder:"
                    );
                    getConsoleInput();
                    SingleSessionSetup singleSessionSetup = new SingleSessionSetup(
                        privateKey,
                        clientId,
                        keyId
                    );
                    singleSessionSetup.run(false);
                    break;
                case "2":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    MultiSessionSetup multiSessionSetup = new MultiSessionSetup(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    multiSessionSetup.run();
                    break;
                case "3":
                    // exemplifies properties file based configuration
                    System.out.println(
                        "Please confirm that UFSdkConfiguration.properties file is configured in the src/main/resources folder:"
                    );
                    getConsoleInput();
                    SingleSessionSetup singleSessionSetupRecovery = new SingleSessionSetup(
                        privateKey,
                        clientId,
                        keyId
                    );
                    singleSessionSetupRecovery.run(true);
                    break;
                case "4":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    DataSessionSetup dataSessionSetup = new DataSessionSetup(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    dataSessionSetup.run(true);
                    break;
                case "5":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    AvailableMarketsPrinter availableMarketsPrinter = new AvailableMarketsPrinter(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    availableMarketsPrinter.print();
                    break;
                case "6":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    AvailableMarketsPrinter availableMarketsPrinterMappings = new AvailableMarketsPrinter(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    availableMarketsPrinterMappings.print(true);
                    break;
                case "8":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    SportEventDataPrinter sportEventDataPrinter = new SportEventDataPrinter(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    sportEventDataPrinter.print();
                    break;
                case "9":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    AdvancedConfigurationSetup advancedConfigurationSetup = new AdvancedConfigurationSetup(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
                    advancedConfigurationSetup.run();
                    break;
                case "10":
                    System.out.println(
                        "Please enter a valid Unified Feed token for 'Integration' environment:"
                    );
                    token = getConsoleInput();
                    ExportImportSetup exportImportSetup = new ExportImportSetup(
                        token,
                        privateKey,
                        clientId,
                        keyId
                    );
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
