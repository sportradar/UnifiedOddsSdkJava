/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions.requestOptions;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.internal.caching.*;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.LatencyTracker;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.LongSdkHistogram;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.FixtureChangeImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.PeriodStatusImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.ResultChangeImpl;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
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
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings(
    {
        "CatchParameterName",
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "ExecutableStatementCount",
        "HiddenField",
        "IllegalCatch",
        "JavaNCSS",
        "LambdaBodyLength",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "OverloadMethodsDeclarationOrder",
        "ParameterAssignment",
        "ParameterNumber",
        "ReturnCount",
    }
)
public class DataRouterManagerImpl implements DataRouterManager {

    public static final String TELEMETRY_TAG_KEY = "endpoint";

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
    private final ExecutionPathDataProvider<Object> summaryEndpointProvider;

    /**
     * The {@link DataProvider} used to fetch fixture data
     */
    private final DataProvider<SapiFixturesEndpoint> fixtureProvider;

    /**
     * The {@link DataProvider} used to fetch fixture data without cache
     */
    private final DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureProvider;

    /**
     * A {@link DataProvider} instance used to fetch the tournament list API endpoint
     */
    private final DataProvider<SapiTournamentsEndpoint> tournamentsListProvider;

    /**
     * A {@link DataProvider} instance which is used to fill the cache with schedule endpoints
     */
    private final DataProvider<SapiScheduleEndpoint> dateScheduleProvider;

    /**
     * A {@link DataProvider} instance which is used to fill the cache
     */
    private final DataProvider<Object> tournamentScheduleProvider;

    /**
     * A {@link DataProvider} instance used to fetch the all available sports API endpoint
     */
    private final DataProvider<SapiSportsEndpoint> sportsListProvider;

    /**
     * A {@link DataProvider} used to fetch player profiles
     */
    private final DataProvider<SapiPlayerProfileEndpoint> playerProvider;

    /**
     * A {@link DataProvider} used to fetch competitor profiles
     */
    private final DataProvider<SapiCompetitorProfileEndpoint> competitorProvider;

    /**
     * A {@link DataProvider} used to fetch simpleteam profiles
     */
    private final DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeamProvider;

    /**
     * A {@link DataProvider} used to fetch tournament seasons
     */
    private final DataProvider<SapiTournamentSeasons> tournamentSeasonsDataProvider;

    /**
     * A {@link DataProvider} used to fetch draw summaries
     */
    private final DataProvider<SapiDrawSummary> drawSummaryDataProvider;

    /**
     * A {@link DataProvider} used to fetch draw fixtures
     */
    private final DataProvider<SapiDrawFixtures> drawFixtureDataProvider;

    /**
     * A {@link DataProvider} used to fetch all lotteries
     */
    private final DataProvider<SapiLotteries> lotteriesListProvider;

    /**
     * A {@link DataProvider} used to fetch lottery schedules
     */
    private final DataProvider<SapiLotterySchedule> lotteryScheduleProvider;

    /**
     * A {@link DataProvider} used to fetch match time lines
     */
    private final DataProvider<SapiMatchTimelineEndpoint> matchTimelineEndpointDataProvider;

    /**
     * A {@link DataProvider} used to fetch sport categories
     */
    private final DataProvider<SapiSportCategoriesEndpoint> sportCategoriesEndpointDataProvider;

    /**
     * A {@link DataProvider} used to fetch available selections
     */
    private final DataProvider<CapiAvailableSelections> availableSelectionsTypeDataProvider;

    /**
     * A {@link DataProvider} used to fetch probability calculations
     */
    private final DataProvider<CapiCalculationResponse> calculateProbabilityDataProvider;

    /**
     * A {@link DataProvider} used to fetch probability calculations (filtered)
     */
    private final DataProvider<CapiFilteredCalculationResponse> calculateProbabilityFilterDataProvider;

    /**
     * A {@link DataProvider} used to fetch fixture changes
     */
    private final DataProvider<SapiFixtureChangesEndpoint> fixtureChangesDataProvider;

    /**
     * A {@link DataProvider} used to fetch result changes
     */
    private final DataProvider<SapiResultChangesEndpoint> resultChangesDataProvider;

    /**
     * A {@link DataProvider} instance which is used to get list of sport events
     */
    private final DataProvider<SapiScheduleEndpoint> listSportEventsProvider;

    /**
     * A {@link DataProvider} instance which is used to get all available tournaments for specific sport
     */
    private final DataProvider<SapiSportTournamentsEndpoint> availableSportTournamentsProvider;

    /**
     * A {@link DataProvider} instance which is used to get period summary endpoint for specific sport event
     */
    private final DataProvider<SapiStagePeriodEndpoint> periodSummaryDataProvider;
    private final TelemetryFactory telemetryFactory;

    /**
     * The extended odds feed listener
     */
    private UofExtListener uofExtListener;

    private final ReentrantLock tournamentListLock = new ReentrantLock();
    private final ReentrantLock sportsListLock = new ReentrantLock();
    private final ReentrantLock lotteriesListLock = new ReentrantLock();

    private boolean isFeedClosed;

    @Inject
    DataRouterManagerImpl(
        SdkInternalConfiguration configuration,
        SdkTaskScheduler scheduler,
        SdkProducerManager producerManager,
        DataRouter dataRouter,
        @Named("InternalSdkTelemetryFactory") TelemetryFactory telemetryFactory,
        @Named("SummaryEndpointDataProvider") ExecutionPathDataProvider<Object> summaryEndpointProvider,
        @Named("FixtureEndpointDataProvider") DataProvider<SapiFixturesEndpoint> fixtureProvider,
        @Named(
            "FixtureChangeFixtureEndpointDataProvider"
        ) DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureProvider,
        DataProvider<SapiTournamentsEndpoint> tournamentsListProvider,
        @Named("DateScheduleEndpointDataProvider") DataProvider<SapiScheduleEndpoint> dateScheduleProvider,
        @Named("TournamentScheduleProvider") DataProvider<Object> tournamentScheduleProvider,
        DataProvider<SapiSportsEndpoint> sportsListProvider,
        DataProvider<SapiPlayerProfileEndpoint> playerProvider,
        DataProvider<SapiCompetitorProfileEndpoint> competitorProvider,
        DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeamProvider,
        DataProvider<SapiTournamentSeasons> tournamentSeasonsDataProvider,
        DataProvider<SapiMatchTimelineEndpoint> matchTimelineEndpointDataProvider,
        DataProvider<SapiSportCategoriesEndpoint> sportCategoriesEndpointDataProvider,
        DataProvider<SapiDrawSummary> drawSummaryDataProvider,
        DataProvider<SapiDrawFixtures> drawFixtureDataProvider,
        DataProvider<SapiLotteries> lotteriesListProvider,
        DataProvider<SapiLotterySchedule> lotteryScheduleProvider,
        DataProvider<CapiAvailableSelections> availableSelectionsTypeDataProvider,
        DataProvider<CapiCalculationResponse> calculateProbabilityDataProvider,
        DataProvider<CapiFilteredCalculationResponse> calculateProbabilityFilterDataProvider,
        DataProvider<SapiFixtureChangesEndpoint> fixtureChangesDataProvider,
        DataProvider<SapiResultChangesEndpoint> resultChangesDataProvider,
        @Named("ListSportEventsDataProvider") DataProvider<SapiScheduleEndpoint> listSportEventsProvider,
        DataProvider<SapiSportTournamentsEndpoint> availableSportTournamentsProvider,
        DataProvider<SapiStagePeriodEndpoint> periodSummaryDataProvider
    ) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(producerManager);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(dataRouter);
        Preconditions.checkNotNull(telemetryFactory);
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
        Preconditions.checkNotNull(calculateProbabilityFilterDataProvider);
        Preconditions.checkNotNull(fixtureChangesDataProvider);
        Preconditions.checkNotNull(resultChangesDataProvider);
        Preconditions.checkNotNull(listSportEventsProvider);
        Preconditions.checkNotNull(availableSportTournamentsProvider);
        Preconditions.checkNotNull(periodSummaryDataProvider);

        this.prefetchLocales = configuration.getDesiredLocales();
        this.isWnsActive =
            producerManager
                .getActiveProducers()
                .values()
                .stream()
                .anyMatch(p -> p.getId() == 7 && p.isEnabled());

        this.dataRouter = dataRouter;
        this.telemetryFactory = telemetryFactory;
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
        this.calculateProbabilityFilterDataProvider = calculateProbabilityFilterDataProvider;
        this.fixtureChangesDataProvider = fixtureChangesDataProvider;
        this.resultChangesDataProvider = resultChangesDataProvider;
        this.listSportEventsProvider = listSportEventsProvider;
        this.availableSportTournamentsProvider = availableSportTournamentsProvider;
        this.periodSummaryDataProvider = periodSummaryDataProvider;
        this.tournamentListDataFetched =
            Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.sportsListDataFetched = Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.lotteriesListDataFetched = Collections.synchronizedList(new ArrayList<>(prefetchLocales.size()));
        this.fetchedScheduleDates = Collections.synchronizedList(new ArrayList<>(SCHEDULE_DAYS_PREFETCH));

        scheduler.scheduleAtFixedRate(
            "DateSchedulePrefetchTask",
            this::onDateScheduleTimerElapsed,
            5,
            60 * 60 * 12L,
            TimeUnit.SECONDS
        );

        // initial is triggered as soon as it is needed
        scheduler.scheduleAtFixedRate(
            "SportsDataRefreshTask",
            this::onSportsDataTimerElapsed,
            12,
            12,
            TimeUnit.HOURS
        );

        this.isFeedClosed = false;
    }

    public void addUofExtListener(UofExtListener uofExtListener) {
        Preconditions.checkNotNull(uofExtListener);
        this.uofExtListener = uofExtListener;
    }

    @Override
    public void requestSummaryEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        RequestOptions requestOptions = requestOptions()
            .setExecutionPath(ExecutionPath.TIME_CRITICAL)
            .build();
        requestSummaryEndpoint(locale, id, requester, requestOptions);
    }

    @Override
    public void requestSummaryEndpoint(
        Locale locale,
        Urn id,
        CacheItem requester,
        RequestOptions requestOptions
    ) throws CommunicationException {
        Object endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportEventSummary"
            )
        ) {
            endpoint = summaryEndpointProvider.getData(requestOptions, locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing summary request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("summary[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            summaryEndpointProvider.getFinalUrl(requestOptions, locale, id.toString()),
            endpoint
        );

        dataRouter.onSummaryFetched(id, endpoint, locale, requester);
    }

    @Override
    public void requestFixtureEndpoint(Locale locale, Urn id, boolean useCachedProvider, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiFixturesEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportEventFixture"
            )
        ) {
            DataProvider<SapiFixturesEndpoint> provider = useCachedProvider
                ? fixtureProvider
                : fixtureChangeFixtureProvider;
            endpoint = provider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            if (
                !useCachedProvider &&
                e.getCause() != null &&
                SdkHelper.checkCauseReason(e.getCause(), "InternalServerError")
            ) {
                try {
                    endpoint = fixtureProvider.getData(locale, id.toString());
                } catch (DataProviderException e1) {
                    throw new CommunicationException(
                        String.format(
                            "Error executing cached fixture request for id=%s, locale=%s",
                            id,
                            locale
                        ),
                        e.tryExtractCommunicationExceptionUrl(
                            String.format("fixture[%s]: %s", locale.getISO3Language(), id)
                        ),
                        e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                        e
                    );
                }
            } else {
                throw new CommunicationException(
                    String.format("Error executing fixture request for id=%s, locale=%s", id, locale),
                    e.tryExtractCommunicationExceptionUrl(
                        String.format("fixture[%s]: %s", locale.getISO3Language(), id)
                    ),
                    e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                    e
                );
            }
        }

        String finalUrl = useCachedProvider
            ? fixtureProvider.getFinalUrl(locale, id.toString())
            : fixtureChangeFixtureProvider.getFinalUrl(locale, id.toString());
        dispatchReceivedRawApiData(finalUrl, endpoint);

        SapiFixture fixture = endpoint.getFixture();
        Urn fixtureId = Urn.parse(fixture.getId());
        dataRouter.onFixtureFetched(fixtureId, fixture, locale, requester);
    }

    @Override
    public void requestDrawSummary(Locale locale, Urn id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiDrawSummary endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "DrawSummary"
            )
        ) {
            endpoint = drawSummaryDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing draw summary request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("draw_summary[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(drawSummaryDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        Urn drawId = Urn.parse(endpoint.getDrawFixture().getId());
        dataRouter.onDrawSummaryFetched(drawId, endpoint, locale, requester);
    }

    @Override
    public void requestDrawFixture(Locale locale, Urn id, CacheItem requester) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiDrawFixtures endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "DrawFixture"
            )
        ) {
            endpoint = drawFixtureDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing draw fixture request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("draw_fixture[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(drawFixtureDataProvider.getFinalUrl(locale, id.toString()), endpoint);

        SapiDrawFixture drawFixture = endpoint.getDrawFixture();
        if (drawFixture != null) {
            Urn drawId = Urn.parse(drawFixture.getId());
            dataRouter.onDrawFixtureFetched(drawId, drawFixture, locale, requester);
        } else {
            logger.warn(
                "Requested draw fixture returned an empty response - drawFixture == null [{} - {}]",
                id,
                locale
            );
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

            SapiTournamentsEndpoint endpoint;
            try (
                LatencyTracker ignored = telemetryFactory.latencyHistogram(
                    LongSdkHistogram.DATA_ROUTER_MANAGER,
                    TELEMETRY_TAG_KEY,
                    "AllTournamentsForAllSport"
                )
            ) {
                endpoint = tournamentsListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(
                    String.format("Error executing all tournaments list request for locale=%s", locale),
                    e.tryExtractCommunicationExceptionUrl(
                        String.format("alltournamentsforallsports[%s]", locale.getISO3Language())
                    ),
                    e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                    e
                );
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

            SapiSportsEndpoint endpoint;
            try (
                LatencyTracker ignored = telemetryFactory.latencyHistogram(
                    LongSdkHistogram.DATA_ROUTER_MANAGER,
                    TELEMETRY_TAG_KEY,
                    "AllSports"
                )
            ) {
                endpoint = sportsListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(
                    String.format("Error execution all sports request for locale=%s", locale),
                    e.tryExtractCommunicationExceptionUrl(
                        String.format("allsports[%s]", locale.getISO3Language())
                    ),
                    e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                    e
                );
            }

            dispatchReceivedRawApiData(sportsListProvider.getFinalUrl(locale, ""), endpoint);

            dataRouter.onSportsListFetched(endpoint, locale);

            sportsListDataFetched.add(locale);
        } finally {
            sportsListLock.unlock();
        }
    }

    @Override
    public List<Urn> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult)
        throws CommunicationException {
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

            SapiLotteries endpoint;
            try (
                LatencyTracker ignored = telemetryFactory.latencyHistogram(
                    LongSdkHistogram.DATA_ROUTER_MANAGER,
                    TELEMETRY_TAG_KEY,
                    "AllLotteries"
                )
            ) {
                endpoint = lotteriesListProvider.getData(locale);
            } catch (DataProviderException e) {
                throw new CommunicationException(
                    String.format("Error executing all lotteries list request for locale=%s", locale),
                    e.tryExtractCommunicationExceptionUrl(
                        String.format("lotteries[%s]", locale.getISO3Language())
                    ),
                    e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                    e
                );
            }

            dispatchReceivedRawApiData(lotteriesListProvider.getFinalUrl(locale, ""), endpoint);

            dataRouter.onAllLotteriesListFetched(endpoint, locale);

            lotteriesListDataFetched.add(locale);

            return endpoint.getLottery().stream().map(e -> Urn.parse(e.getId())).collect(Collectors.toList());
        } finally {
            lotteriesListLock.unlock();
        }
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(tournamentId);

        Object endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportEventsForTournament"
            )
        ) {
            endpoint = tournamentScheduleProvider.getData(locale, tournamentId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing tournament schedule request for id=%s, locale=%s",
                    tournamentId,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("tournament_events[%s]: %s", locale.getISO3Language(), tournamentId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            tournamentScheduleProvider.getFinalUrl(locale, tournamentId.toString()),
            endpoint
        );

        dataRouter.onTournamentScheduleFetched(endpoint, locale);

        if (endpoint instanceof SapiTournamentSchedule) {
            return extractEventIds(
                ((SapiTournamentSchedule) endpoint).getSportEvents()
                    .stream()
                    .flatMap(seWrapper -> seWrapper.getSportEvent().stream())
                    .collect(Collectors.toList())
            );
        } else if (endpoint instanceof SapiRaceScheduleEndpoint) {
            SapiRaceScheduleEndpoint raceSchedule = (SapiRaceScheduleEndpoint) endpoint;
            if (
                raceSchedule.getSportEvents() != null && raceSchedule.getSportEvents().getSportEvent() != null
            ) {
                return raceSchedule
                    .getSportEvents()
                    .getSportEvent()
                    .stream()
                    .map(e -> Urn.parse(e.getId()))
                    .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<Urn> requestLotterySchedule(Locale locale, Urn lotteryId, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(lotteryId);

        SapiLotterySchedule endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "LotterySchedule"
            )
        ) {
            endpoint = lotteryScheduleProvider.getData(locale, lotteryId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing lottery schedule request for id=%s, locale=%s",
                    lotteryId,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("lottery_schedule[%s]: %s", locale.getISO3Language(), lotteryId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            lotteryScheduleProvider.getFinalUrl(locale, lotteryId.toString()),
            endpoint
        );

        dataRouter.onLotteryScheduleFetched(endpoint, locale, requester);

        if (endpoint.getDrawEvents() != null && endpoint.getDrawEvents().getDrawEvent() != null) {
            return endpoint
                .getDrawEvents()
                .getDrawEvent()
                .stream()
                .map(draw -> Urn.parse(draw.getId()))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        Preconditions.checkNotNull(locale);

        SapiScheduleEndpoint endpoint;
        String formattedDate = "live";
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportEventsForDate"
            )
        ) {
            if (date != null) {
                formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                endpoint = dateScheduleProvider.getData(locale, formattedDate);
            } else {
                endpoint = dateScheduleProvider.getData(locale, formattedDate);
            }
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing tournament schedule request for id=%s, locale=%s",
                    date == null ? "live" : date,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("date_schedule[%s]: %s", locale.getISO3Language(), date)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(dateScheduleProvider.getFinalUrl(locale, formattedDate), endpoint);

        dataRouter.onDateScheduleFetched(endpoint, locale);

        return extractEventIds(endpoint.getSportEvent());
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiPlayerProfileEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "PlayerProfile"
            )
        ) {
            endpoint = playerProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing player profile request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("player_profile[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(playerProvider.getFinalUrl(locale, id.toString()), endpoint);

        SapiPlayerExtended player = endpoint.getPlayer();
        Urn playerId = Urn.parse(player.getId());
        dataRouter.onPlayerFetched(playerId, player, locale, requester, null);
    }

    @Override
    public void requestCompetitorEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiCompetitorProfileEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "CompetitorProfile"
            )
        ) {
            endpoint = competitorProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing competitor profile request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("competitor_profile[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(competitorProvider.getFinalUrl(locale, id.toString()), endpoint);

        SapiTeamExtended competitor = endpoint.getCompetitor();
        Urn competitorId = Urn.parse(competitor.getId());
        dataRouter.onCompetitorFetched(competitorId, endpoint, locale, requester);
    }

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiSimpleTeamProfileEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "CompetitorProfile"
            )
        ) {
            endpoint = simpleTeamProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing simpleteam profile request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("simpleteam_profile[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(simpleTeamProvider.getFinalUrl(locale, id.toString()), endpoint);

        SapiTeam competitor = endpoint.getCompetitor();
        Urn competitorId = Urn.parse(competitor.getId());
        dataRouter.onSimpleTeamFetched(competitorId, endpoint, locale, requester);
        if (!competitorId.equals(id)) {
            dataRouter.onSimpleTeamFetched(id, endpoint, locale, requester);
        }
    }

    @Override
    public List<Urn> requestSeasonsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(tournamentId);

        SapiTournamentSeasons endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SeasonsForTournament"
            )
        ) {
            endpoint = tournamentSeasonsDataProvider.getData(locale, tournamentId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing tournament seasons request for id=%s, locale=%s",
                    tournamentId,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("tournament_seasons[%s]: %s", locale.getISO3Language(), tournamentId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            tournamentSeasonsDataProvider.getFinalUrl(locale, tournamentId.toString()),
            endpoint
        );

        dataRouter.onTournamentSeasonsFetched(tournamentId, endpoint, locale);

        if (endpoint.getSeasons() != null) {
            return endpoint
                .getSeasons()
                .getSeason()
                .stream()
                .map(s -> Urn.parse(s.getId()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public SapiMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiMatchTimelineEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "InformationAboutOngoingEvent"
            )
        ) {
            endpoint = matchTimelineEndpointDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing match timeline request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("event_timeline[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            matchTimelineEndpointDataProvider.getFinalUrl(locale, id.toString()),
            endpoint
        );

        dataRouter.onMatchTimelineFetched(id, endpoint, locale, requester);

        return endpoint;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(id);

        SapiSportCategoriesEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportCategories"
            )
        ) {
            endpoint = sportCategoriesEndpointDataProvider.getData(locale, id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing sport categories request for id=%s, locale=%s", id, locale),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("sport_categories[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            sportCategoriesEndpointDataProvider.getFinalUrl(locale, id.toString()),
            endpoint
        );

        dataRouter.onSportCategoriesFetched(endpoint, locale, requester);
    }

    @Override
    public AvailableSelections requestAvailableSelections(Urn id) throws CommunicationException {
        Preconditions.checkNotNull(id);

        CapiAvailableSelections availableSelections;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "AvailableSelections"
            )
        ) {
            availableSelections = availableSelectionsTypeDataProvider.getData(id.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format("Error executing available selections request for id=%s", id),
                e.tryExtractCommunicationExceptionUrl(String.format("custombet_availableselections: %s", id)),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dataRouter.onAvailableSelectionsFetched(id, availableSelections);
        return new AvailableSelectionsImpl(availableSelections);
    }

    @Override
    public Calculation requestCalculateProbability(List<Selection> selections) throws CommunicationException {
        Preconditions.checkNotNull(selections);

        CapiCalculationResponse calculation;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "CalculateProbability"
            )
        ) {
            CapiSelections content = new CapiSelections();
            content
                .getSelections()
                .addAll(
                    selections
                        .stream()
                        .map(s -> {
                            CapiSelectionType selection = new CapiSelectionType();
                            selection.setId(s.getEventId().toString());
                            selection.setMarketId(s.getMarketId());
                            selection.setSpecifiers(s.getSpecifiers());
                            selection.setOutcomeId(s.getOutcomeId());
                            selection.setOdds(s.getOdds());
                            return selection;
                        })
                        .collect(Collectors.toList())
                );

            calculation = calculateProbabilityDataProvider.postData(content);
        } catch (DataProviderException e) {
            throw new CommunicationException(
                "Error executing calculate probability request",
                e.tryExtractCommunicationExceptionUrl("calculate_probability"),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dataRouter.onCalculateProbabilityFetched(selections, calculation);
        return new CalculationImpl(calculation);
    }

    @Override
    public CalculationFilter requestCalculateProbabilityFilter(List<Selection> selections)
        throws CommunicationException {
        Preconditions.checkNotNull(selections);

        CapiFilteredCalculationResponse calculation;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "CalculateProbabilityFiltered"
            )
        ) {
            CapiFilterSelections content = new CapiFilterSelections();

            for (Selection selection : selections) {
                CapiFilterSelectionMarketType filterSelectionMarketType = new CapiFilterSelectionMarketType();
                filterSelectionMarketType.setMarketId(selection.getMarketId());
                filterSelectionMarketType.setOutcomeId(selection.getOutcomeId());
                filterSelectionMarketType.setSpecifiers(selection.getSpecifiers());
                filterSelectionMarketType.setOdds(selection.getOdds());

                CapiFilterSelectionType filterSelectionType = new CapiFilterSelectionType();
                filterSelectionType.setId(selection.getEventId().toString());
                filterSelectionType.getMarkets().add(filterSelectionMarketType);
                content.getSelections().add(filterSelectionType);
            }

            calculation = calculateProbabilityFilterDataProvider.postData(content);
        } catch (DataProviderException e) {
            throw new CommunicationException(
                "Error executing calculate probability request",
                e.tryExtractCommunicationExceptionUrl("calculate_probability_filter"),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dataRouter.onCalculateProbabilityFilterFetched(selections, calculation);
        return new CalculationFilterImpl(calculation);
    }

    @Override
    public List<FixtureChange> requestFixtureChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "FixtureChanges"
            )
        ) {
            String query = getChangesQueryString(after, sportId);
            return fixtureChangesDataProvider
                .getData(locale, query)
                .getFixtureChange()
                .stream()
                .map(FixtureChangeImpl::new)
                .collect(Collectors.toList());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                "Error executing fixture changes request",
                e.tryExtractCommunicationExceptionUrl(
                    String.format("fixture_changes[%s]: %s", locale.getISO3Language(), sportId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "ResultChanges"
            )
        ) {
            String query = getChangesQueryString(after, sportId);
            return resultChangesDataProvider
                .getData(locale, query)
                .getResultChange()
                .stream()
                .map(ResultChangeImpl::new)
                .collect(Collectors.toList());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                "Error executing result changes request",
                e.tryExtractCommunicationExceptionUrl(
                    String.format("result_changes[%s]: %s", locale.getISO3Language(), sportId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }
    }

    @Override
    public List<Urn> requestListSportEvents(Locale locale, int startIndex, int limit)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);

        SapiScheduleEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "ListOfSportEvents"
            )
        ) {
            endpoint =
                listSportEventsProvider.getData(locale, String.valueOf(startIndex), String.valueOf(limit));
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing list sport events request for startIndex=%s, limit=%s, locale=%s",
                    startIndex,
                    limit,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("sport_events[%s]", locale.getISO3Language())
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            listSportEventsProvider.getFinalUrl(
                locale,
                new String[] { String.valueOf(startIndex), String.valueOf(limit) }
            ),
            endpoint
        );

        dataRouter.onListSportEventsFetched(endpoint, locale);

        return extractEventIds(endpoint.getSportEvent());
    }

    @Override
    public List<Urn> requestAvailableTournamentsFor(Locale locale, Urn sportId)
        throws CommunicationException {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(sportId);

        SapiSportTournamentsEndpoint endpoint;
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "SportAvailableTournaments"
            )
        ) {
            endpoint = availableSportTournamentsProvider.getData(locale, sportId.toString());
        } catch (DataProviderException e) {
            throw new CommunicationException(
                String.format(
                    "Error executing getting available tournaments for id=%s, locale=%s",
                    sportId,
                    locale
                ),
                e.tryExtractCommunicationExceptionUrl(
                    String.format("available_tournaments[%s]: %s", locale.getISO3Language(), sportId)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }

        dispatchReceivedRawApiData(
            availableSportTournamentsProvider.getFinalUrl(locale, sportId.toString()),
            endpoint
        );

        dataRouter.onSportTournamentsFetched(sportId, endpoint, locale);

        if (endpoint.getTournaments() != null) {
            return endpoint
                .getTournaments()
                .getTournament()
                .stream()
                .map(s -> Urn.parse(s.getId()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(
        Urn id,
        Locale locale,
        List<Urn> competitorIds,
        List<Integer> periods
    ) throws CommunicationException {
        Preconditions.checkNotNull(id);

        if (locale == null) {
            locale = prefetchLocales.get(0);
        }

        List<PeriodStatus> results = new ArrayList<>();
        try (
            LatencyTracker ignored = telemetryFactory.latencyHistogram(
                LongSdkHistogram.DATA_ROUTER_MANAGER,
                TELEMETRY_TAG_KEY,
                "PeriodSummary"
            )
        ) {
            String query = getPeriodSummaryQueryString(competitorIds, periods);
            SapiStagePeriodEndpoint response = periodSummaryDataProvider.getData(
                locale,
                id.toString(),
                query
            );
            if (
                response != null &&
                response.getPeriodStatuses() != null &&
                response.getPeriodStatuses().getPeriodStatus() != null
            ) {
                results =
                    response
                        .getPeriodStatuses()
                        .getPeriodStatus()
                        .stream()
                        .map(PeriodStatusImpl::new)
                        .collect(Collectors.toList());
            }
        } catch (DataProviderException e) {
            throw new CommunicationException(
                "Error executing period summary request",
                e.tryExtractCommunicationExceptionUrl(
                    String.format("period_summary[%s]: %s", locale.getISO3Language(), id)
                ),
                e.tryExtractCommunicationExceptionHttpStatusCode(-1),
                e
            );
        }
        return results;
    }

    @Override
    public void close() {
        this.isFeedClosed = true;
    }

    private String getPeriodSummaryQueryString(List<Urn> competitorIds, List<Integer> periods) {
        //host/v1/sports/en/sport_events/sr:stage:{id}/period_summary.xml?competitors=sr:competitor:{id}&competitors=sr:competitor:{id}&periods=2&periods=3&periods=4
        String query = "";
        String compQuery = "";
        String periodQuery = "";
        if (competitorIds != null && !competitorIds.isEmpty()) {
            compQuery =
                competitorIds
                    .stream()
                    .map(s -> "competitors=" + s.toString())
                    .collect(Collectors.joining("&"));
        }
        if (periods != null && !periods.isEmpty()) {
            periodQuery = periods.stream().map(s -> "periods=" + s).collect(Collectors.joining("&"));
        }
        if (!compQuery.isEmpty()) {
            query = "?" + compQuery;
        }
        if (!periodQuery.isEmpty()) {
            query = query.isEmpty() ? "?" + periodQuery : query + "&" + periodQuery;
        }
        return query;
    }

    private String getChangesQueryString(Date after, Urn sportId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        if (after != null) {
            ZonedDateTime zonedAfter = ZonedDateTime.ofInstant(after.toInstant(), ZoneId.of("UTC"));
            params.add(
                new BasicNameValuePair(
                    "afterDateTime",
                    zonedAfter.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
            );
        }
        if (sportId != null) {
            params.add(new BasicNameValuePair("sportId", sportId.toString()));
        }
        return new URIBuilder().addParameters(params).setCharset(StandardCharsets.UTF_8).toString();
    }

    private List<Urn> extractEventIds(List<SapiSportEvent> sportEvents) {
        if (sportEvents == null || sportEvents.isEmpty()) {
            return Collections.emptyList();
        }

        return sportEvents.stream().map(se -> Urn.parse(se.getId())).collect(Collectors.toList());
    }

    /**
     * Triggers a refresh of the data from the representing tournament endpoints
     */
    void onSportsDataTimerElapsed() {
        try {
            if (prefetchLocales == null || prefetchLocales.isEmpty()) {
                return;
            }
            triggerAllSportsDataFetch(prefetchLocales);
            logger.info(
                "Tournament data for languages [" +
                prefetchLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", ")) +
                "] successfully fetched and merged."
            );
        } catch (Exception e) { // so the timer does not die
            logger.warn(
                "onSportsDataTimerElapsed: An exception occurred while attempting to fetch tournament list data for: [" +
                prefetchLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", ")) +
                "]. Exception was:",
                e
            );
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
            prefetchLocales.forEach(loc ->
                datesToFetch.forEach(date -> {
                    try {
                        requestEventsFor(loc, date);
                    } catch (CommunicationException e) {
                        throw new DataRouterStreamException(e.getMessage(), e);
                    }
                })
            );
            fetchedScheduleDates.addAll(datesToFetch);
        } catch (Exception e) { // so the timer does not die
            logger.warn(
                "onDateScheduleTimerElapsed: There was a problem providing schedule for {} dates",
                datesToFetch,
                e
            );
        }
    }

    /**
     * Triggers the fetch of the all tournaments for all sports endpoint and the all sports endpoint
     *
     * @param locales a {@link List} specifying the languages in which to fetch the data
     */
    private void triggerAllSportsDataFetch(List<Locale> locales) throws CommunicationException {
        Preconditions.checkNotNull(locales);

        logger.info(
            "DataRouterImpl->triggerAllSportsDataFetch [" +
            locales.stream().map(Locale::getLanguage).collect(Collectors.joining(", ")) +
            "]"
        );

        logger.info("DataRouterImpl->Refreshing tournaments/sports data");
        tournamentListDataFetched.clear();
        sportsListDataFetched.clear();
        lotteriesListDataFetched.clear();

        for (Locale locale : prefetchLocales) {
            try {
                requestAllTournamentsForAllSportsEndpoint(locale);
                requestAllSportsEndpoint(locale);
            } catch (CommunicationException e) {
                throw e;
            }
            try {
                requestAllLotteriesEndpoint(locale, false);
            } catch (CommunicationException e) {
                logger.warn(
                    "DataRouterImpl->Lotteries endpoint request failed while refreshing tournaments/sports data",
                    e
                );
            }
        }
    }

    private void dispatchReceivedRawApiData(String uri, Object restMessage) {
        if (uofExtListener == null) {
            return;
        }

        if (isFeedClosed) {
            return;
        }

        // send RawFeedMessage

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            uofExtListener.onRawApiDataReceived(URI.create(uri), restMessage);
            stopwatch.stop();
            String msg = String.format(
                "Dispatching raw api message for %s took %s ms.",
                uri,
                stopwatch.elapsed(TimeUnit.MILLISECONDS)
            );
            logger.info(msg);
        } catch (Exception e) {
            stopwatch.stop();
            String errorMsg = String.format(
                "Error dispatching raw api data for %s. Took %s ms.",
                uri,
                stopwatch.elapsed(TimeUnit.MILLISECONDS)
            );
            logger.error(errorMsg, e);
        }
        // continue normal processing
    }
}
