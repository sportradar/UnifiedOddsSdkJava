/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.example.common.SdkConstants;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import java.io.IOException;
import java.util.Locale;

/**
 * The following example is a very simple example that just connects to the Unified Odds Feed and
 * prints out some information about all the messages it receives.
 */
@SuppressWarnings({ "HideUtilityClassConstructor", "MagicNumber", "MethodLength" })
public class BasicUofSdkExampleMain {

    public static void main(String[] args) throws InitException, IOException, InterruptedException {
        // first you need to prepare a configuration suitable to your use. The most important thing is the access
        // token provided by Sportradar. You can set the token trough the builder via two ways:
        //      - as a system property(JVM argument -Duf.accesstoken=<your-access-token>)
        //        and than invoking setAccessTokenFromSystemVar on the builder
        //      - directly setting the access token in the builder using the setAccessToken(String accessToken) method
        UofConfiguration config = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken("your-staging-token-here")
            .selectEnvironment(Environment.GlobalIntegration)
            .setNodeId(SdkConstants.NODE_ID)
            .setDefaultLanguage(Locale.ENGLISH)
            .build();
        // create the new feed
        UofSdk uofSdk = new UofSdk(new GlobalEventsListener(), config);

        // access the producer manager
        ProducerManager producerManager = uofSdk.getProducerManager();

        // set the last received message timestamp trough the producer - if known
        // (as an example, we set the last message received timestamp as 2 days ago)
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        producerManager.setProducerRecoveryFromTimestamp(1,
                cal.getTime().getTime());*/

        // with the marketManager you can access various data about the available markets
        MarketDescriptionManager marketManager = uofSdk.getMarketDescriptionManager();

        // With the sportDataProvider helper you can access various data about the ongoing events
        SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();

        // In this example we will create 1 session which will receive all messages from all active producers
        // We can accomplish this with the UofSessionBuilder class.
        UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();

        MessageListener listener = new MessageListener("AllMessages");
        sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.AllMessages).build();

        // Open the feed with all the built sessions
        uofSdk.open();

        // The messages will now arrive in a separate thread to the MessageListener

        // Let's sleep awhile (30 minutes) and see what gets printed.
        Thread.sleep(1000 * 60 * 30L);

        // finally we close the feed.
        uofSdk.close();
    }
}
