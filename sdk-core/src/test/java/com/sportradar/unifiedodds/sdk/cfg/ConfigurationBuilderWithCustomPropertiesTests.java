/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals" })
public class ConfigurationBuilderWithCustomPropertiesTests extends ConfigurationBuilderSetup {

    @Test
    public void accessTokenHasCorrectValue() {
        final String propertiesKey = "uf.sdk.accessToken";
        customSection.put(propertiesKey, "my_token");

        Assert.assertEquals(customSection.get(propertiesKey), custBuilder().build().getAccessToken());
    }

    @Test
    public void defaultLanguageFromPropertiesHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        customSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(languageDe, custBuilder().build().getDefaultLanguage());

        Assert.assertEquals(
            2,
            custBuilder()
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
        customSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(languageDe, custBuilder().build().getDefaultLanguage());

        Assert.assertEquals(
            2,
            custBuilder()
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
        customSection.put(propertiesKey, defaultLanguage.toLanguageTag());

        Assert.assertEquals(
            languageDe,
            custBuilder().setDefaultLanguage(languageDe).build().getDefaultLanguage()
        );

        Assert.assertEquals(
            1,
            custBuilder()
                .setDefaultLanguage(languageDe)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void defaultLanguageFromYamlCanOverrideManually() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        customSection.put(propertiesKey, defaultLanguage.toLanguageTag());

        Assert.assertEquals(
            1,
            custBuilder()
                .setDefaultLanguage(languageDe)
                .loadConfigFromApplicationYml()
                .build()
                .getLanguages()
                .size()
        );
    }

    @Test
    public void defaultLanguageFromPropertiesCanOverrideManuallyAndBack() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        customSection.put(propertiesKey, languageDe.toLanguageTag());

        Assert.assertEquals(
            languageDe,
            custBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getDefaultLanguage()
        );

        Assert.assertEquals(
            2,
            custBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .size()
        );

        Assert.assertEquals(
            languageDe,
            custBuilder()
                .setDefaultLanguage(defaultLanguage)
                .loadConfigFromSdkProperties()
                .build()
                .getLanguages()
                .get(0)
        );
    }

    @Test
    public void languagesOnlyInPropertiesBuildsCorrectly() {
        final String propertyKey = "uf.sdk.desiredLanguages";
        final String propertyValue = "de,nl";
        customSection.put(propertyKey, propertyValue);
        customSection.remove("uf.sdk.defaultLanguage");

        Assert.assertEquals(
            languageDe,
            custBuilder().loadConfigFromSdkProperties().build().getDefaultLanguage()
        );
    }

    @Test
    public void languagesHasCorrectValue() {
        final String propertiesKey = "uf.sdk.defaultLanguage";
        final String desiredLanguagesKey = "uf.sdk.desiredLanguages";
        customSection.put(propertiesKey, languageDe.toLanguageTag());
        customSection.put(desiredLanguagesKey, "de,en,nl");

        List<Locale> setLanguages = getLanguageList(customSection.get(desiredLanguagesKey));
        Assert.assertTrue(sequenceEqual(setLanguages, custBuilder().build().getLanguages()));

        setLanguages = getLanguageList(customSection.get(propertiesKey));
        Assert.assertTrue(
            sequenceEqual(setLanguages, custBuilder().setDesiredLanguages(null).build().getLanguages())
        );

        customSection.put(propertiesKey, "sl");
        String langString = "sl," + customSection.get(desiredLanguagesKey);
        setLanguages = getLanguageList(langString);
        Assert.assertTrue(sequenceEqual(setLanguages, custBuilder().build().getLanguages()));
    }

    @Test
    public void disabledProducersHasCorrectValue() {
        final String propertiesKey = "uf.sdk.disabledProducers";
        customSection.put(propertiesKey, "1,3");

        List<Integer> setInts = getIntList(customSection.get(propertiesKey));
        Assert.assertTrue(seqIntEqual(setInts, custBuilder().build().getProducer().getDisabledProducers()));

        Assert.assertEquals(
            0,
            custBuilder().setDisabledProducers(null).build().getProducer().getDisabledProducers().size()
        );
    }

    @Test
    public void disabledProducersInvalidValueThrows() {
        final String propertyKey = "uf.sdk.disabledProducers";
        final String propertyValue = "I0";
        customSection.put(propertyKey, propertyValue);

        assertThatThrownBy(() -> custBuilder().build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void nodeIdHasCorrectValue() {
        final String propertyKey = "uf.sdk.nodeId";
        final Integer propertyValue = 11;
        customSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(propertyValue, custBuilder().build().getNodeId());

        Integer zeroNodeId = 0;
        Assert.assertEquals(zeroNodeId, custBuilder().setNodeId(zeroNodeId).build().getNodeId());

        Assert.assertEquals(
            propertyValue,
            custBuilder().setNodeId(zeroNodeId).loadConfigFromSdkProperties().build().getNodeId()
        );
    }

    @Test
    public void nodeIdInvalidValueThrows() {
        final String propertyKey = "uf.sdk.nodeId";
        final String propertyValue = "I0";
        customSection.put(propertyKey, propertyValue);

        assertThatThrownBy(() -> custBuilder().build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void environmentHasCorrectValue() {
        final String propertyKey = "uf.sdk.environment";
        final Environment propertyValue = Environment.Custom;
        customSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(propertyValue, custBuilder().build().getEnvironment());
    }

    @Test
    public void exceptionHandlingStrategyHasCorrectValue() {
        final String propertyKey = "uf.sdk.exceptionHandlingStrategy";
        final ExceptionHandlingStrategy propertyValue = ExceptionHandlingStrategy.Throw;
        customSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(propertyValue, custBuilder().build().getExceptionHandlingStrategy());

        Assert.assertEquals(
            ExceptionHandlingStrategy.Catch,
            custBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .build()
                .getExceptionHandlingStrategy()
        );

        Assert.assertEquals(
            propertyValue,
            custBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .loadConfigFromSdkProperties()
                .build()
                .getExceptionHandlingStrategy()
        );
    }

    @Test
    public void exceptionHandlingStrategyFromYamlHasCorrectValue() {
        final String propertyKey = "uf.sdk.exceptionHandlingStrategy";
        final ExceptionHandlingStrategy propertyValue = ExceptionHandlingStrategy.Throw;
        customSection.put(propertyKey, propertyValue.toString());

        Assert.assertEquals(
            propertyValue,
            custBuilder()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Catch)
                .loadConfigFromApplicationYml()
                .build()
                .getExceptionHandlingStrategy()
        );
    }

    @Test
    public void messagingHostUsesValueFromProperties() {
        final String propertyKey = "uf.sdk.messagingHost";
        final String propertyValue = "mq.localhost.local";
        customSection.put(propertyKey, propertyValue);

        Assert.assertEquals(propertyValue, custBuilder().build().getRabbit().getHost());
        Assert.assertEquals(getCustomSectionValue("apiHost"), custBuilder().build().getApi().getHost());
    }

    @Test
    public void messagingPortHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingPort";
        final String propertyValue = "123";
        customSection.put(propertyKey, propertyValue);
        customSection.put("messagingUseSsl", "true");

        Assert.assertEquals(
            Integer.valueOf(propertyValue).intValue(),
            custBuilder().build().getRabbit().getPort()
        );

        customSection.put("messagingUseSsl", "false");
        Assert.assertEquals(
            Integer.valueOf(propertyValue).intValue(),
            custBuilder().build().getRabbit().getPort()
        );
    }

    @Test
    public void messagingPortZeroHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingPort";
        final String propertyValue = "0";
        customSection.put(propertyKey, propertyValue);

        Assert.assertEquals(
            EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1,
            custBuilder().build().getRabbit().getPort()
        );
    }

    @Test
    public void messagingPortInvalidValueThrows() {
        final String propertyKey = "uf.sdk.messagingPort";
        final String propertyValue = "I0";
        customSection.put(propertyKey, propertyValue);

        assertThatThrownBy(() -> custBuilder().build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void usernameHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingUsername";
        final String propertyValue = "customUsername";
        customSection.put(propertyKey, propertyValue);

        Assert.assertEquals(propertyValue, custBuilder().build().getRabbit().getUsername());
    }

    @Test
    public void passwordHasDefaultValue() {
        final String propertyKey = "uf.sdk.messagingPassword";
        final String propertyValue = "customPassword";
        customSection.put(propertyKey, null);

        Assert.assertNull(custBuilder().build().getRabbit().getPassword());

        customSection.put(propertyKey, propertyValue);
        Assert.assertEquals(propertyValue, custBuilder().build().getRabbit().getPassword());
    }

    @Test
    public void virtualHostHasDefaultValue() {
        final String propertyKey = "uf.sdk.messagingVirtualHost";
        final String propertyValue = "/custom-virtual-host";
        customSection.put(propertyKey, null);

        Assert.assertEquals(defaultVirtualHost, custBuilder().build().getRabbit().getVirtualHost());

        customSection.put(propertyKey, propertyValue);
        Assert.assertEquals(propertyValue, custBuilder().build().getRabbit().getVirtualHost());
    }

    @Test
    public void useMessagingSslHasCorrectValue() {
        final String propertyKey = "uf.sdk.messagingUseSsl";
        final String propertyValue = "false";
        customSection.put(propertyKey, propertyValue);

        Assert.assertFalse(custBuilder().build().getRabbit().getUseSsl());
    }

    @Test
    public void apiHostHasDefaultValue() {
        final String propertyKey = "uf.sdk.apiHost";
        final String propertyValue = "stgapi.localhost.com";
        customSection.put(propertyKey, propertyValue);

        Assert.assertEquals(propertyValue, custBuilder().build().getApi().getHost());

        Assert.assertEquals(
            customApiHost,
            customBuilder("token").setDefaultLanguage(defaultLanguage).build().getApi().getHost()
        );
    }

    @Test
    public void useApiSslHasCorrectValue() {
        final String propertyKey = "uf.sdk.apiUseSsl";
        final String propertyValue = "false";
        customSection.put(propertyKey, propertyValue);

        Assert.assertFalse(custBuilder().build().getApi().getUseSsl());
    }

    @Test
    public void apiPortInvalidValueThrows() {
        final String propertyKey = "uf.sdk.apiPort";
        final String propertyValue = "I0";
        customSection.put(propertyKey, propertyValue);

        assertThatThrownBy(() -> custBuilder().build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void customEnvironmentSetMessagingConfig() {
        final String customAccessToken = "myCustomToken";
        final int rabbitPort = 1234;
        final boolean rabbitUseSsl = false;
        customSection.put("uf.sdk.accessToken", customAccessToken);
        customSection.put("uf.sdk.defaultLanguage", "nl");
        customSection.put("uf.sdk.desiredLanguages", "nl,de");
        customSection.put("uf.sdk.environment", "Custom");
        customSection.put("uf.sdk.messagingHost", customRabbitHost);
        customSection.put("uf.sdk.messagingPort", Integer.toString(rabbitPort));
        customSection.put("uf.sdk.messagingUseSsl", Boolean.toString(rabbitUseSsl));
        UofConfiguration config = getTokenSetter(customSection)
            .setAccessTokenFromSdkProperties()
            .selectCustom()
            .loadConfigFromSdkProperties()
            .setAdjustAfterAge(true)
            .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
            .setInactivitySeconds(45)
            .setMaxRecoveryTime(750)
            .setMinIntervalBetweenRecoveryRequests(45)
            .setHttpClientTimeout(45)
            .setHttpClientRecoveryTimeout(55)
            .build();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.Custom,
            "nl",
            2,
            customRabbitHost,
            "stgapi.localhost.com",
            rabbitPort,
            "username",
            "password",
            "customVirtualHost",
            rabbitUseSsl,
            false,
            45,
            750,
            45,
            11,
            2,
            ExceptionHandlingStrategy.Throw,
            true,
            45,
            55,
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT
        );
    }

    @Test
    public void customEnvironmentSetApiConfig() {
        final String customAccessToken = "myCustom-Token";
        final boolean apiUseSsl = false;
        customSection.put("uf.sdk.accessToken", customAccessToken);
        customSection.put("uf.sdk.desiredLanguages", "nl,de");
        customSection.put("uf.sdk.environment", "Custom");
        customSection.put("uf.sdk.nodeId", "123");
        customSection.put("uf.sdk.apiHost", customApiHost);
        customSection.put("uf.sdk.apiUseSsl", Boolean.toString(apiUseSsl));
        UofConfiguration config = getTokenSetter(customSection)
            .setAccessTokenFromSdkProperties()
            .selectCustom()
            .loadConfigFromSdkProperties()
            .setAdjustAfterAge(true)
            .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
            .setInactivitySeconds(45)
            .setMaxRecoveryTime(750)
            .setMinIntervalBetweenRecoveryRequests(45)
            .setHttpClientTimeout(45)
            .setHttpClientRecoveryTimeout(55)
            .build();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.Custom,
            "en",
            3,
            "stgmq.localhost.com",
            customApiHost,
            5000,
            "username",
            "password",
            "customVirtualHost",
            false,
            apiUseSsl,
            45,
            750,
            45,
            123,
            2,
            ExceptionHandlingStrategy.Throw,
            true,
            45,
            55,
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT
        );
    }

    @SuppressWarnings("checkstyle:MethodLength")
    @Test
    public void customEnvironmentSetMessagingAndApiConfig() {
        final String customAccessToken = "my-Custom-Token";
        final int rabbitPort = 1234;
        final String rabbitPassword = "customPassword";
        final boolean rabbitUseSsl = true;
        final boolean apiUseSsl = false;
        final String rabbitVirtualHost = "/custom-virtual-host";
        List<Integer> disabledProducers = Arrays.asList(1, 3, 5, 5, 3, 1, 1, 1, 3);
        customSection.put("uf.sdk.accessToken", customAccessToken);
        customSection.put("uf.sdk.desiredLanguages", "hu,de,nl,en");
        customSection.put("uf.sdk.environment", "Custom");
        customSection.put("uf.sdk.messagingHost", customRabbitHost);
        customSection.put("uf.sdk.messagingPort", Integer.toString(rabbitPort));
        customSection.put("uf.sdk.messagingUseSsl", Boolean.toString(rabbitUseSsl));
        customSection.put("uf.sdk.messagingVirtualHost", rabbitVirtualHost);
        customSection.put("uf.sdk.messagingPassword", rabbitPassword);
        customSection.put("uf.sdk.nodeId", "1234");
        customSection.put("uf.sdk.apiHost", customApiHost);
        customSection.put("uf.sdk.apiUseSsl", Boolean.toString(apiUseSsl));

        UofConfiguration config = getTokenSetter(customSection)
            .setAccessTokenFromSdkProperties()
            .selectCustom()
            .loadConfigFromSdkProperties()
            .setAdjustAfterAge(false)
            .setInactivitySeconds(35)
            .setInactivitySecondsPrematch(35)
            .setMaxRecoveryTime(850)
            .setMinIntervalBetweenRecoveryRequests(55)
            .setHttpClientTimeout(55)
            .setHttpClientRecoveryTimeout(50)
            .setHttpClientFastFailingTimeout(8)
            .setDisabledProducers(disabledProducers)
            .build();

        validateConfiguration(
            config,
            customAccessToken,
            Environment.Custom,
            "en",
            4,
            customRabbitHost,
            customApiHost,
            rabbitPort,
            "username",
            rabbitPassword,
            rabbitVirtualHost,
            rabbitUseSsl,
            apiUseSsl,
            35,
            850,
            55,
            1234,
            3,
            ExceptionHandlingStrategy.Throw,
            false,
            55,
            50,
            8
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

    private CustomConfigurationBuilder custBuilder() {
        return customBuilder(customSection);
    }
}
