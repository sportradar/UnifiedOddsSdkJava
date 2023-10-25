/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import java.util.HashMap;
import java.util.Map;

public class StubSdkConfigurationPropertiesReader extends SdkConfigurationPropertiesReader {

    private Map<String, String> properties;

    public StubSdkConfigurationPropertiesReader() {
        super(new ResourceReader());
        this.properties = new HashMap<>();
    }

    public StubSdkConfigurationPropertiesReader(Map<String, String> properties) {
        super(new ResourceReader());
        this.properties = properties;
    }

    @Override
    Map<String, String> readConfiguration() {
        return properties;
    }
}
