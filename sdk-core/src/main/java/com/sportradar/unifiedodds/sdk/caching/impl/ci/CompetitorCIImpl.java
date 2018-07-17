/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPITeam;
import com.sportradar.uf.sportsapi.datamodel.SAPITeamCompetitor;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.ci.JerseyCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ManagerCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.LanguageHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link CompetitorCI}
 */
class CompetitorCIImpl implements CompetitorCI {
    private static final Logger logger = LoggerFactory.getLogger(CompetitorCIImpl.class);
    /**
     * An {@link URN} specifying the id of the associated sport event
     */
    private final URN id;

    /**
     * A {@link Locale} specifying the default language
     */
    private final Locale defaultLocale;

    /**
     * The {@link DataRouterManager} which is used to trigger data fetches
     */
    private final DataRouterManager dataRouterManager;

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * A {@link Map} containing competitor names in different languages
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing competitor's country name in different languages
     */
    private final Map<Locale, String> countryNames = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing competitor abbreviations in different languages
     */
    private final Map<Locale, String> abbreviations = Maps.newConcurrentMap();

    /**
     * A value indicating whether represented competitor is virtual
     */
    private boolean isVirtual;

    /**
     * A {@link String} representation of a country code
     */
    private String countryCode;

    /**
     * The reference ids associated with the current instance
     */
    private ReferenceIdCI referenceId;

    /**
     * A {@link List} of associated player {@link URN}s
     */
    private List<URN> associatedPlayerIds;

    /**
     * A {@link List} of known competitor jerseys
     */
    private List<JerseyCI> jerseys;

    /**
     * The associated competitor manager
     */
    private ManagerCI manager;

    /**
     * The associated competitor home venue
     */
    private VenueCI venue;

    /**
     * The locales which are merged into the CI
     */
    private final List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    private final ReentrantLock fetchLock = new ReentrantLock();


    CompetitorCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.defaultLocale = defaultLocale;
        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    CompetitorCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPICompetitorProfileEndpoint data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        merge(data, dataLocale);
    }

    CompetitorCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITeam data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        merge(data, dataLocale);
    }

    /**
     * Returns the {@link URN} representing id of the related entity
     *
     * @return the {@link URN} representing id of the related entity
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the {@link Map} containing translated names of the competitor
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the competitor
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (names.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the {@link Map} containing translated country names
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated country names
     */
    @Override
    public Map<Locale, String> getCountryNames(List<Locale> locales) {
        if (countryNames.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(countryNames);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(countryNames);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(countryNames);
    }

    /**
     * Returns the {@link Map} containing translated competitor abbreviations
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated competitor abbreviations
     */
    @Override
    public Map<Locale, String> getAbbreviations(List<Locale> locales) {
        if (abbreviations.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(abbreviations);
    }

    /**
     * Returns a value indicating whether represented competitor is virtual
     *
     * @return - a value indicating whether represented competitor is virtual
     */
    @Override
    public boolean isVirtual() {
        ensureDataLoaded(isVirtual);

        return isVirtual;
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        ensureDataLoaded(countryCode);

        return countryCode;
    }

    /**
     * Returns the reference ids associated with the current instance
     *
     * @return - the reference ids associated with the current instance
     */
    @Override
    public ReferenceIdCI getReferenceId() {
        ensureDataLoaded(referenceId);

        return referenceId;
    }

    /**
     * Returns a {@link List} of associated player ids
     *
     * @param locales the locales in which the players data should be pre-fetched
     * @return {@link List} of associated player ids
     */
    @Override
    public List<URN> getAssociatedPlayerIds(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return associatedPlayerIds == null ? null : ImmutableList.copyOf(associatedPlayerIds);
        }

        requestMissingCompetitorData(locales);

        return associatedPlayerIds == null ? null : ImmutableList.copyOf(associatedPlayerIds);
    }

    /**
     * Returns a {@link List} of known competitor jerseys
     *
     * @return {@link List} of known competitor jerseys
     */
    @Override
    public List<JerseyCI> getJerseys() {
        ensureDataLoaded(jerseys);

        return jerseys;
    }

    /**
     * Returns the associated competitor manager
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor manager
     */
    @Override
    public ManagerCI getManager(List<Locale> locales) {
        if (manager != null && manager.hasTranslationsFor(locales)) {
            return manager;
        }

        if (cachedLocales.containsAll(locales)) {
            return manager;
        }

        requestMissingCompetitorData(locales);

        return manager;
    }

    /**
     * Return the associated competitor home venue
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor home venue
     */
    @Override
    public VenueCI getVenue(List<Locale> locales) {
        if (venue != null && venue.hasTranslationsFor(locales)) {
            return venue;
        }

        if (cachedLocales.containsAll(locales)) {
            return venue;
        }

        requestMissingCompetitorData(locales);

        return venue;
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return false;
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPITeamCompetitor) {
            internalMerge((SAPITeamCompetitor) endpointData, dataLocale);
        } else if (endpointData instanceof SAPITeam) {
            internalMerge((SAPITeam) endpointData, dataLocale);
        } else if (endpointData instanceof SAPICompetitorProfileEndpoint) {
            internalMerge((SAPICompetitorProfileEndpoint) endpointData, dataLocale);
            cachedLocales.add(dataLocale);
        }
    }
    private void internalMerge(SAPITeamCompetitor data, Locale dataLocale) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        internalMerge((SAPITeam) data, dataLocale);
    }

    private void internalMerge(SAPICompetitorProfileEndpoint data, Locale dataLocale) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        internalMerge(data.getCompetitor(), dataLocale);

        associatedPlayerIds = Optional.ofNullable(data.getPlayers())
                .map(p -> p.getPlayer().stream().map(pp -> URN.parse(pp.getId())).collect(Collectors.toList()))
                .orElse(null);


        jerseys = Optional.ofNullable(data.getJerseys())
                .map(j -> j.getJersey().stream().map(JerseyCI::new).collect(Collectors.toList()))
                .orElse(null);

        if (data.getManager() != null) {
            if (manager == null) {
                manager = new ManagerCI(data.getManager(), dataLocale);
            } else {
                manager.merge(data.getManager(), dataLocale);
            }
        }

        if (data.getVenue() != null){
            if (venue == null) {
                venue = new VenueCI(data.getVenue(), dataLocale);
            } else {
                venue.merge(data.getVenue(), dataLocale);
            }
        }
    }

    private void internalMerge(SAPITeam competitor, Locale dataLocale) {
        Preconditions.checkNotNull(competitor);
        Preconditions.checkNotNull(dataLocale);

        isVirtual = competitor.isVirtual() == null ? false : competitor.isVirtual();
        countryCode = competitor.getCountryCode();

        Optional.ofNullable(competitor.getName()).ifPresent(s -> names.put(dataLocale, s));
        Optional.ofNullable(competitor.getCountry()).ifPresent(s -> countryNames.put(dataLocale, s));
        Optional.ofNullable(competitor.getAbbreviation()).ifPresent(s -> abbreviations.put(dataLocale, s));

        referenceId = competitor.getReferenceIds() == null ? null :
                new ReferenceIdCI(competitor.getReferenceIds().getReferenceId().stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(HashMap::new, (map, i) -> map.put(i.getName(), i.getValue()), HashMap::putAll));
    }

    private void requestMissingCompetitorData(List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);

        if (id.getType().equals(UnifiedFeedConstants.SIMPLETEAM_URN_TYPE)) {
            handleSimpleTeamLoad();
            return;
        }

        List<Locale> missingLocales = LanguageHelper.findMissingLocales(cachedLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        fetchLock.lock();
        try {
            // recheck missing locales after lock
            missingLocales = LanguageHelper.findMissingLocales(cachedLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug("Fetching competitor data for id='{}' for languages '{}'",
                    id, String.join(", ", missingLocales.stream()
                            .map(Locale::toString).collect(Collectors.toList())));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestCompetitorEndpoint(l, id, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingCompetitorData(%s)", missingLocales), e);
        } finally {
            fetchLock.unlock();
        }
    }

    private void handleSimpleTeamLoad() {
        if (referenceId != null) {
            return;
        }

        referenceId = new ReferenceIdCI(
                ImmutableMap.<String, String>builder()
                        .put("betradar", String.valueOf(id.getId()))
                        .build()
        );
    }

    private void ensureDataLoaded(Object object) {
        if (object != null || !cachedLocales.isEmpty()) {
            return;
        }

        requestMissingCompetitorData(Collections.singletonList(defaultLocale));
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("CompetitorCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing CompetitorCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing CompetitorCI[{}] request({}), ex:", id, request, e);
            }
        }
    }
}
