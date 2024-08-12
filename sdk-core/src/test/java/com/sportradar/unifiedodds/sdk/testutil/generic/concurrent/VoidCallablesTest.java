/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables.voidCallable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import org.junit.jupiter.api.Test;

public class VoidCallablesTest {

    @Test
    public void shouldAllowToCreateCallableWithoutReturnType() {
        assertThat(voidCallable(() -> {})).isInstanceOf(Callable.class);
    }

    @Test
    public void shouldCreateCallableWithDefinedLogicInIt() throws Exception {
        val reference = new AtomicReference<>("A");
        val updated = "B";

        voidCallable(() -> reference.set(updated)).call();

        assertEquals(updated, reference.get());
    }

    @Test
    public void shouldBeAbleToThrowCheckedException() {
        val callable = voidCallable(() -> {
            throw new IOException();
        });

        assertThatThrownBy(() -> callable.call()).isInstanceOf(IOException.class);
    }

    @Test
    public void shouldReturnNull() throws Exception {
        assertNull(voidCallable(() -> {}).call());
    }
}
