/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.utils.domain.names.Languages;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ConfigureIgnoreBetPalTimelineSportEventStatusTest {

    @Test
    public void withoutConfiguringDefaultsToRespecting() {
        UofConfiguration config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getCache().getIgnoreBetPalTimelineSportEventStatus()).isEqualTo(false);
    }

    @Test
    public void configureViaJavaApiToIgnore() {
        boolean ignore = true;
        try (
            MockedStatic<RuntimeConfiguration> operationManager = Mockito.mockStatic(
                RuntimeConfiguration.class
            )
        ) {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken("any")
                .selectEnvironment(Environment.Integration)
                .setDefaultLanguage(Languages.any())
                .setIgnoreBetPalTimelineSportEventStatus(ignore)
                .build();

            operationManager.verify(() -> RuntimeConfiguration.setIgnoreBetPalTimelineSportEventStatus(ignore)
            );
            assertThat(config.getCache().getIgnoreBetPalTimelineSportEventStatus()).isEqualTo(ignore);
        }
    }

    @Test
    public void configureViaJavaApiToRespect() {
        try (
            MockedStatic<RuntimeConfiguration> operationManager = Mockito.mockStatic(
                RuntimeConfiguration.class
            )
        ) {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken("any")
                .selectEnvironment(Environment.Integration)
                .setDefaultLanguage(Languages.any())
                .setIgnoreBetPalTimelineSportEventStatus(false)
                .build();

            operationManager.verify(() -> RuntimeConfiguration.setIgnoreBetPalTimelineSportEventStatus(false)
            );
            assertThat(config.getCache().getIgnoreBetPalTimelineSportEventStatus()).isFalse();
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
}
