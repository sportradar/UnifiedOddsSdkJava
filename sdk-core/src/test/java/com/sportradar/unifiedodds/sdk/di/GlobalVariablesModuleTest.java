/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.internal.di.GlobalVariablesModule;
import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GlobalVariablesModuleTest {

    public static final String RESOURCE_NAME = "/sr-sdk-version.properties";

    private GlobalVariablesModuleTest() {}

    @Nested
    public class FailureToReadVersion {

        @Test
        public void readsZeroAsSdkCoreArtifactVersio() {
            ResourceReader reader = mock(ResourceReader.class);
            when(reader.readAsInputStream(RESOURCE_NAME)).thenReturn(new IoExceptionThrowingInputStream());
            val injector = Guice.createInjector(new GlobalVariablesModule(reader));

            val versionHolder = injector.getInstance(VersionHolder.class);

            assertThat(versionHolder.version).isEqualTo("0.0");
        }
    }

    @Nested
    public class HappyPathVersionRead {

        public static final String VERSION = "2.6.7-SNAPSHOT";
        public static final String RESOURCE_CONTENT = "version=" + VERSION;

        @Test
        public void readsSdkCoreArtifactVersion() {
            ResourceReader reader = mock(ResourceReader.class);
            when(reader.readAsInputStream(RESOURCE_NAME)).thenReturn(asInputStream(RESOURCE_CONTENT));
            val injector = Guice.createInjector(new GlobalVariablesModule(reader));

            val versionHolder = injector.getInstance(VersionHolder.class);

            assertThat(versionHolder.version).isEqualTo(VERSION);
        }

        private ByteArrayInputStream asInputStream(String version) {
            return new ByteArrayInputStream(version.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static class VersionHolder {

        private String version;

        @Inject
        public VersionHolder(@Named("version") String version) {
            this.version = version;
        }
    }

    @AllArgsConstructor
    public static class IoExceptionThrowingInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException();
        }
    }
}
