/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.internal.caching.ExecutionPath.NON_TIME_CRITICAL;
import static com.sportradar.unifiedodds.sdk.internal.caching.ExecutionPath.TIME_CRITICAL;
import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions.requestOptions;

public class RequestOptionsProviders {

    public static RequestOptions nonTimeCriticalRequestOptions() {
        return requestOptions().setExecutionPath(NON_TIME_CRITICAL).build();
    }

    public static RequestOptions timeCriticalRequestOptions() {
        return requestOptions().setExecutionPath(TIME_CRITICAL).build();
    }
}
