/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.stream.optional;

import static java.util.Optional.ofNullable;

import java.util.function.Function;

public class NonNullMapper<T> {

    private final T source;

    private NonNullMapper(T source) {
        this.source = source;
    }

    public static <T> NonNullMapper<T> ifNotNull(T source) {
        return new NonNullMapper(source);
    }

    public <U> U map(Function<T, U> mapper) {
        return ofNullable(source).map(mapper).orElse(null);
    }
}
