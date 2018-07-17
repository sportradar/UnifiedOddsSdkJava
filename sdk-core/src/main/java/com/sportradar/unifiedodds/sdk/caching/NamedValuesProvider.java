/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

/**
 * Defines the possible provide methods used to access various {@link NamedValueCache} implementations
 * (e.g. bet stop reasons, void reasons, ...)
 */
public interface NamedValuesProvider {
    /**
     * Returns the {@link NamedValueCache} providing void reason descriptions
     *
     * @return - the {@link NamedValueCache} providing void reason descriptions
     */
    NamedValueCache getVoidReasons();

    /**
     * Returns the {@link NamedValueCache} providing bet stop reason descriptions
     *
     * @return - the {@link NamedValueCache} providing bet stop reason descriptions
     */
    NamedValueCache getBetStopReasons();

    /**
     * Returns the {@link NamedValueCache} providing betting status descriptions
     *
     * @return - the {@link NamedValueCache} providing betting status descriptions
     */
    NamedValueCache getBettingStatuses();

    /**
     * Returns the {@link LocalizedNamedValueCache} providing match status descriptions
     *
     * @return - the {@link LocalizedNamedValueCache} providing match status descriptions
     */
    LocalizedNamedValueCache getMatchStatuses();
}
