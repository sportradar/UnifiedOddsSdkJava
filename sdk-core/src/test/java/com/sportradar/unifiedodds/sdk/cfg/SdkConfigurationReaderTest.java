package com.sportradar.unifiedodds.sdk.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({ "LineLength", "MagicNumber" })
public class SdkConfigurationReaderTest {

    //Default config files:
    @Test
    public void loadsDefaultYamlFile() {
        Optional<String> token = new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader()
        )
            .readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void loadsDefaultPropertiesFile() {
        Optional<String> token = SdkConfigurationPropertiesReaderFactory.create().readAccessToken();

        assertEquals("test-token-props", token.get());
    }

    //Custom config files:
    @Test
    public void loadsCustomYamlFile() {
        Optional<String> token = new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader(),
            "application.yml"
        )
            .readAccessToken();

        assertEquals("test-token-yaml", token.get());
    }

    @Test
    public void returnsEmptyPropertyWhenYamlFileDoesNotExist() {
        Optional<String> token = new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader(),
            "non-existing-file.yml"
        )
            .readAccessToken(); //causes loading of the file

        assertFalse(token.isPresent());
    }

    @Test
    public void loadsCustomPropertiesFile() {
        Optional<String> token = SdkConfigurationPropertiesReaderFactory
            .create("UFSdkConfiguration.properties")
            .readAccessToken();

        assertEquals("test-token-props", token.get());
    }

    @Test
    public void returnsEmptyPropertyWhenPropertiesFileDoesNotExist() {
        Optional<String> token = SdkConfigurationPropertiesReaderFactory
            .create("non-existing-file.properties")
            .readAccessToken(); //causes loading of the file

        assertFalse(token.isPresent());
    }

    @Test
    public void readApiPortFromProperties() {
        Optional<Integer> port = defaultProperties().readApiPort();

        assertEquals(80, (int) port.get());
    }

    private SdkConfigurationPropertiesReader defaultProperties() {
        return SdkConfigurationPropertiesReaderFactory.create();
    }

    private SdkConfigurationYamlReader defaultYaml() {
        return yaml("application.yml");
    }

    private SdkConfigurationYamlReader yaml(String yamlFilename) {
        return new SdkConfigurationYamlReader(
            new ClassResolver(),
            new Yaml(),
            new ResourceReader(),
            "application.yml"
        );
    }
}
