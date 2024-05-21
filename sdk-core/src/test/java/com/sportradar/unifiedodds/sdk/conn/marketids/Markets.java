/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import java.util.List;
import java.util.stream.Collectors;

public class Markets {

    public static OutcomeOdds findOutcome(String outcomeId, MarketWithOdds market) {
        List<OutcomeOdds> outcomes = market
            .getOutcomeOdds()
            .stream()
            .filter(o -> outcomeId.equals(o.getId()))
            .collect(toList());
        assertThat(outcomes).hasSize(1);
        return outcomes.get(0);
    }
}
