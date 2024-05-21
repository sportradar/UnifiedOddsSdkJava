/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import static com.sportradar.utils.generic.testing.RandomInteger.fromRange;
import static com.sportradar.utils.generic.testing.RandomInteger.fromRangeInclusive;

import java.util.ArrayList;
import java.util.Collection;

public final class RandomObjectPicker {

    private RandomObjectPicker() {}

    public static <T> T pickOneRandomlyFrom(T... objects) {
        return objects[fromRangeInclusive(0, objects.length - 1)];
    }

    public static <T> T pickOneRandomlyFrom(Collection<T> objects) {
        final int anyIndex = fromRange(0, objects.size());
        return asList(objects).get(anyIndex);
    }

    private static <T> ArrayList<T> asList(Collection<T> objects) {
        return new ArrayList<>(objects);
    }
}
