/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic stream utility methods
 */
public class StreamUtils {

    private StreamUtils() {}

    /**
     * Utility method used to filter out objects by some parameter
     *
     * @param keyExtractor the provider of the parameter on which to distinct the objects
     * @param <T> the objects type
     * @return <code>true</code> if the object was already filtered, otherwise <code>false</code>
     */
    public static <T> Predicate<T> distinctObjects(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
