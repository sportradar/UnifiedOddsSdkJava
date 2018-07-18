/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * Provides access to sport related data (sports, tournaments, sport events, ...)
 */
public class SportsInfoManagerImpl implements SportsInfoManager {
    /**
     * The client interaction log instance
     */
    private final static Logger clientInteractionLog = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);

    /**
     * The execution log instance
     */
    private final static Logger logger = LoggerFactory.getLogger(SportsInfoManagerImpl.class);

    /**
     * A {@link SportEntityFactory} instance used to build sport related instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link SportEventCache} instance used to retrieve and purge sport event cache items
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link SportEventStatusCache} instance used to purge cached sport event statuses
     */
    private final SportEventStatusCache sportEventStatusCache;

    /**
     * A {@link ProfileCache} instance used to purge competitor/player profile cache items
     */
    private final ProfileCache profileCache;

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
     * Initializes a new instance of the {@link SportsInfoManagerImpl}
     *
     * @param config the main SDK config used to prepare the {@link SportsInfoManagerImpl}
     * @param entityFactory a {@link SportEntityFactory} instance used to build sport related instances
     * @param eventCache a {@link SportEventCache} instance used to retrieve and purge sport event cache items
     * @param profileCache a {@link ProfileCache} instance used to purge competitor/player profile cache items
     * @param sportEventStatusCache a {@link SportEventStatusCache} instance used to purge cached sport event statuses
     */
    @Inject
    SportsInfoManagerImpl(SDKInternalConfiguration config, SportEntityFactory entityFactory, SportEventCache eventCache, ProfileCache profileCache, SportEventStatusCache sportEventStatusCache) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(config.getDesiredLocales());
        Preconditions.checkArgument(!config.getDesiredLocales().isEmpty());
        Preconditions.checkNotNull(entityFactory);
        Preconditions.checkNotNull(eventCache);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(sportEventStatusCache);

        this.sportEntityFactory = entityFactory;
        this.sportEventCache = eventCache;
        this.profileCache = profileCache;
        this.defaultLocale = config.getDefaultLocale();
        this.desiredLocales = config.getDesiredLocales();
        this.exceptionHandlingStrategy = config.getExceptionHandlingStrategy();
        this.sportEventStatusCache = sportEventStatusCache;
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
        List<Sport> sports = internalGetSports(desiredLocales);
        clientInteractionLog.info("SportsInfoManager.getSports() invoked. Execution time: {}", timer.stop());
        return sports;
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
        List<Sport> sports = internalGetSports(Lists.newArrayList(locale));
        clientInteractionLog.info("SportsInfoManager.getSports({}) invoked. Execution time: {}", locale, timer.stop());
        return sports;
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
        List<SportEvent> tournaments = internalGetActiveTournaments(defaultLocale);
        clientInteractionLog.info("SportsInfoManager.getActiveTournaments() invoked. Execution time: {}", timer.stop());
        return tournaments;
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
        List<SportEvent> tournaments = internalGetActiveTournaments(locale);
        clientInteractionLog.info("SportsInfoManager.getActiveTournaments({}) invoked. Execution time: {}", locale, timer.stop());
        return tournaments;
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
        List<SportEvent> tournaments = internalGetActiveTournaments(sportName, defaultLocale);
        clientInteractionLog.info("SportsInfoManager.getActiveTournaments({}) invoked. Execution time: {}", sportName, timer.stop());
        return tournaments;
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
        List<SportEvent> tournaments = internalGetActiveTournaments(sportName, locale);
        clientInteractionLog.info("SportsInfoManager.getActiveTournaments({},{}) invoked. Execution time: {}", sportName, locale, timer.stop());
        return tournaments;
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

        Stopwatch timer = Stopwatch.createStarted();
        List<Competition> competitions = internalGetSportEventsFor(date, desiredLocales);
        clientInteractionLog.info("SportsInfoManager.getCompetitionsFor({}) invoked. Execution time: {}", date, timer.stop());
        return competitions;
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
        List<Competition> competitions = internalGetSportEventsFor(date, Lists.newArrayList(locale));
        clientInteractionLog.info("SportsInfoManager.getCompetitionsFor({},{}) invoked. Execution time: {}", date, locale, timer.stop());
        return competitions;
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
        List<Competition> competitions = internalGetLiveSportEvents(desiredLocales);
        clientInteractionLog.info("SportsInfoManager.getLiveCompetitions() invoked. Execution time: {}", timer.stop());
        return competitions;
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
        List<Competition> competitions = internalGetLiveSportEvents(Lists.newArrayList(locale));
        clientInteractionLog.info("SportsInfoManager.getLiveCompetitions({}) invoked. Execution time: {}", locale, timer.stop());
        return competitions;
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
        clientInteractionLog.info("SportsInfoManager.getSportEvent({}) invoked. Execution time: {}", id, timer.stop());
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
        clientInteractionLog.info("SportsInfoManager.getSportEvent({}, {}) invoked. Execution time: {}", id, locale, timer.stop());
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
        LongTermEvent longTermEvent = internalGetLongTermEvent(id, desiredLocales);
        clientInteractionLog.info("SportsInfoManager.getLongTermEvent({}) invoked. Execution time: {}", id, timer.stop());
        return longTermEvent;
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
        LongTermEvent longTermEvent = internalGetLongTermEvent(id, Lists.newArrayList(locale));
        clientInteractionLog.info("SportsInfoManager.getLongTermEvent({},{}) invoked. Execution time: {}", id, locale, timer.stop());
        return longTermEvent;
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
        Competition competition = internalGetCompetition(id, desiredLocales);
        clientInteractionLog.info("SportsInfoManager.getCompetition({}) invoked. Execution time: {}", id, timer.stop());
        return competition;
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
        Competition competition =  internalGetCompetition(id, Lists.newArrayList(locale));
        clientInteractionLog.info("SportsInfoManager.getCompetition({},{}) invoked. Execution time: {}", id, locale, timer.stop());
        return competition;
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

        Competitor competitor = internalGetCompetitor(id, desiredLocales);

        clientInteractionLog.info("sportsInfo.getCompetitor({}) invoked. Execution time: {}", id, timer.stop());
        return competitor;
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

        List<Locale> builtLocales = Lists.newArrayList(locale);
        Competitor competitor = internalGetCompetitor(id, builtLocales);

        clientInteractionLog.info("sportsInfo.getCompetitor({}, {}) invoked. Execution time: {}", id, builtLocales, timer.stop());
        return competitor;
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

        PlayerProfile player = internalGetPlayerProfile(id, desiredLocales);

        clientInteractionLog.info("sportsInfo.getPlayerProfile({}) invoked. Execution time: {}", id, timer.stop());
        return player;
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

        List<Locale> builtLocales = Lists.newArrayList(locale);
        PlayerProfile player = internalGetPlayerProfile(id, builtLocales);

        clientInteractionLog.info("sportsInfo.getPlayerProfile({}, {}) invoked. Execution time: {}", id, builtLocales, timer.stop());
        return player;
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

    private List<Competition> internalGetLiveSportEvents(List<Locale> locales) {
        return internalGetSportEventsFor(null, locales);
    }

    private List<Competition> internalGetSportEventsFor(Date date, List<Locale> locales) {
        try {
            List<URN> eventIds = this.sportEventCache.getEventIds(date);

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
            return sportEntityFactory.buildCompetitor(id, null, locales);
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

    private List<SportEvent> extractTournamentsFromSport(Sport sport){
        if (sport == null) {
            return null;
        }

        LinkedList<SportEvent> ls = new LinkedList<>();
        for (Category cat : sport.getCategories()) {
            ls.addAll(cat.getTournaments());
        }
        return ls;
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
