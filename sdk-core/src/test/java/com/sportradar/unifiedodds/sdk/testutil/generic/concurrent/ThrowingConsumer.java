/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}
