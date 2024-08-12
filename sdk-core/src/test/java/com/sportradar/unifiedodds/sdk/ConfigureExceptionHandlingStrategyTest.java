/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigureExceptionHandlingStrategyTest {

    private final boolean replayMode = true;
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

    @ParameterizedTest
    @MethodSource("allStrategies")
    public void configureViaJavaApi(ExceptionHandlingStrategy strategy) {
        UofConfiguration config = buildFromPropsFile
            .setAccessToken("any")
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setExceptionHandlingStrategy(strategy)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(strategy, config.getExceptionHandlingStrategy());
        assertEquals(strategy, internalConfig.getExceptionHandlingStrategy());
        assertEquals(strategy, internalConfigForReplay.getExceptionHandlingStrategy());
    }

    private static Object[] allStrategies() {
        return new Object[] { ExceptionHandlingStrategy.Throw, ExceptionHandlingStrategy.Catch };
    }

    private static SdkConfigurationYamlReader anyYaml() {
        return mock(SdkConfigurationYamlReader.class);
    }

    private static SdkConfigurationPropertiesReader anyProps() {
        return mock(SdkConfigurationPropertiesReader.class);
    }
}
