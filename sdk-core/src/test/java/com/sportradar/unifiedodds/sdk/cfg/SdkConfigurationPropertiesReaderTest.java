/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class SdkConfigurationPropertiesReaderTest {

    public static final String SLASH = "/";

    private SdkConfigurationPropertiesReaderTest() {}

    @Nested
    public class SdkConfigurationPropertiesReaderReadsPropertiesFromDefaultFileNameTest {

        public static final String DEFAULT_PROPERTIES_FILENAME = "/UFSdkConfiguration.properties";

        private ResourceReader resourceReader = mock(ResourceReader.class, withSettings().verboseLogging());

        private SdkConfigurationPropertiesReader reader = new SdkConfigurationPropertiesReader(
            resourceReader
        );

        @Test
        public void readsEmptyConfigurationFile() {
            whenPropertyFileContains("");

            assertThat(reader.readConfiguration()).isEmpty();
        }

        @Test
        public void readsProperty() {
            whenPropertyFileContains("uf.sdk.accessToken=someAccessToken");

            assertThat(reader.readConfiguration()).containsEntry("uf.sdk.accessToken", "someAccessToken");
        }

        private void whenPropertyFileContains(String yamlFileContent) {
            when(resourceReader.readAsInputStream(DEFAULT_PROPERTIES_FILENAME))
                .thenReturn(new ByteArrayInputStream(yamlFileContent.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Nested
    public class SdkConfigurationYamlReaderReadsPropertiesFromUserProvidedFileTest {

        public static final String PROPERTIES_FILENAME = "someFileName";
        private ResourceReader resourceReader = mock(ResourceReader.class);
        private SdkConfigurationPropertiesReader reader = new SdkConfigurationPropertiesReader(
            resourceReader,
            PROPERTIES_FILENAME
        );

        @Test
        public void appendsSlashToFileNameToBeAbleToReadResource() {
            whenYamlFileContains("");

            reader.readConfiguration();

            ArgumentCaptor<String> filePathCaptor = ArgumentCaptor.forClass(String.class);
            verify(resourceReader).readAsInputStream(filePathCaptor.capture());
            assertThat(filePathCaptor.getValue()).isEqualTo(SLASH + PROPERTIES_FILENAME);
        }

        @Test
        public void readsEmptyConfigurationFile() {
            whenYamlFileContains("");

            assertThat(reader.readConfiguration()).isEmpty();
        }

        @Test
        public void readsPropertyPrefixedWithUfSdk() {
            whenYamlFileContains("uf.sdk.accessToken=someAccessToken");

            assertThat(reader.readConfiguration()).containsEntry("uf.sdk.accessToken", "someAccessToken");
        }

        @Test
        public void fileReadingFailuresShouldNotThrowException() {
            when(resourceReader.readAsInputStream(SLASH + PROPERTIES_FILENAME))
                .thenReturn(new IoExceptionThrowingInputStream());

            assertThat(reader.readConfiguration()).isNotNull();
        }

        @Test
        public void fileClosingFailuresShouldNotThrowException() {
            when(resourceReader.readAsInputStream(SLASH + PROPERTIES_FILENAME))
                .thenReturn(new IoExceptionThrowingInputStreamOnClosing());

            assertThat(reader.readConfiguration()).isNotNull();
        }

        private void whenYamlFileContains(String yamlFileContent) {
            when(resourceReader.readAsInputStream(SLASH + PROPERTIES_FILENAME))
                .thenReturn(new ByteArrayInputStream(yamlFileContent.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @AllArgsConstructor
    public static class IoExceptionThrowingInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException();
        }
    }

    @AllArgsConstructor
    public static class IoExceptionThrowingInputStreamOnClosing extends InputStream {

        private static final int STREAM_END = -1;

        @Override
        public int read() throws IOException {
            return STREAM_END;
        }

        @Override
        public void close() throws IOException {
            throw new IOException();
        }
    }
}
