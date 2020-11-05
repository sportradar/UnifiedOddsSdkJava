/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.StageCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ChildRaceCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCI;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableRaceStageCI;
import com.sportradar.unifiedodds.sdk.entities.*;
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
class RaceStageCIImpl implements StageCI, ExportableCacheItem {
    private static final Logger logger = LoggerFactory.getLogger(RaceStageCIImpl.class);

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
     * A {@link List} of competitorIds that participate in the sport event
     * associated with the current instance
     */
    private List<URN> competitorIds;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<URN, ReferenceIdCI> competitorsReferences;

    /**
     * The {@link URN} specifying the id of the parent stage
     */
    private URN parentStageId;

    /**
     * The associated category id (extracted from the parent tournament - if available
     */
    private URN categoryId;

    /**
     * A {@link VenueCI} instance representing a venue where the sport event associated with the
     * current instance will take place
     */
    private VenueCI venue;

    /**
     * A {@link SportEventConditionsCI} instance representing live conditions of the sport event associated with the current instance
     */
    private SportEventConditionsCI conditions;

    /**
     * A {@link List} of {@link ChildRaceCI} representing child races of the race represented by the current instance
     */
    private List<URN> childStagesIds;

    /**
     * A {@link Map} storing the available sport event names
     */
    private final Map<Locale, String> sportEventNames = Maps.newConcurrentMap();

    /**
     * An indication of what kind of stage it is
     */
    private StageType stageType;

    /**
     * A {@link List} of locales that are already fully cached
     */
    private List<Locale> loadedSummaryLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A {@link List} of locales that are already fully cached
     */
    private List<Locale> loadedFixtureLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A {@link List} indicating which event competitors translations were already fetched
     */
    private final List<Locale> loadedCompetitorLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * An {@link ReentrantLock} used to synchronize summary request operations
     */
    private final ReentrantLock summaryRequest = new ReentrantLock();

    /**
     * An {@link ReentrantLock} used to synchronize fixture request operations
     */
    private final ReentrantLock fixtureRequest = new ReentrantLock();

    /**
     * The {@link Cache} used to cache the sport events fixture timestamps
     */
    private final Cache<URN, Date> fixtureTimestampCache;

    /**
     * The {@link Boolean} indicating if the start time to be determined is set
     */
    private Boolean startTimeTbd;

    /**
     * The {@link URN} indicating the replacement sport event
     */
    private URN replacedBy;

    /**
     * The liveOdds
     */
    private String liveOdds;

    /**
     * An indication of what kind of event it is
     */
    private SportEventType sportEventType;

    /**
     * List of additional parents ids
     */
    List<URN> additionalParentIds;

    RaceStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPISportEvent endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
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

        this.bookingStatus = BookingStatus.getLiveBookingStatus(endpointData.getLiveodds());
        this.scheduled = endpointData.getScheduled() == null ? null : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? null : SdkHelper.toDate(endpointData.getScheduledEnd());
        this.startTimeTbd = endpointData.isStartTimeTbd();
        this.replacedBy = endpointData.getReplacedBy() == null
                ? null
                : URN.parse(endpointData.getReplacedBy());

        this.parentStageId = endpointData.getParent() != null
                ? URN.parse(endpointData.getParent().getId())
                : (endpointData.getTournament() != null && !endpointData.getTournament().getId().equals(endpointData.getId())
                    ? URN.parse(endpointData.getTournament().getId())
                    : null);

        this.categoryId = endpointData.getTournament() != null && endpointData.getTournament().getCategory() != null ?
                URN.parse(endpointData.getTournament().getCategory().getId()) :
                null;

        this.competitorIds = endpointData.getCompetitors() == null ? null :
                endpointData.getCompetitors().getCompetitor().stream()
                        .map(c -> URN.parse(c.getId())).collect(Collectors.toList());
        loadedCompetitorLocales.add(dataLocale);

        this.childStagesIds = endpointData.getRaces() == null ? null :
                endpointData.getRaces().getSportEvent().stream()
                        .map(se -> URN.parse(se.getId())).collect(Collectors.toList());

        if (endpointData.getName() != null) {
            this.sportEventNames.put(defaultLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(defaultLocale, "");
        }

        this.fixtureTimestampCache = fixtureTimestampCache;

        this.stageType = StageType.mapFromApiValue(endpointData.getStageType());

        this.liveOdds = endpointData.getLiveodds();

        this.sportEventType = SportEventType.mapFromApiValue(endpointData.getType());
        if(this.sportEventType == null && endpointData.getTournament() != null && this.id.toString().equals(endpointData.getTournament().getId())){
            this.sportEventType = SportEventType.PARENT;
        }

        if(endpointData.getAdditionalParents() != null && !endpointData.getAdditionalParents().getParent().isEmpty()){
            additionalParentIds = new ArrayList<>();
            endpointData.getAdditionalParents().getParent().forEach(f-> additionalParentIds.add(URN.parse(f.getId())));
        }
        else{
            additionalParentIds = null;
        }
    }

    RaceStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIStageSummaryEndpoint endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData.getSportEvent(), dataLocale, fixtureTimestampCache);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.venue =(endpointData.getSportEvent().getSportEventConditions() != null && endpointData.getSportEvent().getSportEventConditions().getVenue() != null) ?
                new VenueCI(endpointData.getSportEvent().getSportEventConditions().getVenue(), dataLocale) :
                null;

        this.conditions = endpointData.getSportEvent().getSportEventConditions() == null ?
                null :
                new SportEventConditionsCI(endpointData.getSportEvent().getSportEventConditions(), dataLocale);

        loadedSummaryLocales.add(dataLocale);
    }

    RaceStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIFixture endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, (SAPISportEvent) endpointData, dataLocale, fixtureTimestampCache);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.venue = endpointData.getVenue() == null ? null : new VenueCI(endpointData.getVenue(), dataLocale);

        loadedFixtureLocales.add(dataLocale);
    }

    RaceStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPISportEventChildren.SAPISportEvent endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
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

        if (endpointData.getName() != null) {
            this.sportEventNames.put(defaultLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(defaultLocale, "");
        }

        this.scheduled = endpointData.getScheduled() == null ? null : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? null : SdkHelper.toDate(endpointData.getScheduledEnd());
        this.startTimeTbd = endpointData.isStartTimeTbd();
        this.replacedBy = endpointData.getReplacedBy() == null
                ? null
                : URN.parse(endpointData.getReplacedBy());

        this.fixtureTimestampCache = fixtureTimestampCache;

        this.stageType = StageType.mapFromApiValue(endpointData.getStageType());

        this.liveOdds = null;

        this.sportEventType = SportEventType.mapFromApiValue(endpointData.getType());

         additionalParentIds = null;
    }

    RaceStageCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIParentStage endpointData, Locale dataLocale, Cache<URN, Date> fixtureTimestampCache) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.fixtureTimestampCache = fixtureTimestampCache;
        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;

        this.scheduled = endpointData.getScheduled() == null ? null : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? null : SdkHelper.toDate(endpointData.getScheduledEnd());
        this.startTimeTbd = endpointData.isStartTimeTbd();
        this.replacedBy = endpointData.getReplacedBy() == null ? null : URN.parse(endpointData.getReplacedBy());

        if (endpointData.getName() != null) {
            this.sportEventNames.put(defaultLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(defaultLocale, "");
        }

        this.stageType = StageType.mapFromApiValue(endpointData.getStageType());

        this.sportEventType = SportEventType.mapFromApiValue(endpointData.getType());
    }

    RaceStageCIImpl(ExportableRaceStageCI exportable, DataRouterManager dataRouterManager, ExceptionHandlingStrategy exceptionHandlingStrategy, Cache<URN, Date> fixtureTimestampCache) {
        Preconditions.checkNotNull(exportable);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkNotNull(fixtureTimestampCache);

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.fixtureTimestampCache = fixtureTimestampCache;

        this.defaultLocale = exportable.getDefaultLocale();
        this.id = URN.parse(exportable.getId());
        this.bookingStatus = exportable.getBookingStatus();
        this.scheduled = exportable.getScheduled();
        this.scheduledEnd = exportable.getScheduledEnd();
        this.competitorIds = exportable.getCompetitorIds() != null ? exportable.getCompetitorIds().stream().map(URN::parse).collect(Collectors.toList()) : null;
        this.competitorsReferences = exportable.getCompetitorsReferences() != null ? exportable.getCompetitorsReferences().entrySet().stream().collect(Collectors.toMap(r -> URN.parse(r.getKey()), r -> new ReferenceIdCI(r.getValue()))) : null;
        this.parentStageId = exportable.getParentStageId() != null ? URN.parse(exportable.getParentStageId()) : null;
        this.categoryId = exportable.getCategoryId() != null ? URN.parse(exportable.getCategoryId()) : null;
        this.venue = exportable.getVenue() != null ? new VenueCI(exportable.getVenue()) : null;
        this.conditions = exportable.getConditions() != null ? new SportEventConditionsCI(exportable.getConditions()) : null;
        this.childStagesIds = exportable.getStagesIds() != null ? exportable.getStagesIds().stream().map(URN::parse).collect(Collectors.toList()) : null;
        this.sportEventNames.putAll(exportable.getNames());
        this.stageType = exportable.getStageType();
        this.loadedSummaryLocales.addAll(exportable.getLoadedSummaryLocales());
        this.loadedFixtureLocales.addAll(exportable.getLoadedFixtureLocales());
        this.loadedCompetitorLocales.addAll(exportable.getLoadedCompetitorLocales());
        this.startTimeTbd = exportable.getStartTimeTbd();
        this.replacedBy = exportable.getReplacedBy() != null ? URN.parse(exportable.getReplacedBy()) : null;
        this.liveOdds = exportable.getLiveOdds();
        this.sportEventType = exportable.getSportEventType();
        this.additionalParentIds = exportable.getAdditionalParentsIds() != null ?
                exportable.getAdditionalParentsIds().stream().map(URN::parse).collect(Collectors.toList()) : null;
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

        if (loadedSummaryLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(sportEventNames);
        }

        requestMissingSummaryData(locales, false);

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
        return loadedSummaryLocales.containsAll(localeList) && loadedFixtureLocales.containsAll(localeList);
    }

    /**
     * Returns the identifier of the stage parent
     *
     * @return the {@link URN} identifier of the parent stage if available; otherwise null
     */
    @Override
    public URN getParentStageId() {
        if (parentStageId != null) {
            return parentStageId;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return null;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return parentStageId;
    }

    /**
     * Returns a {@link List} of known child stages identifiers
     *
     * @return a {@link List} known child stages identifiers if available; otherwise null
     */
    @Override
    public List<URN> getStagesIds() {
        if (childStagesIds != null) {
            return ImmutableList.copyOf(childStagesIds);
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return null;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return childStagesIds == null ? null : ImmutableList.copyOf(childStagesIds);
    }

    /**
     * Returns a {@link StageType} indicating the type of the associated stage
     *
     * @return a {@link StageType} indicating the type of the associated stage
     */
    @Override
    public StageType getStageType() {
        if (stageType != null) {
            return stageType;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return null;
}
        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return stageType;
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

        if (!loadedSummaryLocales.isEmpty()) {
            return null;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return categoryId;
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return a {@link BookingStatus} enum member providing booking status of the current instance
     */
    @Override
    public BookingStatus getBookingStatus() {
        if (!loadedFixtureLocales.isEmpty()) {
            return bookingStatus;
        }

        requestMissingFixtureData(Collections.singletonList(defaultLocale));

        return bookingStatus;
    }

    /**
     * Returns a {@link List} of competitor ids that participate in the sport event
     * associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} in which the competitor data should be provided
     * @return a {@link List} of competitor ids that participate in the sport event
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
     * Fetch a {@link SportEventStatusDTO} via event summary
     */
    @Override
    public void fetchSportEventStatus() {
        requestMissingSummaryData(Collections.singletonList(defaultLocale), true);
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
            return null;
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
            return null;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return scheduledEnd;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled (no api request is invoked)
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled; otherwise null;
     */
    @Override
    public Date getScheduledRaw() {
        return scheduled;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end (no api request is invoked)
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end; otherwise null;
     */
    @Override
    public Date getScheduledEndRaw() {
        return scheduledEnd;
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        if (startTimeTbd != null) {
            return startTimeTbd;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return startTimeTbd;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return startTimeTbd;
    }

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    @Override
    public URN getReplacedBy() {
        if (replacedBy != null) {
            return replacedBy;
        }

        if (!loadedSummaryLocales.isEmpty()) {
            return null;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale), false);

        return replacedBy;
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPIFixture) {
            internalMerge((SAPIFixture) endpointData, dataLocale);
        } else if (endpointData instanceof SAPISportEvent) {
            internalMerge((SAPISportEvent) endpointData, dataLocale, false);
        } else if (endpointData instanceof SAPIStageSummaryEndpoint) {
            internalMerge((SAPIStageSummaryEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SAPISportEventChildren.SAPISportEvent) {
            internalMerge(((SAPISportEventChildren.SAPISportEvent) endpointData), dataLocale);
        } else if (endpointData instanceof SAPIParentStage) {
            internalMerge(((SAPIParentStage) endpointData), dataLocale);
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
     * Returns list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    @Override
    public Map<URN, ReferenceIdCI> getCompetitorsReferences() {
        if(competitorsReferences == null || loadedCompetitorLocales.isEmpty()) {
            requestMissingSummaryData(Collections.singletonList(defaultLocale), false);
        }

        return competitorsReferences == null
                ? null
                : ImmutableMap.copyOf(competitorsReferences);
    }

    @Override
    public String getLiveOdds(List<Locale> locales) {
        if (liveOdds != null) {
            return liveOdds;
        }

        if (loadedFixtureLocales.containsAll(locales)) {
            return liveOdds;
        }

        requestMissingSummaryData(locales, false);

        return liveOdds;
    }

    @Override
    public SportEventType getSportEventType(List<Locale> locales) {
        if (sportEventType != null) {
            return sportEventType;
        }

        if (loadedFixtureLocales.containsAll(locales)) {
            return sportEventType;
        }

        requestMissingSummaryData(locales, false);

        return sportEventType;
    }

    @Override
    public List<URN> getAdditionalParentStages(List<Locale> locales) {
        if (additionalParentIds != null) {
            return additionalParentIds;
        }

        if (loadedFixtureLocales.containsAll(locales)) {
            return additionalParentIds;
        }

        requestMissingSummaryData(locales, false);

        return additionalParentIds;
    }

    /**
     * Merges the current instance with the provided {@link SAPIStageSummaryEndpoint}
     *
     * @param endpointData the endpoint data which should be merged into the current instance
     * @param dataLocale the locale in which the data is provided
     */
    private void internalMerge(SAPIStageSummaryEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (loadedSummaryLocales.contains(dataLocale)) {
            logger.info("RaceStageCI [{}] already contains summary info for language {}", id, dataLocale);
        }

        internalMerge(endpointData.getSportEvent(), dataLocale, false);

        if (endpointData.getSportEvent().getSportEventConditions() != null) {
            if (endpointData.getSportEvent().getSportEventConditions().getVenue() != null) {
                if (venue == null) {
                    venue = new VenueCI(endpointData.getSportEvent().getSportEventConditions().getVenue(), dataLocale);
                } else {
                    venue.merge(endpointData.getSportEvent().getSportEventConditions().getVenue(), dataLocale);
                }
            }

            if (conditions == null) {
                conditions = new SportEventConditionsCI(endpointData.getSportEvent().getSportEventConditions(), dataLocale);
            } else {
                conditions.merge(endpointData.getSportEvent().getSportEventConditions(), dataLocale);
            }
        }

        loadedSummaryLocales.add(dataLocale);
    }

    /**
     * Merges the current instance with the {@link SAPIFixture}
     *
     * @param endpointData - the {@link SAPIFixture} containing the data to be merged
     * @param dataLocale - the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPIFixture endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (loadedFixtureLocales.contains(dataLocale)) {
            logger.info("RaceStageCI [{}] already contains fixture info for language {}", id, dataLocale);
        }

        internalMerge(endpointData, dataLocale, true);

        if (endpointData.getVenue() != null) {
            if (venue == null) {
                venue = new VenueCI(endpointData.getVenue(), dataLocale);
            } else {
                venue.merge(endpointData.getVenue(), dataLocale);
            }
        }

        loadedFixtureLocales.add(dataLocale);
    }

    /**
     * Merges the current instance with the {@link SAPISportEvent}
     *
     * @param sportEvent - the {@link SAPISportEvent} containing the data to be merged
     * @param locale - the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPISportEvent sportEvent, Locale locale, boolean isFixtureEndpoint) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(locale);

        // booking status can be obtained only from the fixture endpoint, so we need to be careful on when to merge it
        // re-cache it only if the fetch is from a fixture endpoint or if it was null so we set the default value
        if (isFixtureEndpoint || bookingStatus == null) {
            bookingStatus = BookingStatus.getLiveBookingStatus(sportEvent.getLiveodds());
        }

        this.scheduled = sportEvent.getScheduled() == null ? this.scheduled : SdkHelper.toDate(sportEvent.getScheduled());
        this.scheduledEnd = sportEvent.getScheduledEnd() == null ? this.scheduledEnd : SdkHelper.toDate(sportEvent.getScheduledEnd());
        this.startTimeTbd = sportEvent.isStartTimeTbd() == null ? this.startTimeTbd : sportEvent.isStartTimeTbd();
        this.replacedBy = sportEvent.getReplacedBy() == null ? this.replacedBy : URN.parse(sportEvent.getReplacedBy());

        if (sportEvent.getParent() != null) {
            parentStageId = URN.parse(sportEvent.getParent().getId());
        } else if (sportEvent.getTournament() != null) {
            parentStageId = URN.parse(sportEvent.getTournament().getId());
        }

        if (sportEvent.getTournament() != null && sportEvent.getTournament().getCategory() != null) {
            categoryId = URN.parse(sportEvent.getTournament().getCategory().getId());
        }

        if (sportEvent.getCompetitors() != null && sportEvent.getCompetitors().getCompetitor() != null) {
            competitorIds = sportEvent.getCompetitors().getCompetitor().stream()
                    .map(c -> URN.parse(c.getId())).collect(Collectors.toList());
            loadedCompetitorLocales.add(locale);
            competitorsReferences = SdkHelper.parseTeamCompetitorsReferences(sportEvent.getCompetitors().getCompetitor(), competitorsReferences);
        }

        if (sportEvent.getRaces() != null) {
            if (childStagesIds == null) {
                childStagesIds = new ArrayList<>(sportEvent.getRaces().getSportEvent().size());
            }
            sportEvent.getRaces().getSportEvent().forEach(se -> {
                URN seId = URN.parse(se.getId());
                if (!childStagesIds.contains(seId)) {
                    childStagesIds.add(seId);
                }
            });
        }

        if (sportEvent.getName() != null) {
            this.sportEventNames.put(locale, sportEvent.getName());
        }
        else{
            this.sportEventNames.put(locale, "");
        }

        if (sportEvent.getStageType() != null) {
            this.stageType = StageType.mapFromApiValue(sportEvent.getStageType());
        }

        if(sportEvent.getLiveodds() != null){
            this.liveOdds = sportEvent.getLiveodds();
        }

        if (sportEvent.getType() != null) {
            this.sportEventType = SportEventType.mapFromApiValue(sportEvent.getType());
        }
        if(this.sportEventType == null && sportEvent.getTournament() != null && this.id.toString().equals(sportEvent.getTournament().getId())){
            this.sportEventType = SportEventType.PARENT;
        }

        if(sportEvent.getAdditionalParents() != null && !sportEvent.getAdditionalParents().getParent().isEmpty()) {
            additionalParentIds = new ArrayList<>();
            sportEvent.getAdditionalParents().getParent().forEach(f-> additionalParentIds.add(URN.parse(f.getId())));
        }
    }

    /**
     * Merges the current instance with the {@link SAPISportEvent}
     *
     * @param parentStage - the {@link SAPISportEvent} containing the data to be merged
     * @param locale - the {@link Locale} in which the data is provided
     */
    private void internalMerge(SAPIParentStage parentStage, Locale locale) {
        Preconditions.checkNotNull(parentStage);
        Preconditions.checkNotNull(locale);

        this.scheduled = parentStage.getScheduled() == null ? this.scheduled : SdkHelper.toDate(parentStage.getScheduled());
        this.scheduledEnd = parentStage.getScheduledEnd() == null ? this.scheduledEnd : SdkHelper.toDate(parentStage.getScheduledEnd());
        this.startTimeTbd = parentStage.isStartTimeTbd() == null ? this.startTimeTbd : parentStage.isStartTimeTbd();
        this.replacedBy = parentStage.getReplacedBy() == null ? this.replacedBy : URN.parse(parentStage.getReplacedBy());

        if (parentStage.getName() != null) {
            this.sportEventNames.put(locale, parentStage.getName());
        }
        else{
            this.sportEventNames.put(locale, "");
        }

        if (parentStage.getStageType() != null) {
            this.stageType = StageType.mapFromApiValue(parentStage.getStageType());
        }

        if (parentStage.getType() != null) {
            this.sportEventType = SportEventType.mapFromApiValue(parentStage.getType());
        }
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

        this.scheduled = endpointData.getScheduled() == null ? this.scheduled : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd = endpointData.getScheduledEnd() == null ? this.scheduledEnd : SdkHelper.toDate(endpointData.getScheduledEnd());
        this.startTimeTbd = endpointData.isStartTimeTbd() == null ? this.startTimeTbd : endpointData.isStartTimeTbd();
        this.replacedBy = endpointData.getReplacedBy() == null ? this.replacedBy : URN.parse(endpointData.getReplacedBy());

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        }
        else{
            this.sportEventNames.put(dataLocale, "");
        }
        if (endpointData.getStageType() != null) {
            this.stageType = StageType.mapFromApiValue(endpointData.getStageType());
        }

        if (endpointData.getType() != null) {
            this.sportEventType = SportEventType.mapFromApiValue(endpointData.getType());
        }
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

            logger.debug("Fetching stage fixtures for eventId='{}' for languages '{}'",
                    id, missingLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", ")));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestFixtureEndpoint(l, id, fixtureTimestampCache.getIfPresent(id) == null,this);
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

            logger.debug("Fetching stage summary for eventId='{}' for languages '{}'",
                    id, missingLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", ")));

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

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw && !SdkHelper.isDataNotFound(e)) {
            if (e == null) {
                throw new ObjectNotFoundException("RaceStageCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing RaceStageCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing RaceStageCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    @Override
    public ExportableCI export() {
        return new ExportableRaceStageCI(
                id.toString(),
                new HashMap<>(sportEventNames),
                scheduled,
                scheduledEnd,
                startTimeTbd,
                replacedBy.toString(),
                bookingStatus,
                competitorIds != null ? competitorIds.stream().map(URN::toString).collect(Collectors.toList()) : null,
                venue != null ? venue.export() : null,
                conditions != null ? conditions.export() : null,
                competitorsReferences != null ? competitorsReferences.entrySet().stream().collect(Collectors.toMap(c -> c.getKey().toString(), c -> c.getValue().getReferenceIds())) : null,
                parentStageId != null ? parentStageId.toString() : null,
                childStagesIds != null ? childStagesIds.stream().map(URN::toString).collect(Collectors.toList()) : null,
                stageType,
                categoryId != null ? categoryId.toString() : null,
                defaultLocale,
                new ArrayList<>(loadedSummaryLocales),
                new ArrayList<>(loadedFixtureLocales),
                new ArrayList<>(loadedCompetitorLocales),
                liveOdds,
                sportEventType,
                additionalParentIds != null ? additionalParentIds.stream().map(URN::toString).collect(Collectors.toList()) : null
        );
    }
}