/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;

import java.util.Arrays;
import java.util.Random;
import lombok.val;

public class ExceptionHandlingStrategies {

    private static Random random = new Random();

    private ExceptionHandlingStrategies() {}

    public static ExceptionHandlingStrategy anyErrorHandlingStrategy() {
        val strategiesPool = Arrays.asList(Throw, Catch);
        return strategiesPool.get(random.nextInt(strategiesPool.size()));
    }
}
