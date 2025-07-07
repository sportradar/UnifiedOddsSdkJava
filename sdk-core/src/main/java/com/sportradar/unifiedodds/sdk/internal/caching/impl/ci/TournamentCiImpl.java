/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl.ci;

import static com.sportradar.unifiedodds.sdk.internal.caching.ExecutionPath.TIME_CRITICAL;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import com.sportradar.unifiedodds.sdk.internal.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.*;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableTournamentCi;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 19/10/2017.
 * Tournament cache item
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "IllegalCatch",
        "JavaNCSS",
        "LambdaBodyLength",
        "LineLength",
        "MethodLength",
        "NPathComplexity",
        "NestedIfDepth",
        "ReturnCount",
    }
)
class TournamentCiImpl implements TournamentCi, ExportableCacheItem {

    private static final Logger logger = LoggerFactory.getLogger(TournamentCiImpl.class);

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
     * A {@link Map} containing translated names of the item
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Urn} specifying the id of the parent category
     */
    private Urn categoryId;

    /**
     * A {@link Date} specifying the scheduled start time of the associated tournament or
     * a null reference if start time is not known
     */
    private Date scheduled;

    /**
     * A {@link Date} specifying the scheduled end time of the associated tournament or
     * a null reference if end time is not known
     */
    private Date scheduledEnd;

    /**
     * A {@link SeasonCi} representing the current season of the tournament
     */
    private SeasonCi currentSeason;

    /**
     * A {@link SeasonCi} representing the season of the tournament endpoint
     */
    private SeasonCi season;

    /**
     * A {@link SeasonCoverageCi} containing information about the tournament coverage
     */
    private SeasonCoverageCi seasonCoverage;

    /**
     * A {@link TournamentCoverageCi} instance describing the current tournament coverage
     */
    private TournamentCoverageCi tournamentCoverage;

    /**
     * A list of groups related to the current instance
     */
    private List<GroupCi> groups;

    /**
     * The round related to the current instance
     */
    private CompleteRoundCi round;

    /**
     * A {@link List} of associated tournament competitors
     */
    private List<Urn> competitorIds;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<Urn, ReferenceIdCi> competitorsReferences;

    /**
     * An indication if the associated season ids were loaded
     */
    private boolean associatedSeasonIdsLoaded;

    /**
     * A {@link List} of associated season ids
     */
    private List<Urn> associatedSeasonIds;

    /**
     * A {@link List} of locales that are already fully cached - only when the full tournament info endpoint is cached
     */
    private List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A lock used to synchronize api requests
     */
    private final ReentrantLock dataRequestLock = new ReentrantLock();

    /**
     * A {@link Boolean} specifying if the tournament is exhibition game
     */
    private Boolean exhibitionGames;

    TournamentCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    TournamentCiImpl(
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

        this.round =
            endpointData.getRound() == null
                ? null
                : new CompleteRoundCiImpl(endpointData.getRound(), dataLocale);
        this.season =
            endpointData.getSeason() != null ? new SeasonCi(endpointData.getSeason(), dataLocale) : null;

        this.groups =
            endpointData.getGroups() == null
                ? null
                : Collections.synchronizedList(
                    endpointData
                        .getGroups()
                        .getGroup()
                        .stream()
                        .map(g -> new GroupCi(g, dataLocale))
                        .collect(Collectors.toList())
                );

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
            competitorsReferences =
                SdkHelper.parseCompetitorsReferences(
                    endpointCompetitors.getCompetitor(),
                    competitorsReferences
                );
        } else {
            this.competitorIds = null;
            this.competitorsReferences = null;
        }

        this.tournamentCoverage =
            endpointData.getCoverageInfo() == null
                ? null
                : new TournamentCoverageCi(endpointData.getCoverageInfo());

        cachedLocales.add(dataLocale);
    }

    TournamentCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiTournamentExtended endpointData,
        Locale dataLocale
    ) {
        this(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            (SapiTournament) endpointData,
            dataLocale
        );
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.currentSeason =
            endpointData.getCurrentSeason() == null
                ? null
                : new SeasonCi(endpointData.getCurrentSeason(), dataLocale);
        this.seasonCoverage =
            endpointData.getSeasonCoverageInfo() == null
                ? null
                : new SeasonCoverageCi(endpointData.getSeasonCoverageInfo());
    }

    TournamentCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiTournament endpointData,
        Locale dataLocale
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.names.put(dataLocale, endpointData.getName());
        } else {
            this.names.put(dataLocale, "");
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
    }

    TournamentCiImpl(
        ExportableTournamentCi exportable,
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
        this.names.putAll(exportable.getNames());
        this.categoryId = exportable.getCategoryId() != null ? Urn.parse(exportable.getCategoryId()) : null;
        this.scheduled = exportable.getScheduled();
        this.scheduledEnd = exportable.getScheduledEnd();
        this.currentSeason =
            exportable.getCurrentSeason() != null ? new SeasonCi(exportable.getCurrentSeason()) : null;
        this.season = exportable.getSeason() != null ? new SeasonCi(exportable.getSeason()) : null;
        this.seasonCoverage =
            exportable.getSeasonCoverage() != null
                ? new SeasonCoverageCi(exportable.getSeasonCoverage())
                : null;
        this.tournamentCoverage =
            exportable.getTournamentCoverage() != null
                ? new TournamentCoverageCi(exportable.getTournamentCoverage())
                : null;
        this.groups =
            exportable.getGroups() != null
                ? Collections.synchronizedList(
                    exportable.getGroups().stream().map(GroupCi::new).collect(Collectors.toList())
                )
                : null;
        this.round = exportable.getRound() != null ? new CompleteRoundCiImpl(exportable.getRound()) : null;
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
        this.associatedSeasonIdsLoaded = exportable.isAssociatedSeasonIdsLoaded();
        this.associatedSeasonIds =
            exportable.getAssociatedSeasonIds() != null
                ? exportable.getAssociatedSeasonIds().stream().map(Urn::parse).collect(Collectors.toList())
                : null;
        this.cachedLocales.addAll(exportable.getCachedLocales());
        this.exhibitionGames = exportable.getExhibitionGames();
    }

    /**
     * Returns the {@link Urn} specifying the id of the parent category
     *
     * @return the {@link Urn} specifying the id of the parent category
     */
    @Override
    public Urn getCategoryId() {
        if (categoryId != null || !cachedLocales.isEmpty()) {
            return categoryId;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

        return categoryId;
    }

    /**
     * Returns a {@link SeasonCi} representing the current season of the tournament
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SeasonCi} representing the current season of the tournament
     */
    @Override
    public SeasonCi getCurrentSeason(List<Locale> locales) {
        if (currentSeason != null && currentSeason.hasTranslationsFor(locales)) {
            return currentSeason;
        }

        if (cachedLocales.containsAll(locales)) {
            return currentSeason;
        }

        requestMissingTournamentData(locales);

        return currentSeason;
    }

    /**
     * Returns a {@link SeasonCoverageCi} containing information about the tournament coverage
     *
     * @return a {@link SeasonCoverageCi} containing information about the tournament coverage
     */
    @Override
    public SeasonCoverageCi getSeasonCoverage() {
        if (seasonCoverage != null || !cachedLocales.isEmpty()) {
            return seasonCoverage;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

        return seasonCoverage;
    }

    /**
     * Returns the associated endpoint season
     *
     * @param locales the locales in which the data should be available
     * @return the associated season cache item
     */
    @Override
    public SeasonCi getSeason(List<Locale> locales) {
        if (season != null && season.hasTranslationsFor(locales)) {
            return season;
        }

        if (cachedLocales.containsAll(locales)) {
            return season;
        }

        requestMissingTournamentData(locales);

        return season;
    }

    /**
     * Returns a {@link List} of the associated tournament competitor ids
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - if available a {@link List} of the associated tournament competitor ids; otherwise null
     */
    @Override
    public List<Urn> getCompetitorIds(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return prepareCompetitorList(competitorIds, () -> getGroups(locales));
        }

        requestMissingTournamentData(locales);

        return prepareCompetitorList(competitorIds, () -> getGroups(locales));
    }

    /**
     * Returns a list of groups related to the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a list of groups related to the current instance
     */
    @Override
    public List<GroupCi> getGroups(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return groups == null ? null : ImmutableList.copyOf(groups);
        }

        requestMissingTournamentData(locales);

        return groups == null ? null : ImmutableList.copyOf(groups);
    }

    /**
     * Returns the rounds related to the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return the rounds related to the current instance
     */
    @Override
    public RoundCi getRound(List<Locale> locales) {
        if (round != null && round.hasTranslationsFor(locales)) {
            return round;
        }

        if (cachedLocales.containsAll(locales)) {
            return round;
        }

        requestMissingTournamentData(locales);

        return round;
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
        if (!cachedLocales.isEmpty()) {
            return scheduled;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

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
        if (!cachedLocales.isEmpty()) {
            return scheduledEnd;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

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
        return Optional.empty();
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        return null;
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
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (names.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        requestMissingTournamentData(locales);

        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the current tournament coverage information
     *
     * @return a {@link TournamentCoverageCi} instance describing the current coverage indication
     */
    @Override
    public TournamentCoverageCi getTournamentCoverage() {
        if (!cachedLocales.isEmpty()) {
            return tournamentCoverage;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

        return tournamentCoverage;
    }

    /**
     * Returns a list of associated season identifiers
     *
     * @return a list of associated season identifiers
     */
    @Override
    public List<Urn> getSeasonIds() {
        if (associatedSeasonIdsLoaded) {
            return associatedSeasonIds;
        }

        requestAssociatedSeasonIds();

        return associatedSeasonIds;
    }

    /**
     * Returns list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    @Override
    public Map<Urn, ReferenceIdCi> getCompetitorsReferences() {
        if (cachedLocales.isEmpty()) {
            requestMissingTournamentData(Collections.singletonList(defaultLocale));
        }

        return prepareCompetitorReferences(
            competitorsReferences,
            () -> getGroups(Collections.singletonList(defaultLocale))
        );
    }

    /**
     * Returns the {@link Boolean} specifying if the tournament is exhibition game
     *
     * @return if available, the {@link Boolean} specifying if the tournament is exhibition game
     */
    @Override
    public Boolean isExhibitionGames() {
        if (!cachedLocales.isEmpty()) {
            return exhibitionGames;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

        return exhibitionGames;
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

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SapiTournamentInfoEndpoint) {
            internalMerge((SapiTournamentInfoEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SapiTournamentExtended) {
            internalMerge((SapiTournamentExtended) endpointData, dataLocale);
        } else if (endpointData instanceof SapiTournament) {
            internalMerge((SapiTournament) endpointData, dataLocale);
        }
    }

    @SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high
    private void internalMerge(SapiTournamentInfoEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (cachedLocales.contains(dataLocale)) {
            logger.info(
                "TournamentCI [{}] already contains TournamentInfo data for language {}",
                id,
                dataLocale
            );
        }

        if (endpointData.getGroups() != null) {
            if (groups == null) {
                groups = Collections.synchronizedList(new ArrayList<>());
                endpointData.getGroups().getGroup().forEach(g -> groups.add(new GroupCi(g, dataLocale)));
            } else {
                List<GroupCi> tmpGroups = Collections.synchronizedList(new ArrayList<>(groups));

                // remove obsolete groups
                if (groups != null && !groups.isEmpty()) {
                    try {
                        groups.forEach(tmpGroup -> {
                            if (tmpGroup.getId() != null && !tmpGroup.getId().isEmpty()) {
                                if (
                                    endpointData
                                        .getGroups()
                                        .getGroup()
                                        .stream()
                                        .filter(f -> f.getId() != null && f.getId().equals(tmpGroup.getId()))
                                        .findFirst()
                                        .orElse(null) ==
                                    null
                                ) {
                                    tmpGroups.remove(tmpGroup);
                                }
                            }
                            if (
                                tmpGroup.getId() == null &&
                                tmpGroup.getName() != null &&
                                !tmpGroup.getName().isEmpty()
                            ) {
                                if (
                                    endpointData
                                        .getGroups()
                                        .getGroup()
                                        .stream()
                                        .filter(f ->
                                            f.getName() != null && f.getName().equals(tmpGroup.getName())
                                        )
                                        .findFirst()
                                        .orElse(null) ==
                                    null
                                ) {
                                    tmpGroups.remove(tmpGroup);
                                }
                            }
                            if (
                                tmpGroup.getId() == null &&
                                tmpGroup.getName() == null &&
                                endpointData
                                    .getGroups()
                                    .getGroup()
                                    .stream()
                                    .filter(f -> f.getId() == null && f.getName() == null)
                                    .findFirst()
                                    .orElse(null) ==
                                null
                            ) {
                                tmpGroups.remove(tmpGroup);
                            }
                        });
                    } catch (Exception e) {
                        logger.debug("Error removing changed group: {}", e.getMessage());
                    }
                }

                // add or merge groups
                for (int i = 0; i < endpointData.getGroups().getGroup().size(); i++) {
                    SapiTournamentGroup sapiGroup = endpointData.getGroups().getGroup().get(i);
                    GroupCi tmpGroup = sapiGroup.getName() != null
                        ? tmpGroups
                            .stream()
                            .filter(existingGroup ->
                                existingGroup.getName() != null &&
                                existingGroup.getName().equals(sapiGroup.getName())
                            )
                            .findFirst()
                            .orElse(null)
                        : sapiGroup.getId() != null
                            ? tmpGroups
                                .stream()
                                .filter(existingGroup ->
                                    existingGroup.getId() != null &&
                                    existingGroup.getId().equals(sapiGroup.getId())
                                )
                                .findFirst()
                                .orElse(null)
                            : tmpGroups
                                .stream()
                                .filter(existingGroup ->
                                    existingGroup.getId() == null && existingGroup.getName() == null
                                )
                                .findFirst()
                                .orElse(null);
                    if (tmpGroup == null) {
                        tmpGroups.add(new GroupCi(sapiGroup, dataLocale));
                    } else {
                        tmpGroup.merge(sapiGroup, dataLocale);
                    }
                }
                groups = tmpGroups;
            }
        }

        if (endpointData.getRound() != null) {
            if (round == null) {
                round = new CompleteRoundCiImpl(endpointData.getRound(), dataLocale);
            } else {
                round.merge(endpointData.getRound(), dataLocale);
            }
        }

        SapiCompetitors endpointCompetitors = endpointData.getCompetitors() != null
            ? endpointData.getCompetitors()
            : endpointData.getTournament().getCompetitors();

        if (endpointCompetitors != null) {
            if (this.competitorIds == null) {
                this.competitorIds = new ArrayList<>(endpointCompetitors.getCompetitor().size());
            }
            endpointCompetitors
                .getCompetitor()
                .forEach(c -> {
                    Urn parsedId = Urn.parse(c.getId());
                    if (!this.competitorIds.contains(parsedId)) {
                        this.competitorIds.add(parsedId);
                    }
                });
            competitorsReferences =
                SdkHelper.parseCompetitorsReferences(
                    endpointCompetitors.getCompetitor(),
                    competitorsReferences
                );
        }

        if (endpointData.getSeason() != null) {
            if (this.season == null) {
                this.season = new SeasonCi(endpointData.getSeason(), dataLocale);
            } else {
                this.season.merge(endpointData.getSeason(), dataLocale);
            }
        }

        if (endpointData.getCoverageInfo() != null) {
            this.tournamentCoverage = new TournamentCoverageCi(endpointData.getCoverageInfo());
        }

        internalMerge(endpointData.getTournament(), dataLocale);

        cachedLocales.add(dataLocale);
    }

    private void internalMerge(SapiTournamentExtended endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        internalMerge((SapiTournament) endpointData, dataLocale);

        if (endpointData.getCurrentSeason() != null) {
            if (this.currentSeason == null) {
                this.currentSeason = new SeasonCi(endpointData.getCurrentSeason(), dataLocale);
            } else {
                this.currentSeason.merge(endpointData.getCurrentSeason(), dataLocale);
            }
        }

        if (endpointData.getSeasonCoverageInfo() != null) {
            this.seasonCoverage = new SeasonCoverageCi(endpointData.getSeasonCoverageInfo());
        }
    }

    private void internalMerge(SapiTournament endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.names.put(dataLocale, endpointData.getName());
        } else {
            this.names.put(dataLocale, "");
        }

        if (endpointData.getCategory() != null) {
            this.categoryId = Urn.parse(endpointData.getCategory().getId());
        }

        Date endpointScheduled = endpointData.getScheduled() == null
            ? null
            : SdkHelper.toDate(endpointData.getScheduled());
        Date endpointScheduledEnd = endpointData.getScheduledEnd() == null
            ? null
            : SdkHelper.toDate(endpointData.getScheduledEnd());

        if (
            (endpointScheduled == null || endpointScheduledEnd == null) &&
            endpointData.getTournamentLength() != null
        ) {
            SapiTournamentLength tournamentLength = endpointData.getTournamentLength();
            endpointScheduled =
                tournamentLength.getStartDate() == null
                    ? null
                    : SdkHelper.toDate(tournamentLength.getStartDate());
            endpointScheduledEnd =
                tournamentLength.getEndDate() == null
                    ? null
                    : SdkHelper.toDate(tournamentLength.getEndDate());
        }

        this.scheduled = endpointScheduled == null ? this.scheduled : endpointScheduled;
        this.scheduledEnd = endpointScheduledEnd == null ? this.scheduledEnd : endpointScheduledEnd;

        this.exhibitionGames = endpointData.isExhibitionGames();
    }

    @Override
    public void requestMissingSummaryData(
        List<Locale> requiredLocales,
        boolean forceFetch,
        RequestOptions requestOptions
    ) {
        requestMissingTournamentData(requiredLocales, requestOptions);
    }

    private void requestMissingTournamentData(List<Locale> requiredLocales) {
        RequestOptions timeCiticalRequestOptions = RequestOptions
            .requestOptions()
            .setExecutionPath(TIME_CRITICAL)
            .build();
        requestMissingTournamentData(requiredLocales, timeCiticalRequestOptions);
    }

    /**
     * Requests the data for the missing translations
     *
     * @param requiredLocales a {@link List} of locales in which the tournament data should be translated
     */
    private void requestMissingTournamentData(List<Locale> requiredLocales, RequestOptions requestOptions) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        dataRequestLock.lock();
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
            logger.debug("Fetching missing tournament data for id='{}' for languages '{}'", id, localesStr);

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestSummaryEndpoint(l, id, this, requestOptions);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingTournamentData(%s)", missingLocales), e);
        } finally {
            dataRequestLock.unlock();
        }
    }

    private void requestAssociatedSeasonIds() {
        if (associatedSeasonIdsLoaded) {
            return;
        }

        logger.debug("Fetching associated seasons for tournament[{}], language: {}", id, defaultLocale);

        associatedSeasonIdsLoaded = true;

        dataRequestLock.lock();
        try {
            associatedSeasonIds = dataRouterManager.requestSeasonsFor(defaultLocale, id);
        } catch (CommunicationException e) {
            handleException(String.format("requestAssociatedSeasonIds(%s)", defaultLocale), e);
        } finally {
            dataRequestLock.unlock();
        }
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("TournamentCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing TournamentCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing TournamentCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    private static List<Urn> prepareCompetitorList(
        List<Urn> competitors,
        Supplier<List<GroupCi>> groupSupplier
    ) {
        if (competitors != null) {
            return ImmutableList.copyOf(competitors);
        }

        if (groupSupplier != null && groupSupplier.get() != null) {
            return groupSupplier
                .get()
                .stream()
                .map(GroupCi::getCompetitorIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(ImmutableList.toImmutableList());
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high
    private static Map<Urn, ReferenceIdCi> prepareCompetitorReferences(
        Map<Urn, ReferenceIdCi> references,
        Supplier<List<GroupCi>> groupSupplier
    ) {
        if (references != null && !references.isEmpty()) {
            return ImmutableMap.copyOf(references);
        }

        if (groupSupplier != null && groupSupplier.get() != null) {
            Map<Urn, ReferenceIdCi> tmpRefs = new HashMap<>();
            for (GroupCi group : groupSupplier.get()) {
                if (group.getCompetitorsReferences() != null) {
                    for (Map.Entry<Urn, ReferenceIdCi> entry : group.getCompetitorsReferences().entrySet()) {
                        if (!tmpRefs.containsKey(entry.getKey())) {
                            tmpRefs.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            return tmpRefs;
        }

        return null;
    }

    @Override
    public ExportableCi export() {
        return new ExportableTournamentCi(
            id.toString(),
            new HashMap<>(names),
            scheduled,
            scheduledEnd,
            null,
            null,
            defaultLocale,
            categoryId != null ? categoryId.toString() : null,
            currentSeason != null ? currentSeason.export() : null,
            season != null ? season.export() : null,
            seasonCoverage != null ? seasonCoverage.export() : null,
            tournamentCoverage != null ? tournamentCoverage.export() : null,
            groups != null ? groups.stream().map(GroupCi::export).collect(Collectors.toList()) : null,
            round != null ? ((CompleteRoundCiImpl) round).export() : null,
            competitorIds != null
                ? competitorIds.stream().map(Urn::toString).collect(Collectors.toList())
                : null,
            competitorsReferences != null
                ? competitorsReferences
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(c -> c.getKey().toString(), c -> c.getValue().getReferenceIds())
                    )
                : null,
            associatedSeasonIdsLoaded,
            associatedSeasonIds != null
                ? associatedSeasonIds.stream().map(Urn::toString).collect(Collectors.toList())
                : null,
            new ArrayList<>(cachedLocales),
            exhibitionGames
        );
    }
}
