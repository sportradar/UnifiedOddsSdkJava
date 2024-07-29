/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;

public class ExceptionHandlingStrategies {

    private ExceptionHandlingStrategies() {}

    public static ExceptionHandlingStrategy anyErrorHandlingStrategy() {
        return Throw;
    }
}
