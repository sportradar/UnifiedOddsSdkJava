/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.readerProvidingBookmaker;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.entities.BookmakerDetailsImpl;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class UofConfigurationTests {

    private final Function<UofConfiguration, WhoAmIReader> anyBookmaker = c -> emptyBookmakerDetailsReader();
    private final Function<UofConfiguration, ProducerDataProvider> anyProducers = c ->
        providerOfSingleEmptyProducer();
    private final UofConfigurationImpl config = new UofConfigurationImpl(anyBookmaker, anyProducers);

    @Test(expected = NullPointerException.class)
    public void requiresBookmakerDetailsProviderBuilder() {
        new UofConfigurationImpl(null, anyProducers);
    }

    @Test(expected = NullPointerException.class)
    public void requiresProducersProviderBuilder() {
        new UofConfigurationImpl(anyBookmaker, null);
    }

    @Test
    public void configLimitIsCreatedTest() {
        Assert.assertNotNull(ConfigLimit.class);
    }

    @Test
    public void defaultImplementationUsesDefaultValues() {
        Assert.assertNull(config.getAccessToken());
        Assert.assertNull(config.getDefaultLanguage());
        Assert.assertNotNull(config.getLanguages());
        Assert.assertEquals(0, config.getLanguages().size());
        Assert.assertEquals(0, config.getNodeId().intValue());
        Assert.assertEquals(ExceptionHandlingStrategy.Catch, config.getExceptionHandlingStrategy());
        Assert.assertEquals(Environment.Integration, config.getEnvironment());
        Assert.assertNull(config.getBookmakerDetails());
        Assert.assertNotNull(config.getApi());
        Assert.assertNotNull(config.getRabbit());
        Assert.assertNotNull(config.getCache());
        Assert.assertNotNull(config.getProducer());
        Assert.assertNotNull(config.getAdditional());
    }

    @Test
    public void setAccessToken_ValidValue() {
        final String newValue = "customToken";
        config.setAccessToken(newValue);

        Assert.assertEquals(newValue, config.getAccessToken());
    }

    @Test
    public void setAccessToken_EmptyValue() {
        final String newValue = "";
        config.setAccessToken(newValue);

        Assert.assertNull(config.getAccessToken());
    }

    @Test
    public void setAccessToken_NullValue() {
        config.setAccessToken(null);

        Assert.assertNull(config.getAccessToken());
    }

    @Test
    public void setAccessToken_NullValueDoesNotReplaceExisting() {
        final String newValue = "custom-token";
        config.setAccessToken(newValue);
        Assert.assertEquals(newValue, config.getAccessToken());

        config.setAccessToken(null);
        Assert.assertEquals(newValue, config.getAccessToken());
    }

    @Test
    public void setDefaultLanguage_ValidValue() {
        final Locale newValue = Locale.FRENCH;
        config.setDefaultLanguage(newValue);

        Assert.assertEquals(newValue, config.getDefaultLanguage());
        Assert.assertEquals(0, config.getLanguages().size());
    }

    @Test
    public void setDefaultLanguage_LastValue() {
        final Locale newValue = Locale.FRENCH;
        config.setDefaultLanguage(Locale.CHINESE);
        config.setDefaultLanguage(newValue);

        Assert.assertEquals(newValue, config.getDefaultLanguage());
        Assert.assertEquals(0, config.getLanguages().size());
    }

    @Test
    public void setLanguages() {
        final List<Locale> newValue = Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN);
        config.setLanguages(newValue);

        Assert.assertNotNull(config.getLanguages());
        Assert.assertEquals(newValue.size(), config.getLanguages().size());
    }

    @Test
    public void setLanguagesSavesOnlyUniqueValues() {
        final List<Locale> newValue = Arrays.asList(Locale.ENGLISH, Locale.ENGLISH, Locale.ENGLISH);
        config.setLanguages(newValue);

        Assert.assertEquals(1, config.getLanguages().size());
    }

    @Test
    public void setLanguagesSavesOnlyUniqueValuesWhenCalledMultipleTimes() {
        final List<Locale> newValue = Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN);
        config.setLanguages(newValue);
        config.setLanguages(newValue);

        Assert.assertEquals(newValue.size(), config.getLanguages().size());
    }

    @Test
    public void setLanguagesCanNotRemovePreviouslySavedValues() {
        final List<Locale> newValue = Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN);
        config.setLanguages(newValue);

        Assert.assertEquals(newValue.size(), config.getLanguages().size());

        config.setLanguages(new ArrayList<>());

        Assert.assertEquals(newValue.size(), config.getLanguages().size());
    }

    @Test
    public void setLanguagesOverridesNewValues() {
        final List<Locale> newValues = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
        final List<Locale> newValues2 = Arrays.asList(Locale.FRENCH, Locale.GERMAN, Locale.ITALY);
        config.setLanguages(newValues);

        Assert.assertEquals(newValues.size(), config.getLanguages().size());

        config.setLanguages(newValues2);

        Assert.assertEquals(newValues2.size(), config.getLanguages().size());
    }

    @Test
    public void validateMinimumSettings_MissingDefaultLanguage() {
        final String accessToken = "myAccessToken";
        config.setAccessToken(accessToken);

        final List<Locale> newValues = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
        config.setLanguages(newValues);
        Assert.assertEquals(newValues.size(), config.getLanguages().size());
        Assert.assertNull(config.getDefaultLanguage());

        config.validateMinimumSettings();

        Assert.assertEquals(newValues.size(), config.getLanguages().size());
        Assert.assertNotNull(config.getDefaultLanguage());
        Assert.assertEquals(config.getDefaultLanguage(), config.getLanguages().stream().findFirst().get());
    }

    @Test
    public void validateMinimumSettings_MissingLanguages() {
        final String accessToken = "myAccessToken";
        config.setAccessToken(accessToken);

        config.setDefaultLanguage(Locale.ENGLISH);
        Assert.assertEquals(Locale.ENGLISH, config.getDefaultLanguage());
        Assert.assertEquals(0, config.getLanguages().size());

        config.validateMinimumSettings();

        Assert.assertEquals(Locale.ENGLISH, config.getDefaultLanguage());
        Assert.assertEquals(1, config.getLanguages().size());
        Assert.assertEquals(config.getDefaultLanguage(), config.getLanguages().stream().findFirst().get());
    }

    @Test
    public void validateMinimumSettings_InsertingDefaultLanguage() {
        final String accessToken = "myAccessToken";
        config.setAccessToken(accessToken);

        final List<Locale> newValues = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
        final Locale defaultLocale = Locale.FRENCH;
        config.setLanguages(newValues);
        config.setDefaultLanguage(defaultLocale);
        Assert.assertEquals(defaultLocale, config.getDefaultLanguage());
        Assert.assertEquals(newValues.size(), config.getLanguages().size());

        config.validateMinimumSettings();

        Assert.assertEquals(defaultLocale, config.getDefaultLanguage());
        Assert.assertEquals(newValues.size() + 1, config.getLanguages().size());
        Assert.assertEquals(config.getDefaultLanguage(), config.getLanguages().stream().findFirst().get());
    }

    @Test(expected = InvalidParameterException.class)
    public void validateMinimumSettings_MissingAccessToken() {
        final List<Locale> newValues = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
        config.setLanguages(newValues);
        Assert.assertNull(config.getDefaultLanguage());
        Assert.assertEquals(newValues.size(), config.getLanguages().size());

        config.validateMinimumSettings();
    }

    @Test(expected = InvalidParameterException.class)
    public void validateMinimumSettings_MissingLanguagesAndDefaultLanguage() {
        Assert.assertNull(config.getDefaultLanguage());
        Assert.assertEquals(0, config.getLanguages().size());

        config.validateMinimumSettings();
    }

    @Test
    public void setNodeId_ValidValue() {
        final int newValue = 25;
        config.setNodeId(newValue);

        Assert.assertEquals(newValue, config.getNodeId().intValue());
    }

    @Test
    public void setNodeId_CanAcceptNegative() {
        final int newValue = -25;
        config.setNodeId(newValue);

        Assert.assertEquals(newValue, config.getNodeId().intValue());
    }

    @Test
    public void setNodeId_CanAcceptZero() {
        final int newValue = 0;
        config.setNodeId(newValue);

        Assert.assertEquals(newValue, config.getNodeId().intValue());
    }

    @Test
    public void setExceptionHandlingStrategy_ValidValue() {
        final ExceptionHandlingStrategy newValue = ExceptionHandlingStrategy.Throw;
        config.setExceptionHandlingStrategy(newValue);

        Assert.assertEquals(newValue, config.getExceptionHandlingStrategy());
    }

    @Test
    public void setEnvironment_ValidValue() {
        final Environment newValue = Environment.GlobalProduction;
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(newValue, config.getEnvironment());
    }

    @Test
    public void setEnvironmentWithMissingApiHost_ValidValue() {
        final Environment newValue = Environment.Integration;
        ((UofRabbitConfigurationImpl) config.getRabbit()).setHost("customhost");
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(newValue, config.getEnvironment());
        Assert.assertEquals(EnvironmentManager.getApiHost(newValue), config.getApi().getHost());
        Assert.assertEquals(EnvironmentManager.getMqHost(newValue), config.getRabbit().getHost());
    }

    @Test
    public void setEnvironmentWithMissingRabbitHost_ValidValue() {
        final Environment newValue = Environment.Integration;
        ((UofApiConfigurationImpl) config.getApi()).setHost("custom-host");
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(newValue, config.getEnvironment());
        Assert.assertEquals(EnvironmentManager.getApiHost(newValue), config.getApi().getHost());
        Assert.assertEquals(EnvironmentManager.getMqHost(newValue), config.getRabbit().getHost());
    }

    @Test
    public void setCustomEnvironmentWithMissingApiHost_ValidValue() {
        final String customHost = "mycustomhost";
        final Environment newValue = Environment.Custom;
        ((UofRabbitConfigurationImpl) config.getRabbit()).setHost(customHost);
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(newValue, config.getEnvironment());
        Assert.assertEquals(
            EnvironmentManager.getApiHost(Environment.Integration),
            config.getApi().getHost()
        );
        Assert.assertEquals(customHost, config.getRabbit().getHost());
    }

    @Test
    public void setCustomEnvironmentWithMissingRabbitHost_ValidValue() {
        final String customHost = "custom-host.com";
        final Environment newValue = Environment.Custom;
        ((UofApiConfigurationImpl) config.getApi()).setHost(customHost);
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(newValue, config.getEnvironment());
        Assert.assertEquals(customHost, config.getApi().getHost());
        Assert.assertEquals(
            EnvironmentManager.getMqHost(Environment.Integration),
            config.getRabbit().getHost()
        );
    }

    @Test
    public void getApiHostWithPort() {
        final int newPort = 555;
        final Environment newValue = Environment.GlobalProduction;
        config.updateSdkEnvironment(newValue);
        ((UofApiConfigurationImpl) config.getApi()).setPort(newPort);

        String hostWithPort = EnvironmentManager.getApiHost(newValue) + ":" + newPort;
        Assert.assertEquals(hostWithPort, config.getApiHostAndPort());
    }

    @Test
    public void getApiWithPortWhenPortZero() {
        final int newPort = 0;
        final Environment newValue = Environment.GlobalProduction;
        config.updateSdkEnvironment(newValue);
        ((UofApiConfigurationImpl) config.getApi()).setPort(newPort);

        String hostWithPort = EnvironmentManager.getApiHost(newValue);
        Assert.assertEquals(hostWithPort, config.getApiHostAndPort());
    }

    @Test
    public void getApiWithPortWhenPortDefault() {
        final int newPort = 80;
        final Environment newValue = Environment.GlobalProduction;
        config.updateSdkEnvironment(newValue);
        ((UofApiConfigurationImpl) config.getApi()).setPort(newPort);

        String hostWithPort = EnvironmentManager.getApiHost(newValue);
        Assert.assertEquals(hostWithPort, config.getApiHostAndPort());
    }

    @Test
    public void useCorrectRabbitPortWhenChangingEnvironmentWhenUseSslFalse() {
        final Environment newValue = Environment.GlobalProduction;
        ((UofRabbitConfigurationImpl) config.getRabbit()).useSsl(false);
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1, config.getRabbit().getPort());
    }

    @Test
    public void useCorrectRabbitPortWhenChangingEnvironmentWhenUseSslTrue() {
        final Environment newValue = Environment.GlobalProduction;
        ((UofRabbitConfigurationImpl) config.getRabbit()).useSsl(true);
        config.updateSdkEnvironment(newValue);

        Assert.assertEquals(EnvironmentManager.DEFAULT_MQ_HOST_PORT, config.getRabbit().getPort());
    }

    @Test
    public void toStringHasAllTheValues() {
        Assert.assertNotNull(config);

        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("UofConfiguration"));
        Assert.assertTrue(summary.contains("accessToken="));
        Assert.assertTrue(summary.contains("environment="));
        Assert.assertTrue(summary.contains("nodeId="));
        Assert.assertTrue(summary.contains("defaultLanguage="));
        Assert.assertTrue(summary.contains("languages=("));
        Assert.assertTrue(summary.contains("exceptionHandlingStrategy="));
        Assert.assertTrue(summary.contains("connectionTimeout"));
        Assert.assertTrue(summary.contains("ApiConfiguration"));
        Assert.assertTrue(summary.contains("RabbitConfiguration"));
        Assert.assertTrue(summary.contains("CacheConfiguration"));
        Assert.assertTrue(summary.contains("ProducerConfiguration"));
        Assert.assertTrue(summary.contains("AdditionalConfiguration"));
    }

    @Test
    public void toStringWithoutBookmakerDetails() {
        Assert.assertNotNull(config);

        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("bookmakerId=,"));
    }

    @Test
    public void toStringWithBookmakerDetails() {
        final String vhost = "/customVhost";
        final BookmakerDetails bookmakerDetails = mock(BookmakerDetailsImpl.class);
        when(bookmakerDetails.getBookmakerId()).thenReturn(2);
        when(bookmakerDetails.getExpireAt()).thenReturn(new Date());
        when(bookmakerDetails.getVirtualHost()).thenReturn(vhost);
        when(bookmakerDetails.getServerTimeDifference()).thenReturn(Duration.ofMillis(2));

        val configuration = new UofConfigurationImpl(
            anyConfig -> readerProvidingBookmaker(bookmakerDetails),
            anyProducers
        );
        configuration.acquireBookmakerDetailsAndProducerData();
        val configSummary = configuration.toString();

        Assert.assertNotNull(configSummary);
        assertThat(configSummary).contains("bookmakerId=2,");
    }

    @Test
    public void setCustomApiHostWithHttp_RemovesPrefix() {
        final String host = "custom-domain.com";
        final String fullHost = "http://" + host;
        final int port = 81;
        final Environment environment = Environment.Custom;
        UofApiConfigurationImpl apiConfig = (UofApiConfigurationImpl) config.getApi();
        apiConfig.setHost(fullHost);
        apiConfig.setPort(port);
        apiConfig.useSsl(true);

        config.updateSdkEnvironment(environment);

        Assert.assertEquals(environment, config.getEnvironment());
        Assert.assertNotNull(config.getApi().getHost());
        Assert.assertEquals(host, config.getApi().getHost());
        Assert.assertEquals(port, config.getApi().getPort());
        Assert.assertFalse(config.getApi().getUseSsl());
        Assert.assertEquals(host + ":" + port, config.getApiHostAndPort());
    }

    @Test
    public void setCustomApiHostWithHttps_RemovesPrefix() {
        final String host = "custom-domain.com";
        final String fullHost = "https://" + host;
        final int port = 81;
        final Environment environment = Environment.Custom;
        UofApiConfigurationImpl apiConfig = (UofApiConfigurationImpl) config.getApi();
        apiConfig.setHost(fullHost);
        apiConfig.setPort(port);
        apiConfig.useSsl(true);

        config.updateSdkEnvironment(environment);

        Assert.assertEquals(environment, config.getEnvironment());
        Assert.assertNotNull(config.getApi().getHost());
        Assert.assertEquals(host, config.getApi().getHost());
        Assert.assertEquals(port, config.getApi().getPort());
        Assert.assertTrue(config.getApi().getUseSsl());
        Assert.assertEquals(host + ":" + port, config.getApiHostAndPort());
    }

    @Test
    public void setBookmakerDetails() {
        final String vhost = "/custom-vhost";
        final BookmakerDetails bookmakerDetails = mock(BookmakerDetailsImpl.class);
        when(bookmakerDetails.getBookmakerId()).thenReturn(1);
        when(bookmakerDetails.getExpireAt()).thenReturn(new Date());
        when(bookmakerDetails.getVirtualHost()).thenReturn(vhost);
        when(bookmakerDetails.getServerTimeDifference()).thenReturn(Duration.ofMillis(1));

        val configuration = new UofConfigurationImpl(
            anyConfig -> readerProvidingBookmaker(bookmakerDetails),
            anyProducers
        );
        configuration.acquireBookmakerDetailsAndProducerData();

        Assert.assertEquals(1, configuration.getBookmakerDetails().getBookmakerId());
        Assert.assertEquals(vhost, configuration.getBookmakerDetails().getVirtualHost());
        Assert.assertEquals(
            Duration.ofMillis(1),
            configuration.getBookmakerDetails().getServerTimeDifference()
        );
    }

    @Test
    public void setBookmakerDetailsDoesNotOverrideVirtualHostIfPreset() {
        final String originalVirtualHost = "/originalVirtualHost";
        final String bookmakerDetailsVirtualHost = "/custom-vhost";
        final BookmakerDetails bookmakerDetails = mock(BookmakerDetailsImpl.class);
        when(bookmakerDetails.getBookmakerId()).thenReturn(1);
        when(bookmakerDetails.getExpireAt()).thenReturn(new Date());
        when(bookmakerDetails.getVirtualHost()).thenReturn(bookmakerDetailsVirtualHost);
        when(bookmakerDetails.getServerTimeDifference()).thenReturn(Duration.ofMillis(1));

        val configuration = new UofConfigurationImpl(
            anyConfig -> readerProvidingBookmaker(bookmakerDetails),
            anyProducers
        );
        ((UofRabbitConfigurationImpl) configuration.getRabbit()).setVirtualHost(originalVirtualHost);
        configuration.acquireBookmakerDetailsAndProducerData();

        Assert.assertEquals(1, configuration.getBookmakerDetails().getBookmakerId());
        Assert.assertEquals(
            bookmakerDetailsVirtualHost,
            configuration.getBookmakerDetails().getVirtualHost()
        );
        Assert.assertEquals(originalVirtualHost, configuration.getRabbit().getVirtualHost());
    }

    @Test(expected = IllegalStateException.class)
    public void settingNullBookmakerDetailsThrows() {
        val configuration = new UofConfigurationImpl(
            anyConfig -> readerProvidingBookmaker(null),
            anyProducers
        );
        configuration.acquireBookmakerDetailsAndProducerData();
    }
}
