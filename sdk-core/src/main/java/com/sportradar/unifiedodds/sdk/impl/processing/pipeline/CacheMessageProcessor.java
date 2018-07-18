/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.processing.pipeline;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFBetSettlement;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFFixtureChange;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;

import java.util.UUID;

/**
 * A {@link FeedMessageProcessor} implementation which is used in the message processing pipeline
 */
public class CacheMessageProcessor implements FeedMessageProcessor {
    /**
     * The processor identifier
     */
    private final String processorId;

    /**
     * A {@link SportEventStatusCache} used to store/purge sport event statuses
     */
    private final SportEventStatusCache sportEventStatusCache;

    /**
     * A {@link SportEventCache} used to purge sport events data
     */
    private final SportEventCache sportEventCache;

    /**
     * The {@link FeedMessageProcessor} implementation which should be called after the message has been processed
     */
    private FeedMessageProcessor nextMessageProcessor;


    /**
     * Initializes a new {@link CacheMessageProcessor} instance
     *
     * @param sportEventStatusCache - a {@link SportEventStatusCache} used to store sport event statuses
     */
    public CacheMessageProcessor(SportEventStatusCache sportEventStatusCache, SportEventCache sportEventCache) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportEventCache);

        this.processorId = UUID.randomUUID().toString();
        this.sportEventStatusCache = sportEventStatusCache;
        this.sportEventCache = sportEventCache;
    }


    /**
     * Processes the provided message. If the {@link #nextMessageProcessor} is defined, the instance will forward the
     * message to the {@link #nextMessageProcessor}
     *
     * @param message - the message that should be processed
     * @param body - the raw body of the received message
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     */
    @Override
    public void processMessage(UnmarshalledMessage message, byte[] body, RoutingKeyInfo routingKeyInfo) {
        if (message instanceof UFOddsChange) {
            processOddsChangeMessage((UFOddsChange) message);
        } else if (message instanceof UFFixtureChange) {
            processFixtureChangeMessage((UFFixtureChange) message);
        } else if (message instanceof UFBetStop) {
            processBetStopMessage((UFBetStop) message);
        } else if (message instanceof UFBetSettlement) {
            processBetSettlementMessage((UFBetSettlement) message);
        }

        if (nextMessageProcessor != null) {
            nextMessageProcessor.processMessage(message, body, routingKeyInfo);
        }
    }

    /**
     * Returns the processor identifier
     *
     * @return - the processor identifier
     */
    @Override
    public String getProcessorId() {
        return processorId;
    }

    /**
     * Sets the next message processor that will be invoked when the message processing is finished
     *
     * @param nextMessageProcessor - the {@link FeedMessageProcessor} implementation that will be
     *                               invoked after the message process is finished
     */
    @Override
    public void setNextMessageProcessor(FeedMessageProcessor nextMessageProcessor) {
        this.nextMessageProcessor = nextMessageProcessor;
    }

    /**
     * Processes the messages of type {@link UFFixtureChange}
     *
     * @param message - the received messages of type {@link UFFixtureChange}
     */
    private void processFixtureChangeMessage(UFFixtureChange message) {
        Preconditions.checkNotNull(message);

        URN relatedEventId = URN.parse(message.getEventId());

        sportEventCache.purgeCacheItem(relatedEventId);
        sportEventStatusCache.purgeSportEventStatus(relatedEventId);
    }

    /**
     * Processes the messages of type {@link UFOddsChange}
     *
     * @param message - the received messages of type {@link UFOddsChange}
     */
    private void processOddsChangeMessage(UFOddsChange message) {
        Preconditions.checkNotNull(message);

        if (message.getSportEventStatus() == null) {
            return;
        }

        sportEventStatusCache.addSportEventStatus(URN.parse(message.getEventId()),
                new SportEventStatusDTO(message.getSportEventStatus()));
    }

    /**
     * Processes the messages of type {@link UFBetStop}
     *
     * @param message - the received messages of type {@link UFBetStop}
     */
    private void processBetStopMessage(UFBetStop message) {
        Preconditions.checkNotNull(message);

        URN relatedEventId = URN.parse(message.getEventId());

        sportEventStatusCache.purgeSportEventStatus(relatedEventId);

        if (isDrawEvent(relatedEventId)) {
            sportEventCache.purgeCacheItem(relatedEventId);
        }
    }

    /**
     * Processes the messages of type {@link UFBetSettlement}
     *
     * @param message - the received messages of type {@link UFBetSettlement}
     */
    private void processBetSettlementMessage(UFBetSettlement message) {
        Preconditions.checkNotNull(message);

        URN relatedEventId = URN.parse(message.getEventId());

        sportEventStatusCache.purgeSportEventStatus(relatedEventId);

        if (isDrawEvent(relatedEventId)) {
            sportEventCache.purgeCacheItem(relatedEventId);
        }
    }

    private static boolean isDrawEvent(URN eventId) {
        Preconditions.checkNotNull(eventId);

        return eventId.getType() != null &&
                eventId.getType().equals("draw");
    }
}
