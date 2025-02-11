/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.internal.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;

/**
 * The default implementation of the {@link NamedValuesProvider}
 */
public class NamedValuesProviderImpl implements NamedValuesProvider {

    /**
     * An implementation of {@link NamedValueCache} providing void reason descriptions
     */
    private final NamedValueCache voidReasons;

    /**
     * An implementation of {@link NamedValueCache} providing bet stop reason descriptions
     */
    private final NamedValueCache betStopReasons;

    /**
     * An implementation of {@link NamedValueCache} providing betting status descriptions
     */
    private final NamedValueCache bettingStatuses;

    /**
     * An implementation of {@link LocalizedNamedValueCache} providing match status descriptions
     */
    private final LocalizedNamedValueCache matchStatuses;

    /**
     * Initializes a new instance of {@link NamedValuesProviderImpl}
     *
     * @param voidReasons - The {@link NamedValueCache} providing void reason descriptions
     * @param betStopReasons - The {@link NamedValueCache} providing bet stop reason descriptions
     * @param bettingStatuses - The {@link NamedValueCache} providing betting status descriptions
     * @param matchStatuses - The {@link LocalizedNamedValueCache} providing match status descriptions
     */
    @Inject
    public NamedValuesProviderImpl(
        @Named("VoidReasonsCache") NamedValueCache voidReasons,
        @Named("BetStopReasonCache") NamedValueCache betStopReasons,
        @Named("BettingStatusCache") NamedValueCache bettingStatuses,
        @Named("MatchStatusCache") LocalizedNamedValueCache matchStatuses
    ) {
        Preconditions.checkNotNull(voidReasons);
        Preconditions.checkNotNull(betStopReasons);
        Preconditions.checkNotNull(bettingStatuses);
        Preconditions.checkNotNull(matchStatuses);

        this.voidReasons = voidReasons;
        this.betStopReasons = betStopReasons;
        this.bettingStatuses = bettingStatuses;
        this.matchStatuses = matchStatuses;
    }

    /**
     * Returns the {@link NamedValueCache} providing void reason descriptions
     *
     * @return - the {@link NamedValueCache} providing void reason descriptions
     */
    @Override
    public NamedValueCache getVoidReasons() {
        return voidReasons;
    }

    /**
     * Returns the {@link NamedValueCache} providing bet stop reason descriptions
     *
     * @return - the {@link NamedValueCache} providing bet stop reason descriptions
     */
    @Override
    public NamedValueCache getBetStopReasons() {
        return betStopReasons;
    }

    /**
     * Returns the {@link NamedValueCache} providing betting status descriptions
     *
     * @return - the {@link NamedValueCache} providing betting status descriptions
     */
    @Override
    public NamedValueCache getBettingStatuses() {
        return bettingStatuses;
    }

    /**
     * Returns the {@link LocalizedNamedValueCache} providing match status descriptions
     *
     * @return - the {@link LocalizedNamedValueCache} providing match status descriptions
     */
    @Override
    public LocalizedNamedValueCache getMatchStatuses() {
        return matchStatuses;
    }
}
