/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import org.mockito.stubbing.Answer;

public class GenericAnswers {

    public static <T> Answer<T> withAllMethodsThrowingByDefault() {
        return inv -> {
            throw new IllegalStateException(
                "Stub is no prepared to handle this method. Consider using Mockito.mock()"
            );
        };
    }
}
