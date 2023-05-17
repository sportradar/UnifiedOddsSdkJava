/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import lombok.NonNull;

public class Credentials {

    private String username;
    private String password;

    private Credentials(@NonNull final String username, @NonNull final String password) {
        this.username = username;
        this.password = password;
    }

    public static Credentials with(final String username, final String password) {
        return new Credentials(username, password);
    }

    public static Credentials any() {
        return with("SomeName", "someSurname");
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
