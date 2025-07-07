/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.internal.caching.ExecutionPath;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import java.util.Locale;

@SuppressWarnings({ "ClassFanOutComplexity", "ClassTypeParameterName", "LineLength" })
public class ExecutionPathDataProvider<T> {

    private final DataProvider<T> criticalDataProvider;
    private final DataProvider<T> nonCriticalDataProvider;

    public ExecutionPathDataProvider(
        DataProvider<T> criticalDataProvider,
        DataProvider<T> nonCriticalDataProvider
    ) {
        Preconditions.checkNotNull(criticalDataProvider);
        Preconditions.checkNotNull(nonCriticalDataProvider);
        this.criticalDataProvider = criticalDataProvider;
        this.nonCriticalDataProvider = nonCriticalDataProvider;
    }

    public T getData(RequestOptions requestOptions, Locale locale, String... args)
        throws DataProviderException {
        if (requestOptions.getExecutionPath() == ExecutionPath.NON_TIME_CRITICAL) {
            return nonCriticalDataProvider.getData(locale, args);
        } else {
            return criticalDataProvider.getData(locale, args);
        }
    }

    public String getFinalUrl(RequestOptions requestOptions, Locale locale, String arg) {
        if (requestOptions.getExecutionPath() == ExecutionPath.NON_TIME_CRITICAL) {
            return nonCriticalDataProvider.getFinalUrl(locale, arg);
        } else {
            return criticalDataProvider.getFinalUrl(locale, arg);
        }
    }

    public String toString() {
        return String.join(
            "",
            "RoutingDataProvider{",
            criticalDataProvider.toString(),
            ", ",
            nonCriticalDataProvider.toString() + "}"
        );
    }
}
