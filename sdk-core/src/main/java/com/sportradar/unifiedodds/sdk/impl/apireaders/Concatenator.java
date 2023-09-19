/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

public class Concatenator {

    private final StringBuilder concatenated = new StringBuilder();
    private final String separator;

    private Concatenator(String separator) {
        this.separator = separator;
    }

    public static Concatenator separatingWith(String separator) {
        return new Concatenator(separator);
    }

    Concatenator appendIfNotNull(String token) {
        if (token != null) {
            appendCommaAfterLast();
            concatenated.append(token);
        }
        return this;
    }

    Concatenator appendIfNotNull(Integer token) {
        if (token != null) {
            appendCommaAfterLast();
            concatenated.append(token);
        }
        return this;
    }

    private void appendCommaAfterLast() {
        if (concatenated.length() > 0) {
            concatenated.append(separator);
        }
    }

    String retrieve() {
        return concatenated.toString();
    }
}
