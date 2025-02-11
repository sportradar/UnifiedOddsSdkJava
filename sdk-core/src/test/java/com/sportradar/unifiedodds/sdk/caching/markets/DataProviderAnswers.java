/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import org.mockito.stubbing.Answer;

public class DataProviderAnswers {

    public static <T> Answer<T> withGetDataThrowingByDefault() {
        return inv -> {
            if (inv.getMethod().getName().startsWith("getData")) {
                throw new DataProviderException("Failed to fetch from a DataProvider Stub");
            }
            throw new IllegalStateException(
                "DataProviderStub is no prepared to handle this method. Consider using Mockito.mock()"
            );
        };
    }
}
