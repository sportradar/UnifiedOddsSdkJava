/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class RandomIntegerTest {

    private final int sampleSize = 100;

    @Test
    public void generatesValuesFromRangeInclusively() {
        final int lowestValue = 5;
        final int midValue = 6;
        final int highestValue = 7;
        final int rangeInclusive = 3;

        List<Integer> integers = Stream
            .generate(() -> RandomInteger.fromRangeInclusive(lowestValue, highestValue))
            .limit(sampleSize)
            .distinct()
            .collect(Collectors.toList());

        assertThat(integers).hasSize(rangeInclusive);
        assertThat(integers).containsOnly(lowestValue, midValue, highestValue);
    }
}
