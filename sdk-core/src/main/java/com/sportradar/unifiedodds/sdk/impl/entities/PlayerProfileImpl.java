/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.entities.PlayerProfile;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Represents player's profile information
 */
public class PlayerProfileImpl implements PlayerProfile {
    private static final Logger logger = LoggerFactory.getLogger(PlayerProfileImpl.class);
    private final URN playerId;
    private final ProfileCache profileCache;
    private final List<URN> possibleAssociatedCompetitorIds;
    private final List<Locale> locales;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new instance of {@link PlayerProfileImpl}
     *
     * @param playerId the associated player identifier
     * @param profileCache the cache used to provide the data
     * @param possibleAssociatedCompetitorIds a list of possible associated competitor ids (used to prefetch data)
     * @param locales the {@link Locale}s in which the data should be available
     * @param exceptionHandlingStrategy the preferred exception handling strategy
     */
    public PlayerProfileImpl(URN playerId, ProfileCache profileCache, List<URN> possibleAssociatedCompetitorIds, List<Locale> locales, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkArgument(!locales.isEmpty());

        this.playerId = playerId;
        this.profileCache = profileCache;
        this.possibleAssociatedCompetitorIds = possibleAssociatedCompetitorIds;
        this.locales = locales;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns the unique {@link URN} identifier representing the current {@link PlayerProfile} instance
     *
     * @return - the unique {@link URN} identifier representing the current {@link PlayerProfile} instance
     */
    @Override
    public URN getId() {
        return playerId;
    }

    /**
     * Returns an unmodifiable map of available translated names
     *
     * @return - an unmodifiable map of available translated names
     */
    @Override
    public Map<Locale, String> getNames() {
        return loadCacheItem().map(ci -> ci.getNames(locales)).map(Collections::unmodifiableMap)
                .orElse(null);
    }

    /**
     * Returns the name of the player in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the player in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return loadCacheItem().map(ci -> ci.getNames(locales).get(locale))
                .orElse(null);
    }

    /**
     * Returns the player full name in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned player name
     * @return - The player full name in the specified language if it exists. Null otherwise.
     */
    @Override
    public String getFullName(Locale locale) {
        return loadCacheItem().map(ci -> ci.getFullNames(locales).get(locale))
                .orElse(null);
    }

    /**
     * Returns the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     *
     * @return - the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     */
    @Override
    public String getType() {
        return loadCacheItem().map(PlayerProfileCI::getType).orElse(null);
    }

    /**
     * Returns the {@link Date} specifying the date of birth of the player associated with the current instance
     *
     * @return - the {@link Date} specifying the date of birth of the player associated with the current instance
     */
    @Override
    public Date getDateOfBirth() {
        return loadCacheItem().map(PlayerProfileCI::getDateOfBirth).orElse(null);
    }

    /**
     * Returns the height in centimeters of the player represented by the current instance or a null reference if height is not known
     *
     * @return - the height in centimeters of the player represented by the current instance or a null reference if height is not known
     */
    @Override
    public Integer getHeight() {
        return loadCacheItem().map(PlayerProfileCI::getHeight).orElse(null);
    }

    /**
     * Returns the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     *
     * @return - the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     */
    @Override
    public Integer getWeight() {
        return loadCacheItem().map(PlayerProfileCI::getWeight).orElse(null);
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        return loadCacheItem().map(PlayerProfileCI::getCountryCode).orElse(null);
    }

    /**
     * Returns the nationality of the player in the requested {@link Locale}
     *
     * @param locale - the {@link Locale} in which to return the nationality
     * @return - the nationality of the player in the requested {@link Locale}
     */
    @Override
    public String getNationality(Locale locale) {
        return loadCacheItem().map(ci -> ci.getNationalities(locales).get(locale))
                .orElse(null);
    }

    /**
     * Returns an unmodifiable {@link Map} containing player's nationality in different languages
     * @see com.google.common.collect.ImmutableMap
     *
     * @return - an unmodifiable {@link Map} containing player's nationality in different languages
     */
    @Override
    public Map<Locale, String> getNationalities() {
        return loadCacheItem().map(ci -> ci.getNationalities(locales)).orElse(null);
    }

    /**
     * Returns the player jersey number
     *
     * @return the jersey number if available; otherwise null
     */
    @Override
    public Integer getJerseyNumber() {
        return loadCacheItem().map(PlayerProfileCI::getJerseyNumber).orElse(null);
    }

    /**
     * Returns the player nickname
     *
     * @return the player nickname if available; otherwise null
     */
    @Override
    public String getNickname() {
        return loadCacheItem().map(PlayerProfileCI::getNickname).orElse(null);
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private Optional<PlayerProfileCI> loadCacheItem() {
        try {
            return Optional.ofNullable(profileCache.getPlayerProfile(playerId, locales, possibleAssociatedCompetitorIds));
        } catch (IllegalCacheStateException | CacheItemNotFoundException e) {
            handleException("loadCacheItem", e);
            return Optional.empty();
        }
    }

    /**
     * Method used to throw or return null value based on the SDK configuration
     *
     * @param request the requested object method
     * @param e the actual exception
     */
    private void  handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException("PlayerProfileImpl[" + playerId + "], request(" + request + ")");
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing PlayerProfileImpl[{}] request({})", playerId, request);
            } else {
                logger.warn("Error providing PlayerProfileImpl[{}] request({}), ex:", playerId, request, e);
            }
        }
    }

    /**
     * Returns a {@link String} describing the current {@link PlayerProfile} instance
     *
     * @return - a {@link String} describing the current {@link PlayerProfile} instance
     */
    @Override
    public String toString() {
        return "PlayerProfileImpl{" +
                "playerId=" + playerId +
                ", locales=" + locales +
                '}';
    }
}
