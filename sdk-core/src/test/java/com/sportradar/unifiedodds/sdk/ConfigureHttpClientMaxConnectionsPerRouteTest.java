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
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ConfigureHttpClientMaxConnectionsPerRouteTest {

    private final boolean replayMode = true;
    private final int amount = 17;
    private final Locale anyLanguage = Locale.FRENCH;
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
    private final TokenSetter buildFromPropsFile = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @Test
    public void prepareConfigurationForNormalHttpClientViaJavaApi() {
        UofConfiguration config = buildFromPropsFile
            .setAccessToken("any")
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setHttpClientMaxConnPerRoute(amount)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(amount, config.getApi().getHttpClientMaxConnPerRoute());
        assertEquals(amount, internalConfig.getHttpClientMaxConnPerRoute());
        assertEquals(amount, internalConfigForReplay.getHttpClientMaxConnPerRoute());
    }

    @Test
    public void prepareConfigurationForRecoveryHttpClientViaJavaApi() {
        UofConfiguration config = buildFromPropsFile
            .setAccessToken("any")
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setHttpClientMaxConnPerRoute(amount)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(amount, config.getApi().getHttpClientMaxConnPerRoute());
        assertEquals(amount, internalConfig.getRecoveryHttpClientMaxConnPerRoute());
        assertEquals(amount, internalConfigForReplay.getRecoveryHttpClientMaxConnPerRoute());
    }

    private static SdkConfigurationYamlReader anyYaml() {
        return mock(SdkConfigurationYamlReader.class);
    }

    private static SdkConfigurationPropertiesReader anyProps() {
        return mock(SdkConfigurationPropertiesReader.class);
    }
}
