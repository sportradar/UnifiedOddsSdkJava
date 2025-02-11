/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.managers.CacheType;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCi;
import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * A basic demonstration on how to export/import current cache state
 */
@SuppressWarnings({ "ClassDataAbstractionCoupling", "IllegalCatch", "MagicNumber" })
public class ExportImportSetup {

    private final UofSdk uofSdk;
    private final File cacheFile = new File("cache");

    public ExportImportSetup(String token) {
        logEntry("Running the UofSdk SDK Basic example - cache export/import");

        logEntry("Building the configuration using the provided token");
        UofConfiguration configuration = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken(token)
            .selectEnvironment(Environment.GlobalIntegration)
            .setNodeId(SdkConstants.NODE_ID)
            .setDefaultLanguage(Locale.ENGLISH)
            .build();

        logEntry("Creating a new UofSdk instance");
        uofSdk = new UofSdk(new GlobalEventsListener(), configuration);
    }

    public void run() throws IOException, InitException, InterruptedException {
        logEntry("Building a simple session which will receive all messages");
        uofSdk
            .getSessionBuilder()
            .setMessageInterest(MessageInterest.AllMessages)
            .setListener(new MessageListener("SingleSessionSetup"))
            .build();
        SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();

        logEntry("Opening the feed instance");
        logEntry("Feed instance will remain open for 10 seconds");

        if (cacheFile.exists()) {
            logEntry("Importing cache state from existing file");
            try (
                FileInputStream stream = new FileInputStream(cacheFile);
                ObjectInputStream reader = new ObjectInputStream(stream)
            ) {
                List<ExportableCi> exportableCis = (List<ExportableCi>) reader.readObject();
                sportDataProvider.cacheImport(exportableCis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        uofSdk.open();

        logEntry("Example successfully started");

        Thread.sleep(1000 * 10L);

        logEntry("Exporting cache state to file");
        try (
            FileOutputStream stream = new FileOutputStream(cacheFile);
            ObjectOutputStream writer = new ObjectOutputStream(stream)
        ) {
            List<ExportableCi> exportableCis = sportDataProvider.cacheExport(CacheType.All);
            writer.writeObject(exportableCis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logEntry("Closing the odds feed instance (10sec elapsed)");
        uofSdk.close();

        logEntry("ExportImportSetup example finished");
        logEntry("");
    }

    private static void logEntry(String s) {
        System.out.println(s);
    }
}
