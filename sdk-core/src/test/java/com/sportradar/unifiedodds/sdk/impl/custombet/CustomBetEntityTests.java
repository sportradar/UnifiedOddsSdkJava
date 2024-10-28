/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.custombet;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.unifiedodds.sdk.CapiCustomBet;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilder;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilderImpl;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.conn.UofConnListener;
import com.sportradar.unifiedodds.sdk.custombetentities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.unifiedodds.sdk.shared.TestDataRouterManager;
import com.sportradar.unifiedodds.sdk.shared.TestFeed;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "MagicNumber",
        "MethodLength",
        "MethodNameTest",
        "MultipleStringLiterals",
        "ClassDataAbstractionCoupling",
        "AnonInnerLength",
    }
)
public class CustomBetEntityTests {

    private final Urn eventId = Urn.parse("sr:match:31561675");
    private DataRouterManager dataRouterManager;
    private CustomBetSelectionBuilder customBetSelectionBuilder;

    @Before
    public void setup() {
        StubUofConfiguration stubUofConfiguration = new StubUofConfiguration();
        stubUofConfiguration.setEnvironment(Environment.Production);
        stubUofConfiguration.resetNbrSetEnvironmentCalled();

        UofConnListener sdkListener = new UofConnListener();
        TestFeed feed = new TestFeed(sdkListener, stubUofConfiguration, sdkListener);

        DataRouterImpl dataRouter = new DataRouterImpl();
        dataRouter.setDataListeners(new ArrayList<>());
        dataRouterManager = new TestDataRouterManager(feed.TestHttpHelper, dataRouter);
        customBetSelectionBuilder = new CustomBetSelectionBuilderImpl();
    }

    @Test
    public void availableSelectionsMapTest() {
        CapiAvailableSelections availableSelectionsType = CapiCustomBet.getAvailableSelectionsResponse(
            Urn.parse("sr:match:1000"),
            10
        );
        AvailableSelections resultAvailableSelections = CapiCustomBet.getAvailableSelections(
            availableSelectionsType
        );

        availableSelectionsCompare(availableSelectionsType, resultAvailableSelections);
    }

    @Test
    public void availableSelectionsEmptyMapTest() {
        CapiAvailableSelections availableSelectionsType = CapiCustomBet.getAvailableSelectionsResponse(
            Urn.parse("sr:match:1000"),
            0
        );
        Assert.assertTrue(availableSelectionsType.getEvent().getMarkets().getMarkets().isEmpty());
        AvailableSelections resultAvailableSelections = CapiCustomBet.getAvailableSelections(
            availableSelectionsType
        );
        Assert.assertTrue(resultAvailableSelections.getMarkets().isEmpty());
        availableSelectionsCompare(availableSelectionsType, resultAvailableSelections);
    }

    @Test
    public void calculationEmptyMapTest() throws ParseException {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(
            Urn.parse("sr:match:1000"),
            0
        );
        Calculation calculation = CapiCustomBet.getCalculation(calculationResponseType);

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
    public void calculationMapTest() throws ParseException {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(
            Urn.parse("sr:match:1000"),
            7
        );
        Calculation calculation = CapiCustomBet.getCalculation(calculationResponseType);

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
            CapiEventType sourceAvailableSelection = calculationResponseType
                .getAvailableSelections()
                .getEvents()
                .get(i);
            AvailableSelections resultAvailableSelection = calculation.getAvailableSelections().get(i);

            Assert.assertEquals(
                Urn.parse(sourceAvailableSelection.getId()),
                resultAvailableSelection.getEvent()
            );
            for (CapiMarketType sourceMarket : sourceAvailableSelection.getMarkets().getMarkets()) {
                Market resultMarket = resultAvailableSelection
                    .getMarkets()
                    .stream()
                    .filter(f -> f.getId() == sourceMarket.getId())
                    .findFirst()
                    .get();
                Assert.assertNotNull(resultMarket);
                this.marketCompare(sourceMarket, resultMarket);
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
        System.out.printf("Calculation has {} markets and {} outcomes.%n", marketCount, outcomeCount);
    }

    @Test
    public void calculationFilterEmptyMapTest() throws ParseException {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            Urn.parse("sr:match:1000"),
            0
        );
        CalculationFilter calculation = CapiCustomBet.getCalculationFilter(calculationResponseType);

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
        System.out.printf("Calculation has {} markets and {} outcomes.%n", marketCount, outcomeCount);
    }

    @Test
    public void calculationFilterMapTest() throws ParseException {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            Urn.parse("sr:match:1000"),
            0
        );
        CalculationFilter calculation = CapiCustomBet.getCalculationFilter(calculationResponseType);

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
            CapiFilteredEventType sourceAvailableSelection = calculationResponseType
                .getAvailableSelections()
                .getEvents()
                .get(i);
            AvailableSelectionsFilter resultAvailableSelection = calculation.getAvailableSelections().get(i);

            Assert.assertEquals(
                Urn.parse(sourceAvailableSelection.getId()),
                resultAvailableSelection.getEvent()
            );
            for (CapiFilteredMarketType sourceMarket : sourceAvailableSelection.getMarkets().getMarkets()) {
                MarketFilter resultMarket = resultAvailableSelection
                    .getMarkets()
                    .stream()
                    .filter(f -> f.getId() == sourceMarket.getId())
                    .findFirst()
                    .get();
                Assert.assertNotNull(resultMarket);
                marketCompare(sourceMarket, resultMarket);
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
        System.out.printf("Calculation has {} markets and {} outcomes.%n", marketCount, outcomeCount);
    }

    @Test
    public void getAvailableSelectionsTest() throws CommunicationException {
        AvailableSelections availableSelections = dataRouterManager.requestAvailableSelections(eventId);
        Assert.assertNotNull(availableSelections);
        Assert.assertEquals(eventId, availableSelections.getEvent());
        Assert.assertFalse(availableSelections.getMarkets().isEmpty());
    }

    @Test
    public void getCalculationTest() throws CommunicationException {
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
    public void getCalculationFilterTest() throws CommunicationException {
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

    @Test
    public void selectionConstructWhenAllDataThenBuildCorrectly() {
        Selection selection = customBetSelectionBuilder
            .setEventId(eventId)
            .setMarketId(1)
            .setOutcomeId("2")
            .setSpecifiers("specifier=value")
            .setOdds(1.5)
            .build();

        Assert.assertNotNull(selection);
        Assert.assertEquals(eventId, selection.getEventId());
        Assert.assertEquals(1, selection.getMarketId());
        Assert.assertEquals("2", selection.getOutcomeId());
        Assert.assertEquals("specifier=value", selection.getSpecifiers());
        Assert.assertEquals(1.5, selection.getOdds(), 0);
    }

    @Test
    public void selectionConstructedWithConstructor() {
        Selection selection = new SelectionImpl(eventId, 1, "2", "specifier=value");

        Assert.assertNotNull(selection);
        Assert.assertEquals(eventId, selection.getEventId());
        Assert.assertEquals(1, selection.getMarketId());
        Assert.assertEquals("2", selection.getOutcomeId());
        Assert.assertEquals("specifier=value", selection.getSpecifiers());
        Assert.assertNull(selection.getOdds());
    }

    @Test
    public void selectionConstructWhenAllDataDirectThenBuildCorrectly() {
        Selection selection = customBetSelectionBuilder.build(eventId, 1, "specifier=value", "2", 1.5);

        Assert.assertNotNull(selection);
        Assert.assertEquals(eventId, selection.getEventId());
        Assert.assertEquals(1, selection.getMarketId());
        Assert.assertEquals("2", selection.getOutcomeId());
        Assert.assertEquals("specifier=value", selection.getSpecifiers());
        Assert.assertEquals(1.5, selection.getOdds(), 0);
    }

    @Test
    public void selectionConstructWhenNoOddsThenBuildCorrectly() {
        Selection selection = customBetSelectionBuilder.build(eventId, 1, "specifier=value", "2", null);

        Assert.assertNotNull(selection);
        Assert.assertEquals(eventId, selection.getEventId());
        Assert.assertEquals(1, selection.getMarketId());
        Assert.assertEquals("2", selection.getOutcomeId());
        Assert.assertEquals("specifier=value", selection.getSpecifiers());
        Assert.assertNull(selection.getOdds());
    }

    @Test
    public void selectionConstructWhenWithoutOddsThenBuildCorrectly() {
        Selection selection = customBetSelectionBuilder.build(eventId, 1, "specifier=value", "2");

        Assert.assertNotNull(selection);
        Assert.assertEquals(eventId, selection.getEventId());
        Assert.assertEquals(1, selection.getMarketId());
        Assert.assertEquals("2", selection.getOutcomeId());
        Assert.assertEquals("specifier=value", selection.getSpecifiers());
        Assert.assertNull(selection.getOdds());
    }

    @Test
    public void selectionConstructWhenMissingEventIdThenThrow() {
        customBetSelectionBuilder
            .setMarketId(1)
            .setOutcomeId("2")
            .setSpecifiers("specifier=value")
            .setOdds(1.5);

        assertThrows(NullPointerException.class, customBetSelectionBuilder::build);
    }

    @Test
    public void selectionConstructWhenMissingMarketIdThenThrow() {
        customBetSelectionBuilder
            .setEventId(eventId)
            .setOutcomeId("2")
            .setSpecifiers("specifier=value")
            .setOdds(1.5);

        assertThrows(IllegalArgumentException.class, customBetSelectionBuilder::build);
    }

    @Test
    public void selectionConstructWhenMissingOutcomeIdThenThrow() {
        customBetSelectionBuilder
            .setMarketId(1)
            .setEventId(eventId)
            .setMarketId(123)
            .setSpecifiers("specifier=value")
            .setOdds(1.5);

        assertThrows(NullPointerException.class, customBetSelectionBuilder::build);
    }

    @Test
    public void defaultImplementationCustomBetSelectionBuilderThrowsUnsupportedOperationException() {
        CustomBetSelectionBuilder defaultCbSelectionBuilder = CapiCustomBet.getDefaultImplCustomBetSelectionBuilder();

        assertThrows(UnsupportedOperationException.class, () -> defaultCbSelectionBuilder.setOdds(1.5));
        assertThrows(
            UnsupportedOperationException.class,
            () -> defaultCbSelectionBuilder.build(eventId, 5, "spec=23", "23", 1.5)
        );
    }

    @Test
    public void defaultImplementationSelectionGetOddsThrowsUnsupportedOperationException() {
        Selection selection = CapiCustomBet.getDefaultImplementationSelection();

        assertThrows(UnsupportedOperationException.class, selection::getOdds);
    }

    @Test
    public void defaultImplementationCalculationIsHarmonizationThrowsUnsupportedOperationException() {
        Calculation calculation = CapiCustomBet.getDefaultImplementationCalculation();

        assertThrows(UnsupportedOperationException.class, calculation::isHarmonization);
    }

    @Test
    public void defaultImplementationCalculationFilterIsHarmonizationThrowsUnsupportedOperationException() {
        CalculationFilter calculation = CapiCustomBet.getDefaultImplementationCalculationFilter();

        assertThrows(UnsupportedOperationException.class, calculation::isHarmonization);
    }

    @Test
    public void calculateResponseWhenHarmonizationMissingThenIsNull() {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(eventId, 7);

        Calculation calculation = new CalculationImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertNull(calculation.isHarmonization());
    }

    @Test
    public void calculateResponseWhenHarmonizationTrueThenMapCorrectly() {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(eventId, 7);
        calculationResponseType.getCalculation().setHarmonization(true);

        Calculation calculation = new CalculationImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertTrue(calculation.isHarmonization());
    }

    @Test
    public void calculateResponseWhenHarmonizationFalseThenMapCorrectly() {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(eventId, 7);
        calculationResponseType.getCalculation().setHarmonization(false);

        Calculation calculation = new CalculationImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertFalse(calculation.isHarmonization());
    }

    @Test
    public void calculateResponseWhenGeneratedAtInWrongFormatFalseThenReturnCurrentDate() {
        CapiCalculationResponse calculationResponseType = CapiCustomBet.getCalculationResponse(eventId, 7);
        calculationResponseType.setGeneratedAt("a-b-c");

        Calculation calculation = new CalculationImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertNotNull(calculation.getGeneratedAt());
        long dateDiff = new Date().getTime() - calculation.getGeneratedAt().getTime();
        Assert.assertTrue(Math.abs(dateDiff) < 10);
    }

    @Test
    public void calculateFilterResponseWhenHarmonizationMissingThenIsNull() {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            eventId,
            7
        );

        CalculationFilter calculation = new CalculationFilterImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertNull(calculation.isHarmonization());
    }

    @Test
    public void calculateFilterResponseWhenHarmonizationTrueThenMapCorrectly() {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            eventId,
            7
        );
        calculationResponseType.getCalculation().setHarmonization(true);

        CalculationFilter calculation = new CalculationFilterImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertTrue(calculation.isHarmonization());
    }

    @Test
    public void calculateFilterResponseWhenHarmonizationFalseThenMapCorrectly() {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            eventId,
            7
        );
        calculationResponseType.getCalculation().setHarmonization(false);

        CalculationFilter calculation = new CalculationFilterImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertFalse(calculation.isHarmonization());
    }

    @Test
    public void calculateFilterResponseWhenGeneratedAtInWrongFormatFalseThenReturnCurrentDate() {
        CapiFilteredCalculationResponse calculationResponseType = CapiCustomBet.getFilteredCalculationResponse(
            eventId,
            7
        );
        calculationResponseType.setGeneratedAt("a-b-c");

        CalculationFilter calculation = new CalculationFilterImpl(calculationResponseType);

        Assert.assertNotNull(calculation);
        Assert.assertNotNull(calculation.getGeneratedAt());
        long dateDiff = new Date().getTime() - calculation.getGeneratedAt().getTime();
        Assert.assertTrue(Math.abs(dateDiff) < 10);
    }

    private void availableSelectionsCompare(CapiAvailableSelections source, AvailableSelections result) {
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
        for (CapiMarketType sourceMarket : source.getEvent().getMarkets().getMarkets()) {
            Market resultMarket = result
                .getMarkets()
                .stream()
                .filter(f -> f.getId() == sourceMarket.getId())
                .findFirst()
                .get();
            Assert.assertNotNull(resultMarket);
            marketCompare(sourceMarket, resultMarket);
        }
    }

    private void marketCompare(CapiMarketType source, Market result) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(result);

        Assert.assertEquals(source.getId(), result.getId());
        Assert.assertEquals(source.getSpecifiers(), result.getSpecifiers());

        if (source.getOutcomes().isEmpty()) {
            Assert.assertTrue(result.getOutcomes().isEmpty());
            return;
        }

        Assert.assertEquals(source.getOutcomes().size(), result.getOutcomes().size());
        for (CapiOutcomeType outcomeType : source.getOutcomes()) {
            Assert.assertTrue(result.getOutcomes().contains(outcomeType.getId()));
        }
    }

    private void marketCompare(CapiFilteredMarketType source, MarketFilter result) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(result);

        Assert.assertEquals(source.getId(), result.getId());
        Assert.assertEquals(source.getSpecifiers(), result.getSpecifiers());

        if (source.getOutcomes().isEmpty()) {
            Assert.assertTrue(result.getOutcomes().isEmpty());
            return;
        }

        Assert.assertEquals(source.getOutcomes().size(), result.getOutcomes().size());
        for (CapiFilteredOutcomeType sourceOutcome : source.getOutcomes()) {
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
