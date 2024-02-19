/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import java.util.Random;
import lombok.val;

public final class Cardinality {

    private static Random random = new Random();

    private Cardinality() {}

    public static Integer anyFromZeroToTwo() {
        val zeroToMultiple = new Integer[] { 0, 1, 2 };
        return zeroToMultiple[random.nextInt(zeroToMultiple.length)];
    }
}
