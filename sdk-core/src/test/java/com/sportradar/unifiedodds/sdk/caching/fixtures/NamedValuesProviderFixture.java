/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.fixtures;

import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import java.util.Optional;
import lombok.NonNull;

public class NamedValuesProviderFixture implements NamedValuesProvider {

    private Optional<NamedValueStub> stubbedBetstopReasons = Optional.empty();

    private Optional<NamedValueStub> stubbedBettingStatus = Optional.empty();

    @Override
    public NamedValueCache getVoidReasons() {
        return null;
    }

    @Override
    public NamedValueCache getBetStopReasons() {
        return buildNamedValueCacheWith(stubbedBetstopReasons);
    }

    @Override
    public NamedValueCache getBettingStatuses() {
        return buildNamedValueCacheWith(stubbedBettingStatus);
    }

    @Override
    public LocalizedNamedValueCache getMatchStatuses() {
        return null;
    }

    public void stubBetstopReason(@NonNull final NamedValueStub betstopReasonToStub) {
        this.stubbedBetstopReasons = Optional.of(betstopReasonToStub);
    }

    public void stubBettingStatus(@NonNull final NamedValueStub bettingStatusToStub) {
        this.stubbedBettingStatus = Optional.of(bettingStatusToStub);
    }

    private NamedValueCache buildNamedValueCacheWith(final Optional<NamedValueStub> stubbedNamedValue) {
        return new NamedValueCache() {
            @Override
            public NamedValue getNamedValue(int id) {
                return stubbedNamedValue.filter(r -> r.getId() == id).orElse(new NamedValueStub(id, null));
            }

            @Override
            public boolean isValueDefined(int id) {
                return false;
            }
        };
    }
}
