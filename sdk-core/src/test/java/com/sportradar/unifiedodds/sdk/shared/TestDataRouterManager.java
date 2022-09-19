package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.CalculationFilter;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationImpl;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.serializer.Deserializer;

import java.time.Duration;
import java.util.*;

public class TestDataRouterManager implements DataRouterManager {

    private static final Logger logger = LoggerFactory.getLogger(TestDataRouterManager.class);

    public TestHttpHelper testHttpHelper;
    private final DataRouter dataRouter;

    private Duration _delay = Duration.ofSeconds(0);
    private boolean _delayVariable;
    private int _delayPercent = 10;

    public Map<String, Integer> RestCalls;

    private Object lock = new Object();

    public TestDataRouterManager(TestHttpHelper testHttpHelper, DataRouter dataRouter){
        this.testHttpHelper = testHttpHelper;
        this.dataRouter = dataRouter;
        this.RestCalls = new HashMap<>();
    }

    public int totalRestCalls() { return RestCalls.values().stream().mapToInt(i -> i).sum(); }

    @Override
    public void requestSummaryEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestFixtureEndpoint(Locale locale, URN id, boolean useCachedProvider, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestDrawSummary(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestDrawFixture(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException {

    }

    @Override
    public void requestAllSportsEndpoint(Locale locale) throws CommunicationException {

    }

    @Override
    public List<URN> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, URN tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestLotterySchedule(Locale locale, URN lotteryId, CacheItem requester) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        return null;
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestCompetitorEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public List<URN> requestSeasonsFor(Locale locale, URN tournamentID) throws CommunicationException {
        return null;
    }

    @Override
    public SAPIMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {
        return null;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, URN id, CacheItem requester) throws CommunicationException {

    }

    @Override
    public AvailableSelections requestAvailableSelections(URN id) throws CommunicationException {
        recordCall("requestAvailableSelections");
        DataProvider<CAPIAvailableSelections> dataProvider = new TestingDataProvider<>("test/rest/custombet/available_selections.xml");

        CAPIAvailableSelections result = null;
        try {
            result = dataProvider.getData();
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("available_selections", Locale.getDefault());

        if (id.getId() == 0) {
            result = null;
        }
        else if (id.getId() != 31561675) {
            result = RestMessageBuilder.getAvailableSelections(id, StaticRandom.I100());
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
        DataProvider<CAPICalculationResponse> dataProvider = new TestingDataProvider<>("test/rest/custombet/calculate_response.xml");

        CAPICalculationResponse result = null;
        try {
            result = dataProvider.getData();
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("calculate", Locale.getDefault());

        if (selections.isEmpty()) {
            result = null;
        }
        else if (selections.stream().findFirst().get().getEventId().getId() != 31561675) {
            result = RestMessageBuilder.getCalculationResponse(selections.stream().findFirst().get().getEventId(), StaticRandom.I100());
        }

        if (result != null) {
            dataRouter.onCalculateProbabilityFetched(selections, result);
            return new CalculationImpl(result);
        }

        return null;
    }

    @Override
    public CalculationFilter requestCalculateProbabilityFilter(List<Selection> selections) throws CommunicationException {
        recordCall("requestCalculateProbabilityFilter");
        DataProvider<CAPIFilteredCalculationResponse> dataProvider = new TestingDataProvider<>("test/rest/custombet/calculate_filter_response.xml");

        CAPIFilteredCalculationResponse result = null;
        try {
            result = dataProvider.getData();
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }

        executeDelay("calculate", Locale.getDefault());

        if (selections.isEmpty()) {
            result = null;
        }
        else if (selections.stream().findFirst().get().getEventId().getId() != 31561675) {
            result = RestMessageBuilder.getFilteredCalculationResponse(selections.stream().findFirst().get().getEventId(), StaticRandom.I100());
        }

        if (result != null) {
            dataRouter.onCalculateProbabilityFilterFetched(selections, result);
            return new CalculationFilterImpl(result);
        }

        return null;
    }

    @Override
    public List<FixtureChange> requestFixtureChanges(Date after, URN sportId, Locale locale) throws CommunicationException {
        return null;
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, URN sportId, Locale locale) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestListSportEvents(Locale locale, int startIndex, int limit) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestAvailableTournamentsFor(Locale locale, URN sportId) throws CommunicationException {
        return null;
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(URN id, Locale locale, List<URN> competitorIds, List<Integer> periods) throws CommunicationException {
        return null;
    }

    @Override
    public void close() {

    }

    private void recordCall(String callType) {
        synchronized (lock) {
            if (RestCalls.containsKey(callType)) {
                Integer currentValue = RestCalls.get(callType);
                RestCalls.put(callType, currentValue + 1);
            }
            else {
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

    private void executeDelay(URN id, Locale locale)
    {
        executeDelay(id.toString(), locale);
    }

    private void executeDelay(String id, Locale locale)
    {
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
            int delayMs = (int)_delay.toMillis();
            if (_delayVariable) {
                delayMs = StaticRandom.I(delayMs);
            }
            System.out.println(String.format("DRM - executing delay for {} and {}: {} ms START", id, locale.getISO3Language(), delayMs));
            Helper.sleep(delayMs);
            System.out.println(String.format("DRM - executing delay for {} and {}: {} ms END", id, locale.getISO3Language(), delayMs));
        }
    }
}
