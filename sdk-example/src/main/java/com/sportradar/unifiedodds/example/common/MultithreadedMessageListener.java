/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A feed listener implementation which uses separate thread for message parsing
 */
public class MultithreadedMessageListener implements OddsFeedListener, Closeable {
    private final Logger logger;
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final Thread thread;

    public MultithreadedMessageListener(String listener_version) {
        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "-" + listener_version);
        thread = new Thread(parseMessages());
        thread.start();
    }

    /**
     * Message parsing method that will be executed in another thread.
     */
    private Runnable parseMessages() {
        return () -> {
            try {
                logger.info("Parsing thread started");
                while (true) {
                    Message message = messages.take();
                    logger.info("Parsing message: {}", message);
                }
            } catch (InterruptedException e) {
                logger.info("Parsing thread stopped");
                Thread.currentThread().interrupt();
            }
        };
    }

    /**
     * Any kind of odds update, or betstop signal results in an OddsChanges Message.
     *
     * @param sender the session
     * @param oddsChanges the odds changes message
     */
    @Override
    public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChanges) {
        logger.info("Received odds change for: " + oddsChanges.getEvent());
        queueMessage(oddsChanges);
    }

    /**
     * Send to rapidly suspend a set of markets (often all)
     *
     * @param sender the session
     * @param betStop the betstop message
     */
    @Override
    public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {
        logger.info("Received betstop for sport event " + betStop.getEvent());
        queueMessage(betStop);
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
    public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> clearBets) {
        logger.info("Received bet settlement for sport event " + clearBets.getEvent());
        queueMessage(clearBets);
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
    public void onRollbackBetSettlement(OddsFeedSession sender, RollbackBetSettlement<SportEvent> rollbackBetSettlement) {
        logger.info("Received rollback betsettlement for sport event " + rollbackBetSettlement.getEvent());
        queueMessage(rollbackBetSettlement);
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
    public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {
        logger.info("Received bet cancel for sport event " + betCancel.getEvent());
        queueMessage(betCancel);
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
    public void onRollbackBetCancel(OddsFeedSession sender, RollbackBetCancel<SportEvent> rbBetCancel) {
        logger.info("Received rollback betcancel for sport event " + rbBetCancel.getEvent());
        queueMessage(rbBetCancel);
    }

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
    @Override
    public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {
        logger.info("Received fixture change for sport event " + fixtureChange.getEvent());
        queueMessage(fixtureChange);
    }

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
            logger.info("Problems detected on received message for event " + unparsableMessage.getEvent().getId());
        } else {
            logger.info("Problems detected on received message"); // probably a system message deserialization failure
        }
        queueMessage(unparsableMessage);
    }

    private void queueMessage(Message message) {
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            logger.error("Error while adding message to queue.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        thread.interrupt();
    }
}
