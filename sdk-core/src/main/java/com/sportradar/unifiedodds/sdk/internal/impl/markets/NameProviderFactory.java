/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import java.util.Map;

/**
 * Created on 20/06/2017.
 * // TODO @eti: Javadoc
 */
public interface NameProviderFactory {
    NameProvider buildNameProvider(
        SportEvent sportEvent,
        int marketId,
        Map<String, String> specifiers,
        int producerId
    );
}
