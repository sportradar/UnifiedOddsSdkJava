/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl.ci;

import com.sportradar.unifiedodds.sdk.internal.caching.StageCi;

class RaceStageCiToMergeTo {

    private StageCi value;

    private RaceStageCiToMergeTo(StageCi value) {
        this.value = value;
    }

    static RaceStageCiToMergeTo into(StageCi target) {
        return new RaceStageCiToMergeTo(target);
    }

    StageCi getValue() {
        return value;
    }
}
