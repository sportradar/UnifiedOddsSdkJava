/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "ConstantName" })
public class TokenSetterTests extends ConfigurationBuilderSetup {

    @Test
    public void directSettingMinimalProperties() {
        Assert.assertEquals(
            defaultAccessToken,
            getTokenSetter()
                .setAccessToken(defaultAccessToken)
                .selectEnvironment(Environment.Integration)
                .setDefaultLanguage(defaultLanguage)
                .build()
                .getAccessToken()
        );
    }

    @Test(expected = InvalidParameterException.class)
    public void directSettingMinimalPropertiesWithoutAccessTokenFails() {
        getTokenSetter(baseSection)
            .setAccessTokenFromSdkProperties()
            .selectEnvironment(Environment.Integration)
            .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void directSettingMinimalPropertiesWithoutLanguageFails() {
        getTokenSetter()
            .setAccessToken(defaultAccessToken)
            .selectEnvironment(Environment.Integration)
            .build();
    }

    @Test
    public void tokenFromPropertiesIsConfigured() {
        Assert.assertEquals(Environment.Integration, basePropertiesReader.readEnvironment());

        UofConfiguration config = getTokenSetter(baseSection).buildConfigFromSdkProperties();

        Assert.assertEquals(basePropertiesReader.readAccessToken().get(), config.getAccessToken());
        Assert.assertEquals(basePropertiesReader.readEnvironment(), config.getEnvironment());

        validateDefaultConfig(config, basePropertiesReader.readEnvironment());
        validateDefaultProducerConfig(config);
        validateDefaultCacheConfig(config);
        validateApiConfigForEnvironment(config, basePropertiesReader.readEnvironment());
        validateRabbitConfigForEnvironment(config, basePropertiesReader.readEnvironment());
    }

    @Test
    public void tokenFromPropertiesIsConfiguredWithCustomEnvironment() {
        Assert.assertEquals(Environment.Custom, customPropertiesReader.readEnvironment());

        UofConfiguration config = getTokenSetter(customSection).buildConfigFromSdkProperties();

        Assert.assertNotNull(config);
        Assert.assertEquals(customPropertiesReader.readAccessToken().get(), config.getAccessToken());
        Assert.assertEquals(customPropertiesReader.readEnvironment(), config.getEnvironment());

        Assert.assertEquals(defaultAccessToken, config.getAccessToken());
        Assert.assertEquals(Environment.Custom, config.getEnvironment());
        Assert.assertEquals(1, config.getLanguages().size());
        Assert.assertTrue(config.getLanguages().contains(defaultLanguage));
        Assert.assertEquals(defaultLanguage, config.getLanguages().get(0));
        Assert.assertEquals(defaultLanguage, config.getDefaultLanguage());
        Assert.assertEquals(ExceptionHandlingStrategy.Throw, config.getExceptionHandlingStrategy());
        Assert.assertEquals(customPropertiesReader.readNodeId().get(), config.getNodeId());
    }

    @Test
    public void tokenFromYamlIsConfigured() {
        Assert.assertEquals(Environment.Integration, basePropertiesReader.readEnvironment());

        UofConfiguration config = getTokenSetter(baseSection).buildConfigFromApplicationYml();

        Assert.assertEquals(basePropertiesReader.readAccessToken().get(), config.getAccessToken());
        Assert.assertEquals(basePropertiesReader.readEnvironment(), config.getEnvironment());

        validateDefaultConfig(config, basePropertiesReader.readEnvironment());
        validateDefaultProducerConfig(config);
        validateDefaultCacheConfig(config);
        validateApiConfigForEnvironment(config, basePropertiesReader.readEnvironment());
        validateRabbitConfigForEnvironment(config, basePropertiesReader.readEnvironment());
    }

    @Test
    public void tokenFromYamlIsConfiguredWithCustomEnvironment() {
        Assert.assertEquals(Environment.Custom, customPropertiesReader.readEnvironment());

        UofConfiguration config = getTokenSetter(customSection).buildConfigFromApplicationYml();

        Assert.assertNotNull(config);
        Assert.assertEquals(customPropertiesReader.readAccessToken().get(), config.getAccessToken());
        Assert.assertEquals(customPropertiesReader.readEnvironment(), config.getEnvironment());

        Assert.assertEquals(defaultAccessToken, config.getAccessToken());
        Assert.assertEquals(Environment.Custom, config.getEnvironment());
        Assert.assertEquals(1, config.getLanguages().size());
        Assert.assertTrue(config.getLanguages().contains(defaultLanguage));
        Assert.assertEquals(defaultLanguage, config.getLanguages().get(0));
        Assert.assertEquals(defaultLanguage, config.getDefaultLanguage());
        Assert.assertEquals(ExceptionHandlingStrategy.Throw, config.getExceptionHandlingStrategy());
        Assert.assertEquals(customPropertiesReader.readNodeId().get(), config.getNodeId());
    }

    @Test
    public void environmentFromPropertiesCanBeOverridden() {
        Environment selectedEnvironment = Environment.ProxyTokyo;
        Assert.assertEquals(Environment.Integration, basePropertiesReader.readEnvironment());
        UofConfiguration config = getTokenSetter(baseSection)
            .setAccessTokenFromSdkProperties()
            .selectEnvironment(selectedEnvironment)
            .loadConfigFromSdkProperties()
            .build();
        Assert.assertEquals(basePropertiesReader.readAccessToken().get(), config.getAccessToken());
        Assert.assertEquals(selectedEnvironment, config.getEnvironment());

        validateDefaultConfig(config, selectedEnvironment);
        validateDefaultProducerConfig(config);
        validateDefaultCacheConfig(config);
        validateApiConfigForEnvironment(config, selectedEnvironment);
        validateRabbitConfigForEnvironment(config, selectedEnvironment);
    }

    @Test
    public void tokenFromPropertiesCanNotBeOverridden() {
        UofConfiguration config = getTokenSetter(baseSection).buildConfigFromSdkProperties();
        Assert.assertEquals(basePropertiesReader.readAccessToken().get(), config.getAccessToken());

        validateDefaultConfig(config, basePropertiesReader.readEnvironment());
        validateDefaultProducerConfig(config);
        validateDefaultCacheConfig(config);
        validateApiConfigForEnvironment(config, basePropertiesReader.readEnvironment());
        validateRabbitConfigForEnvironment(config, basePropertiesReader.readEnvironment());
    }

    @Test(expected = NullPointerException.class)
    public void tokenSetterConstructRequirementsFailOne() {
        new TokenSetterImpl(
            null,
            Mockito.mock(SdkConfigurationYamlReader.class),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        );
    }

    @Test(expected = NullPointerException.class)
    public void tokenSetterConstructRequirementsFailTwo() {
        new TokenSetterImpl(
            Mockito.mock(SdkConfigurationPropertiesReader.class),
            null,
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        );
    }

    @Test
    public void programmaticTokenSet() {
        EnvironmentSelector environmentSelector = getTokenSetter().setAccessToken("some-token");

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void programmaticTokenSetNull() {
        TokenSetter tokenSetter = getTokenSetter();

        tokenSetter.setAccessToken(null);
    }

    @Test
    public void propertiesTokenSet() {
        TokenSetter tokenSetter = getTokenSetter(baseSection);

        EnvironmentSelector environmentSelector = tokenSetter.setAccessTokenFromSdkProperties();

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertiesTokenMissingInProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("uf.sdk.defaultLanguage", defaultLanguage.toLanguageTag());
        properties.put("uf.sdk.exceptionHandlingStrategy", "catch");
        properties.put("uf.sdk.environment", "GlobalIntegration");
        TokenSetter tokenSetter = getTokenSetter(properties);

        tokenSetter.setAccessTokenFromSdkProperties();
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertiesTokenMissingInYaml() {
        Map<String, String> properties = new HashMap<>();
        properties.put("uf.sdk.defaultLanguage", defaultLanguage.toLanguageTag());
        properties.put("uf.sdk.exceptionHandlingStrategy", "catch");
        properties.put("uf.sdk.environment", "GlobalIntegration");
        TokenSetter tokenSetter = getTokenSetter(properties);

        tokenSetter.setAccessTokenFromApplicationYaml();
    }

    @Test
    public void systemVarTokenSet() {
        TokenSetter tokenSetter = getTokenSetter();

        System.setProperty("uf.accesstoken", "some-token");

        EnvironmentSelector environmentSelector = tokenSetter.setAccessTokenFromSystemVar();

        System.clearProperty("uf.accesstoken");

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void systemVarTokenSetNull() {
        TokenSetter tokenSetter = getTokenSetter();

        tokenSetter.setAccessTokenFromSystemVar();
    }
}
