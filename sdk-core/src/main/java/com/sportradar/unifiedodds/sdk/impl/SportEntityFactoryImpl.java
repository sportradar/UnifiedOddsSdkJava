/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.SportData;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.impl.entities.*;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory used to construct {@link Competition} and {@link Tournament} instances
 */
@SuppressWarnings(
    {
        "BooleanExpressionComplexity",
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "LambdaBodyLength",
        "LineLength",
        "MethodLength",
        "MultipleStringLiterals",
        "ParameterAssignment",
        "ReturnCount",
        "UnnecessaryParentheses",
    }
)
public class SportEntityFactoryImpl implements SportEntityFactory {

    private static final Logger logger = LoggerFactory.getLogger(SportEntityFactoryImpl.class);
    /**
     * A {@link SportsDataCache} instance used to retrieve sport related info
     */
    private final SportsDataCache sportsDataCache;

    /**
     * A {@link SportEventCache} instance used to retrieve sport events
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link ProfileCache} instance used to retrieve player/competitor profiles
     */
    private final ProfileCache profileCache;

    /**
     * A {@link SportEventStatusFactory} instance used to build sport event status entities
     */
    private final SportEventStatusFactory sportEventStatusFactory;

    /**
     * An indication of which exception handling strategy should be used in the instance
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * The configured default locale
     */
    private final Locale defaultLocale;

    /**
     * The utility used to identify the proper entity type
     */
    private final MappingTypeProvider mappingTypeProvider;

    private final List<Urn> soccerSportUrns = new ArrayList<Urn>(
        Arrays.asList(Urn.parse("sr:sport:1"), Urn.parse("sr:sport:137"))
    );

    /**
     * Initializes a new instance of the {@link SportEntityFactoryImpl}
     *
     * @param sportsDataCache a {@link SportsDataCache} instance used to retrieve sport related info
     * @param sportEventCache a {@link SportEventCache} instance used to retrieve sport events
     * @param profileCache a {@link ProfileCache} instance used to retrieve player/competitor profiles
     * @param sportEventStatusFactory a {@link SportEventStatusFactory} instance used to build sport event status entities
     * @param mappingTypeProvider a {@link MappingTypeProvider} instance used to identify proper entity mapping types
     * @param oddsFeedConfiguration the associated feed configuration
     */
    @Inject
    public SportEntityFactoryImpl(
        SportsDataCache sportsDataCache,
        SportEventCache sportEventCache,
        ProfileCache profileCache,
        SportEventStatusFactory sportEventStatusFactory,
        MappingTypeProvider mappingTypeProvider,
        SdkInternalConfiguration oddsFeedConfiguration
    ) {
        Preconditions.checkNotNull(sportsDataCache);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(sportEventStatusFactory);
        Preconditions.checkNotNull(mappingTypeProvider);
        Preconditions.checkNotNull(oddsFeedConfiguration);

        this.sportsDataCache = sportsDataCache;
        this.sportEventCache = sportEventCache;
        this.profileCache = profileCache;
        this.sportEventStatusFactory = sportEventStatusFactory;
        this.mappingTypeProvider = mappingTypeProvider;
        this.exceptionHandlingStrategy = oddsFeedConfiguration.getExceptionHandlingStrategy();
        this.defaultLocale = oddsFeedConfiguration.getDefaultLocale();
    }

    /**
     * Builds a {@link List} of available {@link Sport} instances
     *
     * @param locales - a {@link List} of locales specifying the languages used in the returned instances
     * @return - a {@link List} with the constructed {@link Sport} instances
     * @throws ObjectNotFoundException if the sports list failed to build with the selected {@link Locale}s
     */
    @Override
    public List<Sport> buildSports(List<Locale> locales) throws ObjectNotFoundException {
        Preconditions.checkNotNull(locales);

        try {
            return sportsDataCache
                .getSports(locales)
                .stream()
                .map(sp -> {
                    try {
                        return buildSportInternal(sp, locales);
                    } catch (ObjectNotFoundException e) {
                        throw new StreamWrapperException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
        } catch (StreamWrapperException e) {
            throw new ObjectNotFoundException(e.getMessage(), e);
        } catch (IllegalCacheStateException e) {
            throw new ObjectNotFoundException(
                "The requested sport list could not be built[" + locales + "]",
                e
            );
        }
    }

    /**
     * Builds a {@link Sport} instance with the provided data
     *
     * @param sportId - the {@link Urn} sport identifier
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @return - the constructed {@link Sport} instance
     * @throws ObjectNotFoundException if the requested sport failed to build or was not found
     */
    @Override
    public Sport buildSport(Urn sportId, List<Locale> locales) throws ObjectNotFoundException {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(locales);

        SportData sport;
        try {
            sport = sportsDataCache.getSport(sportId, locales);
        } catch (IllegalCacheStateException | CacheItemNotFoundException e) {
            throw new ObjectNotFoundException("The requested sport could not be built[" + sportId + "]", e);
        }

        return buildSportInternal(sport, locales);
    }

    /**
     * Builds the associated category summary
     *
     * @param id the identifier of the category
     * @param locales locales in which the data is provided
     * @return a {@link CategorySummary} associated with the current instance
     * @throws ObjectNotFoundException if the category CI could not be found
     */
    @Override
    public CategorySummary buildCategory(Urn id, List<Locale> locales) throws ObjectNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);

        CategoryCi categoryCi;
        try {
            categoryCi = sportsDataCache.getCategory(id, locales);
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            throw new ObjectNotFoundException("The requested category could not be built[" + id + "]", e);
        }

        return buildCategoryInternal(categoryCi, locales);
    }

    /**
     * Builds the associated category sport summary
     *
     * @param categoryId the identifier of the category
     * @param locales locales in which the data is provided
     * @return a {@link SportSummary} associated with the current instance
     * @throws ObjectNotFoundException if the category CI could not be found
     */
    @Override
    public SportSummary buildSportForCategory(Urn categoryId, List<Locale> locales)
        throws ObjectNotFoundException {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkNotNull(locales);

        CategoryCi categoryCi;
        try {
            categoryCi = sportsDataCache.getCategory(categoryId, locales);
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            throw new ObjectNotFoundException(
                "Could not provide the sport data - category CI missing[" + categoryId + "]",
                e
            );
        }

        if (categoryCi.getSportId() == null) {
            return null;
        }

        return buildSport(categoryCi.getSportId(), locales);
    }

    /**
     * Builds the {@link Competition} derived class based on the provided data
     *
     * @param id - the {@link Urn} specifying the identifier of the sport event
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @param buildBasicEventImpl - an indication if the basic event entity should be built if the mapping type is unknown
     * @return - the constructed object which is derived from the {@link Competition}
     * @throws ObjectNotFoundException if the requested sport event object could not be provided(failure built, api request errors,..)
     */
    @Override
    public SportEvent buildSportEvent(Urn id, List<Locale> locales, boolean buildBasicEventImpl)
        throws ObjectNotFoundException {
        return buildSportEventInternal(id, null, locales, buildBasicEventImpl);
    }

    /**
     * Builds the {@link SportEvent} derived class based on the provided data
     *
     * @param id - the {@link Urn} specifying the identifier of the sport event
     * @param sportId - the {@link Urn} specifying the sport type of the event
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @param buildBasicEventImpl - an indication if the basic event entity should be built if the mapping type is unknown
     * @return - the constructed object
     * @throws ObjectNotFoundException if the requested sport event object could not be provided(failure built, api request errors,..)
     */
    @Override
    public SportEvent buildSportEvent(Urn id, Urn sportId, List<Locale> locales, boolean buildBasicEventImpl)
        throws ObjectNotFoundException {
        return buildSportEventInternal(id, sportId, locales, buildBasicEventImpl);
    }

    /**
     * Builds a list of {@link Competition} derived classes based on the provided data
     *
     * @param ids - the list of {@link Urn} specifying the identifier of the sport events to be built
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @return - the constructed objects
     * @throws ObjectNotFoundException if the requested sport event objects could not be provided(failure built, api request errors,..)
     */
    @Override
    public List<Competition> buildSportEvents(List<Urn> ids, List<Locale> locales)
        throws ObjectNotFoundException {
        Preconditions.checkNotNull(ids);
        Preconditions.checkNotNull(locales);

        try {
            return ids
                .stream()
                .map(eId -> {
                    try {
                        return this.buildSportEvent(eId, locales, true);
                    } catch (ObjectNotFoundException e) {
                        throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(
                            "Error building scheduled event[" + eId + "]",
                            e
                        ); // streams cant handle checked exceptions
                    }
                })
                .filter(se -> {
                    if (se instanceof Competition) {
                        return true; // all ok, as expected
                    } else {
                        logger.warn(
                            "buildSportEvents() received event[{}] which is not derived from Competition(event filtered out)",
                            se.getId()
                        );
                        return false;
                    }
                })
                .map(se -> (Competition) se)
                .collect(Collectors.toList());
        } catch (com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException e) {
            throw new ObjectNotFoundException("There was an error building the schedule list", e);
        }
    }

    /**
     * Builds a {@link Competitor} instance associated with the provided {@link Urn}
     *
     * @param id the competitor identifier
     * @param qualifier the competitor qualifier (if available)
     * @param division the competitor division (if available)
     * @param isVirtual
     * @param parentSportEventCi the parent {@link SportEventCi} this {@link Competitor} belongs to
     * @param locales the {@link Locale}s in which the data should be available
     * @return the constructed object
     * @throws ObjectNotFoundException if the requested instance could not be provided
     */
    @Override
    public Competitor buildCompetitor(
        Urn id,
        String qualifier,
        Integer division,
        Boolean isVirtual,
        SportEventCi parentSportEventCi,
        List<Locale> locales
    ) throws ObjectNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);

        if (qualifier != null) {
            return new TeamCompetitorImpl(
                id,
                profileCache,
                qualifier,
                division,
                isVirtual,
                parentSportEventCi,
                locales,
                this,
                exceptionHandlingStrategy
            );
        }

        return new CompetitorImpl(
            id,
            profileCache,
            parentSportEventCi,
            locales,
            this,
            exceptionHandlingStrategy,
            isVirtual
        );
    }

    /**
     * Builds a {@link List} of {@link Competitor} instances
     * <i>Notice: a {@link com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException} is thrown if any problems are encountered</i>
     *
     * @param competitorIds the ids representing the instances that should be built
     * @param parentSportEventCi the parent {@link SportEventCi} this {@link Competitor} belongs to
     * @param locales the {@link Locale}s in which the data should be available
     * @return the constructed objects
     */
    @Override
    public List<Competitor> buildStreamCompetitors(
        List<Urn> competitorIds,
        SportEventCi parentSportEventCi,
        List<Locale> locales
    ) {
        Preconditions.checkNotNull(competitorIds);
        Preconditions.checkNotNull(locales);

        return competitorIds
            .stream()
            .map(c -> {
                try {
                    return this.buildCompetitor(c, null, null, null, parentSportEventCi, locales);
                } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
                    throw new StreamWrapperException(e.getMessage(), e);
                }
            })
            .collect(Collectors.toList());
    }

    /**
     *
     * @param id the player identifier
     * @param locales the {@link Locale}s in which the data should be available
     * @param possibleAssociatedCompetitorIds a list of possible associated competitor ids (used to prefetch data)
     * @return the constructed object
     */
    @Override
    public PlayerProfile buildPlayerProfile(
        Urn id,
        List<Locale> locales,
        List<Urn> possibleAssociatedCompetitorIds
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);

        return new PlayerProfileImpl(
            id,
            profileCache,
            possibleAssociatedCompetitorIds,
            locales,
            exceptionHandlingStrategy
        );
    }

    /**
     *
     * @param id the player identifier
     * @param locales the {@link Locale}s in which the data should be available
     * @param possibleAssociatedCompetitorIds a list of possible associated competitor ids (used to prefetch data)
     * @return the constructed object
     */
    @Override
    public CompetitorPlayer buildCompetitorPlayerProfile(
        Urn id,
        List<Locale> locales,
        List<Urn> possibleAssociatedCompetitorIds,
        Map<Urn, Integer> associatedJerseyNumbers
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Integer jerseyNumber = associatedJerseyNumbers != null
            ? associatedJerseyNumbers.getOrDefault(id, null)
            : null;

        return new CompetitorPlayerImpl(
            id,
            jerseyNumber,
            profileCache,
            possibleAssociatedCompetitorIds,
            locales,
            exceptionHandlingStrategy
        );
    }

    /**
     * Constructs and returns a new instance derived from the {@link SportEvent} with the provided data
     *
     * @param id - the {@link Urn} specifying the identifier of the sport event
     * @param sportId - the {@link Urn} specifying the sport type of the event
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @param buildBasicEventImpl - an indication if the basic event entity should be built if the mapping type is unknown
     * @return - the constructed object which is derived from the {@link SportEvent}
     */
    private SportEvent buildSportEventInternal(
        Urn id,
        Urn sportId,
        List<Locale> locales,
        boolean buildBasicEventImpl
    ) throws ObjectNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        Optional<Class> mappingType = mappingTypeProvider.getMappingType(id);

        if (mappingType.isPresent()) {
            return buildEntityWithType(mappingType.get(), id, sportId, locales);
        }

        if (buildBasicEventImpl) {
            logger.warn("Built generic sport event for: {} - unknown mapping type", id);
            return new SportEventGenericImpl(id, sportId);
        }

        throw new ObjectNotFoundException(
            "The requested sport event[" + id + "] could not be built - unknown mapping type"
        );
    }

    private SportEvent buildEntityWithType(Class type, Urn id, Urn sportId, List<Locale> locales)
        throws ObjectNotFoundException {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);

        try {
            if (type.equals(Match.class)) {
                return buildMatchEntity(id, sportId, locales);
            } else if (type.equals(Stage.class)) {
                return new StageImpl(
                    id,
                    sportId,
                    sportEventCache,
                    sportEventStatusFactory,
                    this,
                    locales,
                    exceptionHandlingStrategy
                );
            } else if (type.equals(BasicTournament.class)) {
                return new BasicTournamentImpl(
                    id,
                    sportId,
                    locales,
                    sportEventCache,
                    this,
                    exceptionHandlingStrategy
                );
            } else if (type.equals(Season.class)) {
                return new SeasonImpl(id, sportId, locales, sportEventCache, this, exceptionHandlingStrategy);
            } else if (type.equals(Tournament.class)) {
                return new TournamentImpl(
                    id,
                    sportId,
                    locales,
                    sportEventCache,
                    this,
                    exceptionHandlingStrategy
                );
            } else if (type.equals(Lottery.class)) {
                return new LotteryImpl(
                    id,
                    sportId,
                    locales,
                    sportEventCache,
                    this,
                    exceptionHandlingStrategy
                );
            } else if (type.equals(Draw.class)) {
                return new DrawImpl(id, sportId, locales, sportEventCache, this, exceptionHandlingStrategy);
            }
        } catch (CacheItemNotFoundException e) {
            throw new ObjectNotFoundException("Could not provide proper entity for:" + id, e);
        }

        throw new ObjectNotFoundException("Unsupported mapping type: '" + type + "', eventId:'" + id + "'");
    }

    private Match buildMatchEntity(Urn id, Urn sportId, List<Locale> locales)
        throws CacheItemNotFoundException {
        SportEventCi eventCi = sportEventCache.getEventCacheItem(id);

        if (!(eventCi instanceof MatchCi)) {
            throw new CacheItemNotFoundException(
                "Match[" + id + "] entity can not be created from: " + eventCi.getClass()
            );
        }

        if (sportId == null) {
            sportId = provideSportIdForMatch(eventCi);
        }

        if (sportId == null) {
            logger.warn("EventCI missing sportId, providing default Match entity");
        }

        if (sportId != null && soccerSportUrns.contains(sportId)) {
            return new SoccerEventImpl(
                id,
                sportId,
                sportEventCache,
                sportEventStatusFactory,
                this,
                locales,
                exceptionHandlingStrategy
            );
        }

        return new MatchImpl(
            id,
            sportId,
            sportEventCache,
            sportEventStatusFactory,
            this,
            locales,
            exceptionHandlingStrategy
        );
    }

    private Urn provideSportIdForMatch(SportEventCi ci) {
        Preconditions.checkNotNull(ci);

        if (ci instanceof MatchCi) {
            Urn tournamentId = ((MatchCi) ci).getTournamentId();

            if (tournamentId == null) {
                logger.warn("Tournament id missing for {} CI, could not provide sportId", ci.getId());
                return null;
            }

            try {
                SportEvent sportEvent = buildSportEvent(
                    tournamentId,
                    Collections.singletonList(defaultLocale),
                    false
                );
                return sportEvent.getSportId(); // the implementation handles sport id fetching by itself
            } catch (ObjectNotFoundException e) {
                // highly unlikely to happen
                logger.warn("Failed to provide sportId from tournament for {}", ci.getId(), e);
            }
        }

        return null;
    }

    /**
     * Constructs and returns a new instance of {@link Sport} from the provided data
     *
     * @param sportData - a {@link SportData} instance used to build the {@link Sport}
     * @param locales - a {@link List} of locales specifying the languages in which the data is translated
     * @return - the constructed {@link Sport} instance
     */
    private Sport buildSportInternal(SportData sportData, List<Locale> locales)
        throws ObjectNotFoundException {
        Preconditions.checkNotNull(sportData);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        try {
            return new SportImpl(
                sportData.getId(),
                sportData.getNames(),
                sportData
                    .getCategories()
                    .stream()
                    .map(c ->
                        new CategoryImpl(
                            c.getId(),
                            c.getNames(),
                            c
                                .getTournaments()
                                .stream()
                                .map(t -> {
                                    try {
                                        return buildSportEventInternal(t, sportData.getId(), locales, true);
                                    } catch (ObjectNotFoundException e) {
                                        throw new StreamWrapperException(
                                            "Error occurred while building associated tournament list",
                                            e
                                        );
                                    }
                                })
                                .filter(t -> {
                                    if (
                                        (t instanceof Tournament) ||
                                        (t instanceof BasicTournament) ||
                                        (t instanceof Stage) ||
                                        (t instanceof Lottery)
                                    ) {
                                        return true;
                                    } else {
                                        logger.warn(
                                            "buildSportInternal, category list received unsupported tournament[{}] type {}",
                                            t.getId(),
                                            t.getClass()
                                        );
                                        return false;
                                    }
                                })
                                .collect(Collectors.toList()),
                            c.getCountryCode()
                        )
                    )
                    .collect(Collectors.toList())
            );
        } catch (StreamWrapperException e) {
            throw new ObjectNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Builds a {@link CategorySummary} instance from the provided {@link CategoryCi}
     *
     * @param categoryCi the CI which should be used to create the {@link CategorySummary}
     * @param locales the locales in which the data should be available
     * @return the newly built instace
     */
    private CategorySummary buildCategoryInternal(CategoryCi categoryCi, List<Locale> locales) {
        Preconditions.checkNotNull(categoryCi);
        Preconditions.checkNotNull(locales);

        return new CategorySummaryImpl(
            categoryCi.getId(),
            categoryCi.getNames(locales),
            categoryCi.getCountryCode()
        );
    }
}
