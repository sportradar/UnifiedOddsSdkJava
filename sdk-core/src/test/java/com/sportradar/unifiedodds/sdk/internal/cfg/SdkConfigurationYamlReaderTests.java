/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.internal.impl.util.javaclass.ClassResolver;
import java.util.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({ "MagicNumber" })
public class SdkConfigurationYamlReaderTests {

    @Test
    public void basicLoadTest() {
        SdkConfigurationYamlReader sdkConfigurationYamlReader = new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader()
        );
        Map<String, String> stringStringMap = sdkConfigurationYamlReader.readConfiguration();

        assertEquals(33, stringStringMap.size());
    }

    @Test
    public void properParametersLoadedTest() {
        SdkConfigurationYamlReader reader = new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader()
        );

        assertEquals("test-token-yaml", reader.readAccessToken().get());
        assertEquals(46, (long) reader.readNodeId().get());
        assertEquals(Locale.ITALIAN, reader.readDefaultLanguage().get());
        assertTrue(reader.readDesiredLanguages().containsAll(getExpectedDesiredLanguages()));
        assertEquals("test-msg-host", reader.readMessagingHost().get());
        assertEquals(7777, (int) reader.readMessagingPort().get());
        assertEquals("msg-uname", reader.readMessagingUsername().get());
        assertEquals("msg-pass", reader.readMessagingPassword().get());
        assertEquals("msg-vhost", reader.readMessagingVirtualHost().get());
        assertTrue(reader.readMessagingUseSsl().get());
        assertEquals("api-host", reader.readApiHost().get());
        assertEquals(80, (int) reader.readApiPort().get());
        assertTrue(reader.readApiUseSsl().get());
        assertTrue(reader.readDisabledProducers().containsAll(getExpectedDisabledProducers()));
        Assert.assertEquals(ExceptionHandlingStrategy.Throw, reader.readExceptionHandlingStrategy().get());
        Assert.assertEquals(Environment.GlobalIntegration, reader.readEnvironment());
    }

    @Test
    public void unparsableNodeIdThrows() {
        Map<String, String> fileProperties = new HashMap<>();
        fileProperties.put("uf.sdk.accessToken", "defaultAccessToken");
        fileProperties.put("uf.sdk.defaultLanguage", "en");
        fileProperties.put("uf.sdk.exceptionHandlingStrategy", "catch");
        fileProperties.put("uf.sdk.environment", "Integration");
        fileProperties.put("uf.sdk.nodeId", "01a");

        StubSdkConfigurationYamlReader reader = new StubSdkConfigurationYamlReader(fileProperties);

        assertThatThrownBy(() -> reader.readNodeId());
    }

    private static List<Locale> getExpectedDesiredLanguages() {
        return asList(Locale.FRENCH, Locale.GERMAN, Locale.ENGLISH);
    }

    private static List<Integer> getExpectedDisabledProducers() {
        return asList(5, 6, 7, 8);
    }
}
