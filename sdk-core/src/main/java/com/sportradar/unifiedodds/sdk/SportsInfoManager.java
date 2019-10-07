/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.caching.exportable.CacheType;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.utils.URN;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods implemented by classes used to provide sport related data (sports, tournaments, competitions, ...)
 */
public interface SportsInfoManager {
    /**
     * Returns all the available sports
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
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
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
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
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
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
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
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
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
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
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the sport event
     * @return - the specified sport event
     */
    SportEvent getSportEvent(URN id);

    /**
     * Returns the specified sport event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link URN} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified sport event translated in the provided locale
     */
    SportEvent getSportEvent(URN id, Locale locale);

    /**
     * Returns the specified long term event
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the requested long term event
     * @return - the specified tournament
     */
    LongTermEvent getLongTermEvent(URN id);

    /**
     * Returns the specified long term event
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link URN} identifier specifying the requested long term event
     * @param locale - the {@link Locale} in which to provide the data
     * @return - the specified tournament translated in the provided locale
     */
    LongTermEvent getLongTermEvent(URN id, Locale locale);

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - an {@link URN} identifier specifying the competition requested
     * @return - a {@link Competition} representing the specified competition
     */
    Competition getCompetition(URN id);

    /**
     * Returns a {@link Competition} representing the specified competition
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - an {@link URN} identifier specifying the competition requested
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competition} representing the specified competition translated in the provided locale
     */
    Competition getCompetition(URN id, Locale locale);

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - a unique competitor {@link URN} identifier
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link URN}
     */
    Competitor getCompetitor(URN id);

    /**
     * Returns a {@link Competitor} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - a unique competitor {@link URN} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link Competitor} representing the competitor associated with the provided {@link URN}
     */
    Competitor getCompetitor(URN id, Locale locale);

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the configured {@link Locale}s using the {@link OddsFeedConfiguration})
     *
     * @param id - a unique player {@link URN} identifier
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    PlayerProfile getPlayerProfile(URN id);

    /**
     * Returns a {@link PlayerProfile} representing the specified competitor
     * (the returned data is translated in the specified {@link Locale})
     *
     * @param id     - a unique player {@link URN} identifier
     * @param locale - the {@link Locale} in which to provide the data
     * @return - a {@link PlayerProfile} representing the specified competitor
     */
    PlayerProfile getPlayerProfile(URN id, Locale locale);

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId the identifier of the cache item to purge
     */
    void purgeSportEventCacheData(URN eventId);

    /**
     * Purges the associated sport event cache item
     *
     * @param eventId            the identifier of the cache item to purge
     * @param includeStatusPurge an indication if the associated sport event status should be purged too
     */
    void purgeSportEventCacheData(URN eventId, boolean includeStatusPurge);

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    void purgeCompetitorProfileCacheData(URN competitorId);

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    void purgePlayerProfileCacheData(URN playerId);

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     */
    default List<FixtureChange> getFixtureChanges() {
        return null;
    }

    /**
     * Returns the list of all fixtures that have changed in the last 24 hours
     *
     * @param locale - the {@link Locale} in which to provide the data
     */
    default List<FixtureChange> getFixtureChanges(Locale locale) {
        return null;
    }

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     *
     * @param startIndex starting index (zero based)
     * @param limit      how many records to return (max: 1000)
     * @return a list of sport events
     */
    default List<Competition> getListOfSportEvents(int startIndex, int limit) {
        return null;
    }

    /**
     * Lists almost all events we are offering prematch odds for. This endpoint can be used during early startup to obtain almost all fixtures. This endpoint is one of the few that uses pagination.
     *
     * @param startIndex starting index (zero based)
     * @param limit      how many records to return (max: 1000)
     * @param locale     the {@link Locale} in which to provide the data
     * @return a list of sport events
     */
    default List<Competition> getListOfSportEvents(int startIndex, int limit, Locale locale) {
        return null;
    }

    /**
     * Returns all the available tournaments for a specific sport
     * (the returned data is translated in the default locale configured with the {@link OddsFeedConfiguration})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @return - all available tournaments for a sport we provide coverage for in default locale
     */
    default List<SportEvent> getAvailableTournaments(URN sportId) {
        return null;
    }

    /**
     * Returns all the available tournaments for a specific sport
     * (the returned data is translated in the specified {@link Locale})
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @param sportId - the specific sport id
     * @param locale  - the {@link Locale} in which to provide the data
     * @return - all available tournaments for a sport we provide coverage for in specified locale
     */
    default List<SportEvent> getAvailableTournaments(URN sportId, Locale locale) {
        return null;
    }

    /**
     * Deletes the sport events from cache which are scheduled before specified date
     *
     * @param before the scheduled Date used to delete sport events from cache
     * @return number of deleted items
     */
    default Integer deleteSportEventsFromCache(Date before) {
        return null;
    }

    /**
     * Exports current items in the cache
     *
     * @param cacheType specifies what type of cache items will be exported
     * @return List of {@link ExportableCI} containing all the items currently in the cache
     */
    default List<ExportableCI> cacheExport(EnumSet<CacheType> cacheType) {
        return null;
    }

    /**
     * Imports provided items into caches
     *
     * @param items List of {@link ExportableCI} containing the items to be imported
     */
    default void cacheImport(List<ExportableCI> items) {

    }
}
