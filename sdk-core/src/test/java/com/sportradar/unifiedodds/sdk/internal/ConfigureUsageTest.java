/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.utils.domain.names.Languages;
import java.util.Collections;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ConfigureUsageTest {

    @Test
    void alwaysReturnDefaultExportInterval() {
        final int defaultExportInterval = 300;

        UofConfiguration config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getUsage().getExportIntervalInSec()).isEqualTo(defaultExportInterval);
    }

    @Test
    void alwaysReturnDefaultExportTimeout() {
        final int defaultExportTimeout = 20;

        UofConfiguration config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getUsage().getExportTimeoutInSec()).isEqualTo(defaultExportTimeout);
    }

    @ParameterizedTest
    @EnumSource(Environment.class)
    void configureViaJavaApiToForEnvironmentReturnExpectedUsageHost(Environment environment) {
        val config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(environment)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getUsage().getHost()).isEqualTo(EnvironmentManager.getUsageHost(environment));
    }

    private TokenSetter buildViaJavaApi() {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(Collections.emptyMap()),
            new StubSdkConfigurationYamlReader(Collections.emptyMap()),
            anyConfig -> emptyBookmakerDetailsReader(),
            anyConfig -> providerOfSingleEmptyProducer()
        );
    }
}
