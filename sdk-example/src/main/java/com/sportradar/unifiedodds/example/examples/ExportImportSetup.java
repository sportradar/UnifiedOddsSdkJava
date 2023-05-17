/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.caching.exportable.CacheType;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * A basic demonstration on how to export/import current cache state
 */
@SuppressWarnings(
    { "AbbreviationAsWordInName", "ClassDataAbstractionCoupling", "IllegalCatch", "MagicNumber" }
)
public class ExportImportSetup {

    private final OddsFeed oddsFeed;
    private final File cacheFile = new File("cache");

    public ExportImportSetup(String token) {
        logEntry("Running the OddsFeed SDK Basic example - cache export/import");

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
        logEntry("Building a simple session which will receive all messages");
        oddsFeed
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.AllMessages)
            .setListener(new MessageListener("SingleSessionSetup"))
            .build();
        SportsInfoManager sportsInfoManager = oddsFeed.getSportsInfoManager();

        logEntry("Opening the feed instance");
        logEntry("Feed instance will remain open for 10 seconds");

        if (cacheFile.exists()) {
            logEntry("Importing cache state from existing file");
            try (
                FileInputStream stream = new FileInputStream(cacheFile);
                ObjectInputStream reader = new ObjectInputStream(stream)
            ) {
                List<ExportableCI> exportableCIS = (List<ExportableCI>) reader.readObject();
                sportsInfoManager.cacheImport(exportableCIS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        oddsFeed.open();

        logEntry("Example successfully started");

        Thread.sleep(1000 * 10L);

        logEntry("Exporting cache state to file");
        try (
            FileOutputStream stream = new FileOutputStream(cacheFile);
            ObjectOutputStream writer = new ObjectOutputStream(stream)
        ) {
            List<ExportableCI> exportableCIS = sportsInfoManager.cacheExport(CacheType.All);
            writer.writeObject(exportableCIS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logEntry("Closing the odds feed instance (10sec elapsed)");
        oddsFeed.close();

        logEntry("ExportImportSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
