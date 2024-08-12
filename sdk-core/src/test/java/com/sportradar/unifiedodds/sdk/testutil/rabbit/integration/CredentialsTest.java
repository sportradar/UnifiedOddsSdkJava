/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import lombok.val;
import org.junit.jupiter.api.Test;

public class CredentialsTest {

    private final String any = "any";

    @Test
    public void usernameShouldNotBeNull() {
        assertThatThrownBy(() -> Credentials.with(null, any))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("username");
    }

    @Test
    public void passwordShouldNotBeNull() {
        assertThatThrownBy(() -> Credentials.with(any, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("password");
    }

    @Test
    public void shouldPreserveUsername() {
        val username = "John";
        assertEquals(username, Credentials.with(username, any).getUsername());
    }

    @Test
    public void shouldPreservePassword() {
        val password = "P4ssw0rd!";
        assertEquals(password, Credentials.with(any, password).getPassword());
    }

    @Test
    public void shouldCreateAnyCredentials() {
        assertNotNull(Credentials.any().getUsername());
        assertNotNull(Credentials.any().getUsername());
    }
}
