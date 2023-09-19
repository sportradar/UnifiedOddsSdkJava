/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.Environments;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.testutil.javautil.Languages;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ConfigureCacheTimeouts {

    public static final String ANY = "any";

    private ConfigureCacheTimeouts() {}

    private static TokenSetter buildViaJavaApi() {
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

    public static class ProfileCache {

        private static final int DEFAULT_PROFILE_CACHE_TIMEOUT = 24;

        @Test
        public void allowsToConfigureTimeout() {
            final int timeoutToConfigure = 5;
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .setProfileCacheTimeout(timeoutToConfigure)
                .build();

            assertThat(config.getCache().getProfileCacheTimeout()).isEqualTo(ofHours(timeoutToConfigure));
        }

        @Test
        public void defaultsTo24Hours() {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .build();

            assertThat(config.getCache().getProfileCacheTimeout())
                .isEqualTo(ofHours(DEFAULT_PROFILE_CACHE_TIMEOUT));
        }
    }

    public static class SportEventCache {

        private static final int DEFAULT_SPORT_CACHE_TIMEOUT = 12;

        @Test
        public void allowsToConfigureTimeout() {
            final int timeoutToConfigure = 6;
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .setSportEventCacheTimeout(timeoutToConfigure)
                .build();

            assertThat(config.getCache().getSportEventCacheTimeout()).isEqualTo(ofHours(timeoutToConfigure));
        }

        @Test
        public void defaultTo12Hours() {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .build();

            assertThat(config.getCache().getSportEventCacheTimeout())
                .isEqualTo(ofHours(DEFAULT_SPORT_CACHE_TIMEOUT));
        }
    }

    public static class SportEventStatusCache {

        private static final int DEFAULT_SPORT_STATUS_CACHE_TIMEOUT = 5;

        @Test
        public void allowsToConfigureTimeout() {
            final int timeoutToConfigure = 7;
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .setSportEventStatusCacheTimeout(timeoutToConfigure)
                .build();

            assertThat(config.getCache().getSportEventStatusCacheTimeout())
                .isEqualTo(ofMinutes(timeoutToConfigure));
        }

        @Test
        public void defaultTo5Minutes() {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .build();

            assertThat(config.getCache().getSportEventStatusCacheTimeout())
                .isEqualTo(ofMinutes(DEFAULT_SPORT_STATUS_CACHE_TIMEOUT));
        }
    }

    public static class VariantMarketDescriptionTimeout {

        private static final int DEFAULT_VARIANT_MARKET_DESCRIPTION_TIMEOUT = 3;

        @Test
        public void allowsToConfigureTimeout() {
            final int timeoutToConfigure = 8;
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .setVariantMarketDescriptionCacheTimeout(timeoutToConfigure)
                .build();

            assertThat(config.getCache().getVariantMarketDescriptionCacheTimeout())
                .isEqualTo(ofHours(timeoutToConfigure));
        }

        @Test
        public void defaultTo5Minutes() {
            UofConfiguration config = buildViaJavaApi()
                .setAccessToken(ANY)
                .selectEnvironment(Environments.any())
                .setDefaultLanguage(Languages.any())
                .build();

            assertThat(config.getCache().getVariantMarketDescriptionCacheTimeout())
                .isEqualTo(ofHours(DEFAULT_VARIANT_MARKET_DESCRIPTION_TIMEOUT));
        }
    }
}
