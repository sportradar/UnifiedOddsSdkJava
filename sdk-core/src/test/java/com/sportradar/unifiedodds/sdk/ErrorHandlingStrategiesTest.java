/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class ErrorHandlingStrategiesTest {

    private final int sampleSize = 100;
    private List<ExceptionHandlingStrategy> strategies = Stream
        .generate(() -> ExceptionHandlingStrategies.anyErrorHandlingStrategy())
        .limit(sampleSize)
        .distinct()
        .collect(Collectors.toList());

    @Test
    public void anyStrategyGeneratesNotAlwaysTheSameStrategy() {
        assertThat(strategies).hasSizeGreaterThan(1);
    }

    @Test
    public void anyStrategyDoesNotGenerateNulls() {
        assertThat(strategies).doesNotContainNull();
    }
}
