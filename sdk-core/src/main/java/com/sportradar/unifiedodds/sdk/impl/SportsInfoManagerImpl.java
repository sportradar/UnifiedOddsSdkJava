/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.ci.EventTimelineCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.CacheType;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSdkCache;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.entities.EventTimelineImpl;
import com.sportradar.utils.URN;
import java.util.*;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to sport related data (sports, tournaments, sport events, ...)
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "ClassFanOutComplexity",
        "ConstantName",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "MultipleStringLiterals",
        "NeedBraces",
        "OverloadMethodsDeclarationOrder",
        "ParameterAssignment",
        "ParameterNumber",
    }
)
public class SportsInfoManagerImpl implements SportsInfoManager {

    /**
     * The client interaction log instance
     */
    private static final Logger clientInteractionLog = LoggerFactory.getLogger(
        LoggerDefinitions.UFSdkClientInteractionLog.class
    );

    /**
     * The execution log instance
     */
    private static final Logger logger = LoggerFactory.getLogger(SportsInfoManagerImpl.class);

    /**
     * A {@link SportEntityFactory} instance used to build sport related instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link SportEventCache} instance used to retrieve, purge, import and export sport event cache items
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link SportEventStatusCache} instance used to purge cached sport event statuses
     */
    private final SportEventStatusCache sportEventStatusCache;

    /**
     * A {@link ProfileCache} instance used to purge, import and export competitor/player profile cache items
     */
    private final ProfileCache profileCache;

    /**
     * A {@link SportsDataCache} instance used to import and export sports data items
     */
    private final SportsDataCache sportsDataCache;

    /**
     * A {@link Locale} which is the desired default locale
     */
    private final Locale defaultLocale;

    /**
     * A {@link List} of locales in which the data should be provided/cached
     */
    private final List<Locale> desiredLocales;

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * A {@link DataRouterManager} instance used to get sports info
     */
    private final DataRouterManager dataRouterManager;

    /**
     * Initializes a new instance of the {@link SportsInfoManagerImpl}
     *
     * @param config the main SDK config used to prepare the {@link SportsInfoManagerImpl}
     * @param entityFactory a {@link SportEntityFactory} instance used to build sport related instances
     * @param eventCache a {@link SportEventCache} instance used to retrieve and purge sport event cache items
     * @param profileCache a {@link ProfileCache} instance used to purge competitor/player profile cache items
     * @param sportEventStatusCache a {@link SportEventStatusCache} instance used to purge cached sport event statuses
     * @param dataRouterManager a {@link DataRouterManager} instance used to get sports info
     */
    @Inject
    SportsInfoManagerImpl(
        SDKInternalConfiguration config,
        SportEntityFactory entityFactory,
        SportEventCache eventCache,
        ProfileCache profileCache,
        SportEventStatusCache sportEventStatusCache,
        SportsDataCache sportsDataCache,
        DataRouterManager dataRouterManager
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(config.getDesiredLocales());
        Preconditions.checkArgument(!config.getDesiredLocales().isEmpty());
        Preconditions.checkNotNull(entityFactory);
        Preconditions.checkNotNull(eventCache);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportsDataCache);
        Preconditions.checkNotNull(dataRouterManager);

        this.sportEntityFactory = entityFactory;
        this.sportEventCache = eventCache;
        this.profileCache = profileCache;
        this.defaultLocale = config.getDefaultLocale();
        this.desiredLocales = config.getDesiredLocales();
        this.exceptionHandlingStrategy = config.getExceptionHandlingStrategy();
        this.sportEventStatusCache = sportEventStatusCache;
        this.sportsDataCache = sportsDataCache;
        this.dataRouterManager = dataRouterManager;
    }

    /**
     * Returns all the available sports
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @return - all the available sports
     */
    @Override
    public List<Sport> getSports() {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Sport> sports = internalGetSports(desiredLocales);
            clientInteractionLog.info(
                "SportsInfoManager.getSports() invoked. Execution time: {}",
                timer.stop()
            );
            return sports;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getSports", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the available sports
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the available sports translated in the specified locale
     */
    @Override
    public List<Sport> getSports(Locale locale) {
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Sport> sports = internalGetSports(Lists.newArrayList(locale));
            clientInteractionLog.info(
                "SportsInfoManager.getSports({}) invoked. Execution time: {}",
                locale,
                timer.stop()
            );
            return sports;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getSports with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the active tournaments
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @return - all the active tournaments
     */
    @Override
    public List<SportEvent> getActiveTournaments() {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<SportEvent> tournaments = internalGetActiveTournaments(defaultLocale);
            clientInteractionLog.info(
                "SportsInfoManager.getActiveTournaments() invoked. Execution time: {}",
                timer.stop()
            );
            return tournaments;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getActiveTournaments", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the active tournaments
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the active tournaments translated in the specified locale
     */
    @Override
    public List<SportEvent> getActiveTournaments(Locale locale) {
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<SportEvent> tournaments = internalGetActiveTournaments(locale);
            clientInteractionLog.info(
                "SportsInfoManager.getActiveTournaments({}) invoked. Execution time: {}",
                locale,
                timer.stop()
            );
            return tournaments;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getActiveTournaments with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the active tournaments of a specific sport
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportName - the specific sport name
     * @return - all the active tournaments of a specific sport
     */
    @Override
    public List<SportEvent> getActiveTournaments(String sportName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(sportName), "Sport name can not be null/empty");

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<SportEvent> tournaments = internalGetActiveTournaments(sportName, defaultLocale);
            clientInteractionLog.info(
                "SportsInfoManager.getActiveTournaments({}) invoked. Execution time: {}",
                sportName,
                timer.stop()
            );
            return tournaments;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getActiveTournaments for sport", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the active tournaments of a specific sport
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportName - the specific sport name
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the active tournaments of a specific sport translated in the specified locale
     */
    @Override
    public List<SportEvent> getActiveTournaments(String sportName, Locale locale) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(sportName), "Sport name can not be null/empty");
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<SportEvent> tournaments = internalGetActiveTournaments(sportName, locale);
            clientInteractionLog.info(
                "SportsInfoManager.getActiveTournaments({},{}) invoked. Execution time: {}",
                sportName,
                locale,
                timer.stop()
            );
            return tournaments;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getActiveTournaments for sport with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a list of all competitions scheduled on the specified date
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param date - the date for which to list all active competitions
     * @return - a list of all competitions scheduled on the specified date
     */
    @Override
    public List<Competition> getCompetitionsFor(Date date) {
        Preconditions.checkNotNull(date);

        // rest call
        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Competition> competitions = internalGetSportEventsFor(date, desiredLocales);
            clientInteractionLog.info(
                "SportsInfoManager.getCompetitionsFor({}) invoked. Execution time: {}",
                date,
                timer.stop()
            );
            return competitions;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetitionFor date", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a list of all competitions scheduled on the specified date
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param date - the date for which to list all active competitions
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a list of all competitions scheduled on the specified date
     *           (the data is translated in the provided locale)
     */
    @Override
    public List<Competition> getCompetitionsFor(Date date, Locale locale) {
        Preconditions.checkNotNull(date);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Competition> competitions = internalGetSportEventsFor(date, Lists.newArrayList(locale));
            clientInteractionLog.info(
                "SportsInfoManager.getCompetitionsFor({},{}) invoked. Execution time: {}",
                date,
                locale,
                timer.stop()
            );
            return competitions;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetitionFor date with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the competitions that are currently live
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @return - all the competitions that are currently live
     */
    @Override
    public List<Competition> getLiveCompetitions() {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Competition> competitions = internalGetLiveSportEvents(desiredLocales);
            clientInteractionLog.info(
                "SportsInfoManager.getLiveCompetitions() invoked. Execution time: {}",
                timer.stop()
            );
            return competitions;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getLiveCompetitions", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns all the competitions that are currently live
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the competitions that are currently live
     *           (the data is translated in the provided locale)
     */
    @Override
    public List<Competition> getLiveCompetitions(Locale locale) {
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Competition> competitions = internalGetLiveSportEvents(Lists.newArrayList(locale));
            clientInteractionLog.info(
                "SportsInfoManager.getLiveCompetitions({}) invoked. Execution time: {}",
                locale,
                timer.stop()
            );
            return competitions;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getLiveCompetitions with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns the specified sport event
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the sport event
     * @return - the specified sport event
     */
    @Override
    public SportEvent getSportEvent(URN id) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        SportEvent sportEvent;
        try {
            sportEvent = sportEntityFactory.buildSportEvent(id, desiredLocales, false);
        } catch (ObjectNotFoundException e) {
            return handleException("getSportEvent[" + id + "]", e);
        }
        clientInteractionLog.info(
            "SportsInfoManager.getSportEvent({}) invoked. Execution time: {}",
            id,
            timer.stop()
        );
        return sportEvent;
    }

    public SportEvent getSportEventForEventChange(URN id) {
        Preconditions.checkNotNull(id);

        SportEvent sportEvent;
        try {
            sportEvent = sportEntityFactory.buildSportEvent(id, desiredLocales, false);
        } catch (ObjectNotFoundException e) {
            return handleException("getSportEvent[" + id + "]", e);
        }
        return sportEvent;
    }

    /**
     * Returns the specified sport event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id - an {@link URN} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified sport event translated in the provided locale
     */
    @Override
    public SportEvent getSportEvent(URN id, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        SportEvent sportEvent;
        try {
            sportEvent = sportEntityFactory.buildSportEvent(id, Lists.newArrayList(locale), false);
        } catch (ObjectNotFoundException e) {
            return handleException("getSportEvent[" + id + ", " + locale + "]", e);
        }
        clientInteractionLog.info(
            "SportsInfoManager.getSportEvent({}, {}) invoked. Execution time: {}",
            id,
            locale,
            timer.stop()
        );
        return sportEvent;
    }

    /**
     * Returns the specified long term event
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the requested long term event
     * @return - the specified long term event
     */
    @Override
    public LongTermEvent getLongTermEvent(URN id) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            LongTermEvent longTermEvent = internalGetLongTermEvent(id, desiredLocales);
            clientInteractionLog.info(
                "SportsInfoManager.getLongTermEvent({}) invoked. Execution time: {}",
                id,
                timer.stop()
            );
            return longTermEvent;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getLongTermEvent", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns the specified long term event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id - an {@link URN} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified tournament translated in the provided locale
     */
    @Override
    public LongTermEvent getLongTermEvent(URN id, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            LongTermEvent longTermEvent = internalGetLongTermEvent(id, Lists.newArrayList(locale));
            clientInteractionLog.info(
                "SportsInfoManager.getLongTermEvent({},{}) invoked. Execution time: {}",
                id,
                locale,
                timer.stop()
            );
            return longTermEvent;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getLongTermEvent with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the competition requested
     * @return - a {@link Competition} representing the specified competition
     */
    @Override
    public Competition getCompetition(URN id) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            Competition competition = internalGetCompetition(id, desiredLocales);
            clientInteractionLog.info(
                "SportsInfoManager.getCompetition({}) invoked. Execution time: {}",
                id,
                timer.stop()
            );
            return competition;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetition", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id - an {@link URN} identifier specifying the competition requested
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competition} representing the specified competition translated in the provided locale
     */
    @Override
    public Competition getCompetition(URN id, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            Competition competition = internalGetCompetition(id, Lists.newArrayList(locale));
            clientInteractionLog.info(
                "SportsInfoManager.getCompetition({},{}) invoked. Execution time: {}",
                id,
                locale,
                timer.stop()
            );
            return competition;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetition with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - a unique competitor {@link URN} identifier
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link URN}
     */
    @Override
    public Competitor getCompetitor(URN id) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            Competitor competitor = internalGetCompetitor(id, desiredLocales);

            clientInteractionLog.info(
                "sportsInfo.getCompetitor({}) invoked. Execution time: {}",
                id,
                timer.stop()
            );
            return competitor;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetitor", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id - a unique competitor {@link URN} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link URN}
     */
    @Override
    public Competitor getCompetitor(URN id, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Locale> builtLocales = Lists.newArrayList(locale);
            Competitor competitor = internalGetCompetitor(id, builtLocales);

            clientInteractionLog.info(
                "sportsInfo.getCompetitor({}, {}) invoked. Execution time: {}",
                id,
                builtLocales,
                timer.stop()
            );
            return competitor;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getCompetitor with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - a unique player {@link URN} identifier
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    @Override
    public PlayerProfile getPlayerProfile(URN id) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            PlayerProfile player = internalGetPlayerProfile(id, desiredLocales);

            clientInteractionLog.info(
                "sportsInfo.getPlayerProfile({}) invoked. Execution time: {}",
                id,
                timer.stop()
            );
            return player;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getPlayerProfile", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id - a unique player {@link URN} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    @Override
    public PlayerProfile getPlayerProfile(URN id, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        try {
            List<Locale> builtLocales = Lists.newArrayList(locale);
            PlayerProfile player = internalGetPlayerProfile(id, builtLocales);

            clientInteractionLog.info(
                "sportsInfo.getPlayerProfile({}, {}) invoked. Execution time: {}",
                id,
                builtLocales,
                timer.stop()
            );
            return player;
        } catch (Exception e) {
            clientInteractionLog.error("Error executing getPlayerProfile with locale", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
            return null;
        }
    }

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId the identifier of the cache item to purge
     */
    @Override
    public void purgeSportEventCacheData(URN eventId) {
        Preconditions.checkNotNull(eventId);

        purgeSportEventCacheData(eventId, false);
    }

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId the identifier of the cache item to purge
     * @param includeStatusPurge an indication if the associated sport event status should be purged too
     */
    @Override
    public void purgeSportEventCacheData(URN eventId, boolean includeStatusPurge) {
        Preconditions.checkNotNull(eventId);

        clientInteractionLog.info("sportsInfo.purgeSportEventCacheData({}, {})", eventId, includeStatusPurge);
        sportEventCache.purgeCacheItem(eventId);

        if (includeStatusPurge) {
            sportEventStatusCache.purgeSportEventStatus(eventId);
        }
    }

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    @Override
    public void purgeCompetitorProfileCacheData(URN competitorId) {
        Preconditions.checkNotNull(competitorId);

        clientInteractionLog.info("sportsInfo.purgeCompetitorProfileCacheData({})", competitorId);
        profileCache.purgeCompetitorProfileCacheItem(competitorId);
    }

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    @Override
    public void purgePlayerProfileCacheData(URN playerId) {
        Preconditions.checkNotNull(playerId);

        clientInteractionLog.info("sportsInfo.purgePlayerProfileCacheData({})", playerId);
        profileCache.purgePlayerProfileCacheItem(playerId);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     */
    @Override
    public List<FixtureChange> getFixtureChanges() {
        return getFixtureChanges(null, null, defaultLocale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param locale - the {@link Locale} in which to provide the data
     */
    @Override
    public List<FixtureChange> getFixtureChanges(Locale locale) {
        return getFixtureChanges(null, null, locale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after   specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     */
    @Override
    public List<FixtureChange> getFixtureChanges(Date after, URN sportId) {
        return getFixtureChanges(after, sportId, defaultLocale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after   specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @param locale  - the {@link Locale} in which to provide the data
     */
    @Override
    public List<FixtureChange> getFixtureChanges(Date after, URN sportId, Locale locale) {
        Preconditions.checkNotNull(locale);

        try {
            return dataRouterManager.requestFixtureChanges(after, sportId, locale);
        } catch (CommunicationException e) {
            return handleException("getFixtureChanges", e);
        }
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     */
    @Override
    public List<ResultChange> getResultChanges() {
        return getResultChanges(null, null, defaultLocale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param locale - the {@link Locale} in which to provide the data
     */
    @Override
    public List<ResultChange> getResultChanges(Locale locale) {
        return getResultChanges(null, null, locale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after   specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     */
    @Override
    public List<ResultChange> getResultChanges(Date after, URN sportId) {
        return getResultChanges(after, sportId, defaultLocale);
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after   specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @param locale  - the {@link Locale} in which to provide the data
     */
    @Override
    public List<ResultChange> getResultChanges(Date after, URN sportId, Locale locale) {
        Preconditions.checkNotNull(locale);

        try {
            return dataRouterManager.requestResultChanges(after, sportId, locale);
        } catch (CommunicationException e) {
            return handleException("getResultChanges", e);
        }
    }

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     * @param startIndex starting index (zero based)
     * @param limit how many records to return (max: 1000)
     * @return a list of sport events
     */
    @Override
    public List<Competition> getListOfSportEvents(int startIndex, int limit) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("Wrong startIndex");
        }
        if (limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("Wrong limit");
        }

        Stopwatch timer = Stopwatch.createStarted();

        List<Competition> sportEvents = null;

        try {
            List<URN> eventIds = null;
            for (Locale locale : desiredLocales) {
                eventIds = this.dataRouterManager.requestListSportEvents(locale, startIndex, limit);
            }
            if (eventIds != null && !eventIds.isEmpty()) {
                sportEvents = sportEntityFactory.buildSportEvents(eventIds, desiredLocales);
            }
            clientInteractionLog.info(
                "sportsInfo.getListOfSportEvents({}, {}, {}) invoked. Execution time: {}",
                startIndex,
                limit,
                desiredLocales,
                timer.stop()
            );
        } catch (ObjectNotFoundException | CommunicationException e) {
            return handleException(
                String.format("getListOfSportEvents(%s, %s, %s)", startIndex, limit, desiredLocales),
                e
            );
        }
        return sportEvents;
    }

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     * @param startIndex starting index (zero based)
     * @param limit how many records to return (max: 1000)
     * @param locale the {@link Locale} in which to provide the data
     * @return a list of sport events
     */
    @Override
    public List<Competition> getListOfSportEvents(int startIndex, int limit, Locale locale) {
        Preconditions.checkNotNull(locale);

        if (startIndex < 0) {
            throw new IllegalArgumentException("Wrong startIndex");
        }
        if (limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("Wrong limit");
        }

        Stopwatch timer = Stopwatch.createStarted();

        List<Competition> sportEvents = null;
        List<Locale> builtLocales = Lists.newArrayList(locale);

        try {
            List<URN> eventIds = this.dataRouterManager.requestListSportEvents(locale, startIndex, limit);
            if (eventIds != null && !eventIds.isEmpty()) {
                sportEvents = sportEntityFactory.buildSportEvents(eventIds, builtLocales);
            }
            clientInteractionLog.info(
                "sportsInfo.getListOfSportEvents({}, {}, {}) invoked. Execution time: {}",
                startIndex,
                limit,
                builtLocales,
                timer.stop()
            );
        } catch (ObjectNotFoundException | CommunicationException e) {
            return handleException(
                String.format("getListOfSportEvents(%s, %s, %s)", startIndex, limit, locale),
                e
            );
        }
        return sportEvents;
    }

    /**
     * Returns all the available tournaments of a specific sport
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @return - all the available tournaments of a specific sport
     */
    @Override
    public List<SportEvent> getAvailableTournaments(URN sportId) {
        return getAvailableTournaments(sportId, defaultLocale);
    }

    /**
     * Returns all the available tournaments for a specific sport
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the available tournaments for a specific sport translated in the specified locale
     */
    @Override
    public List<SportEvent> getAvailableTournaments(URN sportId, Locale locale) {
        Preconditions.checkNotNull(sportId, "SportId can not be null/empty");
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        List<SportEvent> tournaments;
        try {
            tournaments = internalGetAvailableTournaments(sportId, locale);
        } catch (ObjectNotFoundException | CommunicationException e) {
            return handleException("getAvailableTournaments", e);
        }
        clientInteractionLog.info(
            "SportsInfoManager.getAvailableTournaments({},{}) invoked. Execution time: {}",
            sportId,
            locale,
            timer.stop()
        );
        return tournaments;
    }

    /**
     * Deletes the sport events from cache which are scheduled before specified date
     * @param before the scheduled Date used to delete sport events from cache
     * @return number of deleted items
     */
    @Override
    public Integer deleteSportEventsFromCache(Date before) {
        if (before == null) {
            throw new IllegalArgumentException("Parameter before is not defined");
        }
        return sportEventCache.deleteSportEventsFromCache(before);
    }

    /**
     * Exports current items in the cache
     *
     * @param cacheType specifies what type of cache items will be exported
     * @return List of {@link ExportableCI} containing all the items currently in the cache
     */
    @Override
    public List<ExportableCI> cacheExport(EnumSet<CacheType> cacheType) {
        Preconditions.checkNotNull(cacheType);
        List<ExportableCI> exportables = new ArrayList<>();

        if (cacheType.contains(CacheType.SportData)) exportables.addAll(
            ((ExportableSdkCache) sportsDataCache).exportItems()
        );
        if (cacheType.contains(CacheType.Profile)) exportables.addAll(
            ((ExportableSdkCache) profileCache).exportItems()
        );
        if (cacheType.contains(CacheType.SportEvent)) exportables.addAll(
            ((ExportableSdkCache) sportEventCache).exportItems()
        );

        return exportables;
    }

    /**
     * Imports provided items into caches
     *
     * @param items List of {@link ExportableCI} containing the items to be imported
     */
    @Override
    public void cacheImport(List<ExportableCI> items) {
        ((ExportableSdkCache) sportsDataCache).importItems(items);
        ((ExportableSdkCache) profileCache).importItems(items);
        ((ExportableSdkCache) sportEventCache).importItems(items);
    }

    /**
     * Returns all the available lotteries
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale  - the {@link Locale} in which to provide the data
     * @return - all available lotteries in specified locale
     */
    @Override
    public List<Lottery> getLotteries(Locale locale) {
        Preconditions.checkNotNull(locale);

        Stopwatch timer = Stopwatch.createStarted();
        List<Lottery> lotteries = new ArrayList<>();
        try {
            List<Locale> locales = Lists.newArrayList(locale);
            List<URN> lotteryIds = dataRouterManager.requestAllLotteriesEndpoint(locale, true);
            if (lotteryIds != null) {
                for (URN tId : lotteryIds) {
                    lotteries.add((Lottery) sportEntityFactory.buildSportEvent(tId, null, locales, false));
                }
            }
        } catch (ObjectNotFoundException | CommunicationException e) {
            return handleException("getAllLotteries", e);
        }
        clientInteractionLog.info(
            "SportsInfoManager.getAllLotteries({}) invoked. Execution time: {}",
            locale,
            timer.stop()
        );
        return lotteries;
    }

    /**
     * Returns the list of {@link PeriodStatus} from the sport event period summary endpoint
     *
     * @param id            the id of the sport event to be fetched
     * @param locale        the {@link Locale} in which to provide the data (can be null)
     * @param competitorIds the list of competitor ids to fetch the results for (can be null)
     * @param periods       the list of period ids to fetch the results for (can be null)
     * @return the list of {@link PeriodStatus} from the sport event period summary endpoint
     */
    @Override
    public List<PeriodStatus> getPeriodStatuses(
        URN id,
        Locale locale,
        List<URN> competitorIds,
        List<Integer> periods
    ) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        List<PeriodStatus> periodStatuses = new ArrayList<>();
        try {
            periodStatuses = dataRouterManager.requestPeriodSummary(id, locale, competitorIds, periods);
            clientInteractionLog.info(
                "SportsInfoManager.getPeriodStatuses({}, {}) invoked. Execution time: {}",
                id,
                locale,
                timer.stop()
            );
        } catch (CommunicationException e) {
            Throwable initException = getInitialException(e);
            if (
                initException.getMessage() != null &&
                initException.getMessage().contains("not found") ||
                initException.getMessage().contains("404")
            ) {
                clientInteractionLog.warn(
                    "SportsInfoManager.getPeriodStatuses({}, {}) invoked. SportEvent not found. Execution time: {}",
                    id,
                    locale,
                    timer.stop()
                );
            } else {
                return handleException("getPeriodStatuses", e);
            }
        }
        return periodStatuses;
    }

    /**
     * Returns the list of {@link TimelineEvent} for the sport event
     * @param id the id of the sport event to be fetched
     * @param locale the {@link Locale} in which to provide the data (can be null)
     * @return the list of {@link TimelineEvent} for the sport event
     */
    @Override
    public List<TimelineEvent> getTimelineEvents(URN id, Locale locale) {
        Preconditions.checkNotNull(id);

        Stopwatch timer = Stopwatch.createStarted();
        if (locale == null) {
            locale = defaultLocale;
        }
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        try {
            SAPIMatchTimelineEndpoint matchTimelineEndpoint = dataRouterManager.requestEventTimelineEndpoint(
                locale,
                id,
                null
            );
            if (matchTimelineEndpoint != null && matchTimelineEndpoint.getTimeline() != null) {
                EventTimelineCI eventTimelineCI = new EventTimelineCI(
                    matchTimelineEndpoint.getTimeline(),
                    locale,
                    false
                );
                EventTimeline eventTimeline = new EventTimelineImpl(eventTimelineCI);
                timelineEvents = eventTimeline.getTimelineEvents();
            }
            clientInteractionLog.info(
                "SportsInfoManager.getTimelineEvents({}, {}) invoked. Execution time: {}",
                id,
                locale,
                timer.stop()
            );
        } catch (CommunicationException e) {
            Throwable initException = getInitialException(e);
            if (
                initException.getMessage() != null &&
                initException.getMessage().contains("not found") ||
                initException.getMessage().contains("404")
            ) {
                clientInteractionLog.warn(
                    "SportsInfoManager.getTimelineEvents({}, {}) invoked. MatchTimeline not found. Execution time: {}",
                    id,
                    locale,
                    timer.stop()
                );
            } else {
                return handleException("getTimelineEvents", e);
            }
        }
        return timelineEvents;
    }

    private List<Sport> internalGetSports(List<Locale> locales) {
        try {
            return this.sportEntityFactory.buildSports(locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            return handleException("getSports", e);
        }
    }

    private List<SportEvent> internalGetActiveTournaments(Locale locale) {
        LinkedList<SportEvent> ls = new LinkedList<>();
        for (Sport s : internalGetSports(Lists.newArrayList(locale))) {
            ls.addAll(extractTournamentsFromSport(s));
        }
        return ls;
    }

    private List<SportEvent> internalGetActiveTournaments(String sportName, Locale locale) {
        Sport sport = null;
        for (Sport s : internalGetSports(Lists.newArrayList(locale))) {
            String name = s.getName(locale);
            if (sportName.equalsIgnoreCase(name)) {
                sport = s;
                break;
            }
        }

        if (sport == null) {
            return handleException("No such sport named " + sportName, null);
        }

        return extractTournamentsFromSport(sport);
    }

    private List<SportEvent> internalGetAvailableTournaments(URN sportId, Locale locale)
        throws ObjectNotFoundException, CommunicationException {
        List<Locale> locales = Lists.newArrayList(locale);
        List<URN> tournamentIds = dataRouterManager.requestAvailableTournamentsFor(locale, sportId);
        if (tournamentIds != null) {
            List<SportEvent> tournaments = new LinkedList<>();
            for (URN tId : tournamentIds) {
                tournaments.add(sportEntityFactory.buildSportEvent(tId, sportId, locales, false));
            }
            return tournaments;
        }

        return null;
    }

    private List<Competition> internalGetLiveSportEvents(List<Locale> locales) {
        return internalGetSportEventsFor(null, locales);
    }

    private List<Competition> internalGetSportEventsFor(Date date, List<Locale> locales) {
        try {
            List<URN> eventIds = Lists.newArrayList();
            for (Locale l : locales) {
                eventIds = this.sportEventCache.getEventIds(date, l);
            }
            return sportEntityFactory.buildSportEvents(eventIds, locales);
        } catch (IllegalCacheStateException | ObjectNotFoundException e) {
            return handleException(String.format("getSportEvents(%s)", date != null ? date : "LIVE"), e);
        }
    }

    private LongTermEvent internalGetLongTermEvent(URN id, List<Locale> locales) {
        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(id, locales, false);
            if (sportEvent instanceof LongTermEvent) {
                return (LongTermEvent) sportEvent;
            }
            return null;
        } catch (ObjectNotFoundException e) {
            return handleException("internalGetLongTermEvent[" + id + "]", e);
        }
    }

    private Competition internalGetCompetition(URN id, List<Locale> locales) {
        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(id, locales, false);
            if (sportEvent instanceof Competition) {
                return (Competition) sportEvent;
            }
            return null;
        } catch (ObjectNotFoundException e) {
            return handleException("internalGetCompetition[" + id + "]", e);
        }
    }

    private Competitor internalGetCompetitor(URN id, List<Locale> locales) {
        try {
            return sportEntityFactory.buildCompetitor(id, null, null, null, null, locales);
        } catch (ObjectNotFoundException e) {
            return handleException("getCompetitor", e);
        }
    }

    private PlayerProfile internalGetPlayerProfile(URN id, List<Locale> locales) {
        try {
            return sportEntityFactory.buildPlayerProfile(id, locales, null);
        } catch (ObjectNotFoundException e) {
            return handleException("getPlayerProfile", e);
        }
    }

    private List<SportEvent> extractTournamentsFromSport(Sport sport) {
        if (sport == null) {
            return null;
        }

        LinkedList<SportEvent> ls = new LinkedList<>();
        for (Category cat : sport.getCategories()) {
            ls.addAll(cat.getTournaments());
        }
        return ls;
    }

    private Throwable getInitialException(Exception e) {
        Throwable init = e;
        while (init.getCause() != null) {
            init = init.getCause();
        }
        return init;
    }

    private <T> T handleException(String method, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(method);
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(method, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error executing user request[{}])", method);
            } else {
                logger.warn("Error executing user request[{}])", method, e);
            }
            return null;
        }
    }
}
