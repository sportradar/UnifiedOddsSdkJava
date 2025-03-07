/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import java.util.concurrent.*;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssertAlternative;

public class CompletableFutureAssert<T>
    extends AbstractAssert<CompletableFutureAssert<T>, CompletableFuture<T>> {

    protected CompletableFutureAssert(CompletableFuture<T> future, Class<?> selfType) {
        super(future, selfType);
    }

    public static <T> CompletableFutureAssert<T> assertThat(CompletableFuture<T> future) {
        return new CompletableFutureAssert<>(future, CompletableFutureAssert.class);
    }

    public ThrowableAssertAlternative<Exception> completesExceptionallyWithin(int timeout, TimeUnit unit) {
        isNotNull();
        return Assertions
            .assertThatException()
            .isThrownBy(() -> {
                try {
                    actual.get(timeout, unit);
                } catch (ExecutionException e) {
                    throw e.getCause();
                } catch (TimeoutException e) {
                    throw new AssertionError("Expected future to complete within " + timeout + " " + unit);
                }
            });
    }
}
