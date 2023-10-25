/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.readerProvidingBookmaker;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class EnvironmentSelectorTests extends ConfigurationBuilderSetup {

    public static final Locale ANY_LANGUAGE = Locale.CANADA;
    public static final String ANY_TOKEN = "anyToken";
    private final UofConfigurationImpl configuration = new UofConfigurationImpl(
        anyConfig -> bookmakerDetailsProvider,
        anyConfig -> producerDataProvider
    );

    @Test(expected = NullPointerException.class)
    public void environmentSelectorConstruct_MissingConfiguration() {
        new EnvironmentSelectorImpl(
            null,
            basePropertiesReader,
            new StubSdkConfigurationYamlReader(baseSection)
        );
    }

    @Test(expected = NullPointerException.class)
    public void environmentSelectorConstruct_MissingPropertiesReader() {
        new EnvironmentSelectorImpl(configuration, null, new StubSdkConfigurationYamlReader(baseSection));
    }

    @Test(expected = NullPointerException.class)
    public void environmentSelectorConstruct_MissingYamlReader() {
        new EnvironmentSelectorImpl(configuration, basePropertiesReader, null);
    }

    @Test
    public void selectIntegrationEnvironmentReturnTest() {
        ConfigurationBuilder configurationBuilder = getEnvironmentSelector()
            .selectEnvironment(Environment.Integration);

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectProductionEnvironmentReturnTest() {
        ConfigurationBuilder configurationBuilder = getEnvironmentSelector()
            .selectEnvironment(Environment.Production);

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectReplayEnvironmentReturnTest() {
        ConfigurationBuilder configurationBuilder = getEnvironmentSelector().selectReplay();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectCustomEnvironmentReturnTest() {
        CustomConfigurationBuilder configurationBuilder = getEnvironmentSelector().selectCustom();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void integrationEnvironmentResultValidation() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectEnvironment(Environment.Integration)
            .setDefaultLanguage(languageDe)
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(Environment.Integration, cfg.getEnvironment());
        verifyConfig(cfg, Environment.Integration, languageDe);
    }

    @Test
    public void productionEnvironmentResultValidation() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectEnvironment(Environment.Production)
            .setDefaultLanguage(languageDe)
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(Environment.Production, cfg.getEnvironment());
        verifyConfig(cfg, Environment.Production, languageDe);
    }

    @Test
    public void replayEnvironmentResultValidation() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectReplay()
            .setDesiredLanguages(Collections.singletonList(languageDe))
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(Environment.Replay, cfg.getEnvironment());
        verifyConfig(cfg, Environment.Replay, languageDe);
    }

    @Test
    public void customEnvironmentDefaultResultValidation() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectCustom()
            .setDesiredLanguages(Collections.singletonList(languageNl))
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(Environment.Custom, cfg.getEnvironment());
        verifyConfig(cfg, Environment.Integration, languageNl);
    }

    @Test
    public void replayShouldTargetIntegrationApiBecauseReplayButWeNeedAnExplanationWhy() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectReplay()
            .setDefaultLanguage(defaultLanguage)
            .build();

        Assert.assertEquals(cfg.getApi().getHost(), EnvironmentManager.getApiHost(Environment.Integration));
    }

    @Test
    public void globalAndNonGlobalStgApisSitUnderSameIpsHoweverReplayShouldPointToNonGlobalAsItIsLongTermStrategy() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectReplay()
            .setDefaultLanguage(defaultLanguage)
            .build();

        Assert.assertEquals("stgapi.betradar.com", cfg.getApi().getHost());
    }

    @Test
    public void replayShouldPointToNonGlobalMessagingHostAsItIsLongTermStrategy() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectReplay()
            .setDefaultLanguage(defaultLanguage)
            .build();

        Assert.assertEquals("replaymq.betradar.com", cfg.getRabbit().getHost());
    }

    @Test
    public void replayConfigurationShouldBeCreatedForReplayEnvironment() {
        UofConfiguration cfg = getEnvironmentSelector()
            .selectReplay()
            .setDefaultLanguage(defaultLanguage)
            .build();

        Assert.assertEquals(Environment.Replay, cfg.getEnvironment());
    }

    @Test
    public void buildingNonCustomConfigurationThrowsIfFetchingBookmakerDetailsFails() {
        val configBuilder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> readerProvidingBookmaker(null),
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectEnvironment(Environment.Production)
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> configBuilder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing bookmaker details");
    }

    @Test
    public void buildingNonCustomConfigurationThrowsIfFetchingAvailableProducersFails() {
        ProducerDataProvider producerDataProvider = mock(ProducerDataProvider.class);
        when(producerDataProvider.getAvailableProducers()).thenReturn(null);

        ConfigurationBuilder builder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> bookmakerDetailsProvider,
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectEnvironment(Environment.Production)
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> builder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing available producers");
    }

    @Test
    public void buildingNonCustomConfigurationThrowsIfZeroProducersAreFetched() {
        ProducerDataProvider producerDataProvider = mock(ProducerDataProvider.class);
        when(producerDataProvider.getAvailableProducers()).thenReturn(new ArrayList<>());

        val configBuilder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> bookmakerDetailsProvider,
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectEnvironment(Environment.Production)
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> configBuilder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing available producers");
    }

    @Test
    public void buildingCustomConfigurationThrowsIfFetchingBookmakerDetailsFails() {
        val configBuilder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> readerProvidingBookmaker(null),
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectCustom()
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> configBuilder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing bookmaker details");
    }

    @Test
    public void buildingCustomConfigurationThrowsIfFetchingAvailableProducersFails() {
        ProducerDataProvider producerDataProvider = mock(ProducerDataProvider.class);
        when(producerDataProvider.getAvailableProducers()).thenReturn(null);

        val builder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> bookmakerDetailsProvider,
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectCustom()
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> builder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing available producers");
    }

    @Test
    public void buildingCustomConfigurationThrowsIfZeroProducersAreFetched() {
        ProducerDataProvider producerDataProvider = mock(ProducerDataProvider.class);
        when(producerDataProvider.getAvailableProducers()).thenReturn(new ArrayList<>());

        val configBuilder = new EnvironmentSelectorImpl(
            withAnyToken(
                new UofConfigurationImpl(
                    anyConfig -> bookmakerDetailsProvider,
                    anyConfig -> producerDataProvider
                )
            ),
            new StubSdkConfigurationPropertiesReader(baseSection),
            new StubSdkConfigurationYamlReader(baseSection)
        )
            .selectCustom()
            .setDefaultLanguage(ANY_LANGUAGE);

        assertThatThrownBy(() -> configBuilder.build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Missing available producers");
    }

    private UofConfigurationImpl withAnyToken(final UofConfigurationImpl config) {
        config.setAccessToken(ANY_TOKEN);
        return config;
    }

    private void verifyConfig(UofConfiguration config, Environment environment, Locale baseLocale) {
        Assert.assertEquals(defaultAccessToken, config.getAccessToken());
        Assert.assertEquals(baseLocale, config.getDefaultLanguage());
        Assert.assertEquals(1, config.getLanguages().size());
        Assert.assertEquals(baseLocale, config.getLanguages().iterator().next());

        validateApiConfigForEnvironment(config, environment);
        validateRabbitConfigForEnvironment(config, environment);
        validateDefaultCacheConfig(config);
        validateDefaultProducerConfig(config);
    }
}
