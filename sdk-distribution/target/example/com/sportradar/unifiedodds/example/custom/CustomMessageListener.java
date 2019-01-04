/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.custom;

import com.sportradar.unifiedodds.example.common.MarketWriter;
import com.sportradar.unifiedodds.example.common.SportEntityWriter;
import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * A custom feed listener implementation which outputs the data to the provided logger
 */
public class CustomMessageListener implements OddsFeedListener {
    private final Logger logger;
    private final SportEntityWriter sportEntityWriter;
    private final CustomSportEntityWriter customSportEntityWriter;
    private final MarketWriter marketWriter;
    private final Locale locale = Locale.ENGLISH;

    public CustomMessageListener(String listener_version) {
        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "-" + listener_version);
        sportEntityWriter = new SportEntityWriter(Locale.ENGLISH, true, true);
        customSportEntityWriter = new CustomSportEntityWriter(Locale.ENGLISH, true, true);
        marketWriter = new MarketWriter(Locale.ENGLISH, true, true);
    }

    private void writeSportEvent(SportEvent sportEvent) {

        if(sportEvent == null || !(sportEvent instanceof Match))
        {
            return;
        }

        Match match = (Match) sportEvent;
        String baselineDescription = String.format("Id:'%s', SportId:'%s', Name:'%s', ScheduledTime:'%s'",
                match.getId(),
                match.getSportId(),
                match.getName(locale),
                match.getScheduledTime());
        baselineDescription = baselineDescription + String.format(", Status:[%s], EventStatus:%s, BookingStatus:%s, %s, %s",
                match.getStatus(),
                match.getEventStatus(),
                match.getBookingStatus(),
                match.getVenue(),
                match.getConditions());
        baselineDescription = String.format("Match[%s, HomeCompetitor:[%s], AwayCompetitor:[%s]]",
                baselineDescription,
                match.getHomeCompetitor(),
                match.getAwayCompetitor());
//        baselineDescription = String.format("Match[%s, Season:[%s], TournamentRound:[%s], Status:[%s]]",
//                baselineDescription,
//                match.getSeason(),
//                match.getTournamentRound(),
//                match.getStatus());

        logger.warn(baselineDescription);
    }

    /**
     * Any kind of odds update, or betstop signal results in an OddsChanges Message.
     *
     * @param sender      the session
     * @param oddsChanges the odds changes message
     */
    @Override
    public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChanges) {
        logBaseMessageData(oddsChanges.getClass(), oddsChanges.getEvent(), oddsChanges.getProducer());
//        marketWriter.writeMarketNames(oddsChanges.getMarkets());
//
//        if(5>1)
//            return;

        if(oddsChanges.getOddsGenerationProperties() != null) {
            Double x = oddsChanges.getOddsGenerationProperties().getExpectedTotals();
            if(x != null)
                x = Double.valueOf(1);
        }

        SportEvent sportEvent = oddsChanges.getEvent();
        customSportEntityWriter.writeData(sportEvent);
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.DATE, 5);
        if((sportEvent instanceof Match)) {
            Match match = (Match)sportEvent;
            MatchStatus status = match.getStatus();
            if (status != null)
            {
//                logger.warn("SportEvent {} has score: {}:{}. {}", sportEvent.getId(), status.getHomeScore(), status.getAwayScore(), status.getStatus());

                //if (status.EventResults != null && status.EventResults.Any())
                //{
                //    _log.Warn("Have EventResults");
                //}
            }
//            Optional<CompetitionStatus> status1 = match.getStatusIfPresent();
//            if(status1.isPresent()) {
//                MatchStatus matchStatus1 = (MatchStatus)status1.get();
//            }
//            else {
//                MatchStatus matchStatus2 = match.getStatus();
//                logger.warn("MatchStatus: " + matchStatus2.getStatus());
//            }

//            Optional<EventTimeline> timeline1 = match.getEventTimelineIfPresent(locale);
//            if(timeline1.isPresent()) {
//                for (TimelineEvent timelineEvent : timeline1.get().getTimelineEvents()) {
//                    logger.warn(String.format("Match %s timeline1: %s", match.getId(), timelineEvent.toString()));
//                }
//            }
//            else {
//                EventTimeline timeline2 = match.getEventTimeline(locale);
//                if (timeline2 != null) {
//                    for (TimelineEvent timelineEvent : timeline2.getTimelineEvents()) {
//                        logger.warn(String.format("Match %s timeline2: %s", match.getId(), timelineEvent.toString()));
//                    }
//                }
//            }
        }

//        if(!(sportEvent instanceof Stage)) {
//            return;
//        }
//        if(sportEvent.getScheduledTime() != null && sportEvent.getScheduledTime().after(new Date()) && sportEvent.getScheduledTime().before(c.getTime())) {
////            writeSportEvent(oddsChanges.getEvent());
////            writeSportEvent(oddsChanges.getEvent());
////            sportEntityWriter.writeData(oddsChanges.getEvent());
//        }
////        marketWriter.writeMarketNames(oddsChanges.getMarkets());

//        var sportId = message.Event.GetSportIdAsync().Result;
//        var producerId = message.Producer;
//        foreach (var market in message.Markets)
//        {
//            if (market.Id == 23) // || market.Id == 203)
//            {
//                var mappedMarkets = market.GetMappedMarketIdsAsync().Result.ToList();
//                var marketSpecifiers = market.Specifiers == null
//                        ? null
//                        : string.Join(";", market.Specifiers.Select(s => $"{s.Key}={s.Value}"));
//                foreach (var mappedMarket in mappedMarkets)
//                {
//                    if (mappedMarket is LoMarketMapping)
//                    {
//                        var loMappedMarket = (LoMarketMapping) mappedMarket;
//                        Log.Warn($"MarketMapping:: Market {market.Id}, specifiers={marketSpecifiers}, sportId={sportId}, producerId:{producerId.Id} mapped to {loMappedMarket.GetType().Name} with TypeId:{loMappedMarket.TypeId}, SubType:{loMappedMarket.SubTypeId} and Sov:{loMappedMarket.Sov}");
//                    }
//                        else
//                    {
//                        var lcooMappedMarket = (LcooMarketMapping) mappedMarket;
//                        Log.Warn($"MarketMapping:: Market {market.Id}, specifiers={marketSpecifiers}, sportId={sportId}, producerId:{producerId.Id} mapped to {lcooMappedMarket.GetType().Name} with TypeId:{lcooMappedMarket.TypeId} and Sov:{lcooMappedMarket.Sov}");
//                    }
//
//                    foreach (var outcomeOdd in market.OutcomeOdds)
//                    {
//                        var oddV1 = (IOutcomeOddsV1) outcomeOdd;
//                        var mappedOutcomes = outcomeOdd.GetMappedOutcomeIdsAsync().Result.ToList();
//                        foreach (var mappedOutcome in mappedOutcomes)
//                        {
//                            Log.Warn($"OutcomeMapping:: Market {market.Id}, specifiers={marketSpecifiers}, sportId={sportId}, producerId:{producerId.Id} and OutcomeId={oddV1.Id} and Odds={oddV1.GetOdds()} mapped to mapped outcome {mappedOutcome.GetType().Name} with Id:{mappedOutcome.Id} and MarketId:{mappedOutcome.MarketId}");
//                        }
//                    }
//                }
//                var c = mappedMarkets.Count();
//            }
//        }

//        URN sportId = oddsChanges.getEvent().getSportId();
//        Producer producerId = oddsChanges.getProducer();
//        for (MarketWithOdds market : oddsChanges.getMarkets()) {
//            if(market.getId() == 23 || market.getId() == 203)
//            {
//                List<MarketMappingData> mappedMarkets = market.getMarketDefinition().getValidMappings(Locale.ENGLISH);
//                String marketSpecifiers = market.getSpecifiers() == null
//                        ? null
//                        : market.getSpecifiers().entrySet().stream().map(e -> e.getKey()+"="+e.getValue())
//                        .collect(joining("&"));
//
//                logger.warn(String.format("Market %s and specifiers=%s", market.getId(), marketSpecifiers));
//
//                logger.warn(MarketMappingWriter.writeMarketMapping(market, Locale.ENGLISH));
//                logger.warn(MarketMappingWriter.writeMarketOutcomeMapping(market, Locale.ENGLISH));
////                for (MarketMappingData mappedMarket : mappedMarkets) {
////                    logger.warn(MarketMappingWriter.writeMarketMapping(market, Locale.ENGLISH));
////                }
//            }
//        }


//        // Now loop through the odds for each market
//        for (MarketWithOdds marketOdds : oddsChanges.getMarkets()) {
//            // Now loop through the outcomes within this particular market
//
//            String marketDescription = marketOdds.getName();
//
//            logger.info("Received odds information for : " + marketDescription);
//            logger.info("Market status is : " + marketOdds.getStatus());
//
//            // If the market is active printout odds for all outcomes
//            if (marketOdds.getStatus() == MarketStatus.Active) {
//                for (OutcomeOdds outcomeOdds : marketOdds.getOutcomeOdds()) {
//                    String outcomeDesc = outcomeOdds.getName();
//
//                    logger.info("Outcome " + outcomeDesc + " has odds " + outcomeOdds.getOdds() + " "
//                                + outcomeOdds.getProbability());
//                }
//            }
//        }
    }

    /**
     * Send to rapidly suspend a set of markets (often all)
     *
     * @param sender  the session
     * @param betStop the betstop message
     */
    @Override
    public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {
        logBaseMessageData(betStop.getClass(), betStop.getEvent(), betStop.getProducer());
        sportEntityWriter.writeData(betStop.getEvent());
    }

    /**
     * The onBetSettlement callback is received whenever a BetSettlement message is received. It
     * contains information about what markets that should be settled how. All markets and outcomes
     * that you have received odds changes messages for at some point in time you will receive
     * betsettlement messages for at some later point in time. That is if you receive odds for
     * outcome X for market Y, you will at a later time receive a BetSettlement message that
     * includes outcome X for market Y.
     *
     * @param sender    the session
     * @param clearBets the BetSettlement message
     */
    @Override
    public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> clearBets) {
        logBaseMessageData(clearBets.getClass(), clearBets.getEvent(), clearBets.getProducer());
        sportEntityWriter.writeData(clearBets.getEvent());
        marketWriter.writeMarketNames(clearBets.getMarkets());
    }

    /**
     * If a BetSettlement was generated in error, you may receive a RollbackBetsettlement and have
     * to try to do whatever you can to undo the BetSettlement if possible.
     *
     * @param sender                the session
     * @param rollbackBetSettlement the rollbackBetSettlement message referring to a previous
     *                              BetSettlement
     */
    @Override
    public void onRollbackBetSettlement(OddsFeedSession sender, RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
        logBaseMessageData(rollbackBetSettlement.getClass(), rollbackBetSettlement.getEvent(), rollbackBetSettlement.getProducer());
        sportEntityWriter.writeData(rollbackBetSettlement.getEvent());
        marketWriter.writeMarketNames(rollbackBetSettlement.getMarkets());
    }

    /**
     * If the markets were cancelled you may receive a
     * {@link BetCancel} describing which markets were
     * cancelled
     *
     * @param sender    the session
     * @param betCancel A {@link BetCancel} instance
     *                  specifying which markets were cancelled
     */
    @Override
    public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {
        logBaseMessageData(betCancel.getClass(), betCancel.getEvent(), betCancel.getProducer());
        sportEntityWriter.writeData(betCancel.getEvent());
        marketWriter.writeMarketNames(betCancel.getMarkets());
    }

    /**
     * If the bet cancellations were send in error you may receive a
     * {@link RollbackBetCancel} describing the
     * erroneous cancellations
     *
     * @param sender      the session
     * @param rbBetCancel A {@link RollbackBetCancel}
     *                    specifying erroneous cancellations
     */
    @Override
    public void onRollbackBetCancel(OddsFeedSession sender, RollbackBetCancel<SportEvent> rbBetCancel) {
        logBaseMessageData(rbBetCancel.getClass(), rbBetCancel.getEvent(), rbBetCancel.getProducer());
        sportEntityWriter.writeData(rbBetCancel.getEvent());
        marketWriter.writeMarketNames(rbBetCancel.getMarkets());
    }

    /**
     * If there are important fixture updates you will receive fixturechange message. The thinking
     * is that most fixture updates are queried by you yourself using the SportInfoManager. However,
     * if there are important/urgent changes you will also receive a fixture change message (e.g. if
     * a match gets delayed, or if Sportradar for some reason needs to stop live coverage of a match
     * etc.). This message allows you to promptly respond to such changes
     *
     * @param sender        the session
     * @param fixtureChange the SDKFixtureChange message - describing what sport event and what type
     *                      of fixture change
     */
    @Override
    public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {
        logBaseMessageData(fixtureChange.getClass(), fixtureChange.getEvent(), fixtureChange.getProducer());
        sportEntityWriter.writeData(fixtureChange.getEvent());
    }

    /**
     * This handler is called when the SDK detects that it has problems parsing a certain message.
     * The handler can decide to take some custom action (shutting down everything etc. doing some
     * special analysis of the raw message content etc) or just ignore the message. The SDK itself
     * will always log that it has received an unparseable message and will ignore the message so a
     * typical implementation can leave this handler empty.
     *
     * @param sender     the session
     * @param rawMessage the raw message received from Betradar
     * @param event      if the SDK was able to extract the event this message is for it will be here
     *                   otherwise null
     * @deprecated in favour of {{@link #onUnparsableMessage(OddsFeedSession, UnparsableMessage)}} from v2.0.11
     */
    @Override
    @Deprecated
    public void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event) {
        if (event != null) {
            logger.info("Problems deserializing received message for event " + event.getId());
        } else {
            logger.info("Problems deserializing received message"); // probably a system message deserialization failure
        }
    }

    /**
     * This handler is called when the SDK detects that it has problems parsing/dispatching a message.
     * The handler can decide to take some custom action (shutting down everything etc. doing some
     * special analysis of the raw message content etc) or just ignore the message. The SDK itself
     * will always log that it has received an unparseable message.
     *
     * @param sender            the session
     * @param unparsableMessage A {@link UnparsableMessage} instance describing the message that had issues
     * @since v2.0.11
     */
    @Override
    public void onUnparsableMessage(OddsFeedSession sender, UnparsableMessage unparsableMessage) {
        Producer possibleProducer = unparsableMessage.getProducer(); // the SDK will try to provide the origin of the message

        if (unparsableMessage.getEvent() != null) {
            String xml = new String(unparsableMessage.getRawMessage());
            logger.info("Problems detected on received message for event " + unparsableMessage.getEvent().getId() + ". Message: " + xml);

        } else {
            logger.info("Problems detected on received message"); // probably a system message deserialization failure
        }
    }

    private void logBaseMessageData(Class msgClass, SportEvent event, Producer producer) {
        logger.info("Received " + msgClass.getSimpleName() + " for producer: " + producer.getId() + "-" + producer.getName() + " for sportEvent: " + event.getId());
//        if((event instanceof Match)) {
//            Match match = (Match) event;
//            MatchStatus status = match.getStatus();
//            if (status != null) {
//                logger.warn("SportEvent {} has score: {}:{}. {}",
//                        event.getId(),
//                        status.getHomeScore() == null ? 0 : status.getHomeScore(),
//                        status.getAwayScore() == null ? 0 : status.getAwayScore(),
//                        status.getStatus());
//            }
//        }
    }
}
