/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.functional;

public interface ThrowingFunction<T, U> {
    public U apply(final T argument) throws Exception;
}
