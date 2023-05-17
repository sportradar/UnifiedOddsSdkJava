package com.sportradar.unifiedodds.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Optional;
import org.junit.Test;

@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength", "MagicNumber" })
public class SDKConfigurationReaderTest {

    //Default config files:
    @Test
    public void loadsDefaultYamlFile() {
        Optional<String> token = new SDKConfigurationYamlReader().readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void loadsDefaultPropertiesFile() {
        Optional<String> token = new SDKConfigurationPropertiesReader().readAccessToken();

        assertEquals("test-token-props", token.get());
    }

    //Custom config files:
    @Test
    public void loadsCustomYamlFile() {
        Optional<String> token = new SDKConfigurationYamlReader("application.yml").readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void returnsEmptyPropertyWhenYamlFileDoesNotExist() {
        Optional<String> token = new SDKConfigurationYamlReader("non-existing-file.yml").readAccessToken(); //causes loading of the file

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
