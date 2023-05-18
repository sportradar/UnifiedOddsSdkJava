/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import static java.util.Locale.ENGLISH;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class EnvironmentSelectorTests {

    private final String token = "sample-token";
    private SDKConfigurationPropertiesReader samplePropertiesReader = mock(
        SDKConfigurationPropertiesReader.class
    );
    private SDKConfigurationYamlReader sampleYamlReader = mock(SDKConfigurationYamlReader.class);
    private final EnvironmentSelector environmentSelector = new EnvironmentSelectorImpl(
        token,
        samplePropertiesReader,
        sampleYamlReader
    );

    @Test(expected = IllegalArgumentException.class)
    public void environmentSelectorConstructFailure() {
        new EnvironmentSelectorImpl(null, samplePropertiesReader, sampleYamlReader);
    }

    @Test(expected = NullPointerException.class)
    public void environmentSelectorConstructFailure2() {
        new EnvironmentSelectorImpl("sample-token", null, sampleYamlReader);
    }

    @Test(expected = NullPointerException.class)
    public void environmentSelectorConstructFailure3() {
        new EnvironmentSelectorImpl("sample-token", samplePropertiesReader, null);
    }

    @Test
    public void selectIntegrationEnvironmentReturnTest() {
        ConfigurationBuilder configurationBuilder = environmentSelector.selectIntegration();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectProductionEnvironmentReturnTest() {
        ConfigurationBuilder configurationBuilder = environmentSelector.selectProduction();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectReplayEnvironmentReturnTest() {
        ReplayConfigurationBuilder configurationBuilder = environmentSelector.selectReplay();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void selectCustomEnvironmentReturnTest() {
        CustomConfigurationBuilder configurationBuilder = environmentSelector.selectCustom();

        Assert.assertNotNull(configurationBuilder);
    }

    @Test
    public void integrationEnvironmentResultValidation() {
        OddsFeedConfiguration cfg = environmentSelector
            .selectIntegration()
            .setDefaultLocale(Locale.CHINESE)
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(cfg.getAccessToken(), "sample-token");
        Assert.assertEquals(cfg.getMessagingHost(), EnvironmentManager.getMqHost(Environment.Integration));
        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Integration));
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 5671);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.CHINESE);
        Assert.assertEquals(cfg.getDesiredLocales().size(), 1);
        Assert.assertEquals(cfg.getDesiredLocales().iterator().next(), Locale.CHINESE);
    }

    @Test
    public void productionEnvironmentResultValidation() {
        OddsFeedConfiguration cfg = environmentSelector
            .selectProduction()
            .setDefaultLocale(Locale.CHINESE)
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(cfg.getAccessToken(), "sample-token");
        Assert.assertEquals(cfg.getMessagingHost(), EnvironmentManager.getMqHost(Environment.Production));
        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Production));
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 5671);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.CHINESE);
        Assert.assertEquals(cfg.getDesiredLocales().size(), 1);
        Assert.assertEquals(cfg.getDesiredLocales().iterator().next(), Locale.CHINESE);
    }

    @Test
    public void replayEnvironmentResultValidation() {
        OddsFeedConfiguration cfg = environmentSelector
            .selectReplay()
            .setDesiredLocales(Arrays.asList(Locale.CHINESE))
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(cfg.getAccessToken(), "sample-token");
        Assert.assertEquals(cfg.getEnvironment(), Environment.Replay);
        Assert.assertEquals(cfg.getMessagingHost(), EnvironmentManager.getMqHost(Environment.Replay));
        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Replay));
        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Integration));
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 5671);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.CHINESE);
        Assert.assertEquals(cfg.getDesiredLocales().size(), 1);
        Assert.assertEquals(cfg.getDesiredLocales().iterator().next(), Locale.CHINESE);
    }

    @Test
    public void replayShouldTargetIntegrationApiBecauseReplayButWeNeedAnExplanationWhy() {
        OddsFeedConfiguration cfg = environmentSelector.selectReplay().setDefaultLocale(ENGLISH).build();

        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Integration));
    }

    @Test
    public void globalAndNonGlobalStgApisSitUnderSameIpsHoweverReplayShouldPointToNonGlobalAsItIsLongTermStrategy() {
        OddsFeedConfiguration cfg = environmentSelector.selectReplay().setDefaultLocale(ENGLISH).build();

        Assert.assertEquals(cfg.getAPIHost(), "stgapi.betradar.com");
    }

    @Test
    public void replayShouldPointToNonGlobalMessagingHostAsItIsLongTermStrategy() {
        OddsFeedConfiguration cfg = environmentSelector.selectReplay().setDefaultLocale(ENGLISH).build();

        Assert.assertEquals(cfg.getMessagingHost(), "replaymq.betradar.com");
    }

    @Test
    public void replayConfigurationShouldBeCreatedForReplayEnvironment() {
        OddsFeedConfiguration cfg = environmentSelector.selectReplay().setDefaultLocale(ENGLISH).build();

        Assert.assertEquals(cfg.getEnvironment(), Environment.Replay);
    }

    @Test
    public void customEnvironmentDefaultResultValidation() {
        OddsFeedConfiguration cfg = environmentSelector
            .selectCustom()
            .setDesiredLocales(Arrays.asList(Locale.CHINESE))
            .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(cfg.getAccessToken(), "sample-token");
        Assert.assertEquals(cfg.getMessagingHost(), EnvironmentManager.getMqHost(Environment.Integration));
        Assert.assertEquals(cfg.getAPIHost(), EnvironmentManager.getApiHost(Environment.Integration));
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 5671);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.CHINESE);
        Assert.assertEquals(cfg.getDesiredLocales().size(), 1);
        Assert.assertEquals(cfg.getDesiredLocales().iterator().next(), Locale.CHINESE);
    }
}
