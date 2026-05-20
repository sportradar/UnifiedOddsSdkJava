/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.uf.sportsapi.datamodel.ResponseCode.FORBIDDEN;
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
public class ConfigureBookmakerDetailsTest {

    private static final String PRODUCTION_DOWN = "production down";
    private static final String INTEGRATION_DOWN = "integration down";
    private final String anyToken = "anyToken";
    private final Locale language = Locale.FRENCH;

    @Test
    public void retrievesProductionBookmakerId() throws DataProviderException {
        BookmakerDetails bookmakerDetails = new BookmakerDetails();
        bookmakerDetails.setBookmakerId(3);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(providing(bookmakerDetails))
            .withOneProducer()
            .buildTokenSetter();
        UofConfiguration config = tokenSetter
            .setAccessToken(anyToken)
            .selectReplay()
            .setDefaultLanguage(language)
            .build();

        assertThat(config.getBookmakerDetails().getBookmakerId()).isEqualTo(3);
    }

    @Test
    public void retrievesIntegrationBookmakerIdWhenProductionFails() throws DataProviderException {
        BookmakerDetails integrationDetails = new BookmakerDetails();
        integrationDetails.setBookmakerId(7);
        integrationDetails.setResponseCode(OK);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
            .withIntegrationBookmakerProvider(providing(integrationDetails))
            .withOneProducer()
            .buildTokenSetter();
        UofConfiguration config = tokenSetter
            .setAccessToken(anyToken)
            .selectReplay()
            .setDefaultLanguage(language)
            .build();

        assertThat(config.getBookmakerDetails().getBookmakerId()).isEqualTo(7);
    }

    @Test
    public void failsOverToIntegrationWhenProductionBookmakerDetailsReturnForbidden()
        throws DataProviderException {
        BookmakerDetails productionForbidden = new BookmakerDetails();
        productionForbidden.setResponseCode(FORBIDDEN);

        BookmakerDetails integrationDetails = new BookmakerDetails();
        integrationDetails.setBookmakerId(9);
        integrationDetails.setResponseCode(OK);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(providing(productionForbidden))
            .withIntegrationBookmakerProvider(providing(integrationDetails))
            .withOneProducer()
            .buildTokenSetter();
        UofConfiguration config = tokenSetter
            .setAccessToken(anyToken)
            .selectReplay()
            .setDefaultLanguage(language)
            .build();

        assertThat(config.getBookmakerDetails().getBookmakerId()).isEqualTo(9);
    }

    @Test
    public void configurationIsNotConstructedWhenBothProductionAndIntegrationBookmakerDetailsFailToBeFetched()
        throws DataProviderException {
        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
            .withIntegrationBookmakerProvider(failingWith(new DataProviderException(INTEGRATION_DOWN)))
            .withOneProducer()
            .buildTokenSetter();

        assertThatThrownBy(() ->
                tokenSetter.setAccessToken(anyToken).selectReplay().setDefaultLanguage(language).build()
            )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("failed to fetch required bookmaker details");
    }

    @Test
    public void bookmakerDetailsHasForbiddenStatusWhenProductionFailsAndIntegrationBookmakerDetailsReturnsForbidden()
        throws DataProviderException {
        BookmakerDetails integrationForbidden = new BookmakerDetails();
        integrationForbidden.setResponseCode(FORBIDDEN);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
            .withIntegrationBookmakerProvider(providing(integrationForbidden))
            .withOneProducer()
            .buildTokenSetter();

        assertThat(
            tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build()
                .getBookmakerDetails()
                .getResponseCode()
        )
            .isEqualTo(FORBIDDEN);
    }

    @Test
    public void bookmakerDetailsHasForbiddenStatusWhenProductionReturnsForbiddenAndIntegrationFails()
        throws DataProviderException {
        BookmakerDetails productionForbidden = new BookmakerDetails();
        productionForbidden.setResponseCode(FORBIDDEN);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(providing(productionForbidden))
            .withIntegrationBookmakerProvider(failingWith(new DataProviderException(INTEGRATION_DOWN)))
            .withOneProducer()
            .buildTokenSetter();

        assertThat(
            tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build()
                .getBookmakerDetails()
                .getResponseCode()
        )
            .isEqualTo(FORBIDDEN);
    }

    @Test
    public void bookmakerDetailsHasForbiddenStatusWhenProductionAndIntegrationReturnForbidden()
        throws DataProviderException {
        BookmakerDetails productionForbidden = new BookmakerDetails();
        productionForbidden.setResponseCode(FORBIDDEN);
        BookmakerDetails integrationForbidden = new BookmakerDetails();
        integrationForbidden.setResponseCode(FORBIDDEN);

        TokenSetter tokenSetter = stubbingOutDataProviders()
            .withProductionBookmakerProvider(providing(productionForbidden))
            .withIntegrationBookmakerProvider(providing(integrationForbidden))
            .withOneProducer()
            .buildTokenSetter();

        assertThat(
            tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build()
                .getBookmakerDetails()
                .getResponseCode()
        )
            .isEqualTo(FORBIDDEN);
    }

    @Nested
    class ReplayApiHost {

        @Test
        public void apiHostIsProductionWhenProductionBookmakerDetailsSucceeds() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Production));
        }

        @Test
        public void apiHostIsIntegrationWhenProductionFailsAndIntegrationSucceeds()
            throws DataProviderException {
            BookmakerDetails integrationDetails = new BookmakerDetails();
            integrationDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
                .withIntegrationBookmakerProvider(providing(integrationDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }

        @Test
        public void apiHostIsIntegrationWhenProductionReturnsForbiddenAndIntegrationSucceeds()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);

            BookmakerDetails integrationDetails = new BookmakerDetails();
            integrationDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(providing(integrationDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }

        @Test
        public void apiHostIsIntegrationWhenProductionFailsAndIntegrationReturnsForbidden()
            throws DataProviderException {
            BookmakerDetails integrationForbidden = new BookmakerDetails();
            integrationForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
                .withIntegrationBookmakerProvider(providing(integrationForbidden))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }

        @Test
        public void apiHostIsIntegrationWhenProductionReturnsForbiddenAndIntegrationFails()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(failingWith(new DataProviderException(INTEGRATION_DOWN)))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }

        @Test
        public void apiHostIsIntegrationWhenBothProductionAndIntegrationReturnForbidden()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);
            BookmakerDetails integrationForbidden = new BookmakerDetails();
            integrationForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(providing(integrationForbidden))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getApi().getHost()).isEqualTo(getApiHost(Integration));
        }
    }

    @Nested
    class ReplayRabbitHost {

        @Test
        public void rabbitHostIsReplayWhenProductionBookmakerDetailsSucceeds() throws DataProviderException {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(bookmakerDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }

        @Test
        public void rabbitHostIsReplayWhenProductionFailsAndIntegrationSucceeds()
            throws DataProviderException {
            BookmakerDetails integrationDetails = new BookmakerDetails();
            integrationDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
                .withIntegrationBookmakerProvider(providing(integrationDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }

        @Test
        public void rabbitHostIsReplayWhenProductionReturnsForbiddenAndIntegrationSucceeds()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);

            BookmakerDetails integrationDetails = new BookmakerDetails();
            integrationDetails.setResponseCode(OK);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(providing(integrationDetails))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }

        @Test
        public void rabbitHostIsReplayWhenProductionFailsAndIntegrationReturnsForbidden()
            throws DataProviderException {
            BookmakerDetails integrationForbidden = new BookmakerDetails();
            integrationForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(failingWith(new DataProviderException(PRODUCTION_DOWN)))
                .withIntegrationBookmakerProvider(providing(integrationForbidden))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }

        @Test
        public void rabbitHostIsReplayWhenProductionReturnsForbiddenAndIntegrationFails()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(failingWith(new DataProviderException(INTEGRATION_DOWN)))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }

        @Test
        public void rabbitHostIsReplayWhenBothProductionAndIntegrationReturnForbidden()
            throws DataProviderException {
            BookmakerDetails productionForbidden = new BookmakerDetails();
            productionForbidden.setResponseCode(FORBIDDEN);
            BookmakerDetails integrationForbidden = new BookmakerDetails();
            integrationForbidden.setResponseCode(FORBIDDEN);

            TokenSetter tokenSetter = stubbingOutDataProviders()
                .withProductionBookmakerProvider(providing(productionForbidden))
                .withIntegrationBookmakerProvider(providing(integrationForbidden))
                .withOneProducer()
                .buildTokenSetter();
            UofConfiguration config = tokenSetter
                .setAccessToken(anyToken)
                .selectReplay()
                .setDefaultLanguage(language)
                .build();

            assertThat(config.getRabbit().getHost()).isEqualTo(getMqHost(Replay));
        }
    }
}
