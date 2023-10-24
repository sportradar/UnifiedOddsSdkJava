/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.generationassert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataGenerationAssert {

    private static final int SAMPLE_SIZE = 100;

    private DataGenerationAssert() {}

    public static <T> void assertThatGeneratesDistinctAndNonNull(Supplier<T> source) {
        List<T> distinctOnes = Stream
            .generate(() -> source.get())
            .limit(SAMPLE_SIZE)
            .distinct()
            .collect(Collectors.toList());

        assertThat(distinctOnes).hasSizeGreaterThan(1);
        assertThat(distinctOnes).doesNotContainNull();
    }
}
