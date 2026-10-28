/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.uf.sportsapi.datamodel.ResponseCode.OK;
import static com.sportradar.unifiedodds.sdk.BookmakerDetailsDataProviders.failingWith;
import static com.sportradar.unifiedodds.sdk.BookmakerDetailsDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.ConfigurationUnitBuilders.stubbingOutDataProviders;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.*;
import static com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager.getApiHost;
import static com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager.getMqHost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import java.util.Locale;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassFanOutComplexity")
class ConfigureBookmakerDetailsTest {

    private static final String BOOKMAKER_DOWN = "bookmaker endpoint down";
    private final String anyToken = "anyToken";
    private final Locale language = Locale.FRENCH;

    @Test
    void retrievesBookmakerIdWhenProviderSucceeds() throws DataProviderException {
        BookmakerDetails bookmakerDetails = new BookmakerDetails();
        bookmakerDetails.setBookmakerId(3);
        bookmakerDetails.setResponseCode(OK);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withConfiguredBookmakerProvider(providing(bookmakerDetails))
            .withOneProducer()
            .buildTokenSetter();
        UofConfiguration config = tokenSetter
            .setAccessToken(anyToken)
            .selectEnvironment(ReplayWithIntegrationCredentials)
            .setDefaultLanguage(language)
            .build();

        assertThat(config.getBookmakerDetails().getBookmakerId()).isEqualTo(3);
    }

    @Test
    void configurationIsNotConstructedWhenBookmakerDetailsFails() throws DataProviderException {
        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withConfiguredBookmakerProvider(failingWith(new DataProviderException(BOOKMAKER_DOWN)))
            .withOneProducer()
            .buildTokenSetter();

        assertThatThrownBy(() ->
                tokenSetter
                    .setAccessToken(anyToken)
                    .selectEnvironment(ReplayWithIntegrationCredentials)
                    .setDefaultLanguage(language)
                    .build()
            )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("failed to fetch required bookmaker details");
    }

    @Nested
    class ReplayWithProductionCredentialsApiHost {

        @Test
        void apiHostIsProductionForReplayWithProductionCredentials() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withConfiguredBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectEnvironment(ReplayWithProductionCredentials)
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Production));
        }

        @Test
        void rabbitHostIsReplayForReplayWithProductionCredentials() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withConfiguredBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectEnvironment(ReplayWithProductionCredentials)
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(ReplayWithProductionCredentials));
        }
    }

    @Nested
    class ReplayWithIntegrationCredentialsApiHost {

        @Test
        void apiHostIsIntegrationForReplayWithIntegrationCredentials() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withConfiguredBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectEnvironment(ReplayWithIntegrationCredentials)
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }

        @Test
        void rabbitHostIsReplayForReplayWithIntegrationCredentials() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withConfiguredBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectEnvironment(ReplayWithIntegrationCredentials)
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(ReplayWithIntegrationCredentials));
        }
    }
}
