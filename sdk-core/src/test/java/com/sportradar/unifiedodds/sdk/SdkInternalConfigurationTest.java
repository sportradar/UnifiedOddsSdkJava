/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
public class SdkInternalConfigurationTest {

    public static final Duration ANY_DURATION = Duration.ofHours(4);
    public static final Locale ANY_LANGUAGE = Locale.CANADA_FRENCH;
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

    private final SdkConfigurationPropertiesReader propertiesReader = mock(
        SdkConfigurationPropertiesReader.class
    );

    private final SdkConfigurationYamlReader ymlReader = mock(SdkConfigurationYamlReader.class);
    private final UofConfigurationStub config = new UofConfigurationStub();

    @Test
    public void shouldNotInstantiateWithNullConfiguration() {
        assertThatThrownBy(() -> new SdkInternalConfiguration(null, propertiesReader, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");

        assertThatThrownBy(() -> new SdkInternalConfiguration(null, false, propertiesReader, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");
    }

    @Test
    public void shouldNotInstantiateWithNullPropertiesReader() {
        assertThatThrownBy(() -> new SdkInternalConfiguration(config, null, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationPropertiesReader");

        assertThatThrownBy(() -> new SdkInternalConfiguration(config, false, null, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationPropertiesReader");
    }

    @Test
    public void shouldNotInstantiateWithNullYmlReader() {
        assertThatThrownBy(() -> new SdkInternalConfiguration(config, propertiesReader, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationYamlReader");

        assertThatThrownBy(() -> new SdkInternalConfiguration(config, false, propertiesReader, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationYamlReader");
    }

    @ParameterizedTest
    @MethodSource("everyEnvironment")
    public void shouldPreserveMessagingHostInWhenInEnvironmentOf(final Environment environment) {
        final String host = "rabbit.com";

        val internalConfig = new SdkInternalConfiguration(
            buildFromPropsFile
                .setAccessToken("any")
                .selectCustom()
                .setDefaultLanguage(ANY_LANGUAGE)
                .setMessagingHost(host)
                .build(),
            propertiesReader,
            ymlReader
        );

        assertEquals(host, internalConfig.getMessagingHost());
    }

    @Test
    public void shouldPreserveMessagingHostIfReplaySession() {
        val host = "rabbit.com";
        config.setRabbit(new UofRabbitConfigurationStub().setHost(host));
        config.setEnvironment(Environment.Replay);
        ((UofProducerConfigurationStub) config.getProducer()).setInactivitySeconds(ANY_DURATION);
        ((UofProducerConfigurationStub) config.getProducer()).setMaxRecoveryTime(ANY_DURATION);
        ((UofProducerConfigurationStub) config.getProducer()).setMinIntervalBetweenRecoveryRequests(
                ANY_DURATION
            );
        ((UofApiConfigurationStub) config.getApi()).setHttpClientTimeout(ANY_DURATION);
        ((UofApiConfigurationStub) config.getApi()).setHttpClientRecoveryTimeout(ANY_DURATION);
        val replaySession = true;

        val internalConfig = new SdkInternalConfiguration(config, replaySession, propertiesReader, ymlReader);

        assertEquals(host, internalConfig.getMessagingHost());
    }

    private static Object[] everyEnvironment() {
        return Environment.values();
    }
}
