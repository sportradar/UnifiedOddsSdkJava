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
public class ConfigureAccessTokenTest {

    private final boolean replayMode = true;
    private final String accessToken = "someAccessToken";
    private final Locale anyLanguage = Locale.FRENCH;
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
        propsFileContent.put("uf.sdk.accessToken", accessToken);
        propsFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
        propsFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromSdkProperties();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(accessToken, config.getAccessToken());
        assertEquals(accessToken, internalConfig.getAccessToken());
        assertEquals(accessToken, internalConfigForReplay.getAccessToken());
    }

    @ParameterizedTest
    @MethodSource("allEnvironments")
    public void configureViaYmlFileIn(Environment environment) {
        yamlFileContent.put("uf.sdk.accessToken", accessToken);
        yamlFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
        yamlFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromApplicationYml();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(accessToken, config.getAccessToken());
        assertEquals(accessToken, internalConfig.getAccessToken());
        assertEquals(accessToken, internalConfigForReplay.getAccessToken());
    }

    @Test
    public void configureViaJavaApi() {
        UofConfiguration config = tokenSetter
            .setAccessToken(accessToken)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(accessToken, config.getAccessToken());
        assertEquals(accessToken, internalConfig.getAccessToken());
        assertEquals(accessToken, internalConfigForReplay.getAccessToken());
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
