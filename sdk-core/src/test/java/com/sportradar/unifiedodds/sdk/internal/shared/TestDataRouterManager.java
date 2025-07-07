package com.sportradar.unifiedodds.sdk.internal.shared;

import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.CapiCustomBet;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationImpl;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import com.sportradar.unifiedodds.sdk.shared.StaticRandom;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.time.Duration;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "DeclarationOrder",
        "MagicNumber",
        "MemberName",
        "NestedIfDepth",
        "OverloadMethodsDeclarationOrder",
        "ReturnCount",
        "VisibilityModifier",
    }
)
public class TestDataRouterManager implements DataRouterManager {

    private static final Logger logger = LoggerFactory.getLogger(TestDataRouterManager.class);

    public TestHttpHelper testHttpHelper;
    private final DataRouter dataRouter;

    private Duration _delay = Duration.ofSeconds(0);
    private boolean _delayVariable;
    private int _delayPercent = 10;

    public Map<String, Integer> RestCalls;

    private final Object lock = new Object();

    public TestDataRouterManager(TestHttpHelper testHttpHelper, DataRouter dataRouter) {
        this.testHttpHelper = testHttpHelper;
        this.dataRouter = dataRouter;
        this.RestCalls = new HashMap<>();
    }

    public int totalRestCalls() {
        return RestCalls.values().stream().mapToInt(i -> i).sum();
    }

    @Override
    public void requestSummaryEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestSummaryEndpoint(
        Locale locale,
        Urn id,
        CacheItem requester,
        RequestOptions requestOptions
    ) throws CommunicationException {
        // no-op
    }

    @Override
    public void requestFixtureEndpoint(Locale locale, Urn id, boolean useCachedProvider, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawSummary(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawFixture(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public void requestAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public List<Urn> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestLotterySchedule(Locale locale, Urn lotteryId, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        return null;
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestCompetitorEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public List<Urn> requestSeasonsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public SapiMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public AvailableSelections requestAvailableSelections(Urn id) throws CommunicationException {
        recordCall("requestAvailableSelections");
        DataProvider<CapiAvailableSelections> dataProvider = new TestingDataProvider<>(
            "test/rest/custombet/available_selections.xml"
        );

        CapiAvailableSelections result = null;
        try {
            result = dataProvider.getData();
        } catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("available_selections", Locale.getDefault());

        if (id.getId() == 0) {
            result = null;
        } else if (id.getId() != 31561675) {
            result = CapiCustomBet.getAvailableSelectionsResponse(id, StaticRandom.I100());
        }

        if (result != null) {
            dataRouter.onAvailableSelectionsFetched(id, result);
            return new AvailableSelectionsImpl(result);
        }

        return null;
    }

    @Override
    public Calculation requestCalculateProbability(List<Selection> selections) throws CommunicationException {
        recordCall("requestCalculateProbability");
        DataProvider<CapiCalculationResponse> dataProvider = new TestingDataProvider<>(
            "test/rest/custombet/calculate_response.xml"
        );

        CapiCalculationResponse result = null;
        try {
            result = dataProvider.getData();
        } catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("calculate", Locale.getDefault());

        if (selections.isEmpty()) {
            result = null;
        } else if (selections.stream().findFirst().get().getEventId().getId() != 31561675) {
            result =
                CapiCustomBet.getCalculationResponse(
                    selections.stream().findFirst().get().getEventId(),
                    StaticRandom.I100()
                );
        }

        if (result != null) {
            dataRouter.onCalculateProbabilityFetched(selections, result);
            return new CalculationImpl(result);
        }

        return null;
    }

    @Override
    public CalculationFilter requestCalculateProbabilityFilter(List<Selection> selections)
        throws CommunicationException {
        recordCall("requestCalculateProbabilityFilter");
        DataProvider<CapiFilteredCalculationResponse> dataProvider = new TestingDataProvider<>(
            "test/rest/custombet/calculate_filter_response.xml"
        );

        CapiFilteredCalculationResponse result = null;
        try {
            result = dataProvider.getData();
        } catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("calculate", Locale.getDefault());

        if (selections.isEmpty()) {
            result = null;
        } else if (selections.stream().findFirst().get().getEventId().getId() != 31561675) {
            result =
                CapiCustomBet.getFilteredCalculationResponse(
                    selections.stream().findFirst().get().getEventId(),
                    StaticRandom.I100()
                );
        }

        if (result != null) {
            dataRouter.onCalculateProbabilityFilterFetched(selections, result);
            return new CalculationFilterImpl(result);
        }

        return null;
    }

    @Override
    public List<FixtureChange> requestFixtureChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestListSportEvents(Locale locale, int startIndex, int limit)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestAvailableTournamentsFor(Locale locale, Urn sportId)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(
        Urn id,
        Locale locale,
        List<Urn> competitorIds,
        List<Integer> periods
    ) throws CommunicationException {
        return null;
    }

    @Override
    public void close() {}

    private void recordCall(String callType) {
        synchronized (lock) {
            if (RestCalls.containsKey(callType)) {
                Integer currentValue = RestCalls.get(callType);
                RestCalls.put(callType, currentValue + 1);
            } else {
                RestCalls.put(callType, 1);
            }
        }
    }

    /**
     * Gets the count of the calls (per specific method or all together if not type provided)
     * @param callType Type of the call
     * @return The count calls
     */
    public int getCallCount(String callType) {
        if (SdkHelper.stringIsNullOrEmpty(callType)) {
            return totalRestCalls();
        }
        if (!RestCalls.containsKey(callType)) {
            return 0;
        }
        return RestCalls.get(callType);
    }

    public void addDelay(Duration delay) {
        addDelay(delay, false, 20);
    }

    public void addDelay(Duration delay, boolean variable, int percentOfRequests) {
        _delay = delay;
        _delayVariable = variable;
        _delayPercent = percentOfRequests;
    }

    private void executeDelay(Urn id, Locale locale) {
        executeDelay(id.toString(), locale);
    }

    private void executeDelay(String id, Locale locale) {
        if (_delay != Duration.ZERO) {
            if (_delayPercent < 1) {
                return;
            }
            if (_delayPercent < 100) {
                int percent = StaticRandom.I100();
                if (percent > _delayPercent) {
                    return;
                }
            }
            int delayMs = (int) _delay.toMillis();
            if (_delayVariable) {
                delayMs = StaticRandom.I(delayMs);
            }
            System.out.printf(
                "DRM - executing delay for {} and {}: {} ms START%n",
                id,
                locale.getISO3Language(),
                delayMs
            );
            Helper.sleep(delayMs);
            System.out.printf(
                "DRM - executing delay for {} and {}: {} ms END%n",
                id,
                locale.getISO3Language(),
                delayMs
            );
        }
    }
}
