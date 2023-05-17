/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.processing.pipeline;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFBetSettlement;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFFixtureChange;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import com.sportradar.unifiedodds.sdk.caching.DataRouterListener;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A {@link FeedMessageProcessor} implementation which is used in the message processing pipeline
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "ClassFanOutComplexity", "HiddenField" })
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
     * A {@link ProcessedFixtureChangesTracker} used to track processed fixture change messages
     */
    private final ProcessedFixtureChangesTracker processedFixtureChangesTracker;

    /**
     * The {@link FeedMessageProcessor} implementation which should be called after the message has been processed
     */
    private FeedMessageProcessor nextMessageProcessor;

    private Set<Integer> ignoredProducersForFixtureEndpoint;

    /**
     * Initializes a new {@link CacheMessageProcessor} instance
     *
     * @param sportEventStatusCache the {@link SportEventStatusCache} used by the associated SDK instance
     * @param sportEventCache the {@link SportEventCache} used by the associated SDK instance
     * @param processedFixtureChangesTracker used to track processed fixture change messages
     * @param producerManager to get 'virtual' producers to ignore for fixture_change_fixture endpoint
     */
    public CacheMessageProcessor(
        SportEventStatusCache sportEventStatusCache,
        SportEventCache sportEventCache,
        ProcessedFixtureChangesTracker processedFixtureChangesTracker,
        SDKProducerManager producerManager
    ) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(processedFixtureChangesTracker);
        Preconditions.checkNotNull(producerManager);

        this.processorId = UUID.randomUUID().toString();
        this.sportEventStatusCache = sportEventStatusCache;
        this.sportEventCache = sportEventCache;
        this.processedFixtureChangesTracker = processedFixtureChangesTracker;
        Collection<Producer> producers = producerManager.getAvailableProducers().values();
        this.ignoredProducersForFixtureEndpoint =
            producers
                .stream()
                .filter(f -> f.getProducerScopes().contains(ProducerScope.Virtuals))
                .map(m -> m.getId())
                .collect(Collectors.toSet());
    }

    /**
     * Processes the provided message. If the {@link #nextMessageProcessor} is defined, the instance will forward the
     * message to the {@link #nextMessageProcessor}
     *
     * @param message - the message that should be processed
     * @param body - the raw body of the received message
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     * @param timestamp - all message timestamps
     */
    @Override
    public void processMessage(
        UnmarshalledMessage message,
        byte[] body,
        RoutingKeyInfo routingKeyInfo,
        MessageTimestamp timestamp
    ) {
        if (message instanceof UFOddsChange) {
            UFOddsChange fm = (UFOddsChange) message;
            sportEventStatusCache.addEventIdForTimelineIgnore(
                URN.parse(fm.getEventId()),
                fm.getProduct(),
                fm.getClass().getSimpleName()
            );
            processOddsChangeMessage(fm);
        } else if (message instanceof UFFixtureChange) {
            UFFixtureChange fm = (UFFixtureChange) message;
            sportEventStatusCache.addEventIdForTimelineIgnore(
                URN.parse(fm.getEventId()),
                fm.getProduct(),
                fm.getClass().getSimpleName()
            );
            processFixtureChangeMessage(fm);
        } else if (message instanceof UFBetStop) {
            UFBetStop fm = (UFBetStop) message;
            sportEventStatusCache.addEventIdForTimelineIgnore(
                URN.parse(fm.getEventId()),
                fm.getProduct(),
                fm.getClass().getSimpleName()
            );
            processBetStopMessage(fm);
        } else if (message instanceof UFBetSettlement) {
            UFBetSettlement fm = (UFBetSettlement) message;
            sportEventStatusCache.addEventIdForTimelineIgnore(
                URN.parse(fm.getEventId()),
                fm.getProduct(),
                fm.getClass().getSimpleName()
            );
            processBetSettlementMessage(fm);
        }

        if (nextMessageProcessor != null) {
            nextMessageProcessor.processMessage(message, body, routingKeyInfo, timestamp);
        }
    }

    @Override
    public String getProcessorId() {
        return processorId;
    }

    @Override
    public void setNextMessageProcessor(FeedMessageProcessor nextMessageProcessor) {
        this.nextMessageProcessor = nextMessageProcessor;
    }

    private void processFixtureChangeMessage(UFFixtureChange message) {
        Preconditions.checkNotNull(message);

        if (processedFixtureChangesTracker.onFixtureChangeReceived(message)) {
            return;
        }

        URN relatedEventId = URN.parse(message.getEventId());

        sportEventCache.purgeCacheItem(relatedEventId);
        sportEventStatusCache.purgeSportEventStatus(relatedEventId);
        if (!ignoredProducersForFixtureEndpoint.contains(message.getProduct())) {
            sportEventCache.addFixtureTimestamp(relatedEventId);
        }
    }

    private void processOddsChangeMessage(UFOddsChange message) {
        Preconditions.checkNotNull(message);

        if (message.getSportEventStatus() == null) {
            return;
        }

        URN eventId = URN.parse(message.getEventId());
        SportEventStatusDTO sportEventStatusDTO = new SportEventStatusDTO(message.getSportEventStatus());
        ((DataRouterListener) sportEventStatusCache).onSportEventStatusFetched(
                eventId,
                sportEventStatusDTO,
                null,
                "UFOddsChange"
            );
    }

    private void processBetStopMessage(UFBetStop message) {
        Preconditions.checkNotNull(message);

        URN relatedEventId = URN.parse(message.getEventId());

        sportEventStatusCache.purgeSportEventStatus(relatedEventId);

        if (isDrawEvent(relatedEventId)) {
            sportEventCache.purgeCacheItem(relatedEventId);
        }
    }

    private void processBetSettlementMessage(UFBetSettlement message) {
        Preconditions.checkNotNull(message);

        URN relatedEventId = URN.parse(message.getEventId());

        if (isDrawEvent(relatedEventId)) {
            sportEventCache.purgeCacheItem(relatedEventId);
        }
    }

    private static boolean isDrawEvent(URN eventId) {
        Preconditions.checkNotNull(eventId);

        return eventId.getType() != null && eventId.getType().equals("draw");
    }
}
