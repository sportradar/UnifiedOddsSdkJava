/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.MatchCI;
import com.sportradar.unifiedodds.sdk.caching.ci.*;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.Fixture;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.unifiedodds.sdk.impl.entities.FixtureImpl;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.copyOf;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
class MatchCIImpl implements MatchCI {
    private final static Logger logger = LoggerFactory.getLogger(MatchCIImpl.class);

    /**
     * A {@link Locale} specifying the default language
     */
    private final Locale defaultLocale;

    /**
     * An {@link URN} specifying the id of the associated sport event
     */
    private final URN id;

    /**
     * A {@link Fixture} instance associated with the current instance
     */
    private Fixture fixture;

    /**
     * A {@link BookingStatus} enum member providing booking status of the current instance
     */
    private BookingStatus bookingStatus;

    /**
     * The {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     */
    private Date scheduled;

    /**
     * The {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     */
    private Date scheduledEnd;

    /**
     * A {@link List} of competitor identifiers that participate in the sport event
     * associated with the current instance
     */
    private List<URN> competitorIds;

    /**
     * A map of available competitor qualifiers
     */
    private Map<URN, String> competitorQualifiers;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<URN, ReferenceIdCI> competitorsReferences;

    /**
     * The {@link URN} specifying the id of the tournament to which the sport event belongs to
     */
    private URN tournamentId;

    /**
     * A {@link LoadableRoundCI} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     */
    private LoadableRoundCI tournamentRound;

    /**
     * A {@link SeasonCI} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     */
    private SeasonCI season;

    /**
     * A {@link VenueCI} instance representing a venue where the sport event associated with the
     * current instance will take place
     */
    private VenueCI venue;

    /**
     * A {@link DelayedInfoCI} instance describing possible information about a delay
     */
    private DelayedInfoCI delayedInfo;

    /**
     * A {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     */
    private SportEventConditionsCI conditions;

    /**
     * A {@link SportEventStatusDTO} instance providing the current event status information
     */
    private SportEventStatusDTO sportEventStatusDTO;

    /**
     * A {@link EventStatus} representing event status
     */
    private EventStatus eventStatus;

    /**
     * A {@link List} indicating which fixture translations were already fetched
     */
    private final List<Locale> loadedFixtureLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A {@link List} indicating which event summary translations were already fetched
     */
    private final List<Locale> loadedSummaryLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A {@link List} indicating which event competitors translations were already fetched
     */
    private final List<Locale> loadedCompetitorLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A {@link Map} storing the available sport event names
     */
    private final Map<Locale, String> sportEventNames = Maps.newConcurrentMap();

    /**
     * A {@link Map} associated translated event time lines
     */
    private final Map<Locale, EventTimelineCI> eventTimelines = Maps.newConcurrentMap();

    /**
     * A {@link ReentrantLock} used to synchronize summary request operations
     */
    private final ReentrantLock summaryRequest = new ReentrantLock();

    /**
     * A {@link ReentrantLock} used to synchronize fixture request operations
     */
    private final ReentrantLock fixtureRequest = new ReentrantLock();

    /**
     * A {@link ReentrantLock} used to synchronize event timeline request operations
     */
    private final ReentrantLock timelineRequest = new ReentrantLock();

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * The {@link DataRouterManager} which is used to trigger data fetches
     */
    private final DataRouterManager dataRouterManager;

    /**
     * The {@link Cache} used to cache the sport events fixture timestamps
     */
    private final Cache<URN, Date> fixtureTimestampCache;

    MatchCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, Cache<URN, Date> fixtureTimestampCache) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkNotNull(fixtureTimestampCache);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.bookingStatus = null;
        this.fixtureTimestampCache = fixtureTimestampCache;
    }

    MatchCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPISportEvent data, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, fixtureTimestampCache);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        constructWithSportEventData(data, dataLocale, false);
    }

    MatchCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIFixture data, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, fixtureTimestampCache);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        constructWithSportEventData(data, dataLocale, true);

        this.delayedInfo = data.getDelayedInfo() == null ? null : new DelayedInfoCI(data.getDelayedInfo(), dataLocale);
        this.fixture = new FixtureImpl(data);

        loadedFixtureLocales.add(dataLocale);
    }

    MatchCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIMatchSummaryEndpoint data, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, fixtureTimestampCache);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        constructWithSportEventData(data.getSportEvent(), dataLocale, false);

        this.conditions = data.getSportEventConditions() == null
                ? null
                : new SportEventConditionsCI(data.getSportEventConditions(), dataLocale);

        loadedSummaryLocales.add(dataLocale);
    }

    MatchCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPISportEventChildren.SAPISportEvent endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);
        Preconditions.checkNotNull(fixtureTimestampCache);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.bookingStatus = null;
        this.fixtureTimestampCache = fixtureTimestampCache;

        scheduled = endpointData.getScheduled() == null ? null : endpointData.getScheduled().toGregorianCalendar().getTime();
        scheduledEnd = endpointData.getScheduledEnd() == null ? null : endpointData.getScheduledEnd().toGregorianCalendar().getTime();

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        }
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
     * Returns a {@link Map} of translated sport event names
     * The match object name is composed from the home and away team(eg. Home vs Away)
     *
     * @param locales the {@link Locale}s in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (sportEventNames.keySet().containsAll(locales)) {
            return copyOf(sportEventNames);
        }

        if (loadedSummaryLocales.containsAll(locales)) {
            return copyOf(sportEventNames);
        }

        requestMissingSummaryData(locales, false);

        return copyOf(sportEventNames);
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return loadedFixtureLocales.containsAll(localeList) && loadedSummaryLocales.containsAll(localeList);
    }

    /**
     * Returns the {@link URN} specifying the id of the tournament to which the sport event belongs to
     *
     * @return the {@link URN} specifying the id of the tournament to which the sport event belongs to
     */
    @Override
    public URN getTournamentId() {
        if (tournamentId != null) {
            return tournamentId;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return tournamentId;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return tournamentId;
    }

    /**
     * Returns a {@link RoundCI} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link RoundCI} instance describing the tournament round
     */
    @Override
    public RoundCI getTournamentRound(List<Locale> locales) {
        if (tournamentRound == null) {
            tournamentRound = new LoadableRoundCIImpl(this, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        }

        return tournamentRound;
    }

    /**
     * Returns a {@link SeasonCI} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return  {@link SeasonCI} instance providing basic information about the associated season
     */
    @Override
    public SeasonCI getSeason(List<Locale> locales) {
        if (season != null && season.hasTranslationsFor(locales)) {
            return season;
        }

        if (loadedSummaryLocales.containsAll(locales)) {
            return season;
        }

        requestMissingSummaryData(locales, false);

        return season;
    }

    /**
     * Returns the {@link Fixture} instance containing information about the arranged sport event
     * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return the {@link Fixture} instance containing information about the arranged sport event
     */
    @Override
    public Fixture getFixture(List<Locale> locales) {
        if (loadedFixtureLocales.containsAll(locales)) {
            return fixture;
        }

        requestMissingFixtureData(locales);

        return fixture;
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return a {@link BookingStatus} enum member providing booking status of the current instance
     */
    @Override
    public BookingStatus getBookingStatus() {
        if (bookingStatus != null) {
            return bookingStatus;
        }

        if (!loadedFixtureLocales.isEmpty()) {
            return bookingStatus;
        }

        requestMissingFixtureData(Collections.singletonList(defaultLocale));

        return bookingStatus;
    }

    /**
     * Returns a {@link List} of competitor identifiers that participate in the sport event
     * associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} in which the competitor data should be provided
     * @return a {@link List} of competitor identifiers that participate in the sport event
     * associated with the current instance
     */
    @Override
    public List<URN> getCompetitorIds(List<Locale> locales) {

        if (loadedCompetitorLocales.containsAll(locales)) {
            return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
        }

        requestMissingSummaryData(locales, false);

        return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
    }

    /**
     * Returns a {@link VenueCI} instance representing a venue where the sport event associated with the
     * current instance will take place
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link VenueCI} instance representing a venue where the associated sport event
     */
    @Override
    public VenueCI getVenue(List<Locale> locales) {
        if (venue != null && venue.hasTranslationsFor(locales)) {
            return venue;
        }

        if (loadedSummaryLocales.containsAll(locales)) {
            return venue;
        }

        requestMissingSummaryData(locales, false);

        return venue;
    }

    /**
     * Returns a {@link DelayedInfoCI} instance describing possible information about a delay
     *
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a {@link DelayedInfoCI} instance describing information about a possible delay
     */
    @Override
    public DelayedInfoCI getDelayedInfo(List<Locale> locales) {
        if (delayedInfo != null && delayedInfo.hasTranslationsFor(locales)) {
            return delayedInfo;
        }

        if (loadedFixtureLocales.containsAll(locales)) {
            return delayedInfo;
        }

        requestMissingFixtureData(locales);

        return delayedInfo;
    }

    /**
     * Returns a {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     */
    @Override
    public SportEventConditionsCI getConditions(List<Locale> locales) {
        // conditions available only on summary locales
        if (loadedSummaryLocales.containsAll(locales)) {
            return conditions;
        }

        requestMissingSummaryData(locales, false);

        return conditions;
    }

    /**
     * Returns a {@link SportEventStatusDTO} instance providing the current event status information
     *
     * @return a {@link SportEventStatusDTO} instance providing the current event status information
     */
    @Override
    public SportEventStatusDTO getSportEventStatusDTO() {
        requestMissingSummaryData(Collections.singletonList(defaultLocale), true);

        return sportEventStatusDTO;
    }

    /**
     * Get the event status
     *
     * @return the event status
     */
    @Override
    public EventStatus getEventStatus() {
        return eventStatus;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled; otherwise null;
     */
    @Override
    public Date getScheduled() {
        if (scheduled != null) {
            return scheduled;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return scheduled;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return scheduled;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end; otherwise null;
     */
    @Override
    public Date getScheduledEnd() {
        if (scheduledEnd != null) {
            return scheduledEnd;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return scheduledEnd;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return scheduledEnd;
    }

    /**
     * Returns the associated event timeline
     * (the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @param makeApiCall should the API call be made if necessary
     * @return the associated event timeline
     */
    @Override
    public EventTimelineCI getEventTimeline(Locale locale, boolean makeApiCall) {
        Preconditions.checkNotNull(locale);

        EventTimelineCI eventTimelineCI = eventTimelines.get(locale);
        if (!makeApiCall) {
            return eventTimelineCI;
        }
        if (eventTimelineCI != null && eventTimelineCI.isFinalized()) {
            return eventTimelineCI;
        }

        timelineRequest.lock();
        try {
            eventTimelineCI = eventTimelines.get(locale);
            if (eventTimelineCI != null && eventTimelineCI.isFinalized()) {
                return eventTimelineCI;
            }

            // reset the timeline if it is not finalized & than try to fetch
            eventTimelines.remove(locale);
            try {
                dataRouterManager.requestEventTimelineEndpoint(locale, id, this);
            } catch (CommunicationException e) {
                handleException("Event timeline request failed", e);
            }
        } finally {
            timelineRequest.unlock();
        }

        return eventTimelines.get(locale);
    }

    /**
     * Returns list of {@link URN} of {@link CompetitorCI} and associated {@link ReferenceIdCI} for this sport event
     *
     * @return list of {@link URN} of {@link CompetitorCI} and associated {@link ReferenceIdCI} for this sport event
     */
    @Override
    public Map<URN, ReferenceIdCI> getCompetitorsReferences() {
        if(loadedFixtureLocales.isEmpty()) {
            requestMissingFixtureData(Collections.singletonList(defaultLocale));
        }

        return competitorsReferences == null
                ? null
                : ImmutableMap.copyOf(competitorsReferences);
    }

    /**
     * Returns list of {@link URN} of {@link CompetitorCI} and associated qualifier for this sport event
     *
     * @return list of {@link URN} of {@link CompetitorCI} and associated qualifier for this sport event
     */
    @Override
    public Map<URN, String> getCompetitorsQualifiers() {
        if (competitorQualifiers != null && !competitorQualifiers.isEmpty()) {
            return copyOf(competitorQualifiers);
        }

        if (loadedSummaryLocales.isEmpty()) {
            requestMissingSummaryData(Collections.singletonList(defaultLocale), false);
        }

        return competitorQualifiers == null
                ? null
                : copyOf(competitorQualifiers);
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPIFixture) {
            internalMerge((SAPIFixture) endpointData, dataLocale);
        } else if (endpointData instanceof SAPISportEvent) {
            internalMerge((SAPISportEvent) endpointData, dataLocale, false);
        } else if (endpointData instanceof SAPIMatchSummaryEndpoint) {
            internalMerge((SAPIMatchSummaryEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SAPISportEventChildren.SAPISportEvent) {
            internalMerge((SAPISportEventChildren.SAPISportEvent) endpointData, dataLocale);
        } else if (endpointData instanceof SAPIMatchTimelineEndpoint) {
            internalMerge((SAPIMatchTimelineEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SportEventStatusDTO) {
            internalMerge((SportEventStatusDTO) endpointData);
        } else if (endpointData instanceof MatchStatus) {
            internalMerge((MatchStatus) endpointData);
        }
    }

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link BookingManager}
     */
    @Override
    public void onEventBooked() {

        bookingStatus = BookingStatus.Booked;
    }

    /**
     * Constructs the current instance with all the basic sport event information
     * using the provided {@link SAPISportEvent}
     *
     * @param sportEvent a {@link SAPISportEvent} which contains basic sport event data
     * @param currentLocale the {@link Locale} in which the data is provided
     */
    private void constructWithSportEventData(SAPISportEvent sportEvent, Locale currentLocale, boolean isFixtureEndpoint) {
        this.bookingStatus = BookingStatus.getLiveBookingStatus(sportEvent.getLiveodds());
        this.scheduled = sportEvent.getScheduled() == null
                ? null
                : sportEvent.getScheduled().toGregorianCalendar().getTime();
        this.scheduledEnd = sportEvent.getScheduledEnd() == null
                ? null
                :sportEvent.getScheduledEnd().toGregorianCalendar().getTime();
        this.tournamentId = sportEvent.getTournament() == null
                ? null
                : URN.parse(sportEvent.getTournament().getId());
        this.tournamentRound = sportEvent.getTournamentRound() == null
                ? null
                : new LoadableRoundCIImpl(sportEvent.getTournamentRound(), isFixtureEndpoint, currentLocale, this, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        this.season = sportEvent.getSeason() == null
                ? null
                : new SeasonCI(sportEvent.getSeason(), currentLocale);
        this.venue = sportEvent.getVenue() == null
                ? null
                : new VenueCI(sportEvent.getVenue(), currentLocale);
        cacheCompetitors(sportEvent.getCompetitors() == null
                ? null
                : sportEvent.getCompetitors().getCompetitor(), currentLocale);

        constructEventName(currentLocale, sportEvent.getCompetitors());
    }

    /**
     * Fetches fixture data for the missing translations
     *
     * @param requiredLocales a {@link List} of locales in which the fixture data should be translated
     */
    private void requestMissingFixtureData(List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(loadedFixtureLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        fixtureRequest.lock();
        try {
            // recheck missing locales after lock
            missingLocales = SdkHelper.findMissingLocales(loadedFixtureLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug("Fetching fixtures for eventId='{}' for languages '{}'",
                    id, String.join(", ", missingLocales.stream()
                            .map(Locale::toString).collect(Collectors.toList())));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestFixtureEndpoint(l, id, fixtureTimestampCache.getIfPresent(id) == null, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingFixtureData(%s)", missingLocales), e);
        } finally {
            fixtureRequest.unlock();
        }
    }

    /**
     * Fetches fixture summaries for the missing translations
     *
     * @param requiredLocales a {@link List} of locales in which the fixture summaries should be translated
     */
    private void requestMissingSummaryData(List<Locale> requiredLocales, boolean forceFetch) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(loadedSummaryLocales, requiredLocales);
        if (missingLocales.isEmpty() && !forceFetch) {
            return;
        }

        summaryRequest.lock();
        try {
            // recheck missing locales after lock
            missingLocales = forceFetch ? requiredLocales : SdkHelper.findMissingLocales(loadedSummaryLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug("Fetching summary for eventId='{}' for languages '{}'",
                    id, String.join(", ", missingLocales.stream()
                            .map(Locale::toString).collect(Collectors.toList())));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestSummaryEndpoint(l, id, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingSummaryData(%s)", missingLocales), e);
        } finally {
            summaryRequest.unlock();
        }
    }

    /**
     * Merges the current instance with the {@link SAPIFixture}
     *
     * @param fixtureData the {@link SAPIFixture} containing the data to be merged
     * @param locale the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPIFixture fixtureData, Locale locale) {
        Preconditions.checkNotNull(fixtureData);
        Preconditions.checkNotNull(locale);

        if (loadedFixtureLocales.contains(locale)) {
            logger.info("MatchCI [{}] already contains fixture info for language {}", id, locale);
        }

        internalMerge(fixtureData, locale, true);

        if (fixtureData.getDelayedInfo() != null) {
            if (delayedInfo == null) {
                delayedInfo = new DelayedInfoCI(fixtureData.getDelayedInfo(), locale);
            } else {
                delayedInfo.merge(fixtureData.getDelayedInfo(), locale);
            }
        }

        fixture = new FixtureImpl(fixtureData);

        loadedFixtureLocales.add(locale);
    }

    /**
     * Merges the current instance with the {@link SAPIMatchSummaryEndpoint}
     *
     * @param summaryEndpoint the {@link SAPIMatchSummaryEndpoint} containing the data to be merged
     * @param locale the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPIMatchSummaryEndpoint summaryEndpoint, Locale locale) {
        Preconditions.checkNotNull(summaryEndpoint);
        Preconditions.checkNotNull(locale);

        if (loadedSummaryLocales.contains(locale)) {
            logger.info("MatchCI [{}] already contains summary info for language {}", id, locale);
        }

        internalMerge(summaryEndpoint.getSportEvent(), locale, false);

        if (summaryEndpoint.getSportEventConditions() != null) {
            if (conditions == null) {
                conditions = new SportEventConditionsCI(summaryEndpoint.getSportEventConditions(), locale);
            } else {
                conditions.merge(summaryEndpoint.getSportEventConditions(), locale);
            }
        }

        loadedSummaryLocales.add(locale);
    }

    /**
     * Merges the current instance with the {@link SAPIMatchTimelineEndpoint}
     *
     * @param endpointData the {@link SAPIMatchTimelineEndpoint} containing the data to be merged
     * @param dataLocale the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPIMatchTimelineEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        internalMerge(endpointData.getSportEvent(), dataLocale, false);

        if (endpointData.getSportEventConditions() != null) {
            if (conditions == null) {
                conditions = new SportEventConditionsCI(endpointData.getSportEventConditions(), dataLocale);
            } else {
                conditions.merge(endpointData.getSportEventConditions(), dataLocale);
            }
        }

        if (endpointData.getTimeline() != null) {
            eventTimelines.put(dataLocale,new EventTimelineCI(endpointData.getTimeline(), dataLocale, isTimelineFinalized(endpointData)));
        }
    }

    /**
     * Merges the current instance with the {@link SAPISportEvent}
     *
     * @param sportEvent the {@link SAPISportEvent} containing the data to be merged
     * @param locale the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPISportEvent sportEvent, Locale locale, boolean isFixtureEndpoint) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(locale);

        // booking status can be obtained only from the fixture endpoint, so we need to be careful on when to merge it
        // re-cache it only if the fetch is from a fixture endpoint or if it was null so we set the default value
        if (isFixtureEndpoint || bookingStatus == null) {
            bookingStatus = BookingStatus.getLiveBookingStatus(sportEvent.getLiveodds());
        }

        scheduled = sportEvent.getScheduled() == null ? null : sportEvent.getScheduled().toGregorianCalendar().getTime();
        scheduledEnd = sportEvent.getScheduledEnd() == null ? null : sportEvent.getScheduledEnd().toGregorianCalendar().getTime();

        if (sportEvent.getTournament() != null) {
            tournamentId = URN.parse(sportEvent.getTournament().getId());
        }

        if (sportEvent.getCompetitors() != null && sportEvent.getCompetitors().getCompetitor() != null) {
            cacheCompetitors(sportEvent.getCompetitors().getCompetitor(), locale);
        }

        if (sportEvent.getTournamentRound() != null) {
            if (tournamentRound == null) {
                tournamentRound = new LoadableRoundCIImpl(
                        sportEvent.getTournamentRound(), isFixtureEndpoint, locale, this, dataRouterManager, defaultLocale, exceptionHandlingStrategy
                );
            } else {
                tournamentRound.merge(sportEvent.getTournamentRound(), locale, isFixtureEndpoint);
            }
        }

        if (sportEvent.getSeason() != null) {
            if (season == null) {
                season = new SeasonCI(sportEvent.getSeason(), locale);
            } else {
                season.merge(sportEvent.getSeason(), locale);
            }
        }

        if (sportEvent.getVenue() != null) {
            if (venue == null) {
                venue = new VenueCI(sportEvent.getVenue(), locale);
            } else {
                venue.merge(sportEvent.getVenue(), locale);
            }
        }

        constructEventName(locale, sportEvent.getCompetitors());
    }

    /**
     * Merges the current instance with the {@link SAPISportEventChildren.SAPISportEvent}
     *
     * @param endpointData the data to be merged
     * @param dataLocale the locale in which the data is provided
     */
    private void internalMerge(SAPISportEventChildren.SAPISportEvent endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        scheduled = endpointData.getScheduled() == null ? null : endpointData.getScheduled().toGregorianCalendar().getTime();
        scheduledEnd = endpointData.getScheduledEnd() == null ? null : endpointData.getScheduledEnd().toGregorianCalendar().getTime();

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        }
    }

    /**
     * Merges the current instance with the {@link SportEventStatusDTO}
     *
     * @param statusDTO the data to be merged
     */
    private void internalMerge(SportEventStatusDTO statusDTO) {
        Preconditions.checkNotNull(statusDTO);

        sportEventStatusDTO = statusDTO;

        if(statusDTO.getStatus() != null){
            eventStatus = statusDTO.getStatus();
        }
    }

    /**
     * Merges the current instance with the {@link MatchStatus}
     *
     * @param matchStatus the data to be merged
     */
    private void internalMerge(MatchStatus matchStatus) {
        Preconditions.checkNotNull(matchStatus);

        if(matchStatus.getStatus() != null){
            eventStatus = matchStatus.getStatus();
        }
    }

    /**
     * Enriches the current instance with the provided {@link List} of {@link SAPITeamCompetitor}
     *
     * The competitors data should always be over-written(not merged!) because the competitor list could change over time
     *
     * @param competitors a {@link List} of {@link SAPITeamCompetitor} which should be added to the instance
     * @param locale the {@link Locale} in which the data is provided
     */
    private void cacheCompetitors(List<SAPITeamCompetitor> competitors, Locale locale) {
        Preconditions.checkNotNull(locale);

        if (competitors == null) {
            return;
        }

        competitorIds = new ArrayList<>(competitors.size());
        competitorQualifiers = new HashMap<>(competitors.size());

        competitors.forEach(inputC -> {
            URN parsedId = URN.parse(inputC.getId());
                competitorIds.add(parsedId);

            if (inputC.getQualifier() != null) {
                competitorQualifiers.put(parsedId, inputC.getQualifier());
            }
        });

        competitorsReferences = SdkHelper.ParseTeamCompetitorsReferences(competitors, competitorsReferences);

        loadedCompetitorLocales.add(locale);
    }

    /**
     * Constructs and stores the event name.
     * The name of race objects is the "name" attribute from the fixture endpoint. The match object name is composed
     * from the home and away team(eg. Home vs Away)
     *
     * @param locale the locale in which the data is provided
     * @param competitors the list of match competitors
     */
    private void constructEventName(Locale locale, SAPISportEventCompetitors competitors) {
        Preconditions.checkNotNull(locale);

        if (competitors != null && competitors.getCompetitor().size() == 2) {
            String homeTeam = competitors.getCompetitor().get(0).getName();
            String awayTeam = competitors.getCompetitor().get(1).getName();
            if (!Strings.isNullOrEmpty(homeTeam) && !Strings.isNullOrEmpty(awayTeam)) {
                String name = homeTeam +" vs. " + awayTeam;
                sportEventNames.put(locale, name);
            }
            return;
        }

        logger.warn("MatchCI[{}] name generation failed, competitors count != 2 but '{}'", id, competitors == null ? null : competitors.getCompetitor().size());
    }

    /**
     * Verifies if the provided endpoint data is in a finalized stat(sport event ended, no further timeline events possible)
     *
     * @param endpointData the endpoint data which should be validated
     * @return <code>true</code> if the timeline is finalized, otherwise <code>false</code>
     */
    private static boolean isTimelineFinalized(SAPIMatchTimelineEndpoint endpointData) {
        Preconditions.checkNotNull(endpointData);

        if (endpointData.getSportEventStatus() != null
                && endpointData.getSportEventStatus().getStatus() != null) {
            EventStatus eventStatus = EventStatus.valueOfApiStatusName(endpointData.getSportEventStatus().getStatus());
            return eventStatus == EventStatus.Ended || eventStatus == EventStatus.Finished;
        }

        return false;
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("MatchCIImpl[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing MatchCIImpl[{}] request({})", id, request);
            } else {
                logger.warn("Error providing MatchCIImpl[{}] request({}), ex:", id, request, e);
            }
        }
    }
}