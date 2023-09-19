/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class SdkConfigurationYamlReaderOnUnexpectedYamlElementTest {

    public static final String ANY_FILENAME = "anyFileName";
    public static final String ANY_CONTENT = "anyContent";
    public static final Object UNRECOGNISED_YAML_ELEMENT = new Object();
    private ResourceReader resourceReader = mock(ResourceReader.class);
    private Yaml yaml = mock(Yaml.class);

    @Test
    public void notCrashesWhenReadingFromDefaultFile() {
        whenYamlFileContains(ANY_CONTENT);
        when(yaml.loadAll(any(InputStream.class)))
            .thenReturn(
                createPropertyMap("sportradar", "sdk", "uf", "accessToken", UNRECOGNISED_YAML_ELEMENT)
            );
        val reader = new SdkConfigurationYamlReader(new ClassResolver(), yaml, resourceReader, ANY_FILENAME);

        assertThat(reader.readConfiguration()).isEmpty();
    }

    @Test
    public void notCrashesWhenReadingFromUserProvidedFile() {
        whenYamlFileContains(ANY_CONTENT);
        when(yaml.loadAll(any(InputStream.class)))
            .thenReturn(
                createPropertyMap("sportradar", "sdk", "uf", "accessToken", UNRECOGNISED_YAML_ELEMENT)
            );
        val reader = new SdkConfigurationYamlReader(new ClassResolver(), yaml, resourceReader);

        assertThat(reader.readConfiguration()).isEmpty();
    }

    @Test
    public void notCrashesTopLevelElementIsNotMap() {
        whenYamlFileContains(ANY_CONTENT);
        when(yaml.loadAll(any(InputStream.class))).thenReturn(asList(UNRECOGNISED_YAML_ELEMENT));
        val reader = new SdkConfigurationYamlReader(new ClassResolver(), yaml, resourceReader, ANY_FILENAME);

        assertThat(reader.readConfiguration()).isEmpty();
    }

    private Iterable<Object> createPropertyMap(
        String topLevelName,
        String levelTwoName,
        String levelThreeName,
        String propertyName,
        Object propertyValue
    ) {
        return asList(
            wrapInMap(
                topLevelName,
                wrapInMap(levelTwoName, wrapInMap(levelThreeName, wrapInMap(propertyName, propertyValue)))
            )
        );
    }

    private Map<String, Object> wrapInMap(String name, Object value) {
        val map = new HashMap<String, Object>();
        map.put(name, value);
        return map;
    }

    private void whenYamlFileContains(String yamlFileContent) {
        when(resourceReader.readAsInputStream(any()))
            .thenReturn(new ByteArrayInputStream(yamlFileContent.getBytes(StandardCharsets.UTF_8)));
    }
}
