/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.custombet.*;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.CalculationImpl;
import com.sportradar.unifiedodds.sdk.managers.CustomBetSelectionBuilder;
import com.sportradar.unifiedodds.sdk.shared.StaticRandom;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
public class CapiCustomBet {

    public static CustomBetSelectionBuilder getDefaultImplCustomBetSelectionBuilder() {
        return new CustomBetSelectionBuilder() {
            @Override
            public CustomBetSelectionBuilder setEventId(Urn newEventId) {
                return null;
            }

            @Override
            public CustomBetSelectionBuilder setMarketId(int marketId) {
                return null;
            }

            @Override
            public CustomBetSelectionBuilder setSpecifiers(String specifiers) {
                return null;
            }

            @Override
            public CustomBetSelectionBuilder setOutcomeId(String outcomeId) {
                return null;
            }

            @Override
            public CustomBetSelectionBuilder setOdds(double odds) {
                return null;
            }

            @Override
            public Selection build() {
                return null;
            }

            @Override
            public Selection build(Urn eventId, int marketId, String specifiers, String outcomeId) {
                return null;
            }

            @Override
            public Selection build(
                Urn eventId,
                int marketId,
                String specifiers,
                String outcomeId,
                Double odds
            ) {
                return null;
            }
        };
    }

    public static Selection getDefaultImplementationSelection() {
        return new Selection() {
            @Override
            public Urn getEventId() {
                return null;
            }

            @Override
            public int getMarketId() {
                return 0;
            }

            @Override
            public String getSpecifiers() {
                return null;
            }

            @Override
            public String getOutcomeId() {
                return null;
            }

            @Override
            public Double getOdds() {
                return null;
            }
        };
    }

    public static Calculation getDefaultImplementationCalculation() {
        return new Calculation() {
            @Override
            public double getOdds() {
                return 0;
            }

            @Override
            public double getProbability() {
                return 0;
            }

            @Override
            public List<AvailableSelections> getAvailableSelections() {
                return Collections.emptyList();
            }

            @Override
            public Date getGeneratedAt() {
                return null;
            }

            @Override
            public Boolean isHarmonization() {
                return null;
            }
        };
    }

    public static CalculationFilter getDefaultImplementationCalculationFilter() {
        return new CalculationFilter() {
            @Override
            public double getOdds() {
                return 0;
            }

            @Override
            public double getProbability() {
                return 0;
            }

            @Override
            public List<AvailableSelectionsFilter> getAvailableSelections() {
                return Collections.emptyList();
            }

            @Override
            public Date getGeneratedAt() {
                return null;
            }

            @Override
            public Boolean isHarmonization() {
                return null;
            }
        };
    }

    public static AvailableSelections getAvailableSelections(CapiAvailableSelections availableSelections) {
        return new AvailableSelectionsImpl(availableSelections);
    }

    public static Calculation getCalculation(CapiCalculationResponse calculationResponse) {
        return new CalculationImpl(calculationResponse);
    }

    public static CalculationFilter getCalculationFilter(
        CapiFilteredCalculationResponse calculationResponse
    ) {
        return new CalculationFilterImpl(calculationResponse);
    }

    public static CapiEventType getEventType(Urn eventId, int nbrMarkets) {
        List<CapiMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = StaticRandom.B();
            int startNbr = StaticRandom.I100();
            List<CapiOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < StaticRandom.I(nbrMarkets + 1); j++) {
                CapiOutcomeType outcomeType = new CapiOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeTypes.add(outcomeType);
            }

            CapiMarketType marketType = new CapiMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(StaticRandom.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketTypes.add(marketType);
        }

        CapiMarketsType marketsType = new CapiMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CapiEventType eventType = new CapiEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CapiFilteredEventType getFilteredEventType(Urn eventId, int nbrMarkets) {
        List<CapiFilteredMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = StaticRandom.B();
            int startNbr = StaticRandom.I100();
            List<CapiFilteredOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < StaticRandom.I(nbrMarkets + 1); j++) {
                Boolean isConflict = StaticRandom.I100() > 20 ? StaticRandom.B() : null;
                CapiFilteredOutcomeType outcomeType = new CapiFilteredOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeType.setConflict(isConflict);
                outcomeTypes.add(outcomeType);
            }

            CapiFilteredMarketType marketType = new CapiFilteredMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(StaticRandom.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            Boolean isConflict = StaticRandom.I100() > 20 ? StaticRandom.B() : null;
            marketType.setConflict(isConflict);
            marketTypes.add(marketType);
        }

        CapiFilteredMarketsType marketsType = new CapiFilteredMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CapiFilteredEventType eventType = new CapiFilteredEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CapiAvailableSelections getAvailableSelectionsResponse(Urn eventId, int nbrMarkets) {
        CapiAvailableSelections availableSelections = new CapiAvailableSelections();
        availableSelections.setGeneratedAt(SdkHelper.dateToString(new Date()));
        availableSelections.setEvent(getEventType(eventId, nbrMarkets));
        return availableSelections;
    }

    public static CapiCalculationResponse getCalculationResponse(Urn eventId, int nbrSelections) {
        List<CapiEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CapiEventType eventType = getEventType(eventId, StaticRandom.I(10));
            eventTypes.add(eventType);
        }

        CapiAvailableSelectionsAfterCalculationType availSelections = new CapiAvailableSelectionsAfterCalculationType();
        availSelections.getEvents().addAll(eventTypes);

        CapiCalculationResultType calculation = new CapiCalculationResultType();
        calculation.setOdds(StaticRandom.D(100));
        calculation.setProbability(StaticRandom.D0());

        CapiCalculationResponse calculationResponse = new CapiCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static CapiFilteredCalculationResponse getFilteredCalculationResponse(
        Urn eventId,
        int nbrSelections
    ) {
        List<CapiFilteredEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CapiFilteredEventType eventType = getFilteredEventType(eventId, StaticRandom.I(10));
            eventTypes.add(eventType);
        }

        CapiAvailableSelectionsFilteredOutcomesType availSelections = new CapiAvailableSelectionsFilteredOutcomesType();
        availSelections.getEvents().addAll(eventTypes);

        CapiFilteredCalculationResultType calculation = new CapiFilteredCalculationResultType();
        calculation.setOdds(StaticRandom.D(100));
        calculation.setProbability(StaticRandom.D0());

        CapiFilteredCalculationResponse calculationResponse = new CapiFilteredCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static CapiCalculationResponse getCalculationWithHarmonization(Boolean harmonization) {
        CapiCalculationResultType capiCalculation = new CapiCalculationResultType();
        capiCalculation.setOdds(1.0);
        capiCalculation.setProbability(0.5);
        capiCalculation.setHarmonization(harmonization);

        CapiCalculationResponse capiCalculationResponse = new CapiCalculationResponse();
        capiCalculationResponse.setGeneratedAt("2025-01-01T00:00:00Z");
        capiCalculationResponse.setCalculation(capiCalculation);
        capiCalculationResponse.setAvailableSelections(new CapiAvailableSelectionsAfterCalculationType());
        return capiCalculationResponse;
    }

    public static CapiFilteredCalculationResponse getCalculationFilterWithHarmonization(
        Boolean harmonization
    ) {
        CapiFilteredCalculationResultType capiCalculation = new CapiFilteredCalculationResultType();
        capiCalculation.setOdds(1.0);
        capiCalculation.setProbability(0.5);
        capiCalculation.setHarmonization(harmonization);

        CapiFilteredCalculationResponse capiCalculationResponse = new CapiFilteredCalculationResponse();
        capiCalculationResponse.setGeneratedAt("2025-01-01T00:00:00Z");
        capiCalculationResponse.setCalculation(capiCalculation);
        capiCalculationResponse.setAvailableSelections(new CapiAvailableSelectionsFilteredOutcomesType());
        return capiCalculationResponse;
    }
}
