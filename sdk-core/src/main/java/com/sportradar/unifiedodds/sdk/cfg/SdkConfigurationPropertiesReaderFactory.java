/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import org.yaml.snakeyaml.Yaml;

public class SdkConfigurationPropertiesReaderFactory {

    private SdkConfigurationPropertiesReaderFactory() {}

    public static SdkConfigurationPropertiesReader create() {
        return new SdkConfigurationPropertiesReader(new ResourceReader());
    }

    public static SdkConfigurationPropertiesReader create(String fileName) {
        return new SdkConfigurationPropertiesReader(new ResourceReader(), fileName);
    }
}
