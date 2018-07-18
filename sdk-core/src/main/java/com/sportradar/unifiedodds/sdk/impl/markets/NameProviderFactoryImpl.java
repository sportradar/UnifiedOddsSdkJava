/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;

import java.util.Map;

/**
 * Created on 20/06/2017.
 * // TODO @eti: Javadoc
 */
public class NameProviderFactoryImpl implements NameProviderFactory {
    private final MarketDescriptionProvider descriptorProvider;
    private final ProfileCache profileCache;
    private final NameExpressionFactory expressionFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    public NameProviderFactoryImpl(MarketDescriptionProvider descriptorProvider, ProfileCache profileCache, NameExpressionFactory expressionFactory, SDKInternalConfiguration cfg) {
        Preconditions.checkNotNull(descriptorProvider);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(expressionFactory);

        this.descriptorProvider = descriptorProvider;
        this.profileCache = profileCache;
        this.expressionFactory = expressionFactory;
        this.exceptionHandlingStrategy = cfg.getExceptionHandlingStrategy();
    }

    @Override
    public NameProvider buildNameProvider(SportEvent sportEvent, int marketId, Map<String, String> specifiers, int producerId) {
        return new NameProviderImpl(descriptorProvider, profileCache, expressionFactory, sportEvent, marketId, specifiers, producerId, exceptionHandlingStrategy);
    }
}
