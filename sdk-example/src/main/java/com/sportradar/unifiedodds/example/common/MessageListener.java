/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.UofListener;
import com.sportradar.unifiedodds.sdk.UofSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic feed listener implementation which outputs the data to the provided logger
 */
@SuppressWarnings(
    { "ClassFanOutComplexity", "LineLength", "NeedBraces", "OneStatementPerLine", "ParameterName" }
)
public class MessageListener implements UofListener {

    private final Logger logger;

    public MessageListener(String listener_version) {
        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "-" + listener_version);
    }

    /**
     * Any kind of odds update, or betstop signal results in an OddsChanges Message.
     *
     * @param sender the session
     * @param oddsChanges the odds changes message
     */
    @Override
    public void onOddsChange(UofSession sender, OddsChange<SportEvent> oddsChanges) {
        SportEvent event = oddsChanges.getEvent();
        logger.info("Received odds change for: " + event);

        event.getName(Locale.ENGLISH);
        event.getId();
        event.getSportId();
        event.getScheduledTime();
        event.getScheduledEndTime();

        // Now loop through the odds for each market
        for (MarketWithOdds marketOdds : oddsChanges.getMarkets()) {
            // Now loop through the outcomes within this particular market

            String marketDescription = marketOdds.getName();

            logger.info("Received odds information for: " + marketDescription);
            logger.info("Market status is: " + marketOdds.getStatus());

            // If the market is active printout odds for all outcomes
            if (marketOdds.getStatus() == MarketStatus.Active) {
                marketOdds.getMarketMetadata();
                marketOdds.getMarketDefinition();
                for (OutcomeOdds outcomeOdds : marketOdds.getOutcomeOdds()) {
                    String outcomeDesc = outcomeOdds.getName();
                    outcomeOdds.getAdditionalProbabilities();
                    outcomeOdds.getOutcomeDefinition();

                    logger.info(
                        "Outcome " +
                        outcomeDesc +
                        " has odds " +
                        outcomeOdds.getOdds(null) +
                        " " +
                        outcomeOdds.getProbability()
                    );
                }
            }
        }
    }

    /**
     * Send to rapidly suspend a set of markets (often all)
     *
     * @param sender the session
     * @param betStop the betstop message
     */
    @Override
    public void onBetStop(UofSession sender, BetStop<SportEvent> betStop) {
        logger.info("Received betstop for sport event " + betStop.getEvent());
    }

    /**
     * The onBetSettlement callback is received whenever a BetSettlement message is received. It
     * contains information about what markets that should be settled how. All markets and outcomes
     * that you have received odds changes messages for at some point in time you will receive
     * betsettlement messages for at some later point in time. That is if you receive odds for
     * outcome X for market Y, you will at a later time receive a BetSettlement message that
     * includes outcome X for market Y.
     *
     * @param sender the session
     * @param clearBets the BetSettlement message
     */
    @Override
    public void onBetSettlement(UofSession sender, BetSettlement<SportEvent> clearBets) {
        logger.info("Received bet settlement for sport event " + clearBets.getEvent());

        // Iterate through the betsettlements for each market
        for (MarketWithSettlement marketSettlement : clearBets.getMarkets()) {
            // Then iterate through the result for each outcome (win or loss)
            for (OutcomeSettlement result : marketSettlement.getOutcomeSettlements()) {
                if (result.getOutcomeResult().equals(OutcomeResult.Won)) logger.info(
                    "Outcome " + result.getId() + " is a win"
                ); else logger.info("Outcome " + result.getId() + " is a loss");
            }
        }
    }

    /**
     * If a BetSettlement was generated in error, you may receive a RollbackBetsettlement and have
     * to try to do whatever you can to undo the BetSettlement if possible.
     *
     * @param sender the session
     * @param rollbackBetSettlement the rollbackBetSettlement message referring to a previous
     *        BetSettlement
     */
    @Override
    public void onRollbackBetSettlement(
        UofSession sender,
        RollbackBetSettlement<SportEvent> rollbackBetSettlement
    ) {
        logger.info("Received rollback betsettlement for sport event " + rollbackBetSettlement.getEvent());
    }

    /**
     * If the markets were cancelled you may receive a
     * {@link BetCancel} describing which markets were
     * cancelled
     *
     * @param sender the session
     * @param betCancel A {@link BetCancel} instance
     *        specifying which markets were cancelled
     */
    @Override
    public void onBetCancel(UofSession sender, BetCancel<SportEvent> betCancel) {
        logger.info("Received bet cancel for sport event " + betCancel.getEvent());
    }

    /**
     * If the bet cancellations were send in error you may receive a
     * {@link RollbackBetCancel} describing the
     * erroneous cancellations
     *
     * @param sender the session
     * @param rbBetCancel A {@link RollbackBetCancel}
     *        specifying erroneous cancellations
     */
    @Override
    public void onRollbackBetCancel(UofSession sender, RollbackBetCancel<SportEvent> rbBetCancel) {
        logger.info("Received rollback betcancel for sport event " + rbBetCancel.getEvent());
    }

    /**
     * If there are important fixture updates you will receive fixturechange message. The thinking
     * is that most fixture updates are queried by you yourself using the SportDataProvider. However,
     * if there are important/urgent changes you will also receive a fixture change message (e.g. if
     * a match gets delayed, or if Sportradar for some reason needs to stop live coverage of a match
     * etc.). This message allows you to promptly respond to such changes
     *
     * @param sender the session
     * @param fixtureChange the SDKFixtureChange message - describing what sport event and what type
     *        of fixture change
     */
    @Override
    public void onFixtureChange(UofSession sender, FixtureChange<SportEvent> fixtureChange) {
        logger.info("Received fixture change for sport event " + fixtureChange.getEvent());
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
    public void onUnparsableMessage(UofSession sender, UnparsableMessage unparsableMessage) {
        Producer possibleProducer = unparsableMessage.getProducer(); // the SDK will try to provide the origin of the message

        if (unparsableMessage.getEvent() != null) {
            logger.info(
                "Problems detected on received message for event " + unparsableMessage.getEvent().getId()
            );
        } else {
            logger.info("Problems detected on received message"); // probably a system message deserialization failure
        }
    }

    @Override
    public void onUserUnhandledException(UofSession sender, Exception exception) {}
}
