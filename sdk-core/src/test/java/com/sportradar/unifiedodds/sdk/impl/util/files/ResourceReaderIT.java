/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.util.files;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;

public class ResourceReaderIT {

    @Test
    public void readsAsInputStream() throws IOException {
        String text;
        try (
            Reader reader = new InputStreamReader(
                new ResourceReader().readAsInputStream("/sr-sdk-version.properties")
            )
        ) {
            text = CharStreams.toString(reader);
        }
        assertThat(text).startsWith("version=3");
    }
}
