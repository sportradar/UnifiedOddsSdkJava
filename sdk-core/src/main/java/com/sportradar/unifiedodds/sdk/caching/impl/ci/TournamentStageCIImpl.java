/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitors;
import com.sportradar.uf.sportsapi.datamodel.SAPITournament;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentInfoEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentLength;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.StageCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCI;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.entities.StageType;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
class TournamentStageCIImpl implements StageCI {
    private static final Logger logger = LoggerFactory.getLogger(TournamentStageCIImpl.class);

    /**
     * A {@link Locale} specifying the default language
     */
    private final Locale defaultLocale;

    /**
     * An {@link URN} specifying the id of the associated sport event
     */
    private final URN id;

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * The {@link DataRouterManager} which is used to trigger data fetches
     */
    private final DataRouterManager dataRouterManager;

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
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<URN, ReferenceIdCI> competitorsReferences;

    /**
     * A {@link Map} storing the available sport event names
     */
    private final Map<Locale, String> sportEventNames = Maps.newConcurrentMap();

    /**
     * A {@link URN} specifying the id of the parent category
     */
    private URN categoryId;

    /**
     * A {@link List} of locales that are already fully cached - only when the full tournament info endpoint is cached
     */
    private List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * An {@link ReentrantLock} used to synchronize summary request operations
     */
    private final ReentrantLock fetchRequestLock = new ReentrantLock();

    TournamentStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITournament endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(dataLocale, "");
        }

        this.categoryId = URN.parse(endpointData.getCategory().getId());
        this.scheduled = endpointData.getScheduled() == null ? null :
                endpointData.getScheduled().toGregorianCalendar().getTime();
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? null :
                endpointData.getScheduledEnd().toGregorianCalendar().getTime();

        if ((this.scheduled == null || this.scheduledEnd == null) && endpointData.getTournamentLength() != null) {
            SAPITournamentLength tournamentLength = endpointData.getTournamentLength();
            this.scheduled = tournamentLength.getStartDate() == null ? null :
                    tournamentLength.getStartDate().toGregorianCalendar().getTime();
            this.scheduledEnd = tournamentLength.getEndDate() == null ? null :
                    tournamentLength.getEndDate().toGregorianCalendar().getTime();
        }
    }

    TournamentStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData.getTournament(), dataLocale);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        SAPICompetitors endpointCompetitors = endpointData.getCompetitors() != null ?
                endpointData.getCompetitors() :
                endpointData.getTournament().getCompetitors();
        this.competitorIds = endpointCompetitors == null ? null :
                Collections.synchronizedList(endpointCompetitors.getCompetitor().stream()
                        .map(c -> URN.parse(c.getId())).collect(Collectors.toList()));

        cachedLocales.add(dataLocale);
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
     * The name of race objects is the "name" attribute from the fixture endpoint.
     *
     * @param locales the {@link Locale}s in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (sportEventNames.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(sportEventNames);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(sportEventNames);
        }

        requestMissingStageTournamentData(locales);

        return ImmutableMap.copyOf(sportEventNames);
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return cachedLocales.containsAll(localeList);
    }

    /**
     * Returns the identifier of the stage parent
     *
     * @return the {@link URN} identifier of the parent stage if available; otherwise null
     */
    @Override
    public URN getParentStageId() {
        // tournament info endpoint can not have a "parent" stage
        return null;
    }

    /**
     * Returns a {@link List} of known child stages identifiers
     *
     * @return a {@link List} known child stages identifiers if available; otherwise null
     */
    @Override
    public List<URN> getStagesIds() {
        List<URN> stageIds = null;
        try {
            stageIds = dataRouterManager.requestEventsFor(defaultLocale, id);
        } catch (CommunicationException e) {
            handleException("getStagesIds", e);
        }
        return stageIds;
    }

    /**
     * Returns a {@link StageType} indicating the type of the associated stage
     *
     * @return a {@link StageType} indicating the type of the associated stage
     */
    @Override
    public StageType getStageType() {
        return StageType.Parent;
    }

    /**
     * Returns the {@link URN} specifying the id of the parent category
     *
     * @return the {@link URN} specifying the id of the parent category
     */
    @Override
    public URN getCategoryId() {
        if (categoryId != null) {
            return categoryId;
        }

        if (!cachedLocales.isEmpty()) {
            return categoryId;
        }

        requestMissingStageTournamentData(Collections.singletonList(defaultLocale));

        return categoryId;
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return a {@link BookingStatus} enum member providing booking status of the current instance
     */
    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.Unavailable;
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
        if (cachedLocales.containsAll(locales)) {
            return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
        }

        requestMissingStageTournamentData(locales);

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
        // no venue info
        return null;
    }

    /**
     * Returns a {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     */
    @Override
    public SportEventConditionsCI getConditions(List<Locale> locales) {
        // no conditions info
        return null;
    }

    /**
     * Returns a {@link SportEventStatusDTO} instance providing the current event status information
     *
     * @return a {@link SportEventStatusDTO} instance providing the current event status information
     */
    @Override
    public SportEventStatusDTO getSportEventStatusDTO() {
        // no sport event status data
        return null;
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

        if (!cachedLocales.isEmpty()) {
            return scheduled;
        }

        requestMissingStageTournamentData(Collections.singletonList(defaultLocale));

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

        if (!cachedLocales.isEmpty()) {
            return scheduledEnd;
        }

        requestMissingStageTournamentData(Collections.singletonList(defaultLocale));

        return scheduledEnd;
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPITournamentInfoEndpoint) {
            internalMerge((SAPITournamentInfoEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SAPITournament) {
            internalMerge((SAPITournament) endpointData, dataLocale);
        }
    }

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link BookingManager}
     */
    @Override
    public void onEventBooked() {
        // tournament can not be booked
    }

    /**
     * Returns list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    @Override
    public Map<URN, ReferenceIdCI> getCompetitorsReferences() {
        if(competitorsReferences == null || cachedLocales.isEmpty()) {
            requestMissingStageTournamentData(Collections.singletonList(defaultLocale));
        }

        return competitorsReferences == null
                ? null
                : ImmutableMap.copyOf(competitorsReferences);
    }

    private void internalMerge(SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (cachedLocales.contains(dataLocale)) {
            logger.info("TournamentStageCI [{}] already contains TournamentInfo data for language {}", id, dataLocale);
        }

        SAPICompetitors endpointCompetitors = endpointData.getCompetitors() != null ?
                endpointData.getCompetitors() :
                endpointData.getTournament().getCompetitors();

        if (endpointCompetitors != null) {
            this.competitorIds = Collections.synchronizedList(endpointCompetitors
                    .getCompetitor().stream().map(c -> URN.parse(c.getId()))
                    .collect(Collectors.toList()));
        }

        internalMerge(endpointData.getTournament(), dataLocale);

        cachedLocales.add(dataLocale);
    }

    private void internalMerge(SAPITournament endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(dataLocale, "");
        }

        if (endpointData.getCategory() != null) {
            this.categoryId = URN.parse(endpointData.getCategory().getId());
        }

        this.scheduled = endpointData.getScheduled() == null ? null :
                endpointData.getScheduled().toGregorianCalendar().getTime();
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? null :
                endpointData.getScheduledEnd().toGregorianCalendar().getTime();

        if ((this.scheduled == null || this.scheduledEnd == null) && endpointData.getTournamentLength() != null) {
            SAPITournamentLength tournamentLength = endpointData.getTournamentLength();
            this.scheduled = tournamentLength.getStartDate() == null ? null :
                    tournamentLength.getStartDate().toGregorianCalendar().getTime();
            this.scheduledEnd = tournamentLength.getEndDate() == null ? null :
                    tournamentLength.getEndDate().toGregorianCalendar().getTime();
        }
    }

    /**
     * Requests the data for the missing translations
     *
     * @param requiredLocales a {@link List} of locales in which the tournament data should be translated
     */
    private void requestMissingStageTournamentData(List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        fetchRequestLock.lock();
        try {
            // recheck missing locales after lock
            missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug("Fetching missing stage tournament data for id='{}' for languages '{}'",
                    id, missingLocales.stream()
                            .map(Locale::toString).collect(Collectors.joining(", ")));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestSummaryEndpoint(l, id, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingStageTournamentData(%s)", missingLocales), e);
        } finally {
            fetchRequestLock.unlock();
        }
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("TournamentStageCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing TournamentStageCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing TournamentStageCI[{}] request({}), ex:", id, request, e);
            }
        }
    }
}
