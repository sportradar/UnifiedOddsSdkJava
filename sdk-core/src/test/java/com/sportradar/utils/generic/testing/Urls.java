/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.net.URL;
import lombok.SneakyThrows;

public final class Urls {

    private Urls() {}

    @SneakyThrows
    public static URL anyHttpUrl() {
        String scheme = RandomObjectPicker.pickOneRandomlyFrom("http://", "https://");
        return new URL(scheme + anyDomain() + anyPath());
    }

    private static String anyPath() {
        return randomAlphanumeric(2);
    }

    private static String anyDomain() {
        return randomAlphanumeric(2) + ".com/";
    }
}
