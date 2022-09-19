package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredCalculationResponse;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.CalculationFilter;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.AvailableSelectionsImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationFilterImpl;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.CalculationImpl;
import com.sportradar.utils.URN;

public class SdkMessageBuilder {

    public static StaticRandom SR;
    public static RestMessageBuilder RMB;

    public static AvailableSelections getAvailableSelections(int eventId, int nbrMarkets)
    {
        URN matchId = SR.Urn(eventId == 0 ? SR.I1000() : eventId, "match");
        return new AvailableSelectionsImpl(RMB.getAvailableSelections(matchId, nbrMarkets));
    }

    public static Calculation getCalculation(int eventId, int nbrSelections)
    {
        URN matchId = SR.Urn(eventId == 0 ? SR.I1000() : eventId, "match");
        return new CalculationImpl(RMB.getCalculationResponse(matchId, nbrSelections));
    }

    public static CalculationFilter getCalculationFilter(int eventId, int nbrSelections)
    {
        URN matchId = SR.Urn(eventId == 0 ? SR.I1000() : eventId, "match");
        return new CalculationFilterImpl(RMB.getFilteredCalculationResponse(matchId, nbrSelections));
    }

    public static AvailableSelections getAvailableSelections(CAPIAvailableSelections availableSelections)
    {
        return new AvailableSelectionsImpl(availableSelections);
    }

    public static Calculation getCalculation(CAPICalculationResponse calculationResponse)
    {
        return new CalculationImpl(calculationResponse);
    }

    public static CalculationFilter getCalculationFilter(CAPIFilteredCalculationResponse calculationResponse)
    {
        return new CalculationFilterImpl(calculationResponse);
    }
}
