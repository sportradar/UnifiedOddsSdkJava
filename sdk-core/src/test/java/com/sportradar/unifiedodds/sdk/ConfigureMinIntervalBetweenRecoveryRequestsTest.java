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

public class ConfigureMinIntervalBetweenRecoveryRequestsTest {

    private final boolean replayMode = true;
    private final int durationInSeconds = 27;
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
    public void configureViaJavaApi() {
        UofConfiguration config = buildFromPropsFile
            .setAccessToken("any")
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setMinIntervalBetweenRecoveryRequests(durationInSeconds)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigReplayMode = new SdkInternalConfiguration(
            config,
            replayMode,
            anyProps(),
            anyYaml()
        );

        assertEquals(
            asDuration(durationInSeconds),
            config.getProducer().getMinIntervalBetweenRecoveryRequests()
        );
        assertEquals(durationInSeconds, internalConfig.getMinIntervalBetweenRecoveryRequests());
        assertEquals(durationInSeconds, internalConfigReplayMode.getMinIntervalBetweenRecoveryRequests());
    }

    private Duration asDuration(int amountOfSeconds) {
        return Duration.of(amountOfSeconds, ChronoUnit.SECONDS);
    }

    private static SdkConfigurationYamlReader anyYaml() {
        return mock(SdkConfigurationYamlReader.class);
    }

    private static SdkConfigurationPropertiesReader anyProps() {
        return mock(SdkConfigurationPropertiesReader.class);
    }
}
