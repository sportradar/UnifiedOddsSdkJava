/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain;

import com.sportradar.utils.Urn;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UniqueObjects<T> {

    private Supplier<T> generator;
    private Set<T> uniqueUrns = new HashSet<>();

    public UniqueObjects(Supplier<T> generator) {
        this.generator = generator;
    }

    public T getOne() {
        T unique = Stream.generate(generator).filter(wasNotGeneratedBefore()).findFirst().get();
        uniqueUrns.add(unique);
        return unique;
    }

    private Predicate<T> wasNotGeneratedBefore() {
        return urn -> !uniqueUrns.contains(urn);
    }
}
