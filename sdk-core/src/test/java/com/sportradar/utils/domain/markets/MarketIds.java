/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.markets;

import java.util.Random;
import java.util.stream.IntStream;
import lombok.val;

public final class MarketIds {

    private static Random random = new Random();

    private MarketIds() {}

    public static int anyMarketId() {
        val ids = generateRangeOfIdsForTests();
        return ids[random.nextInt(ids.length)];
    }

    private static int[] generateRangeOfIdsForTests() {
        final int maxIdIsNotTooHighToGenerateNonUniqueValuesInAttemptToExposeWeaknessesInTests = 9;
        final int minIdIsPositiveNumber = 1;
        return IntStream
            .range(
                minIdIsPositiveNumber,
                maxIdIsNotTooHighToGenerateNonUniqueValuesInAttemptToExposeWeaknessesInTests
            )
            .toArray();
    }
}
