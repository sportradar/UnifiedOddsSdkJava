/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.impl.ManagerImpl;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a player or a team competing in a sport event
 */
public class CompetitorImpl implements Competitor {
    private final static Logger logger = LoggerFactory.getLogger(CompetitorImpl.class);
    private final URN competitorId;
    private final ProfileCache profileCache;
    private final List<Locale> locales;
    private final SportEntityFactory sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;


    /**
     * Initializes a new instance of the {@link CompetitorImpl} class
     *
     * @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param locales a {@link List} in which is provided the {@link CompetitorCI}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     */
    public CompetitorImpl(URN competitorId, ProfileCache profileCache, List<Locale> locales, SportEntityFactory sportEntityFactory, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.competitorId = competitorId;
        this.profileCache = profileCache;
        this.locales = locales;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }


    /**
     * Returns the unique {@link URN} identifier representing the current {@link Competitor} instance
     *
     * @return - the unique {@link URN} identifier representing the current {@link Competitor} instance
     */
    @Override
    public URN getId() {
        return competitorId;
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
     * Returns an unmodifiable map of available translated competitor country names
     *
     * @return an unmodifiable map of available translated competitor country names
     * @see com.google.common.collect.ImmutableMap
     */
    @Override
    public Map<Locale, String> getCountries() {
        return loadCacheItem().map(ci -> ci.getCountryNames(locales)).map(Collections::unmodifiableMap)
                .orElse(null);
    }

    /**
     * Returns an unmodifiable map of available translated competitor abbreviations
     *
     * @return an unmodifiable map of available translated competitor abbreviations
     * @see com.google.common.collect.ImmutableMap
     */
    @Override
    public Map<Locale, String> getAbbreviations() {
        return loadCacheItem().map(ci -> ci.getAbbreviations(locales)).map(Collections::unmodifiableMap)
                .orElse(null);
    }

    /**
     * Returns a value indicating whether the current instance represents a virtual {@link Competitor}
     * i.e. competes in a virtual sports
     *
     * @return - a value indicating whether the current instance represents a virtual {@link Competitor}
     */
    @Override
    public boolean isVirtual() {
        return loadCacheItem().map(CompetitorCI::isVirtual).orElse(false);
    }

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    @Override
    public Reference getReferences() {
        return loadCacheItem().map(CompetitorCI::getReferenceId).map(ReferenceImpl::new)
                .orElse(null);
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        return loadCacheItem().map(CompetitorCI::getCountryCode)
                .orElse(null);
    }

    /**
     * Returns the translated competitor country name
     *
     * @param locale - a {@link Locale} specifying the language in which to get the country name
     * @return - the translated competitor country name
     */
    @Override
    public String getCountry(Locale locale) {
        return loadCacheItem().map(ci -> ci.getCountryNames(locales).get(locale))
                .orElse(null);
    }

    /**
     * Returns the translated competitor abbreviation
     *
     * @param locale - a {@link Locale} specifying the language in which to get the abbreviation
     * @return - the translated competitor abbreviation
     */
    @Override
    public String getAbbreviation(Locale locale) {
        return loadCacheItem().map(ci -> ci.getAbbreviations(locales).get(locale))
                .orElse(null);
    }

    /**
     * Returns a {@link List} of associated players
     *
     * @return {@link List} of associated players
     */
    @Override
    public List<PlayerProfile> getPlayers() {
        List<URN> singleton = Collections.singletonList(competitorId);

        try {
            return loadCacheItem()
                    .map(ci -> ci.getAssociatedPlayerIds(locales))
                    .map(pIds -> pIds.stream()
                        .map(id -> {
                            try {
                                return sportEntityFactory.buildPlayerProfile(id, locales, singleton);
                            } catch (ObjectNotFoundException e) {
                                throw new StreamWrapperException(e.getMessage(), e);
                            }
                        })
                    .collect(Collectors.toList()))
                    .orElse(null);
        } catch (StreamWrapperException e) {
            handleException("getPlayers()", e);
            return null;
        }
    }

    /**
     * Returns a {@link List} of known competitor jerseys
     *
     * @return {@link List} of known competitor jerseys
     */
    @Override
    public List<Jersey> getJerseys() {
        return loadCacheItem().map(CompetitorCI::getJerseys)
                .map(j -> j.stream()
                        .map(jerseyCI -> (Jersey) new JerseyImpl(jerseyCI))
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    /**
     * Returns the associated competitor manager
     *
     * @return the associated competitor manager
     */
    @Override
    public Manager getManager() {
        return loadCacheItem().map(ci -> ci.getManager(locales))
                .map(ManagerImpl::new)
                .orElse(null);
    }

    /**
     * Return the associated competitor home venue
     *
     * @return the associated competitor home venue
     */
    @Override
    public Venue getVenue() {
        return loadCacheItem().map(ci -> ci.getVenue(locales))
                .map(v -> new VenueImpl(v, locales))
                .orElse(null);
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private Optional<CompetitorCI> loadCacheItem() {
        try {
            return Optional.ofNullable(profileCache.getCompetitorProfile(competitorId, locales));
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
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException("CompetitorImpl[" + competitorId + "], request(" + request + ")");
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing CompetitorImpl[{}] request({})", competitorId, request);
            } else {
                logger.warn("Error providing CompetitorImpl[{}] request({}), ex:", competitorId, request, e);
            }
        }
    }

    /**
     * Returns a {@link String} describing the current {@link Competitor} instance
     *
     * @return - a {@link String} describing the current {@link Competitor} instance
     */
    @Override
    public String toString() {
        return "CompetitorImpl{" +
                "competitorId=" + competitorId +
                ", locales=" + locales +
                '}';
    }
}
