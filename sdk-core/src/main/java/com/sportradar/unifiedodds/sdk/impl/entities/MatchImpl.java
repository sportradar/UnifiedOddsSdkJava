/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.MatchCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.ci.*;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a sport event with home and away competitor
 */
public class MatchImpl extends SportEventImpl implements Match {
    private static final Logger logger = LoggerFactory.getLogger(MatchImpl.class);

    /**
     * The cache used to access associated cache items
     */
    private final SportEventCache sportEventCache;

    /**
     * The locales with which the object is built
     */
    private final List<Locale> locales;

    /**
     * A {@link SportEventStatusFactory} instance used to build event status entities
     */
    private final SportEventStatusFactory sportEventStatusFactory;

    /**
     * A {@link SportEntityFactory} instance used to construct {@link Tournament} instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link MatchStatus} containing information about the progress of the match associated with the current instance
     */
    private MatchStatus status;

    /**
     * An indication of which exception handling strategy should be used by the instance
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new instance of the {@link MatchImpl}
     *
     * @param id A {@link URN} uniquely identifying the sport event associated with the current instance
     * @param sportId A {@link URN} uniquely identifying the sport to which the match is related
     * @param sportEventCache A {@link SportEventCache} instance used to access the associated cache items
     * @param statusFactory A {@link SportEventStatusFactory} instance used to build status entities
     * @param sportEntityFactory A {@link SportEntityFactory} instance used to construct {@link Tournament} instances
     * @param locales A {@link List} specifying languages the current instance supports
     * @param exceptionHandlingStrategy the exception handling strategy that should be used by the instance
     */
    public MatchImpl(URN id, URN sportId, SportEventCache sportEventCache, SportEventStatusFactory statusFactory,
                     SportEntityFactory sportEntityFactory, List<Locale> locales, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        super(id, sportId);

        Preconditions.checkNotNull(statusFactory);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(locales);

        this.sportEventCache = sportEventCache;
        this.locales = locales;
        this.sportEventStatusFactory = statusFactory;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns a {@link CompetitionStatus} containing information about the progress of a sport event
     * associated with the current instance
     *
     * @return - a {@link CompetitionStatus} containing information about the progress of a sport event
     * associated with the current instance
     */
    @Override
    public MatchStatus getStatus() {
        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, MatchStatus.class, true);
        }
        return status;
    }

    /**
     * Returns a {@link MatchStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     *
     * @return - a {@link MatchStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     */
    @Override
    public Optional<CompetitionStatus> getStatusIfPresent()  {
        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, MatchStatus.class, false);
        }
        if(status == null) {
            return Optional.empty();
        }
        return Optional.of(status);
    }

    /**
     * Returns a {@link EventStatus}
     *
     * @return a {@link EventStatus}
     */
    @Override
    public EventStatus getEventStatus() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getEventStatus", null);
            return null;
        }

        EventStatus eventStatus = cacheItem.getEventStatus();

        if(eventStatus == null)
        {
            if(status == null)
            {
                status = sportEventStatusFactory.buildSportEventStatus(id, MatchStatus.class, true);
            }
            if(status != null)
            {
                cacheItem.merge(status, null);
            }
        }
        return cacheItem.getEventStatus();
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return - a {@link BookingStatus} enum member providing booking status of the current instance
     */
    @Override
    public BookingStatus getBookingStatus() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getBookingStatus", null);
            return null;
        }

        return cacheItem.getBookingStatus();
    }

    /**
     * Returns the venue where the sport event associated with the current instance will take place
     *
     * @return - the {@link Venue} where the sport event associated with the current instance will take place
     */
    @Override
    public Venue getVenue() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getVenue", null);
            return null;
        }

        VenueCI venue = cacheItem.getVenue(locales);

        return venue == null ? null : new VenueImpl(venue, locales);
    }

    /**
     * Returns a {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     *
     * @return - the {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     */
    @Override
    public SportEventConditions getConditions() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getConditions", null);
            return null;
        }

        SportEventConditionsCI conditions = cacheItem.getConditions(locales);

        return conditions == null ? null :
                new SportEventConditionsImpl(conditions, locales);
    }

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    @Override
    public List<Competitor> getCompetitors() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getCompetitors", null);
            return null;
        }

        List<URN> competitors = cacheItem.getCompetitorIds(locales);

        if (competitors == null) {
            return null;
        }

        try {
            return competitors.stream()
                    .map(c -> {
                        try {
                            return sportEntityFactory.buildCompetitor(c, provideCompetitorQualifier(cacheItem, c), cacheItem.getCompetitorsReferences(), locales);
                        } catch (ObjectNotFoundException e) {
                            throw new StreamWrapperException(e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (StreamWrapperException e) {
            handleException("getCompetitors failure", e);
            return null;
        }
    }

    /**
     * Returns a {@link SeasonInfo} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     *
     * @return - a {@link SeasonInfo} instance providing basic information about
     * the season
     */
    @Override
    public SeasonInfo getSeason() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getSeason", null);
            return null;
        }

        SeasonCI season = cacheItem.getSeason(locales);

        return  season == null ? null : new SeasonInfoImpl(season, locales);
    }

    /**
     * Returns a {@link Round} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     *
     * @return - a {@link Round} instance describing the tournament round
     */
    @Override
    public Round getTournamentRound() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getTournamentRound", null);
            return null;
        }

        RoundCI tournamentRound = cacheItem.getTournamentRound(locales);

        return tournamentRound == null ? null : new RoundImpl(tournamentRound, locales);
    }

    /**
     * Returns a {@link TeamCompetitor} instance describing the home competitor
     *
     * @return - a {@link TeamCompetitor} instance describing the home competitor
     */
    @Override
    public TeamCompetitor getHomeCompetitor(){
        List<Competitor> competitors = provideValidHomeAway();

        if (competitors == null) {
            handleException("getHomeCompetitor", null);
            return null;
        }

        Competitor competitor = competitors.get(0);

        if (competitor instanceof TeamCompetitor) {
            return (TeamCompetitor) competitor;
        }

        return null;
    }

    /**
     * Returns a {@link TeamCompetitor} instance describing the away competitor
     *
     * @return - a {@link TeamCompetitor} instance describing the away competitor
     */
    @Override
    public TeamCompetitor getAwayCompetitor(){
        List<Competitor> competitors = provideValidHomeAway();

        if (competitors == null) {
            handleException("getAwayCompetitor", null);
            return null;
        }

        Competitor competitor = competitors.get(1);

        if (competitor instanceof TeamCompetitor) {
            return (TeamCompetitor) competitor;
        }

        return null;
    }

    /**
     * Returns the tournament associated with the current instance
     * (possible types can be {@link BasicTournament} and {@link Tournament})
     *
     * @return - the tournament associated with the current instance
     */
    @Override
    public LongTermEvent getTournament() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getTournament", null);
            return null;
        }

        if (cacheItem.getTournamentId() == null) {
            handleException("Tournament id missing", null);
            return null;
        }

        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(cacheItem.getTournamentId(), locales, false);
            if ((sportEvent instanceof Tournament) || (sportEvent instanceof BasicTournament)) {
                return (LongTermEvent) sportEvent;
            }
            handleException("getTournament - invalid type[" + sportEvent.getId() + "]: " + sportEvent.getClass(), null);
        } catch (ObjectNotFoundException e) {
            handleException("getTournament - not found", e);
        }

        return null;
    }

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public String getName(Locale locale) {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getName", null);
            return null;
        }

        return cacheItem.getNames(locales).get(locale);
    }

    /**
     * Returns the associated sport identifier
     * (This method its overridden because the superclass SportEvent does not contain the sportId in all cases)
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public URN getSportId() {
        if (super.getSportId() != null) {
            return super.getSportId();
        }

        // try to get the sport id from the tournament instance
        LongTermEvent tour = getTournament();
        if(tour == null)
        {
            return null;
        }
        SportSummary sport = getTournament().getSport();
        if (sport != null) {
            return sport.getId();
        }

        return null;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled
     */
    @Override
    public Date getScheduledTime() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getScheduledTime", null);
            return null;
        }

        return cacheItem.getScheduled();
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled to end
     */
    @Override
    public Date getScheduledEndTime() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getScheduledEndTime", null);
            return null;
        }

        return cacheItem.getScheduledEnd();
    }

    /**
     * Returns the {@link Fixture} instance containing information about the arranged sport event
     * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
     *
     * @return - the {@link Fixture} instance containing information about the arranged sport event
     */
    @Override
    public Fixture getFixture() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getFixture", null);
            return null;
        }

        return cacheItem.getFixture(locales);
    }

    /**
     * Returns the associated event timeline
     * (NOTICE: the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @return the associated event timeline
     */
    @Override
    public EventTimeline getEventTimeline(Locale locale) {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getEventTimeline", null);
            return null;
        }

        // this call is an exception on how the data is fetched from the cache,
        // because it could trigger excessive timeline calls - the user might just need some
        // basic timeline data which is not translatable so a prefetch of X locales would be excessive
        if (!locales.contains(locale)) {
            return null;
        }

        EventTimelineCI eventTimeline = cacheItem.getEventTimeline(locale, true);

        return eventTimeline == null ? null : new EventTimelineImpl(eventTimeline);
    }

    /**
     * Returns the associated {@link EventTimeline} if already cached (does not make API call)
     * (NOTICE: the timeline is cached only after the event status indicates that the event has finished)
     *
     * @return - a associated {@link EventTimeline} if already cached (does not make API call)
     */
    @Override
    public Optional<EventTimeline> getEventTimelineIfPresent(Locale locale) {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getEventTimeline", null);
            return Optional.empty();
        }

        // this call is an exception on how the data is fetched from the cache,
        // because it could trigger excessive timeline calls - the user might just need some
        // basic timeline data which is not translatable so a prefetch of X locales would be excessive
        if (!locales.contains(locale)) {
            return Optional.empty();
        }

        EventTimelineCI eventTimeline = cacheItem.getEventTimeline(locale, false);

        return eventTimeline == null
                ? Optional.empty()
                : Optional.of(new EventTimelineImpl(eventTimeline));
    }

    /**
     * Returns a {@link DelayedInfo} instance describing possible information about a delay
     *
     * @return a {@link DelayedInfo} instance describing information about a possible delay
     */
    @Override
    public DelayedInfo getDelayedInfo() {
        MatchCI cacheItem = loadMatchCI();

        if (cacheItem == null) {
            handleException("getDelayedInfo", null);
            return null;
        }

        DelayedInfoCI delayedInfo = cacheItem.getDelayedInfo(locales);

        return delayedInfo == null ? null : new DelayedInfoImpl(delayedInfo, locales);
    }

    /**
     * Returns a {@link List} which contains exactly 2 competitors (if available), which is a requirement
     * for an event of type match
     *
     * @return - a {@link List} which contains exactly 2 competitors (if available), which is a requirement
     * for an event of type match
     */
    private List<Competitor> provideValidHomeAway() {
        List<Competitor> competitors = getCompetitors();

        if (competitors == null) {
            return null;
        }

        if (competitors.size() == 2) {
            return competitors;
        }

        LoggerFactory.getLogger(MatchImpl.class).warn("Received a Match[{}] with an invalid number of competitors -> {}", id, competitors.size());
        return null;
    }

    /**
     * Returns a {@link String} describing the current {@link Match} instance
     *
     * @return - a {@link String} describing the current {@link Match} instance
     */
    @Override
    public String toString() {
        return "MatchImpl{" +
                "id=" + id +
                ", locales=" + locales +
                "} ";
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private MatchCI loadMatchCI() {
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof MatchCI) {
                return (MatchCI) eventCacheItem;
            }
            handleException("loadMatchCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadMatchCI, CI not found", e);
        }
        return null;
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
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException("MatchImpl[" + id + "], request(" + request + ")");
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing MatchImpl[{}] request({})", id, request);
            } else {
                logger.warn("Error providing MatchImpl[{}] request({}), ex:", id, request, e);
            }
        }
    }

    private static String provideCompetitorQualifier(MatchCI ci, URN competitorId) {
        Preconditions.checkNotNull(ci);
        Preconditions.checkNotNull(competitorId);

        return ci.getCompetitorQualifiers() == null ? null : ci.getCompetitorQualifiers().get(competitorId);
    }
}
