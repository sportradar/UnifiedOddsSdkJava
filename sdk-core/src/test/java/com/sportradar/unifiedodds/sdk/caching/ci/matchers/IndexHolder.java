/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci.matchers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexHolder {

    private final int index;

    public static IndexHolder atIndex(int index) {
        return new IndexHolder(index);
    }

    public int get() {
        return index;
    }
}
