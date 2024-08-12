/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigureDefaultLocaleTest {

    private final boolean replayMode = true;
    private final String anyToken = "anyToken";
    private final Locale language = Locale.FRENCH;
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
    private final TokenSetter tokenSetter = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @ParameterizedTest
    @MethodSource("allEnvironments")
    public void configureViaPropertiesFileIn(Environment environment) {
        propsFileContent.put("uf.sdk.accessToken", anyToken);
        propsFileContent.put("uf.sdk.defaultLanguage", language.toString());
        propsFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromSdkProperties();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(language, config.getDefaultLanguage());
        assertEquals(language, internalConfig.getDefaultLocale());
        assertEquals(language, internalConfigForReplay.getDefaultLocale());
    }

    @ParameterizedTest
    @MethodSource("allEnvironments")
    public void configureViaYmlFileIn(Environment environment) {
        yamlFileContent.put("uf.sdk.accessToken", anyToken);
        yamlFileContent.put("uf.sdk.defaultLanguage", language.toString());
        yamlFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromApplicationYml();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(language, config.getDefaultLanguage());
        assertEquals(language, internalConfig.getDefaultLocale());
        assertEquals(language, internalConfigForReplay.getDefaultLocale());
    }

    @Test
    public void configureViaJavaApi() {
        UofConfiguration config = tokenSetter
            .setAccessToken(anyToken)
            .selectCustom()
            .setDefaultLanguage(language)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(language, config.getDefaultLanguage());
        assertEquals(language, internalConfig.getDefaultLocale());
        assertEquals(language, internalConfigForReplay.getDefaultLocale());
    }

    public static Object[][] allEnvironments() {
        return Stream.of(Environment.values()).map(e -> new Object[] { e }).toArray(Object[][]::new);
    }

    private static SdkConfigurationYamlReader anyYaml() {
        return mock(SdkConfigurationYamlReader.class);
    }

    private static SdkConfigurationPropertiesReader anyProps() {
        return mock(SdkConfigurationPropertiesReader.class);
    }
}
