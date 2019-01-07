/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;

import java.io.IOException;
import java.util.Locale;

/**
 * The following example is a very simple example that just connects to the Unified Odds Feed and
 * prints out some information about all the messages it receives.
 */
public class BasicOddsFeedExampleMain {

    public static void main(String[] args) throws InitException, IOException, InterruptedException {
        // first you need to prepare a configuration suitable to your use. The most important thing is the access
        // token provided by Sportradar. You can set the token trough the builder via two ways:
        //      - as a system property(JVM argument -Duf.accesstoken=<your-access-token>)
        //        and than invoking setAccessTokenFromSystemVar on the builder
        //      - directly setting the access token in the builder using the setAccessToken(String accessToken) method
        OddsFeedConfiguration config = OddsFeed.getOddsFeedConfigurationBuilder()
                .setAccessToken("your-staging-token-here")
                .selectIntegration()
                .setDefaultLocale(Locale.ENGLISH)
                .build();

        // create the new feed
        OddsFeed oddsFeed = new OddsFeed(new GlobalEventsListener(), config);

        // access the producer manager
        ProducerManager producerManager = oddsFeed.getProducerManager();

        // set the last received message timestamp trough the producer - if known
        // (as an example, we set the last message received timestamp as 2 days ago)
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        producerManager.setProducerRecoveryFromTimestamp(1,
                cal.getTime().getTime());*/

        // with the marketManager you can access various data about the available markets
        MarketDescriptionManager marketManager = oddsFeed.getMarketDescriptionManager();

        // With the sportsInfoManager helper you can access various data about the ongoing events
        SportsInfoManager sportsInfoManager = oddsFeed.getSportsInfoManager();

        // In this example we will create 1 session which will receive all messages from all active producers
        // We can accomplish this with the OddsFeedSessionBuilder class.
        OddsFeedSessionBuilder sessionBuilder = oddsFeed.getSessionBuilder();

        MessageListener listener = new MessageListener("AllMessages");
        sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.AllMessages).build();

        // Open the feed with all the built sessions
        oddsFeed.open();

        // The messages will now arrive in a separate thread to the MessageListener

        // Let's sleep awhile (30 minutes) and see what gets printed.
        Thread.sleep(1000 * 60 * 30L);

        // finally we close the feed.
        oddsFeed.close();
    }
}
