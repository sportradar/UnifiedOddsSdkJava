package com.sportradar.unifiedodds.sdk;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SDKConfigurationReaderTest {
    //Default config files:
    @Test
    public void loadsDefaultYamlFile() {
        Optional<String> token = new SDKConfigurationYamlReader()
                .readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void loadsDefaultPropertiesFile() {
        Optional<String> token = new SDKConfigurationPropertiesReader()
                .readAccessToken();

        assertEquals("test-token-props", token.get());
    }

    //Custom config files:
    @Test
    public void loadsCustomYamlFile() {
        Optional<String> token = new SDKConfigurationYamlReader("application.yml")
                .readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void returnsEmptyPropertyWhenYamlFileDoesNotExist() {
        Optional<String> token = new SDKConfigurationYamlReader("non-existing-file.yml")
                .readAccessToken(); //causes loading of the file

        assertFalse(token.isPresent());
    }

    @Test
    public void loadsCustomPropertiesFile() {
        Optional<String> token = new SDKConfigurationPropertiesReader("UFSdkConfiguration.properties")
                .readAccessToken();

        assertEquals("test-token-props", token.get());
    }

    @Test
    public void returnsEmptyPropertyWhenPropertiesFileDoesNotExist() {
        Optional<String> token = new SDKConfigurationPropertiesReader("non-existing-file.properties")
                .readAccessToken(); //causes loading of the file

        assertFalse(token.isPresent());
    }

    @Test
    public void readConcurrentListenerEnabledFromYaml() {
        Optional<Boolean> token = defaultYaml().readConcurrentListenerEnabled();

        assertEquals(true, token.get());
    }

    @Test
    public void readConcurrentListenerEnabledFromProperties() {
        Optional<Boolean> token = defaultProperties().readConcurrentListenerEnabled();

        assertEquals(true, token.get());
    }

    @Test
    public void readConcurrentListenerThreadsFromYaml() {
        Optional<Integer> token = defaultYaml().readConcurrentListenerThreads();

        assertEquals(25, token.get().intValue());
    }

    @Test
    public void readConcurrentListenerThreadsFromProperties() {
        Optional<Integer> token = defaultProperties().readConcurrentListenerThreads();

        assertEquals(25, token.get().intValue());
    }

    @Test
    public void readConcurrentListenerQueueSizeFromYaml() {
        Optional<Integer> token = defaultYaml().readConcurrentListenerQueueSize();

        assertEquals(1000, token.get().intValue());
    }

    @Test
    public void readConcurrentListenerQueueSizeFromProperties() {
        Optional<Integer> token = defaultProperties().readConcurrentListenerQueueSize();

        assertEquals(1000, token.get().intValue());
    }

    @Test
    public void readConcurrentListenerHandleErrorsAsynchronouslyFromYaml() {
        Optional<Boolean> token = defaultYaml().readConcurrentListenerHandleErrorsAsynchronously();

        assertEquals(false, token.get());
    }

    @Test
    public void readConcurrentListenerHandleErrorsAsynchronouslyFromProperties() {
        Optional<Boolean> token = defaultProperties().readConcurrentListenerHandleErrorsAsynchronously();

        assertEquals(false, token.get());
    }

    @Test
    public void readApiPortFromProperties() {
        Optional<Integer> port = defaultProperties().readApiPort();

        assertEquals(80, (int) port.get());
    }

    private SDKConfigurationPropertiesReader defaultProperties() {
        return new SDKConfigurationPropertiesReader();
    }

    private SDKConfigurationYamlReader defaultYaml() {
        return yaml("application.yml");
    }

    private SDKConfigurationYamlReader yaml(String yamlFilename) {
        return new SDKConfigurationYamlReader("application.yml");
    }
}
