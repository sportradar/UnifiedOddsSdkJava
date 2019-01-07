/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.custom;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MarketWriter;
import com.sportradar.unifiedodds.example.common.SportEntityWriter;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.Stage;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.replay.ReplayStatus;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DExample {

    public static void main(String[] args) throws InitException, IOException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(DExample.class.getName());

        // first you need to prepare a configuration suitable to your use. The most important thing is the access
        // token provided by Sportradar. You can set the token trough the builder via two ways:
        //      - as a system property(JVM argument -Duf.accesstoken=<your-access-token>)
        //        and than invoking setAccessTokenFromSystemVar on the builder
        //      - directly setting the access token in the builder using the setAccessToken(String accessToken) method
        OddsFeedConfiguration config = OddsFeed.getOddsFeedConfigurationBuilder()
                .setAccessToken("3nK9rHzjWqVK2tifi6")
                .selectIntegration()
                .setSdkNodeId(15)
                .setDefaultLocale(Locale.forLanguageTag("de"))
                .build();
//        executeReplayScenario(config, logger);
//        if(5 > 1)
//            return;

//        OddsFeedConfiguration config = OddsFeed.getOddsFeedConfigurationBuilder()
//                .setAccessToken("t16Mojb2lRoTwSckxa")
//                .selectProduction()
//                .setSdkNodeId(125)
//                .setDesiredLocales(Arrays.asList(Locale.FRENCH, Locale.GERMAN))
//                .setDefaultLocale(Locale.forLanguageTag("cs"))
////                .setApiHost("custom-api.betradar.com")
//                .build();

        // create the new feed
        OddsFeed oddsFeed = new OddsFeed(new GlobalEventsListener(), config);
        BookmakerDetails bookmakerDetails = oddsFeed.getBookmakerDetails();

        // access the producer manager
        ProducerManager producerManager = oddsFeed.getProducerManager();
//        producerManager.disableProducer(1);
////        producerManager.disableProducer(2);
//        producerManager.disableProducer(3);
//        producerManager.disableProducer(4);
//        producerManager.disableProducer(5);
        producerManager.disableProducer(6);
        producerManager.disableProducer(7);
        producerManager.disableProducer(8);
        producerManager.disableProducer(9);
        producerManager.disableProducer(10);
        producerManager.disableProducer(11);
        producerManager.disableProducer(12);

        // set the last received message timestamp trough the producer - if known
        // (as an example, we set the last message received timestamp as 2 days ago)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -2);
        producerManager.setProducerRecoveryFromTimestamp(1, cal.getTime().getTime());
        producerManager.setProducerRecoveryFromTimestamp(3, cal.getTime().getTime());

        SportEntityWriter sportEntityWriter = new SportEntityWriter(config.getDefaultLocale(), true, true);
        MarketWriter marketWriter = new MarketWriter(config.getDefaultLocale(), true, true);

        // with the marketManager you can access various data about the available markets
        MarketDescriptionManager marketManager = oddsFeed.getMarketDescriptionManager();

        // With the sportsInfoManager helper you can access various data about the ongoing events
        SportsInfoManager sportsInfoManager = oddsFeed.getSportsInfoManager();

        BookingManager bookingManager = oddsFeed.getBookingManager();

        // In this example we will create 1 session which will receive all messages from all active producers
        // We can accomplish this with the OddsFeedSessionBuilder class.
        OddsFeedSessionBuilder sessionBuilder = oddsFeed.getSessionBuilder();

//        CustomMessageListener listener = new CustomMessageListener("AllMessages");
        sessionBuilder.setListener(new CustomMessageListener("AllMessages")).setMessageInterest(MessageInterest.AllMessages).build();
//        sessionBuilder.setListener(new CustomMessageListener("VirtualSports")).setMessageInterest(MessageInterest.VirtualSports).build();
//        sessionBuilder.setListener(new CustomMessageListener("LiveMessagesOnly")).setMessageInterest(MessageInterest.LiveMessagesOnly).build();
//        sessionBuilder.setListener(new CustomMessageListener("PrematchMessagesOnly")).setMessageInterest(MessageInterest.PrematchMessagesOnly).build();

        logger.info("Feed open");
        // Open the feed with all the built sessions
        oddsFeed.open();

        // The messages will now arrive in a separate thread to the MessageListener

//        Tournament tournament = (Tournament) sportsInfoManager.getLongTermEvent(URN.parse("sr:tournament:109"));
//        SportEntityWriter.writeSportEventData(tournament, true);
//        Competition competition = sportsInfoManager.getCompetition(URN.parse("sr:match:13640457"));
//        SportEntityWriter.writeSportEventData(competition, true);
//        competition = sportsInfoManager.getCompetition(URN.parse("sr:match:13639683"));
//        SportEntityWriter.writeSportEventData(competition, true);
//        competition = sportsInfoManager.getCompetition(URN.parse("sr:match:13640457"));
//        SportEntityWriter.writeSportEventData(competition, true);
//        competition = sportsInfoManager.getCompetition(URN.parse("sr:match:13639683"));
//        SportEntityWriter.writeSportEventData(competition, true);
//        competition = sportsInfoManager.getCompetition(URN.parse("sr:match:13640457"));
//        SportEntityWriter.writeSportEventData(competition, true);
//        tournament = (Tournament) sportsInfoManager.getLongTermEvent(URN.parse("sr:tournament:109"));
//        SportEntityWriter.writeSportEventData(tournament, true);

//        Thread.sleep(1000 * 30);

//        SportEvent sportEvent = oddsFeed.getSportsInfoManager().getSportEvent(URN.parse("sr:match:15688272"));
//        Match match = (Match)sportEvent;
//        Round round = match.getTournamentRound();
//        MatchStatus matchStatus = match.getStatus();
//        writeSportEvent(sportEvent, logger);
//        sportEvent = oddsFeed.getSportsInfoManager().getSportEvent(URN.parse("sr:stage:340475"));
//        writeSportEvent(sportEvent, logger);

        // Let's sleep awhile (30 minutes) and see what gets printed.
        Thread.sleep(1000 * 60 * 30L);

        // finally we close the feed.
        oddsFeed.close();

        logger.info("Feed close");
    }

    private static void writeSportEvent(SportEvent sportEvent, Logger logger) {

        if(sportEvent == null || !(sportEvent instanceof Stage))
        {
            return;
        }

        Stage match = (Stage) sportEvent;
        String baselineDescription = String.format("Id:'%s', SportId:'%s', Name:'%s', ScheduledTime:'%s', ScheduledEndTime:'%s', Type:%s",
                match.getId(),
                match.getSportId(),
                match.getName(Locale.ENGLISH),
                match.getScheduledTime(),
                match.getScheduledEndTime(),
                match.getStageType());
        baselineDescription = baselineDescription + String.format(", Status:[%s], EventStatus:%s, BookingStatus:%s, %s, %s",
                match.getStatus(),
                match.getEventStatus(),
                match.getBookingStatus(),
                match.getVenue(),
                match.getConditions());
        baselineDescription = String.format("Stage[%s, ParentStage:[%s], Stages:[%s]]",
                baselineDescription,
                match.getParentStage(),
                match.getStages());
        baselineDescription = String.format("Stage[%s, Competitors:[%s]]",
                baselineDescription,
                match.getCompetitors());
//        baselineDescription = String.format("Match[%s, Season:[%s], TournamentRound:[%s], Status:[%s]]",
//                baselineDescription,
//                match.getSeason(),
//                match.getTournamentRound(),
//                match.getStatus());

        logger.warn(baselineDescription);
    }

    private static void executeReplayScenario(OddsFeedConfiguration config, Logger logger) throws InterruptedException, IOException, InitException {
        ReplayOddsFeed replayFeed = new ReplayOddsFeed(new GlobalEventsListener(), config);
        replayFeed.getSessionBuilder()
                .setMessageInterest(MessageInterest.AllMessages)
                .setListener(new CustomMessageListener("ReplaySessionSetup"))
                .build();
        replayFeed.getReplayManager().stop();
        replayFeed.getReplayManager().clear();
        replayFeed.open();
        boolean addResponse = replayFeed.getReplayManager().addSportEventToReplay(URN.parse("sr:match:14893637"));
        List<SportEvent> queueEvents = replayFeed.getReplayManager().getReplayList();
        logger.info("Currently {} items in queue.", queueEvents.size());
        replayFeed.getReplayManager().play(100, 1000);
        Thread.sleep(1000 * 3);
        ReplayStatus replayStatus = replayFeed.getReplayManager().getPlayStatus();
        WriteReplayStatus(replayStatus, logger);
        Thread.sleep(1000 * 300);
        WriteReplayStatus(replayStatus, logger);
        replayFeed.close();
    }

    private static void WriteReplayStatus(ReplayStatus status, Logger logger)
    {
        logger.info("Status of replay: {}.", status);
    }
}
