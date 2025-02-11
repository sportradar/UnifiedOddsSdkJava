/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.utils.domain.names.Languages;
import java.util.Collections;
import lombok.val;
import org.junit.jupiter.api.Test;

class ConfigureUsageIsExportEnabledTest {

    @Test
    void withoutConfiguringDefaultsToTrue() {
        UofConfiguration config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .build();

        assertThat(config.getUsage().isExportEnabled()).isTrue();
    }

    @Test
    void configureViaJavaApiToTrue() {
        val config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .enableUsageExport(true)
            .build();

        assertThat(config.getUsage().isExportEnabled()).isTrue();
    }

    @Test
    void configureViaJavaApiToFalse() {
        val config = buildViaJavaApi()
            .setAccessToken("any")
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(Languages.any())
            .enableUsageExport(false)
            .build();

        assertThat(config.getUsage().isExportEnabled()).isFalse();
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
