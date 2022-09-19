package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for building rest messages
 */
public class RestMessageBuilder {

    public static StaticRandom SR;

    public static CAPIEventType getEventType(URN eventId, int nbrMarkets)
    {
        List<CAPIMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++)
        {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CAPIOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++)
            {
                CAPIOutcomeType outcomeType = new CAPIOutcomeType();
                outcomeType.setId(textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j));
                outcomeTypes.add(outcomeType);
            }

            CAPIMarketType marketType = new CAPIMarketType();
            marketType.setId(i+1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketTypes.add(marketType);
        }

        CAPIMarketsType marketsType = new CAPIMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CAPIEventType eventType = new CAPIEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CAPIFilteredEventType getFilteredEventType(URN eventId, int nbrMarkets)
    {
        List<CAPIFilteredMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++)
        {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CAPIFilteredOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++)
            {
                Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
                CAPIFilteredOutcomeType outcomeType = new CAPIFilteredOutcomeType();
                outcomeType.setId(textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j));
                outcomeType.setConflict(isConflict);
                outcomeTypes.add(outcomeType);
            }

            Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
            CAPIFilteredMarketType marketType = new CAPIFilteredMarketType();
            marketType.setId(i+1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketType.setConflict(isConflict);
            marketTypes.add(marketType);
        }

        CAPIFilteredMarketsType marketsType = new CAPIFilteredMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CAPIFilteredEventType eventType = new CAPIFilteredEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CAPIAvailableSelections getAvailableSelections(URN eventId, int nbrMarkets)
    {
        CAPIAvailableSelections availableSelections = new CAPIAvailableSelections();
        availableSelections.setGeneratedAt(SdkHelper.dateToString(new Date()));
        availableSelections.setEvent(getEventType(eventId, nbrMarkets));
        return availableSelections;
    }

    public static CAPICalculationResponse getCalculationResponse(URN eventId, int nbrSelections)
    {
        List<CAPIEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++)
        {
            CAPIEventType eventType = getEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CAPIAvailableSelectionsAfterCalculationType availableSelections = new CAPIAvailableSelectionsAfterCalculationType();
        availableSelections.getEvents().addAll(eventTypes);

        CAPICalculationResultType calculation = new CAPICalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CAPICalculationResponse calculationResponse = new CAPICalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static CAPIFilteredCalculationResponse getFilteredCalculationResponse(URN eventId, int nbrSelections)
    {
        List<CAPIFilteredEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++)
        {
            CAPIFilteredEventType eventType = getFilteredEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CAPIAvailableSelectionsFilteredOutcomesType availableSelections = new CAPIAvailableSelectionsFilteredOutcomesType();
        availableSelections.getEvents().addAll(eventTypes);

        CAPIFilteredCalculationResultType calculation = new CAPIFilteredCalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CAPIFilteredCalculationResponse calculationResponse = new CAPIFilteredCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }
}
