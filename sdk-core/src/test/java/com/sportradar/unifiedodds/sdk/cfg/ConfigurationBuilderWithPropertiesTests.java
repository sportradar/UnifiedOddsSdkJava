/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ConfigurationBuilderWithPropertiesTests extends ConfigurationBuilderSetup {

    @Test
    public void accessTokenHasCorrectValue() {
        final String propertiesKey = "uf.sdk.accessToken";
        baseSection.put(propertiesKey, "my_token");

        Assert.assertEquals(baseSection.get(propertiesKey), intBuilder().build().getAccessToken());
        Assert.assertEquals(baseSection.get(propertiesKey), prodBuilder().build().getAccessToken());
        Assert.assertEquals(baseSection.get(propertiesKey), replayBuilder().build().getAccessToken());
    }

    @Test
    public void defaultLanguageFromPropertiesHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(languageDe, intBuilder().build().getDefaultLanguage());
        Assert.assertEquals(languageDe, prodBuilder().build().getDefaultLanguage());
        Assert.assertEquals(languageDe, replayBuilder().build().getDefaultLanguage());

        Assert.assertEquals(
            1,
            intBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            prodBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            replayBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void defaultLanguageFromYamlHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(
            1,
            intBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromApplicationYml()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            prodBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromApplicationYml()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            replayBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromApplicationYml()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void defaultLanguageFromPropertiesCanOverrideManually() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, defaultLanguage.toLanguageTag());

        Assert.assertEquals(
            languageDe,
            intBuilder().setDefaultLanguage(languageDe).build().getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            prodBuilder().setDefaultLanguage(languageDe).build().getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            replayBuilder().setDefaultLanguage(languageDe).build().getDefaultLanguage()
        );

        Assert.assertEquals(
            1,
            intBuilder()
                .setDefaultLanguage(languageDe)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            prodBuilder()
                .setDefaultLanguage(languageDe)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            replayBuilder()
                .setDefaultLanguage(languageDe)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void defaultLanguageSetAndOverrideWithPropertiesHaCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(
            languageDe,
            intBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            prodBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            replayBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getDefaultLanguage()
        );
    }

    @Test
    public void languagesOnlyInPropertiesBuildsCorrectly() {
        final String propertyKey = "uf.sdk.desiredLanguages";
        final String propertyValue = "de,nl";
        baseSection.put(propertyKey, propertyValue);
        baseSection.remove("uf.sdk.defaultLanguage");

        Assert.assertEquals(
            languageDe,
            intBuilder().loadConfigFromSdkProperties().build().getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            prodBuilder().loadConfigFromSdkProperties().build().getDefaultLanguage()
        );
        Assert.assertEquals(
            languageDe,
            replayBuilder().loadConfigFromSdkProperties().build().getDefaultLanguage()
        );
    }

    @Test
    public void setDefaultLanguageAndOverrideWithPropertiesResultingLanguagesHasCorrectSize() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(
            1,
            intBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            prodBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
        Assert.assertEquals(
            1,
            replayBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void setDefaultLanguageAndOverrideWithPropertiesResultingLanguagesHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(
            languageDe,
            intBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .get(0)
        );
        Assert.assertEquals(
            languageDe,
            prodBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .get(0)
        );
        Assert.assertEquals(
            languageDe,
            replayBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .get(0)
        );
    }

    @Test
    public void languagesHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        final String desiredLanguagesKey = "uf.sdk.desiredLanguages";
        baseSection.put(propertiesKey, languageDe.toLanguageTag());
        baseSection.put(desiredLanguagesKey, "de,en,nl");

        List<Locale> setLanguages = getLanguageList(baseSection.get(desiredLanguagesKey));
        Assert.assertTrue(sequenceEqual(setLanguages, intBuilder().build().getLanguages()));
        Assert.assertTrue(sequenceEqual(setLanguages, prodBuilder().build().getLanguages()));
        Assert.assertTrue(sequenceEqual(setLanguages, replayBuilder().build().getLanguages()));

        setLanguages = getLanguageList(baseSection.get(propertiesKey));
        Assert.assertTrue(
            sequenceEqual(setLanguages, intBuilder().setDesiredLanguages(null).build().getLanguages())
        );
        Assert.assertTrue(
            sequenceEqual(setLanguages, prodBuilder().setDesiredLanguages(null).build().getLanguages())
        );
        Assert.assertTrue(
            sequenceEqual(setLanguages, replayBuilder().setDesiredLanguages(null).build().getLanguages())
        );

        baseSection.put(propertiesKey, "sl");
        String langString = "sl," + baseSection.get(desiredLanguagesKey);
        setLanguages = getLanguageList(langString);
        Assert.assertTrue(sequenceEqual(setLanguages, intBuilder().build().getLanguages()));
        Assert.assertTrue(sequenceEqual(setLanguages, prodBuilder().build().getLanguages()));
        Assert.assertTrue(sequenceEqual(setLanguages, replayBuilder().build().getLanguages()));
    }

    @Test
    public void disabledProducersHasCorrectValue() {
        final String propertiesKey = "uf.sdk.disabledProducers";
        baseSection.put(propertiesKey, "1,3");

        List<Integer> setInts = getIntList(baseSection.get(propertiesKey));
        Assert.assertTrue(seqIntEqual(setInts, intBuilder().build().getProducer().getDisabledProducers()));
        Assert.assertTrue(seqIntEqual(setInts, prodBuilder().build().getProducer().getDisabledProducers()));
        Assert.assertTrue(seqIntEqual(setInts, replayBuilder().build().getProducer().getDisabledProducers()));

        Assert.assertEquals(
            0,
            intBuilder().setDisabledProducers(null).build().getProducer().getDisabledProducers().size()
        );
        Assert.assertEquals(
            0,
            prodBuilder().setDisabledProducers(null).build().getProducer().getDisabledProducers().size()
        );
        Assert.assertEquals(
            0,
            replayBuilder().setDisabledProducers(null).build().getProducer().getDisabledProducers().size()
        );
    }

    @Test
    public void nodeIdHasCorrectValue() {
        final String propertyKey = "uf.sdk.nodeId";
        final Integer propertyValue = 11;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(propertyValue, intBuilder().build().getNodeId());
        Assert.assertEquals(propertyValue, prodBuilder().build().getNodeId());
        Assert.assertEquals(propertyValue, replayBuilder().build().getNodeId());

        Integer zeroNodeId = 0;
        Assert.assertEquals(zeroNodeId, intBuilder().setNodeId(zeroNodeId).build().getNodeId());
        Assert.assertEquals(zeroNodeId, prodBuilder().setNodeId(zeroNodeId).build().getNodeId());
        Assert.assertEquals(zeroNodeId, replayBuilder().setNodeId(zeroNodeId).build().getNodeId());

        Assert.assertEquals(
            propertyValue,
            intBuilder().setNodeId(zeroNodeId).loadConfigFromSdkProperties().build().getNodeId()
        );
        Assert.assertEquals(
            propertyValue,
            prodBuilder().setNodeId(zeroNodeId).loadConfigFromSdkProperties().build().getNodeId()
        );
        Assert.assertEquals(
            propertyValue,
            replayBuilder().setNodeId(zeroNodeId).loadConfigFromSdkProperties().build().getNodeId()
        );
    }

    @Test
    public void nodeIdFromYamlHasCorrectValue() {
        final String propertyKey = "uf.sdk.nodeId";
        final Integer propertyValue = 11;
        baseSection.put(propertyKey, propertyValue.toString());

        Integer zeroNodeId = 0;
        Assert.assertEquals(
            propertyValue,
            intBuilder().setNodeId(zeroNodeId).loadConfigFromApplicationYml().build().getNodeId()
        );
        Assert.assertEquals(
            propertyValue,
            prodBuilder().setNodeId(zeroNodeId).loadConfigFromApplicationYml().build().getNodeId()
        );
        Assert.assertEquals(
            propertyValue,
            replayBuilder().setNodeId(zeroNodeId).loadConfigFromApplicationYml().build().getNodeId()
        );
    }

    @Test
    public void environmentHasCorrectValue() {
        final String propertyKey = "uf.sdk.environment";
        final Environment propertyValue = Environment.GlobalIntegration;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(
            propertyValue,
            getTokenSetter(baseSection).buildConfigFromSdkProperties().getEnvironment()
        );
    }

    @Test
    public void environmentFromYamlHasCorrectValue() {
        final String propertyKey = "uf.sdk.environment";
        final Environment propertyValue = Environment.GlobalIntegration;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(
            propertyValue,
            getTokenSetter(baseSection).buildConfigFromApplicationYml().getEnvironment()
        );
    }

    @Test
    public void exceptionHandlingStrategySetFromPropertiesHasCorrectValue() {
        final String propertyKey = "uf.sdk.exceptionHandlingStrategy";
        final ExceptionHandlingStrategy propertyValue = ExceptionHandlingStrategy.Throw;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(propertyValue, intBuilder().build().getExceptionHandlingStrategy());
        Assert.assertEquals(propertyValue, prodBuilder().build().getExceptionHandlingStrategy());
        Assert.assertEquals(propertyValue, replayBuilder().build().getExceptionHandlingStrategy());
    }

    @Test
    public void exceptionHandlingStrategySetManuallyHasCorrectValue() {
        final String propertyKey = "uf.sdk.exceptionHandlingStrategy";
        final ExceptionHandlingStrategy propertyValue = ExceptionHandlingStrategy.Throw;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(
            ExceptionHandlingStrategy.Catch,
            intBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .build()
                .getExceptionHandlingStrategy()
        );
        Assert.assertEquals(
            ExceptionHandlingStrategy.Catch,
            prodBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .build()
                .getExceptionHandlingStrategy()
        );
        Assert.assertEquals(
            ExceptionHandlingStrategy.Catch,
            replayBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .build()
                .getExceptionHandlingStrategy()
        );
    }

    @Test
    public void setExceptionHandlingStrategyAndOverrideWithPropertiesHasCorrectValue() {
        final String propertyKey = "uf.sdk.exceptionHandlingStrategy";
        final ExceptionHandlingStrategy propertyValue = ExceptionHandlingStrategy.Throw;
        baseSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(
            propertyValue,
            intBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .loadConfigFromSdkProperties()
                .build()
                .getExceptionHandlingStrategy()
        );
        Assert.assertEquals(
            propertyValue,
            prodBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .loadConfigFromSdkProperties()
                .build()
                .getExceptionHandlingStrategy()
        );
        Assert.assertEquals(
            propertyValue,
            replayBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .loadConfigFromSdkProperties()
                .build()
                .getExceptionHandlingStrategy()
        );
    }

    @Test
    public void messagingHostIsIgnored() {
        final String propertyKey = "uf.sdk.messagingHost";
        final String propertyValue = "mq.localhost.local";
        baseSection.put(propertyKey, propertyValue);

        Assert.assertEquals(
            EnvironmentManager.getMqHost(Environment.Integration),
            intBuilder().build().getRabbit().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getMqHost(Environment.Production),
            prodBuilder().build().getRabbit().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getMqHost(Environment.Replay),
            replayBuilder().build().getRabbit().getHost()
        );
    }

    @Test
    public void portHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingPort";
        final String propertyValue = "123";
        baseSection.put(propertyKey, propertyValue);
        baseSection.put("messagingUseSsl", "true");

        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            intBuilder().build().getRabbit().getPort()
        );
        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            prodBuilder().build().getRabbit().getPort()
        );
        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            replayBuilder().build().getRabbit().getPort()
        );

        baseSection.put("messagingUseSsl", "false");
        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            intBuilder().build().getRabbit().getPort()
        );
        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            prodBuilder().build().getRabbit().getPort()
        );
        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            replayBuilder().build().getRabbit().getPort()
        );
    }

    @Test
    public void usernameHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingUsername";
        final String propertyValue = "customUsername";
        baseSection.put(propertyKey, propertyValue);

        Assert.assertEquals(defaultAccessToken, intBuilder().build().getRabbit().getUsername());
        Assert.assertEquals(defaultAccessToken, prodBuilder().build().getRabbit().getUsername());
        Assert.assertEquals(defaultAccessToken, replayBuilder().build().getRabbit().getUsername());
    }

    @Test
    public void passwordHasDefaultValue() {
        final String propertyKey = "uf.sdk.messagingUsername";
        final String propertyValue = "customUsername";
        baseSection.put(propertyKey, null);

        Assert.assertNull(intBuilder().build().getRabbit().getPassword());
        Assert.assertNull(prodBuilder().build().getRabbit().getPassword());
        Assert.assertNull(replayBuilder().build().getRabbit().getPassword());

        baseSection.put(propertyKey, propertyValue);
        Assert.assertNull(intBuilder().build().getRabbit().getPassword());
        Assert.assertNull(prodBuilder().build().getRabbit().getPassword());
        Assert.assertNull(replayBuilder().build().getRabbit().getPassword());
    }

    @Test
    public void virtualHostHasDefaultValue() {
        final String propertyKey = "uf.sdk.messagingVirtualHost";
        final String propertyValue = "/custom-virtual-host";
        baseSection.put(propertyKey, null);

        Assert.assertEquals(defaultVirtualHost, intBuilder().build().getRabbit().getVirtualHost());
        Assert.assertEquals(defaultVirtualHost, prodBuilder().build().getRabbit().getVirtualHost());
        Assert.assertEquals(defaultVirtualHost, replayBuilder().build().getRabbit().getVirtualHost());

        baseSection.put(propertyKey, propertyValue);
        Assert.assertEquals(defaultVirtualHost, intBuilder().build().getRabbit().getVirtualHost());
        Assert.assertEquals(defaultVirtualHost, prodBuilder().build().getRabbit().getVirtualHost());
        Assert.assertEquals(defaultVirtualHost, replayBuilder().build().getRabbit().getVirtualHost());
    }

    @Test
    public void useMessagingSslHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingUseSsl";
        final String propertyValue = "false";
        baseSection.put(propertyKey, propertyValue);

        Assert.assertTrue(intBuilder().build().getRabbit().getUseSsl());
        Assert.assertTrue(prodBuilder().build().getRabbit().getUseSsl());
        Assert.assertTrue(replayBuilder().build().getRabbit().getUseSsl());
    }

    @Test
    public void apiHostHasDefaultValue() {
        final String propertyKey = "uf.sdk.apiHost";
        final String propertyValue = "stgapi.localhost.com";
        baseSection.put(propertyKey, propertyValue);

        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Integration),
            intBuilder().build().getApi().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Production),
            prodBuilder().build().getApi().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Replay),
            replayBuilder().build().getApi().getHost()
        );

        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Integration),
            integrationBuilder("token").setDefaultLanguage(defaultLanguage).build().getApi().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Production),
            productionBuilder("token").setDefaultLanguage(defaultLanguage).build().getApi().getHost()
        );
        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Replay),
            replayBuilder("token").setDefaultLanguage(defaultLanguage).build().getApi().getHost()
        );
    }

    @Test
    public void useApiSslHasCorrectValue() {
        final String propertyKey = "uf.sdk.apiUseSsl";
        final String propertyValue = "false";
        baseSection.put(propertyKey, propertyValue);

        Assert.assertTrue(intBuilder().build().getApi().getUseSsl());
        Assert.assertTrue(prodBuilder().build().getApi().getUseSsl());
        Assert.assertTrue(replayBuilder().build().getApi().getUseSsl());
    }

    @Test
    public void loadBasicAppConfig() {
        final String customAccessToken = "myCustomToken";
        baseSection.put("uf.sdk.accessToken", customAccessToken);
        baseSection.put("uf.sdk.desiredLanguages", "en");
        UofConfiguration config = integrationBuilder(baseSection).loadConfigFromSdkProperties().build();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.Integration,
            "en",
            1,
            EnvironmentManager.getMqHost(Environment.Integration),
            EnvironmentManager.getApiHost(Environment.Integration),
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            customAccessToken,
            null,
            defaultVirtualHost,
            true,
            true,
            ConfigLimit.INACTIVITY_SECONDS_DEFAULT,
            ConfigLimit.MAX_RECOVERY_TIME_DEFAULT,
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT,
            0,
            0,
            ExceptionHandlingStrategy.Catch,
            true,
            ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT,
            ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT,
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT
        );
    }

    @Test
    public void loadEnvironmentProxyTokyoAppConfig() {
        final String customAccessToken = "myCustomToken";
        baseSection.put("uf.sdk.accessToken", customAccessToken);
        baseSection.put("uf.sdk.desiredLanguages", "en");
        baseSection.put("uf.sdk.environment", "ProxyTokyo");
        UofConfiguration config = getTokenSetter(baseSection).buildConfigFromSdkProperties();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.ProxyTokyo,
            "en",
            1,
            EnvironmentManager.getMqHost(Environment.ProxyTokyo),
            EnvironmentManager.getApiHost(Environment.ProxyTokyo),
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            customAccessToken,
            null,
            defaultVirtualHost,
            true,
            true,
            ConfigLimit.INACTIVITY_SECONDS_DEFAULT,
            ConfigLimit.MAX_RECOVERY_TIME_DEFAULT,
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT,
            0,
            0,
            ExceptionHandlingStrategy.Catch,
            true,
            ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT,
            ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT,
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT
        );
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void builderEnvironmentGlobalProduction() {
        final String customAccessToken = "myCustomToken";
        baseSection.put("uf.sdk.accessToken", customAccessToken);
        baseSection.put("uf.sdk.desiredLanguages", "en,de");
        baseSection.put("uf.sdk.environment", "ProxyTokyo");
        baseSection.put("uf.sdk.nodeId", "11");
        UofConfiguration config = getTokenSetter(baseSection)
            .setAccessTokenFromSdkProperties()
            .selectEnvironment(Environment.GlobalProduction)
            .loadConfigFromSdkProperties()
            .setAdjustAfterAge(true)
            .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
            .setHttpClientTimeout(45)
            .setInactivitySeconds(45)
            .setMaxRecoveryTime(750)
            .setMinIntervalBetweenRecoveryRequests(45)
            .setHttpClientRecoveryTimeout(55)
            .build();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.GlobalProduction,
            "en",
            2,
            EnvironmentManager.getMqHost(Environment.GlobalProduction),
            EnvironmentManager.getApiHost(Environment.GlobalProduction),
            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
            customAccessToken,
            null,
            defaultVirtualHost,
            true,
            true,
            45,
            750,
            45,
            11,
            0,
            ExceptionHandlingStrategy.Throw,
            true,
            45,
            55,
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT
        );
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private void validateConfiguration(
        UofConfiguration config,
        String accessToken,
        Environment environment,
        String defaultCulture,
        int wantedCultures,
        String mqHost,
        String apiHost,
        int port,
        String username,
        String password,
        String virtualHost,
        boolean useMqSsl,
        boolean useApiSsl,
        int inactivitySeconds,
        int maxRecoveryExecutionInSeconds,
        int minIntervalBetweenRecoveryRequests,
        int nodeId,
        int disabledProducers,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        boolean adjustAfterAge,
        int httpClientTimeout,
        int httpClientRecoveryTimeout,
        int httpClientFastFailingTimeout
    ) {
        Assert.assertNotNull(config);
        Assert.assertEquals(accessToken, config.getAccessToken());
        Assert.assertEquals(environment, config.getEnvironment());
        Assert.assertEquals(defaultCulture, config.getDefaultLanguage().toLanguageTag());
        Assert.assertEquals(wantedCultures, config.getLanguages().size());
        Assert.assertEquals(mqHost, config.getRabbit().getHost());
        Assert.assertEquals(apiHost, config.getApi().getHost());
        Assert.assertEquals(port, config.getRabbit().getPort());
        Assert.assertEquals(username, config.getRabbit().getUsername());
        Assert.assertEquals(password, config.getRabbit().getPassword());
        Assert.assertEquals(virtualHost, config.getRabbit().getVirtualHost());
        Assert.assertEquals(useMqSsl, config.getRabbit().getUseSsl());
        Assert.assertEquals(useApiSsl, config.getApi().getUseSsl());
        Assert.assertEquals(inactivitySeconds, config.getProducer().getInactivitySeconds().getSeconds());
        Assert.assertEquals(
            maxRecoveryExecutionInSeconds,
            config.getProducer().getMaxRecoveryTime().getSeconds()
        );
        Assert.assertEquals(
            minIntervalBetweenRecoveryRequests,
            config.getProducer().getMinIntervalBetweenRecoveryRequests().getSeconds()
        );
        Assert.assertEquals(nodeId, config.getNodeId().intValue());
        Assert.assertEquals(disabledProducers, config.getProducer().getDisabledProducers().size());
        Assert.assertEquals(exceptionHandlingStrategy, config.getExceptionHandlingStrategy());
        Assert.assertEquals(adjustAfterAge, config.getProducer().adjustAfterAge());
        Assert.assertEquals(httpClientTimeout, config.getApi().getHttpClientTimeout().getSeconds());
        Assert.assertEquals(
            httpClientRecoveryTimeout,
            config.getApi().getHttpClientRecoveryTimeout().getSeconds()
        );
        Assert.assertEquals(
            httpClientFastFailingTimeout,
            config.getApi().getHttpClientFastFailingTimeout().getSeconds()
        );

        Assert.assertNotNull(config.getBookmakerDetails());
        Assert.assertNotNull(config.getBookmakerDetails().getVirtualHost());
        Assert.assertTrue(config.getBookmakerDetails().getBookmakerId() > 0);
        Assert.assertNotNull(config.getProducer().getProducers());
        Assert.assertTrue(config.getProducer().getProducers().size() > 0);
    }

    private ConfigurationBuilder intBuilder() {
        return integrationBuilder(baseSection);
    }

    private ConfigurationBuilder prodBuilder() {
        return productionBuilder(baseSection);
    }

    private ConfigurationBuilder replayBuilder() {
        return replayBuilder(baseSection);
    }
}
