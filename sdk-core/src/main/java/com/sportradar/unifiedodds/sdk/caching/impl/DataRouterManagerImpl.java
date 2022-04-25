/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.uf.custombet.datamodel.CAPISelectionType;
import com.sportradar.uf.custombet.datamodel.CAPISelections;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.FixtureChangeImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.PeriodStatusImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.ResultChangeImpl;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created on 26/10/2017.
 * // TODO @eti: Javadoc
 */
public class DataRouterManagerImpl implements DataRouterManager {
    /**
     * The {@link Logger} instance used to log {@link DataRouterManagerImpl} events
     */
    private static final Logger logger = LoggerFactory.getLogger(DataRouterManagerImpl.class);

    /**
     * Total amount of days that should be fetched ahead on a daily basis
     */
    private static final int SCHEDULE_DAYS_PREFETCH = 3;

    /**
     * A {@link List} of locales that should be automatically available
     */
    private final List<Locale> prefetchLocales;

    /**
     * An indication if the WNS(7) producer is active on the account used in the current SDK instance
     */
    private final boolean isWnsActive;

    /**
     * A {@link List} of locales in which all tournament list data is already fetched
     */
    private final List<Locale> tournamentListDataFetched;

    /**
     * A {@link List} of locales in which all sports list data is already fetched
     */
    private final List<Locale> sportsListDataFetched;

    /**
     * A {@link List} of locales in which all lotteries data is already fetched
     */
    private final List<Locale> lotteriesListDataFetched;

    /**
     * A {@link List} of dates already pre-loaded by the timer
     */
    private final List<Date> fetchedScheduleDates;

    /**
     * The associated data router
     */
    private final DataRouter dataRouter;

    /**
     * A generic data provider linked with the summary endpoint
     */
    private final DataProvider<Object> summaryEndpointProvider;

    /**
     * The {@link DataProvider} used to fetch fixture data
     */
    private final DataProvider<SAPIFixturesEndpoint> fixtureProvider;

    /**
     * The {@link DataProvider} used to fetch fixture data without cache
     */
    private final DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureProvider;

    /**
     * A {@link DataProvider} instance used to fetch the tournament list API endpoint
     */
    private final DataProvider<SAPITournamentsEndpoint> tournamentsListProvider;

    /**
     * A {@link DataProvider} instance which is used to fill the cache with schedule endpoints
     */
    private final DataProvider<SAPIScheduleEndpoint> dateScheduleProvider;

    /**
     * A {@link DataProvider} instance which is used to fill the cache
     */
    private final DataProvider<Object> tournamentScheduleProvider;

    /**
     * A {@link DataProvider} instance used to fetch the all available sports API endpoint
     */
    private final DataProvider<SAPISportsEndpoint> sportsListProvider;

    /**
     * A {@link DataProvider} used to fetch player profiles
     */
    private final DataProvider<SAPIPlayerProfileEndpoint> playerProvider;

    /**
     * A {@link DataProvider} used to fetch competitor profiles
     */
    private final DataProvider<SAPICompetitorProfileEndpoint> competitorProvider;

    /**
     * A {@link DataProvider} used to fetch simpleteam profiles
     */
    private final DataProvider<SAPISimpleTeamProfileEndpoint> simpleTeamProvider;

    /**
     * A {@link DataProvider} used to fetch tournament seasons
     */
    private final DataProvider<SAPITournamentSeasons> tournamentSeasonsDataProvider;

    /**
     * A {@link DataProvider} used to fetch draw summaries
     */
    private final DataProvider<SAPIDrawSummary> drawSummaryDataProvider;

    /**
     * A {@link DataProvider} used to fetch draw fixtures
     */
    private final DataProvider<SAPIDrawFixtures> drawFixtureDataProvider;

    /**
     * A {@link DataProvider} used to fetch all lotteries
     */
    private final DataProvider<SAPILotteries> lotteriesListProvider;

    /**
     * A {@link DataProvider} used to fetch lottery schedules
     */
    private final DataProvider<SAPILotterySchedule> lotteryScheduleProvider;

    /**
     * A {@link DataProvider} used to fetch match time lines
     */
    private final DataProvider<SAPIMatchTimelineEndpoint> matchTimelineEndpointDataProvider;

    /**
     * A {@link DataProvider} used to fetch sport categories
     */
    private final DataProvider<SAPISportCategoriesEndpoint> sportCategoriesEndpointDataProvider;

    /**
     * A {@link DataProvider} used to fetch available selections
     */
    private final DataProvider<CAPIAvailableSelections> availableSelectionsTypeDataProvider;

    /**
     * A {@link DataProvider} used to fetch probability calculations
     */
    private final DataProvider<CAPICalculationResponse> calculateProbabilityDataProvider;

    /**
     * A {@link DataProvider} used to fetch fixture changes
     */
    private final DataProvider<SAPIFixtureChangesEndpoint> fixtureChangesDataProvider;

    /**
     * A {@link DataProvider} used to fetch result changes
     */
    private final DataProvider<SAPIResultChangesEndpoint> resultChangesDataProvider;

    /**
     * A {@link DataProvider} instance which is used to get list of sport events
     */
    private final DataProvider<SAPIScheduleEndpoint> listSportEventsProvider;

    /**
     * A {@link DataProvider} instance which is used to get all available tournaments for specific sport
     */
    private final DataProvider<SAPISportTournamentsEndpoint> availableSportTournamentsProvider;

    /**
     * A {@link DataProvider} instance which is used to get period summary endpoint for specific sport event
     */
    private final DataProvider<SAPIStagePeriodEndpoint> periodSummaryDataProvider;

    /**
     * The extended odds feed listener
     */
    private OddsFeedExtListener oddsFeedExtListener;

    private final ReentrantLock tournamentListLock = new ReentrantLock();
    private final ReentrantLock sportsListLock = new ReentrantLock();
    private final ReentrantLock lotteriesListLock = new ReentrantLock();

    @Inject
    DataRouterManagerImpl(SDKInternalConfiguration configuration,
                          SDKTaskScheduler scheduler,
                          SDKProducerManager producerManager,
                          DataRouter dataRouter,
                          @Named("SummaryEndpointDataProvider") DataProvider<Object> summaryEndpointProvider,
                          @Named("FixtureEndpointDataProvider") DataProvider<SAPIFixturesEndpoint> fixtureProvider,
                          @Named("FixtureChangeFixtureEndpointDataProvider") DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureProvider,
                          DataProvider<SAPITournamentsEndpoint> tournamentsListProvider,
                          @Named("DateScheduleEndpointDataProvider") DataProvider<SAPIScheduleEndpoint> dateScheduleProvider,
                          @Named("TournamentScheduleProvider") DataProvider<Object> tournamentScheduleProvider,
                          DataProvider<SAPISportsEndpoint> sportsListProvider,
                          DataProvider<SAPIPlayerProfileEndpoint> playerProvider,
                          DataProvider<SAPICompetitorProfileEndpoint> competitorProvider,
                          DataProvider<SAPISimpleTeamProfileEndpoint> simpleTeamProvider,
                          DataProvider<SAPITournamentSeasons> tournamentSeasonsDataProvider,
                          DataProvider<SAPIMatchTimelineEndpoint> matchTimelineEndpointDataProvider,
                          DataProvider<SAPISportCategoriesEndpoint> sportCategoriesEndpointDataProvider,
                          DataProvider<SAPIDrawSummary> drawSummaryDataProvider,
                          DataProvider<SAPIDrawFixtures> drawFixtureDataProvider,
                          DataProvider<SAPILotteries> lotteriesListProvider,
                          DataProvider<SAPILotterySchedule> lotteryScheduleProvider,
                          DataProvider<CAPIAvailableSelections> availableSelectionsTypeDataProvider,
                          DataProvider<CAPICalculationResponse> calculateProbabilityDataProvider,
                          DataProvider<SAPIFixtureChangesEndpoint> fixtureChangesDataProvider,
                          DataProvider<SAPIResultChangesEndpoint> resultChangesDataProvider,
                          @Named("ListSportEventsDataProvider") DataProvider<SAPIScheduleEndpoint> listSportEventsProvider,
                          DataProvider<SAPISportTournamentsEndpoint> availableSportTournamentsProvider,
                          DataProvider<SAPIStagePeriodEndpoint> periodSummaryDataProvider) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(producerManager);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(dataRouter);
        Preconditions.checkNotNull(summaryEndpointProvider);
        Preconditions.checkNotNull(fixtureProvider);
        Preconditions.checkNotNull(fixtureChangeFixtureProvider);
        Preconditions.checkNotNull(tournamentsListProvider);
        Preconditions.checkNotNull(dateScheduleProvider);
        Preconditions.checkNotNull(tournamentScheduleProvider);
        Preconditions.checkNotNull(sportsListProvider);
        Preconditions.checkNotNull(playerProvider);
        Preconditions.checkNotNull(competitorProvider);
        Preconditions.checkNotNull(simpleTeamProvider);
        Preconditions.checkNotNull(tournamentSeasonsDataProvider);
        Preconditions.checkNotNull(matchTimelineEndpointDataProvider);
        Preconditions.checkNotNull(sportCategoriesEndpointDataProvider);
        Preconditions.checkNotNull(drawSummaryDataProvider);
        Preconditions.checkNotNull(drawFixtureDataProvider);
        Preconditions.checkNotNull(lotteriesListProvider);
        Preconditions.checkNotNull(lotteryScheduleProvider);
        Preconditions.checkNotNull(availableSelectionsTypeDataProvider);
        Preconditions.checkNotNull(calculateProbabilityDataProvider);
        Preconditions.checkNotNull(fixtureChangesDataProvider);
        Preconditions.checkNotNull(resultChangesDataProvider);
        Preconditions.checkNotNull(listSportEventsProvider);
        Preconditions.checkNotNull(availableSportTournamentsProvider);
        Preconditions.checkNotNull(periodSummaryDataProvider);

        this.prefetchLocales = configuration.getDesiredLocales();
        this.isWnsActive = producerManager.getActiveProducers().values().stream().anyMatch(p -> p.getId() == 7 && p.isEnabled());

        this.dataRouter = dataRouter;
        this.summaryEndpointProvider = summaryEndpointProvider;
        this.fixtureProvider = fixtureProvider;
        this.fixtureChangeFixtureProvider = fixtureChangeFixtureProvider;
        this.tournamentsListProvider = tournamentsListProvider;
        this.dateScheduleProvider = dateScheduleProvider;
        this.tournamentScheduleProvider = tournamentScheduleProvider;
        this.sportsListProvider = sportsListProvider;
        this.playerProvider = playerProvider;
        this.competitorProvider = competitorProvider;
        this.simpleTeamProvider = simpleTeamProvider;
        this.tournamentSeasonsDataProvider = tournamentSeasonsDataProvider;
        this.matchTimelineEndpointDataProvider = matchTimelineEndpointDataProvider;
        this.sportCategoriesEndpointDataProvider = sportCategoriesEndpointDataProvider;
        this.drawSummaryDataProvider = drawSummaryDataProvider;
        this.drawFixtureDataProvider = drawFixtureDataProvider;
        this.lotteriesListProvider = lotteriesListProvider;
        this.lotteryScheduleProvider = lotteryScheduleProvider;
        this.availableSelectionsTypeDataProvider = availableSelectionsTypeDataProvider;
        this.calculateProbabilityDataProvider = calculateProbabilityDataProvider;
        this.fixtureChangesDataProvider = fixtureChangesDataProvider;
        this.resultChangesDataProvider = resultChangesDataProvider;
        this.listSportEventsProvider = listSportEventsProvider;
        this.availableSportTournamentsProvider = availableSportTournamentsProvider;
        this.periodSummaryDataProvider = periodSummaryDataProvider;

        this.tournamentListDataFetched = Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.sportsListDataFetched = Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.lotteriesListDataFetched = Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.fetchedScheduleDates = Collections.synchronizedList(new ArrayList<>(SCHEDULE_DAYS_PREFETCH));

        scheduler.scheduleAtFixedRate("DateSchedulePrefetchTask", this::onDateScheduleTimerElapsed, 5, 60 * 60 * 12L, TimeUnit.SECONDS);

        // initial is triggered as soon as it is needed
        scheduler.scheduleAtFixedRate("SportsDataRefreshTask", this::onSportsDataTimerElapsed, 12, 12, TimeUnit.HOURS);
    }

    public void addOddsFeedExtListener(OddsFeedExtListener oddsFeedExtListener)
    {
        Preconditions.checkNotNull(oddsFeedExtListener);
        this.oddsFeedExtListener = oddsFeedExtListener;
    }

    @Override
    public void requestSummaryEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        Object endpoint;
        try {
            endpoint = summaryEndpointProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing summary request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(summaryEndpointProvider.getFinalUrl(locale, id.toString()), endpoint);

        dataRouter.onSummaryFetched(id, endpoint, locale, requester);
    }

    @Override
    public void requestFixtureEndpoint(Locale locale, URN id, boolean useCachedProvider, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPIFixturesEndpoint endpoint;
        try {
            DataProvider<SAPIFixturesEndpoint> provider = useCachedProvider ? fixtureProvider : fixtureChangeFixtureProvider;
            endpoint = provider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            if(!useCachedProvider && e.getCause() != null && SdkHelper.checkCauseReason(e.getCause(), "InternalServerError")){
                try {
                    endpoint = fixtureProvider.getData(locale, id.toString());
                }
                catch (DataProviderException e1){
                    throw new CommunicationException(String.format("Error executing cached fixture request for id=%s, locale=%s", id, locale), e);
                }
            }
            else{
                throw new CommunicationException(String.format("Error executing fixture request for id=%s, locale=%s", id, locale), e);
            }
        }

        String finalUrl = useCachedProvider
                ? fixtureProvider.getFinalUrl(locale, id.toString())
                : fixtureChangeFixtureProvider.getFinalUrl(locale, id.toString());
        dispatchReceivedRawApiData(finalUrl, endpoint);

        SAPIFixture fixture = endpoint.getFixture();
        URN fixtureId = URN.parse(fixture.getId());
        dataRouter.onFixtureFetched(fixtureId, fixture, locale, requester);
    }

    @Override
    public void requestDrawSummary(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPIDrawSummary endpoint;
        try {
            endpoint = drawSummaryDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing draw summary request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(drawSummaryDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        URN drawId = URN.parse(endpoint.getDrawFixture().getId());
        dataRouter.onDrawSummaryFetched(drawId, endpoint, locale, requester);
    }

    @Override
    public void requestDrawFixture(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPIDrawFixtures endpoint;
        try {
            endpoint = drawFixtureDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing draw fixture request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(drawFixtureDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        SAPIDrawFixture drawFixture = endpoint.getDrawFixture();
        if (drawFixture != null) {
            URN drawId = URN.parse(drawFixture.getId());
            dataRouter.onDrawFixtureFetched(drawId, drawFixture, locale, requester);
        } else {
            logger.warn("Requested draw fixture returned an empty response - drawFixture == null [{} - {}]", id, locale);
        }
    }

    @Override
    public void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException {
        Preconditions.checkNotNull(locale);

        if (tournamentListDataFetched.contains(locale)) {
            return;
        }

        tournamentListLock.lock();
        try {
            if (tournamentListDataFetched.contains(locale)) {
                return;
            }

            SAPITournamentsEndpoint endpoint;
            try {
                endpoint = tournamentsListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(String.format("Error executing all tournaments list request for locale=%s", locale), e);
            }

            dispatchReceivedRawApiData(tournamentsListProvider.getFinalUrl(locale, ""), endpoint);

            dataRouter.onAllTournamentsListFetched(endpoint, locale);

            tournamentListDataFetched.add(locale);
        } finally {
            tournamentListLock.unlock();
        }
    }

    @Override
    public void requestAllSportsEndpoint(Locale locale) throws CommunicationException {
        if (sportsListDataFetched.contains(locale)) {
            return;
        }

        sportsListLock.lock();
        try {
            if (sportsListDataFetched.contains(locale)) {
                return;
            }

            SAPISportsEndpoint endpoint;
            try {
                endpoint = sportsListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(String.format("Error execution all sports request for locale=%s", locale), e);
            }

            dispatchReceivedRawApiData(sportsListProvider.getFinalUrl(locale, ""), endpoint);

            dataRouter.onSportsListFetched(endpoint, locale);

            sportsListDataFetched.add(locale);
        } finally {
            sportsListLock.unlock();
        }
    }

    @Override
    public List<URN> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult) throws CommunicationException {
        Preconditions.checkNotNull(locale);

        // if WNS producer inactive, ignore lotteries endpoint requests
        if (!isWnsActive) {
            return Collections.emptyList();
        }

        if (!requireResult && lotteriesListDataFetched.contains(locale)) {
            return Collections.emptyList();
        }

        lotteriesListLock.lock();
        try {
            if (!requireResult && lotteriesListDataFetched.contains(locale)) {
                return Collections.emptyList();
            }

            SAPILotteries endpoint;
            try {
                endpoint = lotteriesListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(String.format("Error executing all lotteries list request for locale=%s", locale), e);
            }

            dispatchReceivedRawApiData(lotteriesListProvider.getFinalUrl(locale, ""), endpoint);

            dataRouter.onAllLotteriesListFetched(endpoint, locale);

            lotteriesListDataFetched.add(locale);

            List<URN> lotteryIds = endpoint.getLottery().stream().map(e -> URN.parse(e.getId())).collect(Collectors.toList());
            return lotteryIds;

        } finally {
            lotteriesListLock.unlock();
        }
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, URN tournamentId) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(tournamentId);

        Object endpoint;
        try {
            endpoint = tournamentScheduleProvider.getData(locale, tournamentId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing tournament schedule request for id=%s, locale=%s", tournamentId, locale), e);
        }

        dispatchReceivedRawApiData(tournamentScheduleProvider.getFinalUrl(locale, tournamentId.toString()), endpoint);

        dataRouter.onTournamentScheduleFetched(endpoint, locale);

        if (endpoint instanceof SAPITournamentSchedule) {
            return extractEventIds(((SAPITournamentSchedule) endpoint).getSportEvents().stream()
                    .flatMap(seWrapper -> seWrapper.getSportEvent().stream())
                    .collect(Collectors.toList()));
        } else if (endpoint instanceof SAPIRaceScheduleEndpoint) {
            SAPIRaceScheduleEndpoint raceSchedule = (SAPIRaceScheduleEndpoint) endpoint;
            if (raceSchedule.getSportEvents() != null && raceSchedule.getSportEvents().getSportEvent() != null) {
                return raceSchedule.getSportEvents().getSportEvent().stream()
                        .map(e -> URN.parse(e.getId()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<URN> requestLotterySchedule(Locale locale, URN lotteryId, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(lotteryId);

        SAPILotterySchedule endpoint;
        try {
            endpoint = lotteryScheduleProvider.getData(locale, lotteryId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing lottery schedule request for id=%s, locale=%s", lotteryId, locale), e);
        }

        dispatchReceivedRawApiData(lotteryScheduleProvider.getFinalUrl(locale, lotteryId.toString()), endpoint);

        dataRouter.onLotteryScheduleFetched(endpoint, locale, requester);

        if (endpoint.getDrawEvents() != null && endpoint.getDrawEvents().getDrawEvent() != null) {
            return endpoint.getDrawEvents().getDrawEvent().stream()
                    .map(draw -> URN.parse(draw.getId()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        Preconditions.checkNotNull(locale);

        SAPIScheduleEndpoint endpoint;
        String formattedDate = "live";
        try {
            if (date != null) {
                formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                endpoint = dateScheduleProvider.getData(locale, formattedDate);
            } else {
                endpoint = dateScheduleProvider.getData(locale, formattedDate);
            }
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing tournament schedule request for id=%s, locale=%s", date == null ? "live" : date, locale), e);
        }

        dispatchReceivedRawApiData(dateScheduleProvider.getFinalUrl(locale, formattedDate), endpoint);

        dataRouter.onDateScheduleFetched(endpoint, locale);

        return extractEventIds(endpoint.getSportEvent());
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPIPlayerProfileEndpoint endpoint;
        try {
            endpoint = playerProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing player profile request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(playerProvider.getFinalUrl(locale, id.toString()), endpoint);

        SAPIPlayerExtended player = endpoint.getPlayer();
        URN playerId = URN.parse(player.getId());
        dataRouter.onPlayerFetched(playerId, player, locale, requester, null);
    }

    @Override
    public void requestCompetitorEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPICompetitorProfileEndpoint endpoint;
        try {
            endpoint = competitorProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing competitor profile request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(competitorProvider.getFinalUrl(locale, id.toString()), endpoint);

        SAPITeamExtended competitor = endpoint.getCompetitor();
        URN competitorId = URN.parse(competitor.getId());
        dataRouter.onCompetitorFetched(competitorId, endpoint, locale, requester);
    }

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPISimpleTeamProfileEndpoint endpoint;
        try {
            endpoint = simpleTeamProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing simpleteam profile request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(simpleTeamProvider.getFinalUrl(locale, id.toString()), endpoint);

        SAPITeam competitor = endpoint.getCompetitor();
        URN competitorId = URN.parse(competitor.getId());
        dataRouter.onSimpleTeamFetched(competitorId, endpoint, locale, requester);
        if (!competitorId.equals(id)) {
            dataRouter.onSimpleTeamFetched(id, endpoint, locale, requester);
        }
    }

    @Override
    public List<URN> requestSeasonsFor(Locale locale, URN tournamentId) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(tournamentId);

        SAPITournamentSeasons endpoint;
        try {
            endpoint = tournamentSeasonsDataProvider.getData(locale, tournamentId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing tournament seasons request for id=%s, locale=%s", tournamentId, locale), e);
        }

        dispatchReceivedRawApiData(tournamentSeasonsDataProvider.getFinalUrl(locale, tournamentId.toString()), endpoint);

        dataRouter.onTournamentSeasonsFetched(tournamentId, endpoint, locale);

        if (endpoint.getSeasons() != null) {
            return endpoint.getSeasons().getSeason().stream()
                    .map(s -> URN.parse(s.getId()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public SAPIMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPIMatchTimelineEndpoint endpoint;
        try {
            endpoint = matchTimelineEndpointDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing match timeline request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(matchTimelineEndpointDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        dataRouter.onMatchTimelineFetched(id, endpoint, locale, requester);

        return endpoint;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SAPISportCategoriesEndpoint endpoint;
        try {
            endpoint = sportCategoriesEndpointDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing sport categories request for id=%s, locale=%s", id, locale), e);
        }

        dispatchReceivedRawApiData(sportCategoriesEndpointDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        dataRouter.onSportCategoriesFetched(endpoint, locale, requester);
    }

    @Override
    public AvailableSelections requestAvailableSelections(URN id) throws CommunicationException {
        Preconditions.checkNotNull(id);

        CAPIAvailableSelections availableSelections;
        try {
            availableSelections = availableSelectionsTypeDataProvider.getData(id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing available selections request for id=%s", id), e);
        }

        dataRouter.onAvailableSelectionsFetched(id, availableSelections);
        return new AvailableSelectionsImpl(availableSelections);
    }

    @Override
    public Calculation requestCalculateProbability(List<Selection> selections) throws CommunicationException {
        Preconditions.checkNotNull(selections);

        CAPICalculationResponse calculation;
        try {
            CAPISelections content = new CAPISelections();
            content.getSelections().addAll(selections.stream()
            .map(s -> {
                CAPISelectionType selection = new CAPISelectionType();
                selection.setId(s.getEventId().toString());
                selection.setMarketId(s.getMarketId());
                selection.setSpecifiers(s.getSpecifiers());
                selection.setOutcomeId(s.getOutcomeId());
                return selection;
            })
            .collect(Collectors.toList()));

            calculation = calculateProbabilityDataProvider.postData(content);
        } catch (DataProviderException e) {
            throw new CommunicationException("Error executing calculate probability request", e);
        }

        dataRouter.onCalculateProbabilityFetched(selections, calculation);
        return new CalculationImpl(calculation);
    }

    @Override
    public List<FixtureChange> requestFixtureChanges(Date after, URN sportId, Locale locale) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        try {
            String query = getChangesQueryString(after, sportId);
            return fixtureChangesDataProvider.getData(locale, query)
                    .getFixtureChange()
                    .stream()
                    .map(FixtureChangeImpl::new)
                    .collect(Collectors.toList());
        } catch (DataProviderException e) {
            throw new CommunicationException("Error executing fixture changes request", e);
        }
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, URN sportId, Locale locale) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        try {
            String query = getChangesQueryString(after, sportId);
            return resultChangesDataProvider.getData(locale, query)
                    .getResultChange()
                    .stream()
                    .map(ResultChangeImpl::new)
                    .collect(Collectors.toList());
        } catch (DataProviderException e) {
            throw new CommunicationException("Error executing result changes request", e);
        }
    }

    @Override
    public List<URN> requestListSportEvents(Locale locale, int startIndex, int limit) throws CommunicationException {
        Preconditions.checkNotNull(locale);

        SAPIScheduleEndpoint endpoint;
        try {
            endpoint = listSportEventsProvider.getData(locale, String.valueOf(startIndex), String.valueOf(limit));
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing list sport events request for startIndex=%s, limit=%s, locale=%s", startIndex, limit, locale), e);
        }

        dispatchReceivedRawApiData(listSportEventsProvider.getFinalUrl(locale, new String[]{String.valueOf(startIndex), String.valueOf(limit)}), endpoint);

        dataRouter.onListSportEventsFetched(endpoint, locale);

        return extractEventIds(endpoint.getSportEvent());
    }

    @Override
    public List<URN> requestAvailableTournamentsFor(Locale locale, URN sportId) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(sportId);

        SAPISportTournamentsEndpoint endpoint;
        try {
            endpoint = availableSportTournamentsProvider.getData(locale, sportId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(String.format("Error executing getting available tournaments for id=%s, locale=%s", sportId, locale), e);
        }

        dispatchReceivedRawApiData(availableSportTournamentsProvider.getFinalUrl(locale, sportId.toString()), endpoint);

        dataRouter.onSportTournamentsFetched(sportId, endpoint, locale);

        if (endpoint.getTournaments() != null) {
            return endpoint.getTournaments().getTournament().stream()
                    .map(s -> URN.parse(s.getId()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(URN id, Locale locale, List<URN> competitorIds, List<Integer> periods) throws CommunicationException {
        Preconditions.checkNotNull(id);

        if(locale == null){
            locale = prefetchLocales.get(0);
        }

        List<PeriodStatus> results = new ArrayList<>();
        try {
            String query = getPeriodSummaryQueryString(competitorIds, periods);
            SAPIStagePeriodEndpoint response = periodSummaryDataProvider.getData(locale, id.toString(), query);
            if(response != null && response.getPeriodStatuses() != null && response.getPeriodStatuses().getPeriodStatus() != null){
                results = response.getPeriodStatuses().getPeriodStatus().stream().map(PeriodStatusImpl::new).collect(Collectors.toList());
            }
        } catch (DataProviderException e) {
            throw new CommunicationException("Error executing period summary request", e);
        }
        return results;
    }

    private String getPeriodSummaryQueryString(List<URN> competitorIds, List<Integer> periods)
    {
        //host/v1/sports/en/sport_events/sr:stage:{id}/period_summary.xml?competitors=sr:competitor:{id}&competitors=sr:competitor:{id}&periods=2&periods=3&periods=4
        String query = "";
        String compQuery = "";
        String periodQuery = "";
        if (competitorIds != null && !competitorIds.isEmpty())
        {
            compQuery = competitorIds.stream().map(s -> "competitors=" + s.toString()).collect(Collectors.joining("&"));
        }
        if (periods != null && !periods.isEmpty())
        {
            periodQuery = periods.stream().map(s -> "periods=" + s).collect(Collectors.joining("&"));
        }
        if (!compQuery.isEmpty())
        {
            query = "?" + compQuery;
        }
        if (!periodQuery.isEmpty())
        {
            query = query.isEmpty() ? "?" + periodQuery : query + "&" + periodQuery;
        }
        return query;
    }

    private String getChangesQueryString(Date after, URN sportId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        if (after != null) {
            ZonedDateTime zonedAfter = ZonedDateTime.ofInstant(after.toInstant(), ZoneId.of("UTC"));
            params.add(new BasicNameValuePair("afterDateTime", zonedAfter.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        }
        if (sportId != null) {
            params.add(new BasicNameValuePair("sportId", sportId.toString()));
        }
        String query = URLEncodedUtils.format(params, StandardCharsets.UTF_8);
        if (!query.isEmpty()) {
            query = "?" + query;
        }
        return query;
    }

    private List<URN> extractEventIds(List<SAPISportEvent> sportEvents) {
        if (sportEvents == null || sportEvents.isEmpty()) {
            return Collections.emptyList();
        }

        return sportEvents.stream().map(se -> URN.parse(se.getId())).collect(Collectors.toList());
    }

    /**
     * Triggers a refresh of the data from the representing tournament endpoints
     */
    private void onSportsDataTimerElapsed() {
        try {
            if(prefetchLocales == null || prefetchLocales.isEmpty()){
                return;
            }
            triggerAllSportsDataFetch(prefetchLocales);
            logger.info("Tournament data for languages [" +
                    prefetchLocales.stream().
                            map(Locale::getLanguage).collect(Collectors.joining(", ")) +
                    "] successfully fetched and merged.");
        } catch (Exception e) { // so the timer does not die
            logger.warn("onSportsDataTimerElapsed: An exception occurred while attempting to fetch tournament list data for: [" +
                    prefetchLocales.stream().
                            map(Locale::getLanguage).collect(Collectors.joining(", ")) +
                    "]. Exception was:", e);
        }
    }

    /**
     * Triggers a schedule prefetch for configured days in advance
     */
    private void onDateScheduleTimerElapsed() {
        List<Date> datesToFetch = new ArrayList<>(SCHEDULE_DAYS_PREFETCH);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        for (int day = 0; day <= SCHEDULE_DAYS_PREFETCH; day++) {
            Date prefetchDate = cal.getTime();
            if (fetchedScheduleDates.contains(prefetchDate)) {
                continue;
            }

            datesToFetch.add(prefetchDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        logger.info("Performing event schedule task for dates: {}", datesToFetch);
        try {
            prefetchLocales.forEach(loc -> datesToFetch.forEach(date -> {
                try {
                    requestEventsFor(loc, date);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            }));
            fetchedScheduleDates.addAll(datesToFetch);
        } catch (Exception e) { // so the timer does not die
            logger.warn("onDateScheduleTimerElapsed: There was a problem providing schedule for {} dates", datesToFetch, e);
        }
    }

    /**
     * Triggers the fetch of the all tournaments for all sports endpoint and the all sports endpoint
     *
     * @param locales a {@link List} specifying the languages in which to fetch the data
     */
    private void triggerAllSportsDataFetch(List<Locale> locales) throws CommunicationException {
        Preconditions.checkNotNull(locales);

        logger.info("DataRouterImpl->triggerAllSportsDataFetch [" +
                locales.stream().
                        map(Locale::getLanguage).collect(Collectors.joining(", ")) +
                "]");

        logger.info("DataRouterImpl->Refreshing tournaments/sports data");
        tournamentListDataFetched.clear();
        sportsListDataFetched.clear();
        lotteriesListDataFetched.clear();

        try {
            prefetchLocales.forEach(l -> {
                try {
                    requestAllTournamentsForAllSportsEndpoint(l);
                    requestAllSportsEndpoint(l);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
                try {
                    requestAllLotteriesEndpoint(l, false);
                } catch (CommunicationException e) {
                    logger.warn("DataRouterImpl->Lotteries endpoint request failed while refreshing tournaments/sports data", e);
                }
            });
        } catch (DataRouterStreamException e) {
            throw new CommunicationException("Error occurred while executing request to refresh all tournaments", e);
        }
    }

    private void dispatchReceivedRawApiData(String uri, Object restMessage)
    {
        if(oddsFeedExtListener == null) {
            return;
        }
        // send RawFeedMessage

        Stopwatch stopwatch = Stopwatch.createStarted();
        try
        {
            oddsFeedExtListener.onRawApiDataReceived(URI.create(uri), restMessage);
            stopwatch.stop();
            String msg = String.format("Dispatching raw api message for %s took %sms.",
                                       uri,
                                       stopwatch.elapsed(TimeUnit.MILLISECONDS));
            logger.info(msg);
        }
        catch (Exception e)
        {
            logger.error("Error dispatching raw api data for {}", uri);
            stopwatch.stop();
            String errorMsg = String.format("Error dispatching raw api data for %s. Took %sms.",
                                            uri,
                                            stopwatch.elapsed(TimeUnit.MILLISECONDS));
            logger.error(errorMsg, e);
        }
        // continue normal processing
    }
}
