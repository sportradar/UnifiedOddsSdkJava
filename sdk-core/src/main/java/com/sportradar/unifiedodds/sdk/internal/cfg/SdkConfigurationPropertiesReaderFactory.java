/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;

public class SdkConfigurationPropertiesReaderFactory {

    private SdkConfigurationPropertiesReaderFactory() {}

    public static SdkConfigurationPropertiesReader create() {
        return new SdkConfigurationPropertiesReader(new ResourceReader());
    }

    public static SdkConfigurationPropertiesReader create(String fileName) {
        return new SdkConfigurationPropertiesReader(new ResourceReader(), fileName);
    }
}
