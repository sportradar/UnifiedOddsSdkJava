/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.internal.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.domain.names.Languages;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BookmakerDetailsProviderFactoryTest {

    public static final int HTTP_PORT = 80;

    private BookmakerDetailsProviderFactoryTest() {}

    private static LogHttpDataFetcher anyFetcher() {
        return mock(LogHttpDataFetcher.class);
    }

    private static Deserializer anyDeserializer() {
        return mock(Deserializer.class);
    }

    private static SdkInternalConfiguration configWithPort(int port) {
        SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        when(config.getApiPort()).thenReturn(port);
        return config;
    }

    @Nested
    public class TargetingIntegration {

        private static final String INTEGRATION_HOST = "stgapi.betradar.com";

        @Test
        public void targetsBookmakerEndpointsAtIntegrationEnvironment() {
            final String url = getIntegrationBookmakerDetailsUrlWithPortSetTo(HTTP_PORT);

            assertThat(url).isEqualTo("https://" + INTEGRATION_HOST + "/v1/users/whoami.xml");
        }

        @Test
        public void urlDoesNotMentionPortIfItIsSetToDefaultHttpPort80() {
            final String url = getIntegrationBookmakerDetailsUrlWithPortSetTo(HTTP_PORT);

            assertThat(url).doesNotContain(HTTP_PORT + "");
        }

        @Test
        public void ifPortIsNotSetItIsNotMentionedInUrl() {
            int zeroIsUnsetIntInJava = 0;
            final String url = getIntegrationBookmakerDetailsUrlWithPortSetTo(zeroIsUnsetIntInJava);

            assertThat(url).doesNotContain(zeroIsUnsetIntInJava + "");
        }

        @Test
        public void urlUsesConfiguredPort() {
            final int configuredPort = 8998;
            final String url = getIntegrationBookmakerDetailsUrlWithPortSetTo(configuredPort);

            assertThat(url).contains(INTEGRATION_HOST + ":" + configuredPort);
        }

        private String getIntegrationBookmakerDetailsUrlWithPortSetTo(int zeroIsUnsetIntInJava) {
            BookmakerDetailsProviderFactory factory = new BookmakerDetailsProviderFactory(
                anyFetcher(),
                anyDeserializer(),
                configWithPort(zeroIsUnsetIntInJava)
            );

            val bookmakerProvider = factory.targetingIntegration();
            val url = bookmakerProvider.getFinalUrl(Languages.any(), new String[0]);
            return url;
        }
    }

    @Nested
    public class TargetingProduction {

        public static final String PRODUCTION_HOST = "api.betradar.com";

        @Test
        public void targetsBookmakerEndpointsAtIntegrationEnvironment() {
            final String url = getProductionBookmakerDetailsUrlWithPortSetTo(HTTP_PORT);

            assertThat(url).isEqualTo("https://" + PRODUCTION_HOST + "/v1/users/whoami.xml");
        }

        @Test
        public void urlDoesNotMentionPortIfItIsSetToDefaultHttpPort80() {
            final String url = getProductionBookmakerDetailsUrlWithPortSetTo(HTTP_PORT);

            assertThat(url).doesNotContain(HTTP_PORT + "");
        }

        @Test
        public void ifPortIsNotSetItIsNotMentionedInUrl() {
            int zeroIsUnsetIntInJava = 0;
            final String url = getProductionBookmakerDetailsUrlWithPortSetTo(zeroIsUnsetIntInJava);

            assertThat(url).doesNotContain(zeroIsUnsetIntInJava + "");
        }

        @Test
        public void urlUsesConfiguredPort() {
            final int configuredPort = 8998;
            final String url = getProductionBookmakerDetailsUrlWithPortSetTo(configuredPort);

            assertThat(url).contains(PRODUCTION_HOST + ":" + configuredPort);
        }

        private String getProductionBookmakerDetailsUrlWithPortSetTo(int zeroIsUnsetIntInJava) {
            BookmakerDetailsProviderFactory factory = new BookmakerDetailsProviderFactory(
                anyFetcher(),
                anyDeserializer(),
                configWithPort(zeroIsUnsetIntInJava)
            );

            val bookmakerProvider = factory.targetingProduction();
            val url = bookmakerProvider.getFinalUrl(Languages.any(), new String[0]);
            return url;
        }
    }
}
