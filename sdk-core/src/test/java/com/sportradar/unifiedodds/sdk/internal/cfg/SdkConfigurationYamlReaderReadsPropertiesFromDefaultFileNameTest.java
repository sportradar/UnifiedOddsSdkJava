/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.internal.impl.util.javaclass.ClassResolver;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class SdkConfigurationYamlReaderReadsPropertiesFromDefaultFileNameTest {

    public static final String DEFAULT_YAML_FILENAME = "/application.yml";
    private ResourceReader resourceReader = mock(ResourceReader.class);

    private SdkConfigurationYamlReader reader = new SdkConfigurationYamlReader(
        new ClassResolver(),
        new Yaml(),
        resourceReader
    );

    @Test
    public void readsEmptyConfigurationFile() {
        whenYamlFileContains("");

        assertThat(reader.readConfiguration()).isEmpty();
    }

    @Test
    public void readsPropertyPrefixedWithUfSdk() {
        whenYamlFileContains(
            "" +
            "sportradar:                                              \n" +
            "  sdk:                                                   \n" +
            "    uf:                                                  \n" +
            "      accessToken: someToken                               "
        );

        assertThat(reader.readConfiguration()).containsEntry("uf.sdk.accessToken", "someToken");
    }

    private void whenYamlFileContains(String yamlFileContent) {
        when(resourceReader.readAsInputStream(DEFAULT_YAML_FILENAME))
            .thenReturn(new ByteArrayInputStream(yamlFileContent.getBytes(StandardCharsets.UTF_8)));
    }
}
