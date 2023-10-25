/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.util.files;

import static org.mockito.Mockito.mock;

import junit.framework.TestCase;

public class ResourceReaderStubs {

    private ResourceReaderStubs() {}

    public static ResourceReader anyResourceReader() {
        return mock(ResourceReader.class);
    }
}
