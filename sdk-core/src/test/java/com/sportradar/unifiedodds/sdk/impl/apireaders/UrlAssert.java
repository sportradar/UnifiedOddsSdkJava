/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import java.net.URI;
import org.assertj.core.api.AbstractAssert;

public class UrlAssert extends AbstractAssert<UrlAssert, String> {

    protected UrlAssert(String url, Class<?> selfType) {
        super(url, selfType);
    }

    public static UrlAssert assertThat(String url) {
        return new UrlAssert(url, UrlAssert.class);
    }

    public UrlAssert startsWith(String prefix) {
        isNotNull();
        if (!actual.startsWith(prefix)) {
            failWithMessage("Expected URL to start with <%s> but was <%s>", prefix, actual);
        }
        return this;
    }

    public UrlAssert contains(String substring) {
        isNotNull();
        if (!actual.contains(substring)) {
            failWithMessage("Expected URL to contain <%s> but was <%s>", substring, actual);
        }
        return this;
    }

    public void hasPathEqualTo(String path) {
        isNotNull();
        String actualPath = URI.create(actual).getPath();
        if (!actualPath.equals(path)) {
            failWithMessage("Expected URL path to be <%s> but was <%s>", path, actual);
        }
    }
}
