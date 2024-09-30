/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.*;
import com.sportradar.unifiedodds.sdk.impl.ManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.Urn;
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
        "classDataAbstractionCoupling",
    }
)
public class CompetitorImpl implements Competitor {

    private static final Logger logger = LoggerFactory.getLogger(CompetitorImpl.class);
    private final Urn competitorId;
    private final ProfileCache profileCache;
    private final List<Locale> locales;
    private final SportEntityFactory sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private ReferenceIdCi referenceIdCi;
    private final SportEventCi sportEventCi;
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
     * @param locales a {@link List} in which is provided the {@link CompetitorCi}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     */
    public CompetitorImpl(
        Urn competitorId,
        ProfileCache profileCache,
        Map<Urn, ReferenceIdCi> eventCompetitorsReferences,
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
        referenceIdCi = null;
        sportEventCi = null;
        TeamQualifier = null;
        IsVirtual = isVirtual;
        if (eventCompetitorsReferences != null && !eventCompetitorsReferences.isEmpty()) {
            ReferenceIdCi q = eventCompetitorsReferences.get(competitorId);
            if (q != null) {
                referenceIdCi = q;
            }
        }
    }

    /**
     * Initializes a new instance of the {@link CompetitorImpl} class
     *
     * @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param parentSportEventCi the {@link SportEventCi} this {@link CompetitorCi} belongs to
     * @param locales a {@link List} in which is provided the {@link CompetitorCi}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     * @param isVirtual indication if the competitor is marked as virtual
     */
    public CompetitorImpl(
        Urn competitorId,
        ProfileCache profileCache,
        SportEventCi parentSportEventCi,
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
        referenceIdCi = null;
        sportEventCi = parentSportEventCi;
        TeamQualifier = null;
        IsVirtual = isVirtual;
    }

    /**
     * Returns the unique {@link Urn} identifier representing the current {@link Competitor} instance
     *
     * @return - the unique {@link Urn} identifier representing the current {@link Competitor} instance
     */
    @Override
    public Urn getId() {
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
        try {
            return loadCacheItem().map(c -> c.isVirtual()).orElse(false);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            } else {
                return false;
            }
        }
    }

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    @Override
    public Reference getReferences() {
        FetchEventCompetitorsReferenceIds();
        return referenceIdCi != null ? new ReferenceImpl(referenceIdCi) : null;
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        return loadCacheItem().map(CompetitorCi::getCountryCode).orElse(null);
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
        List<Urn> singleton = Collections.singletonList(competitorId);

        try {
            Optional<CompetitorCi> competitorCi = loadCacheItem();
            Map<Urn, Integer> associatedJerseyNumbers = competitorCi
                .map(c -> c.getAssociatedPlayerJerseyNumbers(locales))
                .orElse(null);

            return competitorCi
                .map(ci -> ci.getAssociatedPlayerIds(locales))
                .map(pIds ->
                    pIds
                        .stream()
                        .map(id -> {
                            try {
                                if (id.getType().equals(UnifiedFeedConstants.PLAYER_URN_TYPE)) {
                                    return sportEntityFactory.buildCompetitorPlayerProfile(
                                        id,
                                        locales,
                                        singleton,
                                        associatedJerseyNumbers
                                    );
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
            .map(CompetitorCi::getJerseys)
            .map(j ->
                j.stream().map(jerseyCi -> (Jersey) new JerseyImpl(jerseyCi)).collect(Collectors.toList())
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
        return loadCacheItem().map(CompetitorCi::getGender).orElse(null);
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
        return loadCacheItem().map(CompetitorCi::getAgeGroup).orElse(null);
    }

    @Override
    public String getState() {
        return loadCacheItem().map(CompetitorCi::getState).orElse(null);
    }

    /**
     * Returns associated sport
     *
     * @return sport if available; otherwise null
     */
    @Override
    public Sport getSport() {
        return loadCacheItem()
            .map(CompetitorCi::getSportId)
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
            .map(CompetitorCi::getCategoryId)
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
        return loadCacheItem().map(CompetitorCi::getShortName).orElse(null);
    }

    @Override
    public Division getDivision() {
        return loadCacheItem()
            .map(competitorCi ->
                competitorCi.getDivision() != null ? new DivisionImpl(competitorCi.getDivision()) : null
            )
            .orElse(null);
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private Optional<CompetitorCi> loadCacheItem() {
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
            Map<Urn, ReferenceIdCi> competitorsReferences = null;
            if (sportEventCi != null && sportEventCi instanceof CompetitionCi) {
                CompetitionCi competitionCi = (CompetitionCi) sportEventCi;
                competitorsReferences = competitionCi.getCompetitorsReferences();
            } else if (sportEventCi != null && sportEventCi instanceof TournamentCi) {
                TournamentCi tournamentCi = (TournamentCi) sportEventCi;
                competitorsReferences = tournamentCi.getCompetitorsReferences();
            } else {
                ReferenceIdCi referenceIdCi = loadCacheItem().map(CompetitorCi::getReferenceId).orElse(null);
                if (referenceIdCi != null) competitorsReferences =
                    ImmutableMap.of(competitorId, referenceIdCi);
            }

            if (competitorsReferences != null && !competitorsReferences.isEmpty()) {
                ReferenceIdCi q = competitorsReferences.get(competitorId);
                if (q != null) {
                    referenceIdCi = q;
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
            if (TeamQualifier == null && sportEventCi != null && sportEventCi instanceof MatchCi) {
                MatchCi matchCi = (MatchCi) sportEventCi;
                Map<Urn, String> competitorsQualifiers = matchCi.getCompetitorsQualifiers();

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
            if (TeamDivision == null && sportEventCi != null && sportEventCi instanceof MatchCi) {
                MatchCi matchCi = (MatchCi) sportEventCi;
                Map<Urn, Integer> competitorsDivisions = matchCi.getCompetitorsDivisions();

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
}
