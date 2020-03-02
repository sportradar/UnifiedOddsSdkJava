/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;

/**
 * Internally used generic interface used as a base for interfaces exposed to the SDK user
 */
interface GenericOddsFeedListener<T extends SportEvent> {

    /**
     * Any kind of odds update, or betstop signal results in an OddsChanges Message.
     *
     * @param sender the session
     * @param oddsChanges the odds changes message
     */
    void onOddsChange(OddsFeedSession sender, OddsChange<T> oddsChanges);

    /**
     * Send to rapidly suspend a set of markets (often all)
     *
     * @param sender the session
     * @param betStop the betstop message
     */
    void onBetStop(OddsFeedSession sender, BetStop<T> betStop);

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
    void onBetSettlement(OddsFeedSession sender, BetSettlement<T> clearBets);

    /**
     * If a BetSettlement was generated in error, you may receive a RollbackBetsettlement and have
     * to try to do whatever you can to undo the BetSettlement if possible.
     *
     * @param sender the session
     * @param rollbackBetSettlement the rollbackBetSettlement message referring to a previous
     *        BetSettlement
     */
    void onRollbackBetSettlement(OddsFeedSession sender, RollbackBetSettlement<T> rollbackBetSettlement);

    /**
     * If the markets were cancelled you may receive a
     * {@link BetCancel} describing which markets were
     * cancelled
     * 
     * @param sender the session
     * @param betCancel A {@link BetCancel} instance
     *        specifying which markets were cancelled
     */
    void onBetCancel(OddsFeedSession sender, BetCancel<T> betCancel);

    /**
     * If the bet cancellations were send in error you may receive a
     * {@link RollbackBetCancel} describing the
     * erroneous cancellations
     * 
     * @param sender the session
     * @param rbBetCancel A {@link RollbackBetCancel}
     *        specifying erroneous cancellations
     */
    void onRollbackBetCancel(OddsFeedSession sender, RollbackBetCancel<T> rbBetCancel);

    /**
     * If there are important fixture updates you will receive fixturechange message. The thinking
     * is that most fixture updates are queried by you yourself using the SportInfoManager. However,
     * if there are important/urgent changes you will also receive a fixture change message (e.g. if
     * a match gets delayed, or if Sportradar for some reason needs to stop live coverage of a match
     * etc.). This message allows you to promptly respond to such changes
     *
     * @param sender the session
     * @param fixtureChange the SDKFixtureChange message - describing what sport event and what type
     *        of fixture change
     */
    void onFixtureChange(OddsFeedSession sender, FixtureChange<T> fixtureChange);

    /**
     * This handler is called when the SDK detects that it has problems parsing a certain message.
     * The handler can decide to take some custom action (shutting down everything etc. doing some
     * special analysis of the raw message content etc) or just ignore the message. The SDK itself
     * will always log that it has received an unparseable message and will ignore the message so a
     * typical implementation can leave this handler empty.
     *
     * @deprecated in favour of {{@link #onUnparsableMessage(OddsFeedSession, UnparsableMessage)}} from v2.0.11
     * 
     * @param sender the session
     * @param rawMessage the raw message received from Betradar
     * @param event if the SDK was able to extract the event this message is for it will be here
     *        otherwise null
     */
    @Deprecated
    void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event);

    /**
     * This handler is called when the SDK detects that it has problems parsing/dispatching a message.
     * The handler can decide to take some custom action (shutting down everything etc. doing some
     * special analysis of the raw message content etc) or just ignore the message. The SDK itself
     * will always log that it has received an unparseable message.
     *
     * @since v2.0.11
     *
     * @param sender the session
     * @param unparsableMessage A {@link UnparsableMessage} instance describing the message that had issues
     */
    default void onUnparsableMessage(OddsFeedSession sender, UnparsableMessage unparsableMessage) {
        // Default NO-OP implementation
    }

    /**
     * This handler is called when the SDK detects problems while {@link OddsFeedListener} process a message.
     * The handler can choose to handle exception or just ignore it.
     * The SDK itself will always log exception.
     *
     * @param sender the session
     * @param exception A {@link Exception} instance containing unhandled exception
     */
    default void onUserUnhandledException(OddsFeedSession sender, Exception exception) {
    }
}
