/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import java.util.List;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class MarketMappingDataAssert
    extends AbstractAssert<MarketMappingDataAssert, List<MarketMappingData>> {

    private MarketMappingDataAssert(List<MarketMappingData> mappings) {
        super(mappings, MarketMappingDataAssert.class);
    }

    public static MarketMappingDataAssert assertThat(List<MarketMappingData> mappings) {
        return new MarketMappingDataAssert(mappings);
    }

    public MarketMappingDataAssert hasOnlyOneMappingWithMarketId(String expectedMarketId) {
        Assertions.assertThat(actual).hasSize(1);
        Assertions.assertThat(actual.get(0).getMarketId()).isEqualTo(expectedMarketId);
        return this;
    }
}
