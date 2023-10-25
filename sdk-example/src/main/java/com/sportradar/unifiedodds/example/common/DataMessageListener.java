/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.UofListener;
import com.sportradar.unifiedodds.sdk.UofSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "NeedBraces", "ParameterName" })
public class DataMessageListener implements UofListener {

    private final Logger logger;
    private final SportEntityWriter sportEntityWriter;
    private final MarketWriter marketWriter;
    private final boolean writeEventData;
    private final boolean writeMarketData;

    public DataMessageListener(
        String listener_version,
        List<Locale> desiredLocales,
        boolean writeEventData,
        boolean writeMarketData
    ) {
        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "-" + listener_version);
        sportEntityWriter = new SportEntityWriter(desiredLocales, true, false);
        marketWriter = new MarketWriter(desiredLocales, true, false);
        this.writeEventData = writeEventData;
        this.writeMarketData = writeMarketData;
    }

    /**
     * Any kind of odds update, or betstop signal results in an OddsChanges Message.
     *
     * @param sender the session
     * @param oddsChanges the odds changes message
     */
    @Override
    public void onOddsChange(UofSession sender, OddsChange<SportEvent> oddsChanges) {
        logBaseMessageData(oddsChanges.getClass(), oddsChanges.getEvent());
        if (writeEventData) sportEntityWriter.writeData(oddsChanges.getEvent());
        if (writeMarketData) marketWriter.writeMarketNames(oddsChanges.getMarkets());
    }

    /**
     * Send to rapidly suspend a set of markets (often all)
     *
     * @param sender the session
     * @param betStop the betstop message
     */
    @Override
    public void onBetStop(UofSession sender, BetStop<SportEvent> betStop) {
        logBaseMessageData(betStop.getClass(), betStop.getEvent());
        if (writeEventData) sportEntityWriter.writeData(betStop.getEvent());
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
        logBaseMessageData(clearBets.getClass(), clearBets.getEvent());
        if (writeEventData) sportEntityWriter.writeData(clearBets.getEvent());
        if (writeMarketData) marketWriter.writeMarketNames(clearBets.getMarkets());
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
        logBaseMessageData(rollbackBetSettlement.getClass(), rollbackBetSettlement.getEvent());
        if (writeEventData) sportEntityWriter.writeData(rollbackBetSettlement.getEvent());
        if (writeMarketData) marketWriter.writeMarketNames(rollbackBetSettlement.getMarkets());
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
        logBaseMessageData(betCancel.getClass(), betCancel.getEvent());
        if (writeEventData) sportEntityWriter.writeData(betCancel.getEvent());
        if (writeMarketData) marketWriter.writeMarketNames(betCancel.getMarkets());
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
        logBaseMessageData(rbBetCancel.getClass(), rbBetCancel.getEvent());
        if (writeEventData) sportEntityWriter.writeData(rbBetCancel.getEvent());
        if (writeMarketData) marketWriter.writeMarketNames(rbBetCancel.getMarkets());
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
        logBaseMessageData(fixtureChange.getClass(), fixtureChange.getEvent());
        if (writeEventData) sportEntityWriter.writeData(fixtureChange.getEvent());
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

    private void logBaseMessageData(Class msgClass, SportEvent event) {
        logger.info("Received " + msgClass.getSimpleName() + " for sport event " + event.getId());
    }
}
