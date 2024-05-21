/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import com.sportradar.uf.sportsapi.datamodel.Mappings.Mapping.MappingOutcome;
import java.util.List;
import java.util.Map;
import lombok.Value;

public interface MarketVariant extends Identifiable {
    String id();

    List<String> outcomeIds();

    default Map<Mapping, List<String>> mappings() {
        return ImmutableMap.of();
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    class Mapping {

        int productId;
        String productIds;
        String sportId;
        String marketId;
        String sovTemplate;

        public Mappings.Mapping toSapiMapping(List<MappingOutcome> mappingOutcome) {
            Mappings.Mapping mapping = new Mappings.Mapping();
            mapping.setProductId(productId);
            mapping.setProductIds(productIds);
            mapping.setSportId(sportId);
            mapping.setMarketId(marketId);
            mapping.setSovTemplate(sovTemplate);
            mapping.getMappingOutcome().addAll(mappingOutcome);
            return mapping;
        }
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    class OutcomeMapping {

        String outcomeId;
        String productOutcomeId;
        String productOutcomeName;

        public Mappings.Mapping.MappingOutcome toSapiOutcomeMapping() {
            MappingOutcome outcome = new MappingOutcome();
            outcome.setOutcomeId(outcomeId);
            outcome.setProductOutcomeId(productOutcomeId);
            outcome.setProductOutcomeName(productOutcomeName);
            return outcome;
        }
    }
}
