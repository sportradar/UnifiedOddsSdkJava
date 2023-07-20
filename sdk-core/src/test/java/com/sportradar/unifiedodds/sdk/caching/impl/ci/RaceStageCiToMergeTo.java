/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.sportradar.unifiedodds.sdk.caching.StageCI;

class RaceStageCiToMergeTo {

    private StageCI value;

    private RaceStageCiToMergeTo(StageCI value) {
        this.value = value;
    }

    static RaceStageCiToMergeTo into(StageCI target) {
        return new RaceStageCiToMergeTo(target);
    }

    StageCI getValue() {
        return value;
    }
}
