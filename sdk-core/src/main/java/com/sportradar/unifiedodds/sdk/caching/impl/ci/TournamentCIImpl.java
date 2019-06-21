/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.unifiedodds.sdk.caching.ci.*;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
class TournamentCIImpl implements TournamentCI {
    private final static Logger logger = LoggerFactory.getLogger(TournamentCIImpl.class);

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
     * A {@link Map} containing translated names of the item
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link URN} specifying the id of the parent category
     */
    private URN categoryId;

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
     * A {@link SeasonCI} representing the current season of the tournament
     */
    private SeasonCI currentSeason;

    /**
     * A {@link SeasonCI} representing the season of the tournament endpoint
     */
    private SeasonCI season;

    /**
     * A {@link SeasonCoverageCI} containing information about the tournament coverage
     */
    private SeasonCoverageCI seasonCoverage;

    /**
     * A {@link TournamentCoverageCI} instance describing the current tournament coverage
     */
    private TournamentCoverageCI tournamentCoverage;

    /**
     * A list of groups related to the current instance
     */
    private List<GroupCI> groups;

    /**
     * The round related to the current instance
     */
    private CompleteRoundCI round;

    /**
     * A {@link List} of associated tournament competitors
     */
    private List<URN> competitorIds;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<URN, ReferenceIdCI> competitorsReferences;

    /**
     * An indication if the associated season ids were loaded
     */
    private boolean associatedSeasonIdsLoaded;

    /**
     * A {@link List} of associated season ids
     */
    private List<URN> associatedSeasonIds;

    /**
     * A {@link List} of locales that are already fully cached - only when the full tournament info endpoint is cached
     */
    private List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * A lock used to synchronize api requests
     */
    private final ReentrantLock dataRequestLock = new ReentrantLock();


    TournamentCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    TournamentCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData.getTournament(), dataLocale);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.round = endpointData.getRound() == null ? null :
                new CompleteRoundCIImpl(endpointData.getRound(), dataLocale);
        this.season = endpointData.getSeason() != null ? new SeasonCI(endpointData.getSeason(), dataLocale) : null;

        this.groups = endpointData.getGroups() == null ? null :
                Collections.synchronizedList(endpointData.getGroups().getGroup().stream().
                        map(g -> new GroupCI(g, dataLocale)).collect(Collectors.toList()));


        SAPICompetitors endpointCompetitors = endpointData.getCompetitors() != null ?
                endpointData.getCompetitors() :
                endpointData.getTournament().getCompetitors();
        if(endpointCompetitors != null) {
            this.competitorIds = Collections.synchronizedList(endpointCompetitors.getCompetitor().stream()
                            .map(c -> URN.parse(c.getId())).collect(Collectors.toList()));
            competitorsReferences = SdkHelper.parseCompetitorsReferences(endpointCompetitors.getCompetitor(), competitorsReferences);
        }
        else  {
            this.competitorIds = null;
            this.competitorsReferences = null;
        }

        this.tournamentCoverage = endpointData.getCoverageInfo() == null ? null :
                new TournamentCoverageCI(endpointData.getCoverageInfo());

        cachedLocales.add(dataLocale);
    }

    TournamentCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITournamentExtended endpointData, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, (SAPITournament) endpointData, dataLocale);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        this.currentSeason = endpointData.getCurrentSeason() == null ? null :
                new SeasonCI(endpointData.getCurrentSeason(), dataLocale);
        this.seasonCoverage = endpointData.getSeasonCoverageInfo() == null ? null :
                new SeasonCoverageCI(endpointData.getSeasonCoverageInfo());
    }

    TournamentCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPITournament endpointData, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.names.put(dataLocale, endpointData.getName());
        }
        else{
            this.names.put(dataLocale, "");
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

    /**
     * Returns the {@link URN} specifying the id of the parent category
     *
     * @return the {@link URN} specifying the id of the parent category
     */
    @Override
    public URN getCategoryId() {
        if (categoryId != null || !cachedLocales.isEmpty()) {
            return categoryId;
        }

        requestMissingTournamentData(Collections.singletonList(defaultLocale));

        return categoryId;
    }

    /**
     * Returns a {@link SeasonCI} representing the current season of the tournament
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SeasonCI} representing the current season of the tournament
     */
    @Override
    public SeasonCI getCurrentSeason(List<Locale> locales) {
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
     * Returns a {@link SeasonCoverageCI} containing information about the tournament coverage
     *
     * @return a {@link SeasonCoverageCI} containing information about the tournament coverage
     */
    @Override
    public SeasonCoverageCI getSeasonCoverage() {
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
    public SeasonCI getSeason(List<Locale> locales) {
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
    public List<URN> getCompetitorIds(List<Locale> locales) {
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
    public List<GroupCI> getGroups(List<Locale> locales) {
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
    public RoundCI getRound(List<Locale> locales) {
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
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        return null;
    }

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    @Override
    public URN getReplacedBy() {
        return null;
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
     * @return a {@link TournamentCoverageCI} instance describing the current coverage indication
     */
    @Override
    public TournamentCoverageCI getTournamentCoverage() {
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
    public List<URN> getSeasonIds() {
        if (associatedSeasonIdsLoaded) {
            return associatedSeasonIds;
        }

        requestAssociatedSeasonIds();

        return associatedSeasonIds;
    }

    /**
     * Returns list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link URN} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    @Override
    public Map<URN, ReferenceIdCI> getCompetitorsReferences() {
        if(cachedLocales.isEmpty()) {
            requestMissingTournamentData(Collections.singletonList(defaultLocale));
        }

        return prepareCompetitorReferences(competitorsReferences, () -> getGroups(Collections.singletonList(defaultLocale)));
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
        if (endpointData instanceof SAPITournamentInfoEndpoint) {
            internalMerge((SAPITournamentInfoEndpoint) endpointData, dataLocale);
        } else if (endpointData instanceof SAPITournamentExtended) {
            internalMerge((SAPITournamentExtended) endpointData, dataLocale);
        } else if (endpointData instanceof SAPITournament) {
            internalMerge((SAPITournament) endpointData, dataLocale);
        }
    }

    private void internalMerge(SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (cachedLocales.contains(dataLocale)) {
            logger.info("TournamentCI [{}] already contains TournamentInfo data for language {}", id, dataLocale);
        }

        if (endpointData.getGroups() != null) {
            if (groups == null) {
                groups = Collections.synchronizedList(new ArrayList<>());
                endpointData.getGroups().getGroup().forEach(g -> groups.add(new GroupCI(g, dataLocale)));
            } else {
                groups.forEach(groupCI -> groupCI.merge(
                        endpointData.getGroups().getGroup().stream()
                                .filter(mergingGroup ->
                                        (groupCI.getName() == null && mergingGroup.getName() == null) ||
                                                (groupCI.getName() != null && groupCI.getName().equals(mergingGroup.getName())))
                                .findFirst().orElse(null),
                        dataLocale
                ));
            }
        }

        if (endpointData.getRound() != null) {
            if (round == null) {
                round = new CompleteRoundCIImpl(endpointData.getRound(), dataLocale);
            } else {
                round.merge(endpointData.getRound(), dataLocale);
            }
        }

        SAPICompetitors endpointCompetitors = endpointData.getCompetitors() != null ?
                endpointData.getCompetitors() :
                endpointData.getTournament().getCompetitors();

        if (endpointCompetitors != null) {
            if (this.competitorIds == null) {
                this.competitorIds = new ArrayList<>(endpointCompetitors.getCompetitor().size());
            }
            endpointCompetitors.getCompetitor().forEach(c -> {
                URN parsedId = URN.parse(c.getId());
                if (!this.competitorIds.contains(parsedId)) {
                    this.competitorIds.add(parsedId);
                }
            });
            competitorsReferences = SdkHelper.parseCompetitorsReferences(endpointCompetitors.getCompetitor(), competitorsReferences);
        }

        if (endpointData.getSeason() != null) {
            if (this.season == null) {
                this.season = new SeasonCI(endpointData.getSeason(), dataLocale);
            } else {
                this.season.merge(endpointData.getSeason(), dataLocale);
            }
        }

        if (endpointData.getCoverageInfo() != null) {
            this.tournamentCoverage = new TournamentCoverageCI(endpointData.getCoverageInfo());
        }

        internalMerge(endpointData.getTournament(), dataLocale);

        cachedLocales.add(dataLocale);
    }

    private void internalMerge(SAPITournamentExtended endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        internalMerge((SAPITournament) endpointData, dataLocale);

        if (endpointData.getCurrentSeason() != null) {
            if (this.currentSeason == null) {
                this.currentSeason = new SeasonCI(endpointData.getCurrentSeason(), dataLocale);
            } else {
                this.currentSeason.merge(endpointData.getCurrentSeason(), dataLocale);
            }
        }

        if (endpointData.getSeasonCoverageInfo() != null) {
            this.seasonCoverage = new SeasonCoverageCI(endpointData.getSeasonCoverageInfo());
        }
    }

    private void internalMerge(SAPITournament endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        if (endpointData.getName() != null) {
            this.names.put(dataLocale, endpointData.getName());
        }
        else{
            this.names.put(dataLocale, "");
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
    private void requestMissingTournamentData(List<Locale> requiredLocales) {
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

            logger.debug("Fetching missing tournament data for id='{}' for languages '{}'",
                    id,
                    missingLocales.stream()
                            .map(Locale::getLanguage)
                            .collect(Collectors.joining(", ")));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestSummaryEndpoint(l, id, this);
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

    private static List<URN> prepareCompetitorList(List<URN> competitors, Supplier<List<GroupCI>> groupSupplier) {
        if (competitors != null) {
            return ImmutableList.copyOf(competitors);
        }

        if (groupSupplier != null && groupSupplier.get() != null) {
            return groupSupplier.get().stream()
                            .map(GroupCI::getCompetitorIds)
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .distinct()
                    .collect(ImmutableList.toImmutableList());
        }

        return null;
    }

    private static Map<URN, ReferenceIdCI> prepareCompetitorReferences(Map<URN, ReferenceIdCI> references, Supplier<List<GroupCI>> groupSupplier) {
        if (references != null && !references.isEmpty()) {
            return ImmutableMap.copyOf(references);
        }

        if (groupSupplier != null && groupSupplier.get() != null) {
            return groupSupplier.get().stream()
                            .map(GroupCI::getCompetitorsReferences)
                            .filter(Objects::nonNull)
                            .flatMap(g -> g.entrySet().stream())
                    .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return null;
    }
}
