/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIEventPlayerAssist;

/**
 * Created on 24/11/2017.
 * // TODO @eti: Javadoc
 */
public class EventPlayerAssistCI extends EventPlayerCI {

    private final String type;

    EventPlayerAssistCI(SAPIEventPlayerAssist assistData) {
        super(assistData);

        Preconditions.checkNotNull(assistData);

        type = assistData.getType();
    }

    public String getType() {
        return type;
    }
}
