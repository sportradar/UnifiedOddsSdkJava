/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;

public class DataRouterManagers {

    private DataRouterManagers() {}

    public static DataRouterManager any() {
        return new NoOpDataRouterManager();
    }
}
