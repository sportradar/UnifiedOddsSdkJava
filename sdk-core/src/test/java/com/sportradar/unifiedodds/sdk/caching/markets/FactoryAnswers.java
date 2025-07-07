/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import org.mockito.stubbing.Answer;

public class FactoryAnswers {

    public static <T> Answer<T> withBuildThrowingByDefault() {
        return inv -> {
            if (inv.getMethod().getName().startsWith("build")) {
                throw new RuntimeException("Failed to call " + inv.getMethod().getName() + " on a stub");
            }
            throw new IllegalStateException(
                "Stub is no prepared to handle this method. Consider using Mockito.mock()"
            );
        };
    }
}
