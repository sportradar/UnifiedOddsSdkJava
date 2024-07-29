/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatusCachePopulator {

    private SportEventStatusCacheImpl cache;

    public static StatusCachePopulator populate(SportEventStatusCacheImpl cache) {
        return new StatusCachePopulator(cache);
    }

    public void fromMessage(Urn sportEventUrn, SportEventStatusDto status) {
        cache.onSportEventStatusFetched(sportEventUrn, status, null, "UfOddsChange");
    }

    public void fromSummary(Urn matchUrn, SportEventStatusDto statusDto) {
        cache.onSportEventStatusFetched(matchUrn, statusDto, null, "SapiMatchSummaryEndpoint");
    }
}
