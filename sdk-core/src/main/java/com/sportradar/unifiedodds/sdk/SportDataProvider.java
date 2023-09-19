/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.caching.exportable.CacheType;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods implemented by classes used to provide sport related data (sports, tournaments, competitions, ...)
 */
@SuppressWarnings({ "LineLength" })
public interface SportDataProvider {
    /**
     * Returns all the available sports
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @return - all the available sports
     */
    List<Sport> getSports();

    /**
     * Returns all the available sports
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the available sports translated in the specified locale
     */
    List<Sport> getSports(Locale locale);

    /**
     * Returns all the active tournaments
     * (the returned data is translated in the default locale configured with the {@link UofConfigurationImpl})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @return - all the active tournaments
     */
    List<SportEvent> getActiveTournaments();

    /**
     * Returns all the active tournaments
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the active tournaments translated in the specified locale
     */
    List<SportEvent> getActiveTournaments(Locale locale);

    /**
     * Returns all the active tournaments of a specific sport
     * (the returned data is translated in the default locale configured with the {@link UofConfigurationImpl})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportName - the specific sport name
     * @return - all the active tournaments of a specific sport
     */
    List<SportEvent> getActiveTournaments(String sportName);

    /**
     * Returns all the active tournaments of a specific sport
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportName - the specific sport name
     * @param locale    - the {@link Locale} in which to provide the data
     * @return - all the active tournaments of a specific sport translated in the specified locale
     */
    List<SportEvent> getActiveTournaments(String sportName, Locale locale);

    /**
     * Returns a list of all competitions scheduled on the specified date
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param date - the date for which to list all active competitions
     * @return - a list of all competitions scheduled on the specified date
     */
    List<Competition> getCompetitionsFor(Date date);

    /**
     * Returns a list of all competitions scheduled on the specified date
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param date   - the date for which to list all active competitions
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a list of all competitions scheduled on the specified date
     * (the data is translated in the provided locale)
     */
    List<Competition> getCompetitionsFor(Date date, Locale locale);

    /**
     * Returns all the competitions that are currently live
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @return - all the competitions that are currently live
     */
    List<Competition> getLiveCompetitions();

    /**
     * Returns all the competitions that are currently live
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return - all the competitions that are currently live
     * (the data is translated in the provided locale)
     */
    List<Competition> getLiveCompetitions(Locale locale);

    /**
     * Returns the specified sport event
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param id - an {@link Urn} identifier specifying the sport event
     * @return - the specified sport event
     */
    SportEvent getSportEvent(Urn id);

    /**
     * Returns the specified sport event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link Urn} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified sport event translated in the provided locale
     */
    SportEvent getSportEvent(Urn id, Locale locale);

    /**
     * Returns the specified long term event
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param id - an {@link Urn} identifier specifying the requested long term event
     * @return - the specified tournament
     */
    LongTermEvent getLongTermEvent(Urn id);

    /**
     * Returns the specified long term event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link Urn} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified tournament translated in the provided locale
     */
    LongTermEvent getLongTermEvent(Urn id, Locale locale);

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param id - an {@link Urn} identifier specifying the competition requested
     * @return - a {@link Competition} representing the specified competition
     */
    Competition getCompetition(Urn id);

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link Urn} identifier specifying the competition requested
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competition} representing the specified competition translated in the provided locale
     */
    Competition getCompetition(Urn id, Locale locale);

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param id - a unique competitor {@link Urn} identifier
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link Urn}
     */
    Competitor getCompetitor(Urn id);

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - a unique competitor {@link Urn} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link Urn}
     */
    Competitor getCompetitor(Urn id, Locale locale);

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link UofConfigurationImpl})
     *
     * @param id - a unique player {@link Urn} identifier
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    PlayerProfile getPlayerProfile(Urn id);

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - a unique player {@link Urn} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    PlayerProfile getPlayerProfile(Urn id, Locale locale);

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId the identifier of the cache item to purge
     */
    void purgeSportEventCacheData(Urn eventId);

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId            the identifier of the cache item to purge
     * @param includeStatusPurge an indication if the associated sport event status should be purged too
     */
    void purgeSportEventCacheData(Urn eventId, boolean includeStatusPurge);

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    void purgeCompetitorProfileCacheData(Urn competitorId);

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    void purgePlayerProfileCacheData(Urn playerId);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     * @return list of {@link FixtureChange}
     */
    List<FixtureChange> getFixtureChanges();

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return list of {@link FixtureChange}
     */
    List<FixtureChange> getFixtureChanges(Locale locale);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @return list of {@link FixtureChange}
     */
    List<FixtureChange> getFixtureChanges(Date after, Urn sportId);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @param locale - the {@link Locale} in which to provide the data
     * @return list of {@link FixtureChange}
     */
    List<FixtureChange> getFixtureChanges(Date after, Urn sportId, Locale locale);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     * @return list of {@link ResultChange}
     */
    List<ResultChange> getResultChanges();

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param locale - the {@link Locale} in which to provide the data
     * @return list of {@link ResultChange}
     */
    List<ResultChange> getResultChanges(Locale locale);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @return list of {@link ResultChange}
     */
    List<ResultChange> getResultChanges(Date after, Urn sportId);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param after specifies the starting date and time for filtering
     * @param sportId specifies the sport for which the fixtures should be returned
     * @param locale - the {@link Locale} in which to provide the data
     * @return list of {@link ResultChange}
     */
    List<ResultChange> getResultChanges(Date after, Urn sportId, Locale locale);

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     *
     * @param startIndex starting index (zero based)
     * @param limit      how many records to return (max: 1000)
     * @return a list of sport events
     */
    List<Competition> getListOfSportEvents(int startIndex, int limit);

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     *
     * @param startIndex starting index (zero based)
     * @param limit      how many records to return (max: 1000)
     * @param locale     the {@link Locale} in which to provide the data
     * @return a list of sport events
     */
    List<Competition> getListOfSportEvents(int startIndex, int limit, Locale locale);

    /**
     * Returns all the available tournaments for a specific sport
     * (the returned data is translated in the default locale configured with the {@link UofConfigurationImpl})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @return - all available tournaments for a sport we provide coverage for in default locale
     */
    List<SportEvent> getAvailableTournaments(Urn sportId);

    /**
     * Returns all the available tournaments for a specific sport
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @param locale  - the {@link Locale} in which to provide the data
     * @return - all available tournaments for a sport we provide coverage for in specified locale
     */
    List<SportEvent> getAvailableTournaments(Urn sportId, Locale locale);

    /**
     * Deletes the sport events from cache which are scheduled before specified date
     *
     * @param before the scheduled Date used to delete sport events from cache
     * @return number of deleted items
     */
    Integer deleteSportEventsFromCache(Date before);

    /**
     * Exports current items in the cache
     *
     * @param cacheType specifies what type of cache items will be exported
     * @return List of {@link ExportableCi} containing all the items currently in the cache
     */
    List<ExportableCi> cacheExport(EnumSet<CacheType> cacheType);

    /**
     * Imports provided items into caches
     *
     * @param items List of {@link ExportableCi} containing the items to be imported
     */
    void cacheImport(List<ExportableCi> items);

    /**
     * Returns all the available lotteries
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param locale  - the {@link Locale} in which to provide the data
     * @return - all available lotteries in specified locale (from SportsAPI All lotteries endpoint)
     */
    List<Lottery> getLotteries(Locale locale);

    /**
     * Returns the list of {@link PeriodStatus} from the sport event period summary endpoint
     * @param id the id of the sport event to be fetched
     * @param locale the {@link Locale} in which to provide the data (can be null)
     * @param competitorIds the list of competitor ids to fetch the results for (can be null)
     * @param periods the list of period ids to fetch the results for (can be null)
     * @return the list of {@link PeriodStatus} from the sport event period summary endpoint
     */
    List<PeriodStatus> getPeriodStatuses(
        Urn id,
        Locale locale,
        List<Urn> competitorIds,
        List<Integer> periods
    );

    /**
     * Returns the list of {@link TimelineEvent} for the sport event
     * @param id the id of the sport event to be fetched
     * @param locale the {@link Locale} in which to provide the data (can be null)
     * @return the list of {@link TimelineEvent} for the sport event
     */
    List<TimelineEvent> getTimelineEvents(Urn id, Locale locale);
}
