/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.Mockito.*;

import com.sportradar.uf.datamodel.UfFixtureChange;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CacheMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.ProcessedFixtureChangesTracker;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.TestProducersProvider;
import com.sportradar.utils.Urn;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class CacheMessageProcessorTest {

    private final Urn eventId = Urn.parse("sr:match:1234");
    private final MessageTimestamp timestamp = mock(MessageTimestamp.class);
    private final SportEventStatusCache sportEventStatusCache = mock(SportEventStatusCache.class);
    private final SportEventCache sportEventCache = mock(SportEventCache.class);
    private final ProcessedFixtureChangesTracker fixtureChangesTracker = mock(
        ProcessedFixtureChangesTracker.class
    );
    private final SdkProducerManager producerManager = mock(SdkProducerManager.class);
    private CacheMessageProcessor cacheMessageProcessor;
    private final TestProducersProvider producersProvider = new TestProducersProvider();
    private final Map<Integer, Producer> producerMap = new HashMap<>();

    @SuppressWarnings("checkstyle:MagicNumber")
    @BeforeEach
    public void setup() {
        when(producerManager.getAvailableProducers()).thenReturn(producerMap);
        setupCacheMessageProcessor();
    }

    private void setupCacheMessageProcessor() {
        cacheMessageProcessor =
            new CacheMessageProcessor(
                sportEventStatusCache,
                sportEventCache,
                fixtureChangesTracker,
                producerManager
            );
    }

    @Test
    public void cacheMessageProcessorIsSetup() {
        Assert.assertNotNull(cacheMessageProcessor);
    }

    @Test
    public void processFixtureChangeMessageForNormalProducer() {
        producerMap.put(1, new ProducerImpl(producersProvider.getProducer(1, true)));

        final String routingKeyStr = "hi.pre.live.fixture_change.40.sr:match.1234.-";
        final RoutingKeyInfo routingKey = new RoutingKeyInfo(
            routingKeyStr,
            Urn.parse("sr:sport:40"),
            eventId
        );
        Assert.assertNotNull(routingKey);
        UfFixtureChange fixtureChange = new UfFixtureChange();
        fixtureChange.setEventId(routingKey.getEventId().toString());
        fixtureChange.setProduct(1);
        Assert.assertEquals(eventId.toString(), fixtureChange.getEventId());
        cacheMessageProcessor.processMessage(fixtureChange, new byte[0], routingKey, timestamp);

        verify(sportEventCache, times(1)).purgeCacheItem(eventId);
        verify(sportEventCache, times(1)).addFixtureTimestamp(eventId);
        verify(sportEventStatusCache, times(1)).purgeSportEventStatus(eventId);
    }

    @Test
    public void processFixtureChangeMessageForVirtualProducer() {
        final int virtualProducerId = 8;
        producerMap.put(
            virtualProducerId,
            new ProducerImpl(producersProvider.getProducer(virtualProducerId, true))
        );
        final String routingKeyStr = "hi.pre.live.fixture_change.40.sr:match.1234.-";
        final RoutingKeyInfo routingKey = new RoutingKeyInfo(
            routingKeyStr,
            Urn.parse("sr:sport:40"),
            eventId
        );
        Assert.assertNotNull(routingKey);
        UfFixtureChange fixtureChange = new UfFixtureChange();
        fixtureChange.setEventId(routingKey.getEventId().toString());
        fixtureChange.setProduct(virtualProducerId);
        Assert.assertEquals(eventId.toString(), fixtureChange.getEventId());
        setupCacheMessageProcessor();
        cacheMessageProcessor.processMessage(fixtureChange, new byte[0], routingKey, timestamp);

        verify(sportEventCache, times(1)).purgeCacheItem(eventId);
        verify(sportEventCache, times(0)).addFixtureTimestamp(eventId);
        verify(sportEventStatusCache, times(1)).purgeSportEventStatus(eventId);
    }
}
