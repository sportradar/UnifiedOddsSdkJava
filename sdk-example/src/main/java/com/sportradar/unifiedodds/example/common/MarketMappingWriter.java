/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import static java.util.Optional.ofNullable;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ConstantName", "HideUtilityClassConstructor", "MethodLength" })
public class MarketMappingWriter {

    private static final Logger logger = LoggerFactory.getLogger("MarketMappingWriter");

    public static String writeMarketMapping(Market market, Locale locale) {
        if (market == null) {
            return "";
        }
        List<MarketMappingData> mappings = market.getMarketDefinition().getValidMappings(locale);

        if (mappings == null || mappings.isEmpty()) {
            logger.warn(
                "MarketId:{}, specifiers={} has no mappings.",
                market.getId(),
                SdkHelper.dictionaryToString(market.getSpecifiers())
            );
            return "";
        }

        if (mappings.size() > 1) {
            logger.warn(
                "MarketId:{}, specifiers={} has too many mappings [{}].",
                market.getId(),
                SdkHelper.dictionaryToString(market.getSpecifiers()),
                mappings.size()
            );
            int i = 0;
            for (MarketMappingData mapping : mappings) {
                logger.debug(
                    "MarketId:{}, producer:{}, sportId:{}, specifiers={}, mapping[{}]: {}",
                    market.getId(),
                    SdkHelper.integerSetToString(mapping.getProducerIds()),
                    ofNullable(mapping.getSportId()).map(Urn::getId).orElse(null),
                    SdkHelper.dictionaryToString(market.getSpecifiers()),
                    i,
                    mapping
                );
                i++;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (MarketMappingData data : mappings) {
            sb
                .append("  ->  Market ")
                .append(market.getId())
                .append(" mapped to TypeId:")
                .append(data.getMarketTypeId())
                .append(", SubTypeId:")
                .append(data.getMarketSubTypeId())
                .append(", Sov:")
                .append(data.getSovTemplate());
        }
        return sb.toString();
    }

    public static String writeMarketOutcomeMapping(Market market, Locale locale) {
        if (market == null || market.getMarketDefinition() == null) {
            return "";
        }

        List<MarketMappingData> mappings = market.getMarketDefinition().getValidMappings(locale);
        if (mappings == null || mappings.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId()).append(" has outcome mappings:");
        for (MarketMappingData data : mappings) {
            Map<String, OutcomeMappingData> outcomeMappings = data.getOutcomeMappings();
            if (outcomeMappings.isEmpty()) {
                logger.warn(
                    "MarketMappingData: MarketId={}, SportId={}, specifiers={} has no outcome mappings.",
                    data.getMarketId(),
                    data.getSportId(),
                    SdkHelper.dictionaryToString(market.getSpecifiers())
                );
            }

            outcomeMappings.forEach((s, o) ->
                sb
                    .append("\n")
                    .append("Legacy marketId:")
                    .append(data.getMarketId())
                    .append(" and outcomeId:")
                    .append(o.getOutcomeId())
                    .append(" is mapped to Id:")
                    .append(o.getProducerOutcomeId())
                    .append(" and Name:")
                    .append(o.getProducerOutcomeName(locale))
            );
        }
        return sb.toString();
    }
}
