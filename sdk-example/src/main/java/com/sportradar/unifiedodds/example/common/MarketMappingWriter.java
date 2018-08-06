/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarketMappingWriter {
    public static String writeMarketMapping(Market market, Locale locale)
    {
        if(market == null)
        {
            return "";
        }
        List<MarketMappingData> mappings = market.getMarketDefinition().getValidMappings(locale);

        if(mappings == null || mappings.isEmpty())
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (MarketMappingData data:mappings) {
            sb.append("  ->  Market ").append(market.getId()).append(" mapped to TypeId:").append(data.getMarketTypeId()).append(", SubTypeId:").append(data.getMarketSubTypeId()).append(", Sov:").append(data.getSovTemplate()); //.append("\n");
        }
        return sb.toString();
    }

    public static String writeMarketOutcomeMapping(Market market, Locale locale)
    {
        if(market == null || market.getMarketDefinition() == null)
        {
            return "";
        }

        List<MarketMappingData> mappings = market.getMarketDefinition().getValidMappings(locale);
        if(mappings == null || mappings.isEmpty())
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId()).append(" has outcome mappings:");
        for (MarketMappingData data:mappings) {
            Map<String, OutcomeMappingData> outcomeMappings = data.getOutcomeMappings();
            outcomeMappings.forEach((s, o) -> sb.append("\n").append("Legacy marketId:").append(data.getMarketId()).append(" and outcomeId:").append(o.getOutcomeId()).append(" is mapped to Id:").append(o.getProducerOutcomeId()).append(" and Name:").append(o.getProducerOutcomeName(locale)));
        }
        return sb.toString();
    }
}
