/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.utils.domain.names.Languages;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ConfigureRabbitConnectionTimeoutTest {

    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 60;

    @Test
    public void withoutConfiguringDefaultsTo60seconds() {
        UofConfiguration config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getRabbit().getConnectionTimeout())
            .isEqualTo(asDuration(DEFAULT_TIMEOUT_IN_SECONDS));
    }

    @Test
    public void configureViaJavaApi() {
        final int timeoutSeconds = 15;
        try (
            MockedStatic<RuntimeConfiguration> operationManager = Mockito.mockStatic(
                RuntimeConfiguration.class
            )
        ) {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken("any")
                .selectEnvironment(Environment.Integration)
                .setDefaultLanguage(Languages.any())
                .setRabbitConnectionTimeout(timeoutSeconds)
                .build();

            operationManager.verify(() -> RuntimeConfiguration.setRabbitConnectionTimeout(timeoutSeconds));
            assertThat(config.getRabbit().getConnectionTimeout()).isEqualTo(asDuration(timeoutSeconds));
        }
    }

    @Test
    public void notConfigureViaJavaApiIfTimeoutIfTooLow() {
        int tooLowTimeout = 1;
        try (
            MockedStatic<RuntimeConfiguration> operationManager = Mockito.mockStatic(
                RuntimeConfiguration.class
            )
        ) {
            assertThatThrownBy(() ->
                buildViaJavaApi()
                    .setAccessToken("any")
                    .selectEnvironment(Environment.Integration)
                    .setDefaultLanguage(Languages.any())
                    .setRabbitConnectionTimeout(tooLowTimeout)
                    .build()
            );

            operationManager.verify(
                () -> RuntimeConfiguration.setRabbitConnectionTimeout(tooLowTimeout),
                never()
            );
        }
    }

    private TokenSetter buildViaJavaApi() {
        final Map<String, String> anyYamlFileContent = mock(Map.class);
        final Map<String, String> anyPropertiesFileContent = mock(Map.class);
        final TokenSetter buildFromPropsFile = new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(anyPropertiesFileContent),
            new StubSdkConfigurationYamlReader(anyYamlFileContent),
            anyConfig -> emptyBookmakerDetailsReader(),
            anyConfig -> providerOfSingleEmptyProducer()
        );
        return buildFromPropsFile;
    }

    private static Duration asDuration(int timeoutSeconds) {
        return Duration.of(timeoutSeconds, ChronoUnit.SECONDS);
    }
}
