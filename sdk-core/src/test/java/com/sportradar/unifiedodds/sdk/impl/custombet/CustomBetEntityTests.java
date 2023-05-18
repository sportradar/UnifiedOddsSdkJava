package com.sportradar.unifiedodds.sdk.impl.custombet;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilder;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilderImpl;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.conn.SdkConnListener;
import com.sportradar.unifiedodds.sdk.custombetentities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.shared.RestMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.SdkMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.TestDataRouterManager;
import com.sportradar.unifiedodds.sdk.shared.TestFeed;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings(
    { "ClassFanOutComplexity", "MagicNumber", "MethodLength", "MethodNameTest", "MultipleStringLiterals" }
)
public class CustomBetEntityTests {

    private DataRouterManager dataRouterManager;
    private CustomBetSelectionBuilder customBetSelectionBuilder;
    private TestFeed feed;
    private OddsFeedConfiguration config;
    private SdkConnListener sdkListener;

    @Before
    public void setup() {
        config =
            OddsFeed
                .getOddsFeedConfigurationBuilder()
                .setAccessToken("testuser")
                .selectCustom()
                .setMessagingUsername(Constants.SDK_USERNAME)
                .setMessagingPassword(Constants.SDK_PASSWORD)
                .setMessagingHost(Constants.RABBIT_IP)
                .useMessagingSsl(false)
                .setApiHost(Constants.RABBIT_IP)
                .setDefaultLocale(Locale.ENGLISH)
                .setMessagingVirtualHost(Constants.UF_VIRTUALHOST)
                .setMinIntervalBetweenRecoveryRequests(20)
                .build();

        sdkListener = new SdkConnListener();
        feed = new TestFeed(sdkListener, config, sdkListener);

        DataRouterImpl dataRouter = new DataRouterImpl();
        dataRouter.setDataListeners(new ArrayList<>());
        dataRouterManager = new TestDataRouterManager(feed.TestHttpHelper, dataRouter);
        customBetSelectionBuilder = new CustomBetSelectionBuilderImpl();
    }

    @Test
    public void AvailableSelectionsMapTest() {
        CAPIAvailableSelections availableSelectionsType = RestMessageBuilder.getAvailableSelections(
            URN.parse("sr:match:1000"),
            10
        );
        AvailableSelections resultAvailableSelections = SdkMessageBuilder.getAvailableSelections(
            availableSelectionsType
        );
        AvailableSelectionsCompare(availableSelectionsType, resultAvailableSelections);
    }

    @Test
    public void AvailableSelectionsEmptyMapTest() {
        CAPIAvailableSelections availableSelectionsType = RestMessageBuilder.getAvailableSelections(
            URN.parse("sr:match:1000"),
            0
        );
        Assert.assertTrue(availableSelectionsType.getEvent().getMarkets().getMarkets().isEmpty());
        AvailableSelections resultAvailableSelections = SdkMessageBuilder.getAvailableSelections(
            availableSelectionsType
        );
        Assert.assertTrue(resultAvailableSelections.getMarkets().isEmpty());
        AvailableSelectionsCompare(availableSelectionsType, resultAvailableSelections);
    }

    @Test
    public void CalculationEmptyMapTest() throws ParseException {
        CAPICalculationResponse calculationResponseType = RestMessageBuilder.getCalculationResponse(
            URN.parse("sr:match:1000"),
            0
        );
        Calculation calculation = SdkMessageBuilder.getCalculation(calculationResponseType);

        Assert.assertNotNull(calculationResponseType);
        Assert.assertNotNull(calculation);
        Assert.assertTrue(calculationResponseType.getAvailableSelections().getEvents().isEmpty());
        Assert.assertTrue(calculation.getAvailableSelections().isEmpty());

        Assert.assertEquals(
            0,
            Double.compare(calculationResponseType.getCalculation().getOdds(), calculation.getOdds())
        );
        Assert.assertEquals(
            0,
            Double.compare(
                calculationResponseType.getCalculation().getProbability(),
                calculation.getProbability()
            )
        );
        if (calculationResponseType.getGeneratedAt().isEmpty()) {
            Assert.assertNull(calculation.getGeneratedAt());
        } else {
            Assert.assertEquals(
                SdkHelper.toDate(calculationResponseType.getGeneratedAt()),
                calculation.getGeneratedAt()
            );
        }
    }

    @Test
    public void CalculationMapTest() throws ParseException {
        CAPICalculationResponse calculationResponseType = RestMessageBuilder.getCalculationResponse(
            URN.parse("sr:match:1000"),
            7
        );
        Calculation calculation = SdkMessageBuilder.getCalculation(calculationResponseType);

        Assert.assertNotNull(calculationResponseType);
        Assert.assertNotNull(calculation);

        Assert.assertEquals(
            0,
            Double.compare(calculationResponseType.getCalculation().getOdds(), calculation.getOdds())
        );
        Assert.assertEquals(
            0,
            Double.compare(
                calculationResponseType.getCalculation().getProbability(),
                calculation.getProbability()
            )
        );
        if (calculationResponseType.getGeneratedAt().isEmpty()) {
            Assert.assertNull(calculation.getGeneratedAt());
        } else {
            Assert.assertEquals(
                SdkHelper.toDate(calculationResponseType.getGeneratedAt()),
                calculation.getGeneratedAt()
            );
        }

        Assert.assertEquals(
            calculationResponseType.getAvailableSelections().getEvents().size(),
            calculation.getAvailableSelections().size()
        );
        for (int i = 0; i < calculationResponseType.getAvailableSelections().getEvents().size(); i++) {
            CAPIEventType sourceAvailableSelection = calculationResponseType
                .getAvailableSelections()
                .getEvents()
                .get(i);
            AvailableSelections resultAvailableSelection = calculation.getAvailableSelections().get(i);

            Assert.assertEquals(
                URN.parse(sourceAvailableSelection.getId()),
                resultAvailableSelection.getEvent()
            );
            for (CAPIMarketType sourceMarket : sourceAvailableSelection.getMarkets().getMarkets()) {
                Market resultMarket = resultAvailableSelection
                    .getMarkets()
                    .stream()
                    .filter(f -> f.getId() == sourceMarket.getId())
                    .findFirst()
                    .get();
                Assert.assertNotNull(resultMarket);
                MarketCompare(sourceMarket, resultMarket);
            }
        }
        int marketCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream())
            .collect(Collectors.toList())
            .size();
        int outcomeCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream().flatMap(f2 -> f2.getOutcomes().stream()))
            .collect(Collectors.toList())
            .size();
        System.out.println(
            String.format("Calculation has {} markets and {} outcomes.", marketCount, outcomeCount)
        );
    }

    @Test
    public void CalculationFilterEmptyMapTest() throws ParseException {
        CAPIFilteredCalculationResponse calculationResponseType = RestMessageBuilder.getFilteredCalculationResponse(
            URN.parse("sr:match:1000"),
            0
        );
        CalculationFilter calculation = SdkMessageBuilder.getCalculationFilter(calculationResponseType);

        Assert.assertNotNull(calculationResponseType);
        Assert.assertNotNull(calculation);
        Assert.assertTrue(calculationResponseType.getAvailableSelections().getEvents().isEmpty());
        Assert.assertTrue(calculation.getAvailableSelections().isEmpty());

        Assert.assertEquals(
            0,
            Double.compare(calculationResponseType.getCalculation().getOdds(), calculation.getOdds())
        );
        Assert.assertEquals(
            0,
            Double.compare(
                calculationResponseType.getCalculation().getProbability(),
                calculation.getProbability()
            )
        );
        if (calculationResponseType.getGeneratedAt().isEmpty()) {
            Assert.assertNull(calculation.getGeneratedAt());
        } else {
            Assert.assertEquals(
                SdkHelper.toDate(calculationResponseType.getGeneratedAt()),
                calculation.getGeneratedAt()
            );
        }
        int marketCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream())
            .collect(Collectors.toList())
            .size();
        int outcomeCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream().flatMap(f2 -> f2.getOutcomes().stream()))
            .collect(Collectors.toList())
            .size();
        System.out.println(
            String.format("Calculation has {} markets and {} outcomes.", marketCount, outcomeCount)
        );
    }

    @Test
    public void CalculationFilterMapTest() throws ParseException {
        CAPIFilteredCalculationResponse calculationResponseType = RestMessageBuilder.getFilteredCalculationResponse(
            URN.parse("sr:match:1000"),
            0
        );
        CalculationFilter calculation = SdkMessageBuilder.getCalculationFilter(calculationResponseType);

        Assert.assertNotNull(calculationResponseType);
        Assert.assertNotNull(calculation);

        Assert.assertEquals(
            0,
            Double.compare(calculationResponseType.getCalculation().getOdds(), calculation.getOdds())
        );
        Assert.assertEquals(
            0,
            Double.compare(
                calculationResponseType.getCalculation().getProbability(),
                calculation.getProbability()
            )
        );
        if (calculationResponseType.getGeneratedAt().isEmpty()) {
            Assert.assertNull(calculation.getGeneratedAt());
        } else {
            Assert.assertEquals(
                SdkHelper.toDate(calculationResponseType.getGeneratedAt()),
                calculation.getGeneratedAt()
            );
        }

        Assert.assertEquals(
            calculationResponseType.getAvailableSelections().getEvents().size(),
            calculation.getAvailableSelections().size()
        );
        for (int i = 0; i < calculationResponseType.getAvailableSelections().getEvents().size(); i++) {
            CAPIFilteredEventType sourceAvailableSelection = calculationResponseType
                .getAvailableSelections()
                .getEvents()
                .get(i);
            AvailableSelectionsFilter resultAvailableSelection = calculation.getAvailableSelections().get(i);

            Assert.assertEquals(
                URN.parse(sourceAvailableSelection.getId()),
                resultAvailableSelection.getEvent()
            );
            for (CAPIFilteredMarketType sourceMarket : sourceAvailableSelection.getMarkets().getMarkets()) {
                MarketFilter resultMarket = resultAvailableSelection
                    .getMarkets()
                    .stream()
                    .filter(f -> f.getId() == sourceMarket.getId())
                    .findFirst()
                    .get();
                Assert.assertNotNull(resultMarket);
                MarketCompare(sourceMarket, resultMarket);
            }
        }
        int marketCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream())
            .collect(Collectors.toList())
            .size();
        int outcomeCount = calculation
            .getAvailableSelections()
            .stream()
            .flatMap(s -> s.getMarkets().stream().flatMap(f2 -> f2.getOutcomes().stream()))
            .collect(Collectors.toList())
            .size();
        System.out.println(
            String.format("Calculation has {} markets and {} outcomes.", marketCount, outcomeCount)
        );
    }

    @Test
    public void GetAvailableSelectionsTest() throws CommunicationException {
        URN eventId = URN.parse("sr:match:31561675");
        AvailableSelections availableSelections = dataRouterManager.requestAvailableSelections(eventId);
        Assert.assertNotNull(availableSelections);
        Assert.assertEquals(eventId, availableSelections.getEvent());
        Assert.assertFalse(availableSelections.getMarkets().isEmpty());
    }

    @Test
    public void GetCalculationTest() throws CommunicationException {
        URN eventId = URN.parse("sr:match:31561675");
        AvailableSelections availableSelections = dataRouterManager.requestAvailableSelections(eventId);
        Assert.assertNotNull(availableSelections);

        List<Selection> matchSelections = new ArrayList<>();
        Market market = availableSelections.getMarkets().get(0);
        Selection selection = customBetSelectionBuilder
            .setEventId(eventId)
            .setMarketId(market.getId())
            .setOutcomeId(market.getOutcomes().get(0))
            .setSpecifiers(market.getSpecifiers())
            .build();
        matchSelections.add(selection);
        market = availableSelections.getMarkets().get(availableSelections.getMarkets().size() - 1);
        selection =
            customBetSelectionBuilder
                .setEventId(eventId)
                .setMarketId(market.getId())
                .setOutcomeId(market.getOutcomes().get(0))
                .setSpecifiers(market.getSpecifiers())
                .build();
        matchSelections.add(selection);

        Calculation calculation = dataRouterManager.requestCalculateProbability(matchSelections);

        Assert.assertNotNull(calculation);
        Assert.assertEquals(eventId, calculation.getAvailableSelections().get(0).getEvent());
        Assert.assertFalse(calculation.getAvailableSelections().isEmpty());
    }

    @Test
    public void GetCalculationFilterTest() throws CommunicationException {
        URN eventId = URN.parse("sr:match:31561675");
        AvailableSelections availableSelections = dataRouterManager.requestAvailableSelections(eventId);
        Assert.assertNotNull(availableSelections);

        List<Selection> matchSelections = new ArrayList<>();
        Market market = availableSelections.getMarkets().get(0);
        Selection selection = customBetSelectionBuilder
            .setEventId(eventId)
            .setMarketId(market.getId())
            .setOutcomeId(market.getOutcomes().get(0))
            .setSpecifiers(market.getSpecifiers())
            .build();
        matchSelections.add(selection);
        market = availableSelections.getMarkets().get(availableSelections.getMarkets().size() - 1);
        selection =
            customBetSelectionBuilder
                .setEventId(eventId)
                .setMarketId(market.getId())
                .setOutcomeId(market.getOutcomes().get(0))
                .setSpecifiers(market.getSpecifiers())
                .build();
        matchSelections.add(selection);

        CalculationFilter calculation = dataRouterManager.requestCalculateProbabilityFilter(matchSelections);

        Assert.assertNotNull(calculation);
        Assert.assertEquals(eventId, calculation.getAvailableSelections().get(0).getEvent());
        Assert.assertFalse(calculation.getAvailableSelections().isEmpty());
    }

    private void AvailableSelectionsCompare(CAPIAvailableSelections source, AvailableSelections result) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(result);

        Assert.assertNotNull(source.getEvent());
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(source.getGeneratedAt()));

        Assert.assertEquals(source.getEvent().getId(), result.getEvent().toString());

        if (source.getEvent().getMarkets().getMarkets().isEmpty()) {
            Assert.assertTrue(result.getMarkets().isEmpty());
            return;
        }

        Assert.assertEquals(source.getEvent().getMarkets().getMarkets().size(), result.getMarkets().size());
        for (CAPIMarketType sourceMarket : source.getEvent().getMarkets().getMarkets()) {
            Market resultMarket = result
                .getMarkets()
                .stream()
                .filter(f -> f.getId() == sourceMarket.getId())
                .findFirst()
                .get();
            Assert.assertNotNull(resultMarket);
            MarketCompare(sourceMarket, resultMarket);
        }
    }

    private void MarketCompare(CAPIMarketType source, Market result) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(result);

        Assert.assertEquals(source.getId(), result.getId());
        Assert.assertEquals(source.getSpecifiers(), result.getSpecifiers());

        if (source.getOutcomes().isEmpty()) {
            Assert.assertTrue(result.getOutcomes().isEmpty());
            return;
        }

        Assert.assertEquals(source.getOutcomes().size(), result.getOutcomes().size());
        for (CAPIOutcomeType outcomeType : source.getOutcomes()) {
            Assert.assertTrue(result.getOutcomes().contains(outcomeType.getId()));
        }
    }

    private void MarketCompare(CAPIFilteredMarketType source, MarketFilter result) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(result);

        Assert.assertEquals(source.getId(), result.getId());
        Assert.assertEquals(source.getSpecifiers(), result.getSpecifiers());

        if (source.getOutcomes().isEmpty()) {
            Assert.assertTrue(result.getOutcomes().isEmpty());
            return;
        }

        Assert.assertEquals(source.getOutcomes().size(), result.getOutcomes().size());
        for (CAPIFilteredOutcomeType sourceOutcome : source.getOutcomes()) {
            OutcomeFilter resultOutcome = result
                .getOutcomes()
                .stream()
                .filter(f -> f.getId() == sourceOutcome.getId())
                .findFirst()
                .get();
            Assert.assertNotNull(resultOutcome);
            Assert.assertEquals(sourceOutcome.getId(), resultOutcome.getId());
            Assert.assertEquals(sourceOutcome.isConflict(), resultOutcome.isConflict());
        }
    }
}
