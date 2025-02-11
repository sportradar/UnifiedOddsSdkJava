/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.internal.impl.util.javaclass.ClassResolver;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("MultipleStringLiterals")
public class SdkConfigurationYamlReaderReadsPropertiesFromUserProvidedFileTest {

    public static final String YAML_FILENAME = "someFileName";
    private ResourceReader resourceReader = mock(ResourceReader.class);
    private SdkConfigurationYamlReader reader = new SdkConfigurationYamlReader(
        new ClassResolver(),
        new Yaml(),
        resourceReader,
        YAML_FILENAME
    );

    @Test
    public void appendsSlashToFileNameToBeAbleToReadResource() {
        whenYamlFileContains("");

        reader.readConfiguration();

        ArgumentCaptor<String> filePathCaptor = ArgumentCaptor.forClass(String.class);
        verify(resourceReader).readAsInputStream(filePathCaptor.capture());
        assertThat(filePathCaptor.getValue()).isEqualTo("/" + YAML_FILENAME);
    }

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

    @Test
    public void ignoresYamlWhichDoesNotHaveSportradarAsTopLevelElement() {
        whenYamlFileContains("" + "unexpectedPropertyName: propertyValue                    \n");

        assertThat(reader.readConfiguration()).hasSize(0);
    }

    @Test
    public void skipsUnexpectedPropertiesAtRootLevel() {
        whenYamlFileContains(
            "" +
            "unexpectedPropertyName: propertyValue                    \n" +
            "sportradar:                                              \n" +
            "  sdk:                                                   \n" +
            "    uf:                                                  \n" +
            "      accessToken: someToken                               "
        );

        assertThat(reader.readConfiguration()).hasSize(1);
    }

    @Test
    public void theOnlyExpectedPropertyAtRootLevelNeedsToBeParentProperty() {
        whenYamlFileContains("sportradar: propertyValue");

        assertThat(reader.readConfiguration()).isEmpty();
    }

    @Test
    public void skipsUnexpectedPropertiesAtLevelTwo() {
        whenYamlFileContains(
            "" +
            "sportradar:                                              \n" +
            "  unexpectedPropertyName: propertyValue                  \n" +
            "  sdk:                                                   \n" +
            "    uf:                                                  \n" +
            "      accessToken: someToken                               "
        );

        assertThat(reader.readConfiguration()).hasSize(1);
    }

    @Test
    public void theOnlyExpectedPropertyAtLevelTwoIsSdkButItNeedsToBeParentProperty() {
        whenYamlFileContains(
            "" +
            "sportradar:                                              \n" +
            "  sdk: someValue                                         \n"
        );

        assertThat(reader.readConfiguration()).isEmpty();
    }

    @Test
    public void skipsUnexpectedPropertiesAtLevelThree() {
        whenYamlFileContains(
            "" +
            "sportradar:                                              \n" +
            "  sdk:                                                   \n" +
            "    unexpectedPropertyName: propertyValue                \n" +
            "    uf:                                                  \n" +
            "      accessToken: someToken                               "
        );

        assertThat(reader.readConfiguration()).hasSize(1);
    }

    @Test
    public void theOnlyExpectedPropertyAtLevelThreeIsUfButItNeedsToBeParentProperty() {
        whenYamlFileContains(
            "" +
            "sportradar:                                              \n" +
            "  sdk:                                                   \n" +
            "    uf: someValue                                        \n"
        );

        assertThat(reader.readConfiguration()).isEmpty();
    }

    private void whenYamlFileContains(String yamlFileContent) {
        when(resourceReader.readAsInputStream("/" + YAML_FILENAME))
            .thenReturn(new ByteArrayInputStream(yamlFileContent.getBytes(StandardCharsets.UTF_8)));
    }
}
