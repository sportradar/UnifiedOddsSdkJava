/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.internal.impl.FeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.CacheMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.NoOpProcessedFixtureChangesTracker;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.ProcessedFixtureChangesTracker;

/**
 * A derived injection module managing SDK message processing pipeline
 */
public class MessageProcessorPipeline extends AbstractModule {

    @Override
    protected void configure() {
        bind(ProcessedFixtureChangesTracker.class)
            .to(NoOpProcessedFixtureChangesTracker.class)
            .in(Singleton.class);
    }

    @Provides
    @Named("CacheMessageProcessor")
    protected FeedMessageProcessor providesCacheMessageProcessor(
        SportEventStatusCache sportEventStatusCache,
        SportEventCache sportEventCache,
        ProcessedFixtureChangesTracker processedFixtureChangesTracker,
        SdkProducerManager producerManager
    ) {
        return new CacheMessageProcessor(
            sportEventStatusCache,
            sportEventCache,
            processedFixtureChangesTracker,
            producerManager
        );
    }

    @Provides
    protected CompositeMessageProcessor providesCompositeMessageProcessor(
        @Named("CacheMessageProcessor") FeedMessageProcessor cacheMessageProcessor
    ) {
        return new CompositeMessageProcessor(Lists.newArrayList(cacheMessageProcessor));
    }
}
