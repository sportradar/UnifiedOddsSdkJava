/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

public final class RandomObjectPicker {

    private RandomObjectPicker() {}

    public static <T> T pickOneRandomlyFrom(T... objects) {
        return objects[RandomInteger.fromRangeInclusive(0, objects.length - 1)];
    }
}
