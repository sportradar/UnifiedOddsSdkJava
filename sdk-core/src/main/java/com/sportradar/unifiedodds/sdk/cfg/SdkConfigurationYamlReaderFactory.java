/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import org.yaml.snakeyaml.Yaml;

public class SdkConfigurationYamlReaderFactory {

    private SdkConfigurationYamlReaderFactory() {}

    public static SdkConfigurationYamlReader create() {
        return new SdkConfigurationYamlReader(new ClassResolver(), new Yaml(), new ResourceReader());
    }

    public static SdkConfigurationYamlReader create(String fileName) {
        return new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader(),
            fileName
        );
    }
}
