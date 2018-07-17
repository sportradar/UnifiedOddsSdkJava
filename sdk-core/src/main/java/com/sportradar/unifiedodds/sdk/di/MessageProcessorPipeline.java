/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CacheMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CompositeMessageProcessor;

/**
 * A derived injection module managing SDK message processing pipeline
 */
public class MessageProcessorPipeline extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides @Named("CacheMessageProcessor")
    private FeedMessageProcessor providesCacheMessageProcessor(SportEventStatusCache sportEventStatusCache, SportEventCache sportEventCache) {
        return new CacheMessageProcessor(sportEventStatusCache, sportEventCache);
    }

    @Provides
    private CompositeMessageProcessor providesCompositeMessageProcessor(@Named("CacheMessageProcessor") FeedMessageProcessor cacheMessageProcessor) {
        return new CompositeMessageProcessor(Lists.newArrayList(
                cacheMessageProcessor
        ));
    }
}
