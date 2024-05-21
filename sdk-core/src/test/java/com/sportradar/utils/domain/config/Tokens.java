/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.config;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class Tokens {

    public static final int TOKEN_LENGTH = 18;

    public static String any() {
        return randomAlphanumeric(TOKEN_LENGTH);
    }
}
