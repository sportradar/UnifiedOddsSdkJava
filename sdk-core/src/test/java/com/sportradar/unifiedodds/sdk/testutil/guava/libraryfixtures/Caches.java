/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.guava.libraryfixtures;

import com.google.common.cache.Cache;

public class Caches {

    private Caches() {}

    public static Cache any() {
        return new NoOpCache();
    }
}
