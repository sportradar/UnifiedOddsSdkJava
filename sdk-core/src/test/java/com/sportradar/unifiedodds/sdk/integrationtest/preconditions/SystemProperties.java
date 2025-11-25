/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

public class SystemProperties {

    private SystemProperties() {}

    public static boolean isBooleanSystemPropertySet(String name) {
        String value = System.getProperty(name);
        return value != null;
    }
}
