/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.net.MalformedURLException;
import java.net.URL;

public final class Urls {

    private Urls() {}

    public static URL anyHttpUrl() throws MalformedURLException {
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
