/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.*;
import com.sportradar.unifiedodds.sdk.impl.ManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.URN;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a player or a team competing in a sport event
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "ClassFanOutComplexity",
        "ConstantName",
        "DeclarationOrder",
        "HiddenField",
        "LambdaBodyLength",
        "MemberName",
        "MethodNameRegular",
        "NeedBraces",
        "ParameterNumber",
        "VisibilityModifier",
    }
)
public class CompetitorImpl implements Competitor {

    private static final Logger logger = LoggerFactory.getLogger(CompetitorImpl.class);
    private final URN competitorId;
    private final ProfileCache profileCache;
    private final List<Locale> locales;
    private final SportEntityFactory sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private ReferenceIdCI referenceIdCI;
    private final SportEventCI sportEventCI;
    protected String TeamQualifier;
    protected Integer TeamDivision;
    protected Boolean IsVirtual;

    /**
     * A {@link ReentrantLock} used to synchronize api request operations
     */
    private final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * Initializes a new instance of the {@link CompetitorImpl} class
     *  @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param eventCompetitorsReferences the list of competitors and associated references
     * @param isVirtual indication if the competitor is marked as virtual
     * @param locales a {@link List} in which is provided the {@link CompetitorCI}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     */
    public CompetitorImpl(
        URN competitorId,
        ProfileCache profileCache,
        Map<URN, ReferenceIdCI> eventCompetitorsReferences,
        Boolean isVirtual,
        List<Locale> locales,
        SportEntityFactory sportEntityFactory,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.competitorId = competitorId;
        this.profileCache = profileCache;
        this.locales = locales;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        referenceIdCI = null;
        sportEventCI = null;
        TeamQualifier = null;
        IsVirtual = isVirtual;
        if (eventCompetitorsReferences != null && !eventCompetitorsReferences.isEmpty()) {
            ReferenceIdCI q = eventCompetitorsReferences.get(competitorId);
            if (q != null) {
                referenceIdCI = q;
            }
        }
    }

    /**
     * Initializes a new instance of the {@link CompetitorImpl} class
     *
     * @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param parentSportEventCI the {@link SportEventCI} this {@link CompetitorCI} belongs to
     * @param locales a {@link List} in which is provided the {@link CompetitorCI}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     * @param isVirtual indication if the competitor is marked as virtual
     */
    public CompetitorImpl(
        URN competitorId,
        ProfileCache profileCache,
        SportEventCI parentSportEventCI,
        List<Locale> locales,
        SportEntityFactory sportEntityFactory,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        Boolean isVirtual
    ) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.competitorId = competitorId;
        this.profileCache = profileCache;
        this.locales = locales;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        referenceIdCI = null;
        sportEventCI = parentSportEventCI;
        TeamQualifier = null;
        IsVirtual = isVirtual;
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
        return loadCacheItem().map(ci -> ci.getNames(locales)).map(Collections::unmodifiableMap).orElse(null);
    }

    /**
     * Returns the name of the player in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the player in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return loadCacheItem().map(ci -> ci.getNames(locales).get(locale)).orElse(null);
    }

    /**
     * Returns an unmodifiable map of available translated competitor country names
     *
     * @return an unmodifiable map of available translated competitor country names
     * @see com.google.common.collect.ImmutableMap
     */
    @Override
    public Map<Locale, String> getCountries() {
        return loadCacheItem()
            .map(ci -> ci.getCountryNames(locales))
            .map(Collections::unmodifiableMap)
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
        return loadCacheItem()
            .map(ci -> ci.getAbbreviations(locales))
            .map(Collections::unmodifiableMap)
            .orElse(null);
    }

    /**
     * Returns a value indicating whether the current instance represents a placeholder team
     *
     * @return - a value indicating whether the current instance represents a placeholder team
     */
    @Override
    public boolean isVirtual() {
        FetchEventCompetitorsVirtual();
        return IsVirtual != null && IsVirtual.booleanValue();
    }

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    @Override
    public Reference getReferences() {
        FetchEventCompetitorsReferenceIds();
        return referenceIdCI != null ? new ReferenceImpl(referenceIdCI) : null;
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        return loadCacheItem().map(CompetitorCI::getCountryCode).orElse(null);
    }

    /**
     * Returns the translated competitor country name
     *
     * @param locale - a {@link Locale} specifying the language in which to get the country name
     * @return - the translated competitor country name
     */
    @Override
    public String getCountry(Locale locale) {
        return loadCacheItem().map(ci -> ci.getCountryNames(locales).get(locale)).orElse(null);
    }

    /**
     * Returns the translated competitor abbreviation
     *
     * @param locale - a {@link Locale} specifying the language in which to get the abbreviation
     * @return - the translated competitor abbreviation
     */
    @Override
    public String getAbbreviation(Locale locale) {
        return loadCacheItem().map(ci -> ci.getAbbreviations(locales).get(locale)).orElse(null);
    }

    /**
     * Returns a {@link List} of associated players
     *
     * @return {@link List} of associated players
     */
    @Override
    public List<Player> getPlayers() {
        List<URN> singleton = Collections.singletonList(competitorId);

        try {
            return loadCacheItem()
                .map(ci -> ci.getAssociatedPlayerIds(locales))
                .map(pIds ->
                    pIds
                        .stream()
                        .map(id -> {
                            try {
                                if (id.getType().equals(UnifiedFeedConstants.PLAYER_URN_TYPE)) {
                                    return sportEntityFactory.buildPlayerProfile(id, locales, singleton);
                                } else {
                                    return sportEntityFactory.buildCompetitor(
                                        id,
                                        null,
                                        null,
                                        null,
                                        null,
                                        locales
                                    );
                                }
                            } catch (ObjectNotFoundException e) {
                                throw new StreamWrapperException(e.getMessage(), e);
                            }
                        })
                        .collect(Collectors.toList())
                )
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
        return loadCacheItem()
            .map(CompetitorCI::getJerseys)
            .map(j ->
                j.stream().map(jerseyCI -> (Jersey) new JerseyImpl(jerseyCI)).collect(Collectors.toList())
            )
            .orElse(null);
    }

    /**
     * Returns the associated competitor manager
     *
     * @return the associated competitor manager
     */
    @Override
    public Manager getManager() {
        return loadCacheItem().map(ci -> ci.getManager(locales)).map(ManagerImpl::new).orElse(null);
    }

    /**
     * Return the associated competitor home venue
     *
     * @return the associated competitor home venue
     */
    @Override
    public Venue getVenue() {
        return loadCacheItem()
            .map(ci -> ci.getVenue(locales))
            .map(v -> new VenueImpl(v, locales))
            .orElse(null);
    }

    /**
     * Returns gender of the competitor
     *
     * @return the gender of the competitor if available; otherwise null
     */
    @Override
    public String getGender() {
        return loadCacheItem().map(CompetitorCI::getGender).orElse(null);
    }

    /**
     * Returns race driver of the competitor
     *
     * @return the race driver of the competitor if available; otherwise null
     */
    @Override
    public RaceDriverProfile getRaceDriver() {
        return loadCacheItem()
            .map(ci -> ci.getRaceDriver() != null ? new RaceDriverProfileImpl(ci.getRaceDriver()) : null)
            .orElse(null);
    }

    /**
     * Returns age group of the competitor
     *
     * @return the age group of the competitor if available; otherwise null
     */
    @Override
    public String getAgeGroup() {
        return loadCacheItem().map(CompetitorCI::getAgeGroup).orElse(null);
    }

    @Override
    public String getState() {
        return loadCacheItem().map(CompetitorCI::getState).orElse(null);
    }

    /**
     * Returns associated sport
     *
     * @return sport if available; otherwise null
     */
    @Override
    public Sport getSport() {
        return loadCacheItem()
            .map(CompetitorCI::getSportId)
            .map(s -> {
                try {
                    return sportEntityFactory.buildSport(s, locales);
                } catch (ObjectNotFoundException e) {
                    throw new StreamWrapperException(e.getMessage(), e);
                }
            })
            .orElse(null);
    }

    /**
     * Returns associated category
     *
     * @return category if available; otherwise null
     */
    @Override
    public CategorySummary getCategory() {
        return loadCacheItem()
            .map(CompetitorCI::getCategoryId)
            .map(c -> {
                try {
                    return sportEntityFactory.buildCategory(c, locales);
                } catch (ObjectNotFoundException e) {
                    throw new StreamWrapperException(e.getMessage(), e);
                }
            })
            .orElse(null);
    }

    /**
     * Returns the short name of the competitor
     *
     * @return the dhort name of the competitor if available; otherwise null
     */
    @Override
    public String getShortName() {
        return loadCacheItem().map(CompetitorCI::getShortName).orElse(null);
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
    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(
                    "CompetitorImpl[" + competitorId + "], request(" + request + ")"
                );
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
        return "CompetitorImpl{" + "competitorId=" + competitorId + ", locales=" + locales + '}';
    }

    protected void FetchEventCompetitorsReferenceIds() {
        reentrantLock.lock();
        try {
            Map<URN, ReferenceIdCI> competitorsReferences = null;
            if (sportEventCI != null && sportEventCI instanceof CompetitionCI) {
                CompetitionCI competitionCI = (CompetitionCI) sportEventCI;
                competitorsReferences = competitionCI.getCompetitorsReferences();
            } else if (sportEventCI != null && sportEventCI instanceof TournamentCI) {
                TournamentCI tournamentCI = (TournamentCI) sportEventCI;
                competitorsReferences = tournamentCI.getCompetitorsReferences();
            } else {
                ReferenceIdCI referenceIdCI = loadCacheItem().map(CompetitorCI::getReferenceId).orElse(null);
                if (referenceIdCI != null) competitorsReferences =
                    ImmutableMap.of(competitorId, referenceIdCI);
            }

            if (competitorsReferences != null && !competitorsReferences.isEmpty()) {
                ReferenceIdCI q = competitorsReferences.get(competitorId);
                if (q != null) {
                    referenceIdCI = q;
                }
            }
        } catch (DataRouterStreamException e) {
            handleException(String.format("getCompetitorsReferences(%s)", competitorId), e);
        } finally {
            reentrantLock.unlock();
        }
    }

    protected void FetchEventCompetitorsQualifiers() {
        reentrantLock.lock();
        try {
            if (TeamQualifier == null && sportEventCI != null && sportEventCI instanceof MatchCI) {
                MatchCI matchCI = (MatchCI) sportEventCI;
                Map<URN, String> competitorsQualifiers = matchCI.getCompetitorsQualifiers();

                if (competitorsQualifiers != null && !competitorsQualifiers.isEmpty()) {
                    TeamQualifier = competitorsQualifiers.get(competitorId);
                }
            }
        } catch (DataRouterStreamException e) {
            handleException(String.format("getCompetitorsQualifiers(%s)", competitorId), e);
        } finally {
            reentrantLock.unlock();
        }
    }

    protected void FetchEventCompetitorsDivisions() {
        reentrantLock.lock();
        try {
            if (TeamDivision == null && sportEventCI != null && sportEventCI instanceof MatchCI) {
                MatchCI matchCI = (MatchCI) sportEventCI;
                Map<URN, Integer> competitorsDivisions = matchCI.getCompetitorsDivisions();

                if (competitorsDivisions != null && !competitorsDivisions.isEmpty()) {
                    TeamDivision = competitorsDivisions.get(competitorId);
                }
            }
        } catch (DataRouterStreamException e) {
            handleException(String.format("getCompetitorsDivisions(%s)", competitorId), e);
        } finally {
            reentrantLock.unlock();
        }
    }

    protected void FetchEventCompetitorsVirtual() {
        reentrantLock.lock();
        try {
            if (IsVirtual == null && sportEventCI != null && sportEventCI instanceof MatchCI) {
                MatchCI matchCI = (MatchCI) sportEventCI;
                List<URN> competitorsVirtual = matchCI.getCompetitorsVirtual();

                if (competitorsVirtual != null && !competitorsVirtual.isEmpty()) {
                    IsVirtual = competitorsVirtual.contains(competitorId);
                }
            }
        } catch (DataRouterStreamException e) {
            handleException(String.format("getCompetitorsVirtual(%s)", competitorId), e);
        } finally {
            reentrantLock.unlock();
        }
    }
}
