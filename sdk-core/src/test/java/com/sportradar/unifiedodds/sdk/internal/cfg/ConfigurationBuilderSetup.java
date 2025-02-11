/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.BookmakerDetailsImpl;
import com.sportradar.unifiedodds.sdk.shared.TestProducersProvider;
import com.sportradar.utils.SdkHelper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Assert;

@SuppressWarnings({ "checkstyle:ClassFanOutComplexity", "checkstyle:VisibilityModifier" })
public class ConfigurationBuilderSetup {

    protected final String customApiHost = "custom_api_host";
    protected final String customRabbitHost = "custom_mq_host";
    protected final String defaultAccessToken = "myAccessToken";
    protected final String defaultVirtualHost = "/virtual-host";
    protected final Locale defaultLanguage = Locale.ENGLISH;
    protected final Locale languageNl = Locale.forLanguageTag("nl");
    protected final Locale languageDe = Locale.forLanguageTag("De");
    protected final Locale languageHu = Locale.forLanguageTag("hu");
    protected Map<String, String> baseSection;
    protected Map<String, String> customSection;
    protected final WhoAmIReader bookmakerDetailsProvider;
    protected final ProducerDataProvider producerDataProvider;

    protected StubSdkConfigurationPropertiesReader basePropertiesReader;
    protected StubSdkConfigurationPropertiesReader customPropertiesReader;

    protected ConfigurationBuilderSetup() {
        populateBaseSection();
        populateCustomSection();

        BookmakerDetails bookmakerDetails = new BookmakerDetailsImpl(
            2,
            defaultVirtualHost,
            Date.from(Instant.now().plus(2, ChronoUnit.DAYS)),
            ResponseCode.ACCEPTED,
            "All good",
            Duration.ofSeconds(1)
        );
        bookmakerDetailsProvider = mock(WhoAmIReader.class);
        when(bookmakerDetailsProvider.getBookmakerDetails()).thenReturn(bookmakerDetails);
        when(bookmakerDetailsProvider.getBookmakerId()).thenReturn(bookmakerDetails.getBookmakerId());
        when(bookmakerDetailsProvider.getVirtualHost()).thenReturn(bookmakerDetails.getVirtualHost());
        when(bookmakerDetailsProvider.getExpiry()).thenReturn(bookmakerDetails.getExpireAt());
        when(bookmakerDetailsProvider.getResponseCode()).thenReturn(bookmakerDetails.getResponseCode());
        when(bookmakerDetailsProvider.getMessage()).thenReturn(bookmakerDetails.getMessage());

        producerDataProvider = new TestProducersProvider();

        basePropertiesReader = new StubSdkConfigurationPropertiesReader(baseSection);
        customPropertiesReader = new StubSdkConfigurationPropertiesReader(customSection);
    }

    private void populateBaseSection() {
        baseSection = new HashMap<>();
        baseSection.put("uf.sdk.accessToken", defaultAccessToken);
        baseSection.put("uf.sdk.defaultLanguage", defaultLanguage.toLanguageTag());
        baseSection.put("uf.sdk.exceptionHandlingStrategy", "catch");
        baseSection.put("uf.sdk.environment", "Integration");
    }

    private void populateCustomSection() {
        customSection = new HashMap<>();
        customSection.put("uf.sdk.accessToken", defaultAccessToken);
        customSection.put("uf.sdk.defaultLanguage", defaultLanguage.toLanguageTag());
        customSection.put("uf.sdk.desiredLanguages", defaultLanguage.toLanguageTag());
        customSection.put("uf.sdk.exceptionHandlingStrategy", "throw");
        customSection.put("uf.sdk.environment", "Custom");
        customSection.put("uf.sdk.messagingHost", "stgmq.localhost.com");
        customSection.put("uf.sdk.messagingVirtualHost", "customVirtualHost");
        customSection.put("uf.sdk.messagingPort", "5000");
        customSection.put("uf.sdk.messagingUsername", "username");
        customSection.put("uf.sdk.messagingPassword", "password");
        customSection.put("uf.sdk.messagingUseSsl", "false");
        customSection.put("uf.sdk.apiHost", "stgapi.localhost.com");
        customSection.put("uf.sdk.apiPort", "123");
        customSection.put("uf.sdk.apiUseSsl", "false");
        customSection.put("uf.sdk.disabledProducers", "1,3");
        customSection.put("uf.sdk.nodeId", "11");
    }

    public static List<Integer> getIntList(String value) {
        return Arrays.stream(value.split(",")).map(Integer::valueOf).collect(Collectors.toList());
    }

    public static List<Locale> getLanguageList(String names) {
        return Arrays.stream(names.split(",")).map(Locale::new).collect(Collectors.toList());
    }

    public String getBaseSectionValue(String key) {
        String fullKey = "uf.sdk." + key;
        if (baseSection.containsKey(fullKey)) {
            return baseSection.get(fullKey);
        }
        return "";
    }

    public String getCustomSectionValue(String key) {
        String fullKey = "uf.sdk." + key;
        if (customSection.containsKey(fullKey)) {
            return customSection.get(fullKey);
        }
        return "";
    }

    public TokenSetter getTokenSetter() {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(null),
            new StubSdkConfigurationYamlReader(null),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        );
    }

    public TokenSetter getTokenSetter(Map<String, String> section) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(section),
            new StubSdkConfigurationYamlReader(section),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        );
    }

    public EnvironmentSelector getEnvironmentSelector() {
        return getTokenSetter().setAccessToken(defaultAccessToken);
    }

    public EnvironmentSelector getEnvironmentSelector(Map<String, String> section) {
        return getTokenSetter(section).setAccessToken(defaultAccessToken);
    }

    public ConfigurationBuilder integrationBuilder(Map<String, String> section) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(section),
            new StubSdkConfigurationYamlReader(section),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessTokenFromSdkProperties()
            .selectEnvironment(Environment.Integration)
            .loadConfigFromSdkProperties();
    }

    public ConfigurationBuilder integrationBuilder(String token) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(null),
            new StubSdkConfigurationYamlReader(null),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessToken(token)
            .selectEnvironment(Environment.Integration);
    }

    public ConfigurationBuilder productionBuilder(Map<String, String> section) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(section),
            new StubSdkConfigurationYamlReader(section),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessTokenFromSdkProperties()
            .selectEnvironment(Environment.Production)
            .loadConfigFromSdkProperties();
    }

    public ConfigurationBuilder productionBuilder(String token) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(null),
            new StubSdkConfigurationYamlReader(null),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessToken(token)
            .selectEnvironment(Environment.Production);
    }

    public ConfigurationBuilder replayBuilder(Map<String, String> section) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(section),
            new StubSdkConfigurationYamlReader(section),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessTokenFromSdkProperties()
            .selectReplay()
            .loadConfigFromSdkProperties();
    }

    public ConfigurationBuilder replayBuilder(String token) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(null),
            new StubSdkConfigurationYamlReader(null),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessToken(token)
            .selectReplay();
    }

    public CustomConfigurationBuilder customBuilder(Map<String, String> section) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(section),
            new StubSdkConfigurationYamlReader(section),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessTokenFromSdkProperties()
            .selectCustom()
            .loadConfigFromSdkProperties();
    }

    public CustomConfigurationBuilder customBuilder(String token) {
        return new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(null),
            new StubSdkConfigurationYamlReader(null),
            anyConfig -> bookmakerDetailsProvider,
            anyConfig -> producerDataProvider
        )
            .setAccessToken(token)
            .selectCustom()
            .setApiHost(customApiHost)
            .setMessagingHost(customRabbitHost);
    }

    protected ConfigurationBuilder buildConfig(String builderType) {
        if ("p".equalsIgnoreCase(builderType)) {
            return productionBuilder(defaultAccessToken).setDefaultLanguage(defaultLanguage);
        } else if ("r".equalsIgnoreCase(builderType)) {
            return replayBuilder(defaultAccessToken).setDefaultLanguage(defaultLanguage);
        } else {
            return integrationBuilder(defaultAccessToken).setDefaultLanguage(defaultLanguage);
        }
    }

    protected CustomConfigurationBuilder buildCustomConfig() {
        return customBuilder(defaultAccessToken).setDefaultLanguage(defaultLanguage);
    }

    protected void configHasDefaultValuesSet(UofConfiguration config) {
        validateDefaultProducerConfig(config);
        validateDefaultCacheConfig(config);
        validateApiConfigForEnvironment(config, config.getEnvironment());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateDefaultUsageConfig(config);
    }

    public void validateDefaultConfig(UofConfiguration config, Environment environment) {
        Assert.assertEquals(defaultAccessToken, config.getAccessToken());
        Assert.assertEquals(environment, config.getEnvironment());
        Assert.assertEquals(1, config.getLanguages().size());
        Assert.assertTrue(config.getLanguages().contains(defaultLanguage));
        Assert.assertEquals(defaultLanguage, config.getLanguages().get(0));
        Assert.assertEquals(defaultLanguage, config.getDefaultLanguage());
        Assert.assertEquals(ExceptionHandlingStrategy.Catch, config.getExceptionHandlingStrategy());
        Assert.assertEquals(0, config.getNodeId().intValue());
    }

    public void validateDefaultProducerConfig(UofConfiguration config) {
        Assert.assertEquals(0, config.getProducer().getDisabledProducers().size());
        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_DEFAULT,
            config.getProducer().getInactivitySeconds().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_PREMATCH_DEFAULT,
            config.getProducer().getInactivitySecondsPrematch().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.MAX_RECOVERY_TIME_DEFAULT,
            config.getProducer().getMaxRecoveryTime().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT,
            config.getProducer().getMinIntervalBetweenRecoveryRequests().getSeconds()
        );
    }

    public void validateDefaultCacheConfig(UofConfiguration config) {
        Assert.assertEquals(
            ConfigLimit.SPORTEVENTCACHE_TIMEOUT_DEFAULT,
            config.getCache().getSportEventCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_DEFAULT,
            config.getCache().getSportEventStatusCacheTimeout().toMinutes()
        );
        Assert.assertEquals(
            ConfigLimit.PROFILECACHE_TIMEOUT_DEFAULT,
            config.getCache().getProfileCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_DEFAULT,
            config.getCache().getVariantMarketDescriptionCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_DEFAULT,
            config.getCache().getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours()
        );
        Assert.assertFalse(config.getCache().getIgnoreBetPalTimelineSportEventStatus());
    }

    public void validateDefaultUsageConfig(UofConfiguration config) {
        Assert.assertTrue(config.getUsage().isExportEnabled());
        Assert.assertEquals(ConfigLimit.USAGE_EXPORT_TIMEOUT_SEC, config.getUsage().getExportTimeoutInSec());
        Assert.assertEquals(
            ConfigLimit.USAGE_EXPORT_INTERVAL_SEC,
            config.getUsage().getExportIntervalInSec()
        );
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(config.getUsage().getHost()));
        Assert.assertEquals(
            EnvironmentManager.getUsageHost(config.getEnvironment()),
            config.getUsage().getHost()
        );
    }

    public void validateApiConfigForEnvironment(UofConfiguration config, Environment environment) {
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT,
            config.getApi().getHttpClientTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT,
            config.getApi().getHttpClientRecoveryTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT,
            config.getApi().getHttpClientFastFailingTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            config.getApi().getHttpClientMaxConnTotal()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            config.getApi().getHttpClientMaxConnPerRoute()
        );
        if (environment != Environment.Custom) {
            Assert.assertTrue(config.getApi().getUseSsl());
            Assert.assertEquals(EnvironmentManager.getApiHost(environment), config.getApi().getHost());
        }
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(config.getApi().getHost()));
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(config.getApi().getReplayHost()));
    }

    public void validateRabbitConfigForEnvironment(UofConfiguration config, Environment environment) {
        if (environment != Environment.Custom) {
            Assert.assertTrue(config.getRabbit().getUseSsl());
            Assert.assertEquals(EnvironmentManager.getMqHost(environment), config.getRabbit().getHost());
            Assert.assertEquals(
                config.getBookmakerDetails().getVirtualHost(),
                config.getRabbit().getVirtualHost()
            );
        }
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(config.getRabbit().getUsername()));
        Assert.assertTrue(config.getRabbit().getPort() > 0);
        Assert.assertFalse(SdkHelper.stringIsNullOrEmpty(config.getRabbit().getHost()));
        Assert.assertTrue(config.getRabbit().getConnectionTimeout().getSeconds() > 0);
        Assert.assertTrue(config.getRabbit().getHeartBeat().getSeconds() > 0);
        Assert.assertNotNull(config.getBookmakerDetails());
        Assert.assertNotNull(config.getBookmakerDetails().getVirtualHost());
    }

    public boolean sequenceEqual(List<Locale> input1, List<Locale> input2) {
        if (input1.size() != input2.size()) {
            return false;
        }
        for (int i = 0; i < input1.size(); i++) {
            if (!Objects.equals(input1.get(i).toLanguageTag(), input2.get(i).toLanguageTag())) {
                return false;
            }
        }
        return true;
    }

    public boolean seqIntEqual(List<Integer> input1, List<Integer> input2) {
        if (input1.size() != input2.size()) {
            return false;
        }
        for (int i = 0; i < input1.size(); i++) {
            if (!Objects.equals(input1.get(i), input2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
