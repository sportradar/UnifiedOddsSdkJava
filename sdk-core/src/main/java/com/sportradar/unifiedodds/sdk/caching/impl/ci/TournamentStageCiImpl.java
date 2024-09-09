/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.StageCi;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCi;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCi;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableTournamentStageCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 19/10/2017.
 * TournamentStage cache item
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "DeclarationOrder",
        "LineLength",
        "NPathComplexity",
        "ReturnCount",
        "VisibilityModifier",
    }
)
class TournamentStageCiImpl implements StageCi, ExportableCacheItem {

    private static final Logger logger = LoggerFactory.getLogger(TournamentStageCiImpl.class);

    /**
     * A {@link Locale} specifying the default language
     */
    private final Locale defaultLocale;

    /**
     * An {@link Urn} specifying the id of the associated sport event
     */
    private final Urn id;

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
    private List<Urn> competitorIds;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<Urn, ReferenceIdCi> competitorsReferences;

    /**
     * A {@link Map} storing the available sport event names
     */
    private final Map<Locale, String> sportEventNames = Maps.newConcurrentMap();

    /**
     * A {@link Urn} specifying the id of the parent category
     */
    private Urn categoryId;

    /**
     * The stage type
     */
    private StageType stageType;

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
    List<Urn> additionalParentIds;

    /**
     * The {@link Boolean} indicating if the start time to be determined is set
     */
    private Boolean startTimeTbd;

    /**
     * The {@link Urn} indicating the replacement sport event
     */
    private Urn replacedBy;

    /**
     * A {@link List} of locales that are already fully cached - only when the full tournament info endpoint is cached
     */
    private List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * An {@link ReentrantLock} used to synchronize summary request operations
     */
    private final ReentrantLock fetchRequestLock = new ReentrantLock();

    TournamentStageCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiTournament endpointData,
        Locale dataLocale
    ) {
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
        } else {
            this.sportEventNames.put(dataLocale, "");
        }

        this.categoryId = Urn.parse(endpointData.getCategory().getId());
        this.scheduled =
            endpointData.getScheduled() == null ? null : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd =
            endpointData.getScheduledEnd() == null ? null : SdkHelper.toDate(endpointData.getScheduledEnd());

        if (
            (this.scheduled == null || this.scheduledEnd == null) &&
            endpointData.getTournamentLength() != null
        ) {
            SapiTournamentLength tournamentLength = endpointData.getTournamentLength();
            this.scheduled =
                tournamentLength.getStartDate() == null
                    ? null
                    : SdkHelper.toDate(tournamentLength.getStartDate());
            this.scheduledEnd =
                tournamentLength.getEndDate() == null
                    ? null
                    : SdkHelper.toDate(tournamentLength.getEndDate());
        }
        this.stageType = null;
        this.liveOdds = null;
        this.sportEventType = null;
        this.additionalParentIds = null;
    }

    TournamentStageCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiTournamentInfoEndpoint endpointData,
        Locale dataLocale
    ) {
        this(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData.getTournament(),
            dataLocale
        );
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        SapiCompetitors endpointCompetitors = endpointData.getCompetitors() != null
            ? endpointData.getCompetitors()
            : endpointData.getTournament().getCompetitors();
        this.competitorIds =
            endpointCompetitors == null
                ? null
                : Collections.synchronizedList(
                    endpointCompetitors
                        .getCompetitor()
                        .stream()
                        .map(c -> Urn.parse(c.getId()))
                        .collect(Collectors.toList())
                );

        if (
            this.sportEventType == null &&
            endpointData.getTournament() != null &&
            this.id.toString().equals(endpointData.getTournament().getId())
        ) {
            this.sportEventType = SportEventType.PARENT;
        }

        cachedLocales.add(dataLocale);
    }

    TournamentStageCiImpl(
        ExportableTournamentStageCi exportable,
        DataRouterManager dataRouterManager,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(exportable);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.defaultLocale = exportable.getDefaultLocale();
        this.id = Urn.parse(exportable.getId());
        this.scheduled = exportable.getScheduled();
        this.scheduledEnd = exportable.getScheduledEnd();
        this.competitorIds =
            exportable.getCompetitorIds() != null
                ? exportable.getCompetitorIds().stream().map(Urn::parse).collect(Collectors.toList())
                : null;
        this.competitorsReferences =
            exportable.getCompetitorsReferences() != null
                ? exportable
                    .getCompetitorsReferences()
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(r -> Urn.parse(r.getKey()), r -> new ReferenceIdCi(r.getValue()))
                    )
                : null;
        this.sportEventNames.putAll(exportable.getNames());
        this.categoryId = exportable.getCategoryId() != null ? Urn.parse(exportable.getCategoryId()) : null;
        this.cachedLocales.addAll(exportable.getCachedLocales());
        this.stageType = exportable.getStageType();
        this.liveOdds = exportable.getLiveOdds();
        this.sportEventType = exportable.getSportEventType();
        //        exportable.getParentStageId() // is always null
        this.additionalParentIds =
            exportable.getAdditionalParentsIds() != null
                ? exportable.getAdditionalParentsIds().stream().map(Urn::parse).collect(Collectors.toList())
                : null;
    }

    /**
     * Returns the {@link Urn} representing id of the related entity
     *
     * @return the {@link Urn} representing id of the related entity
     */
    @Override
    public Urn getId() {
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
     * @return the {@link Urn} identifier of the parent stage if available; otherwise null
     */
    @Override
    public Urn getParentStageId() {
        // tournament info endpoint can not have a "parent" stage
        return null;
    }

    /**
     * Returns a {@link List} of known child stages identifiers
     *
     * @return a {@link List} known child stages identifiers if available; otherwise null
     */
    @Override
    public List<Urn> getStagesIds() {
        List<Urn> stageIds = null;
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
        return stageType;
    }

    /**
     * Returns the {@link Urn} specifying the id of the parent category
     *
     * @return the {@link Urn} specifying the id of the parent category
     */
    @Override
    public Urn getCategoryId() {
        if (categoryId != null) {
            return categoryId;
        }

        if (!cachedLocales.isEmpty()) {
            return null;
        }

        requestMissingStageTournamentData(Collections.singletonList(defaultLocale));

        return categoryId;
    }

    @Override
    public String getLiveOdds(List<Locale> locales) {
        return liveOdds;
    }

    @Override
    public SportEventType getSportEventType(List<Locale> locales) {
        return sportEventType;
    }

    @Override
    public List<Urn> getAdditionalParentStages(List<Locale> locales) {
        return additionalParentIds;
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
    public List<Urn> getCompetitorIds(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
        }

        requestMissingStageTournamentData(locales);

        return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
    }

    /**
     * Returns a {@link VenueCi} instance representing a venue where the sport event associated with the
     * current instance will take place
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link VenueCi} instance representing a venue where the associated sport event
     */
    @Override
    public VenueCi getVenue(List<Locale> locales) {
        // no venue info
        return null;
    }

    /**
     * Returns a {@link SportEventConditionsCi} instance representing live conditions of the sport event associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SportEventConditionsCi} instance representing live conditions of the sport event associated with the current instance
     */
    @Override
    public SportEventConditionsCi getConditions(List<Locale> locales) {
        // no conditions info
        return null;
    }

    /**
     * Fetch a {@link SportEventStatusDto} via event summary
     */
    @Override
    public void fetchSportEventStatus() {
        // tournament stages does not have sportEventStatus
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
            return null;
        }

        requestMissingStageTournamentData(Collections.singletonList(defaultLocale));

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
    public Optional<Boolean> isStartTimeTbd() {
        return startTimeTbd == null ? Optional.empty() : Optional.of(startTimeTbd);
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        return replacedBy;
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SapiTournamentInfoEndpoint) {
            internalMerge((SapiTournamentInfoEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SapiTournament) {
            internalMerge((SapiTournament) endpointData, dataLocale);
        } else if (endpointData instanceof SapiParentStage) {
            internalMerge((SapiParentStage) endpointData, dataLocale);
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
     * Returns list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    @Override
    public Map<Urn, ReferenceIdCi> getCompetitorsReferences() {
        if (competitorsReferences == null || cachedLocales.isEmpty()) {
            requestMissingStageTournamentData(Collections.singletonList(defaultLocale));
        }

        return competitorsReferences == null ? null : ImmutableMap.copyOf(competitorsReferences);
    }

    private void internalMerge(SapiTournamentInfoEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (cachedLocales.contains(dataLocale)) {
            logger.info(
                "TournamentStageCI [{}] already contains TournamentInfo data for language {}",
                id,
                dataLocale
            );
        }

        SapiCompetitors endpointCompetitors = endpointData.getCompetitors() != null
            ? endpointData.getCompetitors()
            : endpointData.getTournament().getCompetitors();

        if (endpointCompetitors != null) {
            this.competitorIds =
                Collections.synchronizedList(
                    endpointCompetitors
                        .getCompetitor()
                        .stream()
                        .map(c -> Urn.parse(c.getId()))
                        .collect(Collectors.toList())
                );
        }

        internalMerge(endpointData.getTournament(), dataLocale);

        cachedLocales.add(dataLocale);
    }

    private void internalMerge(SapiTournament endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.sportEventNames.put(dataLocale, endpointData.getName());
        } else {
            this.sportEventNames.put(dataLocale, "");
        }

        if (endpointData.getCategory() != null) {
            this.categoryId = Urn.parse(endpointData.getCategory().getId());
        }

        this.scheduled =
            endpointData.getScheduled() == null
                ? this.scheduled
                : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd =
            endpointData.getScheduledEnd() == null
                ? this.scheduledEnd
                : SdkHelper.toDate(endpointData.getScheduledEnd());

        if (
            (this.scheduled == null || this.scheduledEnd == null) &&
            endpointData.getTournamentLength() != null
        ) {
            SapiTournamentLength tournamentLength = endpointData.getTournamentLength();
            this.scheduled =
                tournamentLength.getStartDate() == null
                    ? this.scheduled
                    : SdkHelper.toDate(tournamentLength.getStartDate());
            this.scheduledEnd =
                tournamentLength.getEndDate() == null
                    ? this.scheduledEnd
                    : SdkHelper.toDate(tournamentLength.getEndDate());
        }
    }

    private void internalMerge(SapiParentStage endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.scheduled =
            endpointData.getScheduled() == null
                ? this.scheduled
                : SdkHelper.toDate(endpointData.getScheduled());
        this.scheduledEnd =
            endpointData.getScheduledEnd() == null
                ? this.scheduledEnd
                : SdkHelper.toDate(endpointData.getScheduledEnd());
        this.startTimeTbd =
            endpointData.isStartTimeTbd() == null ? this.startTimeTbd : endpointData.isStartTimeTbd();
        this.replacedBy =
            endpointData.getReplacedBy() == null ? this.replacedBy : Urn.parse(endpointData.getReplacedBy());

        if (endpointData.getName() != null) {
            this.sportEventNames.put(defaultLocale, endpointData.getName());
        } else {
            this.sportEventNames.put(defaultLocale, "");
        }

        if (endpointData.getStageType() != null) {
            this.stageType = StageType.mapFromApiValue(endpointData.getStageType());
        }
        if (endpointData.getType() != null) {
            this.sportEventType = SportEventType.mapFromApiValue(endpointData.getType());
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

            String localesStr = missingLocales
                .stream()
                .map(Locale::getLanguage)
                .collect(Collectors.joining(", "));
            logger.debug(
                "Fetching missing stage tournament data for id='{}' for languages '{}'",
                id,
                localesStr
            );

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
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw && !SdkHelper.isDataNotFound(e)) {
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

    @Override
    public ExportableCi export() {
        return new ExportableTournamentStageCi(
            id.toString(),
            new HashMap<>(sportEventNames),
            scheduled,
            scheduledEnd,
            null,
            null,
            BookingStatus.Unavailable,
            competitorIds != null
                ? competitorIds.stream().map(Urn::toString).collect(Collectors.toList())
                : null,
            null,
            null,
            competitorsReferences != null
                ? competitorsReferences
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(c -> c.getKey().toString(), c -> c.getValue().getReferenceIds())
                    )
                : null,
            null,
            null,
            stageType,
            categoryId != null ? categoryId.toString() : null,
            defaultLocale,
            new ArrayList<>(cachedLocales),
            liveOdds,
            sportEventType,
            null
        );
    }
}
