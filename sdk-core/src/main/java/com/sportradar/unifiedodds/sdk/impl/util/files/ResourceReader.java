/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.util.files;

import java.io.InputStream;

public class ResourceReader {

    public InputStream readAsInputStream(final String filename) {
        return this.getClass().getResourceAsStream(filename);
    }
}
