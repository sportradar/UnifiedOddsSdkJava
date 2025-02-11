/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static java.time.Duration.of;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.RecoveryConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.internal.impl.RuntimeConfiguration;
import java.time.temporal.ChronoUnit;

/**
 * A base implementation of the {@link RecoveryConfigurationBuilder}
 */
@SuppressWarnings({ "LineLength", "MagicNumber", "VisibilityModifier" })
abstract class RecoveryConfigurationBuilderImpl<T>
    extends ConfigurationBuilderBaseImpl<T>
    implements RecoveryConfigurationBuilder<T> {

    RecoveryConfigurationBuilderImpl(
        UofConfigurationImpl uofConfiguration,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        super(uofConfiguration, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setInactivitySeconds(int inactivitySeconds) {
        UofProducerConfigurationImpl producerConfiguration = (UofProducerConfigurationImpl) configuration.getProducer();
        producerConfiguration.setInactivitySeconds(inactivitySeconds);
        return (T) this;
    }

    @Override
    public T setInactivitySecondsPrematch(int inactivitySeconds) {
        UofProducerConfigurationImpl producerConfiguration = (UofProducerConfigurationImpl) configuration.getProducer();
        producerConfiguration.setInactivitySecondsPrematch(inactivitySeconds);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setMaxRecoveryTime(int timeInSeconds) {
        UofProducerConfigurationImpl producerConfiguration = (UofProducerConfigurationImpl) configuration.getProducer();
        producerConfiguration.setMaxRecoveryTime(timeInSeconds);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setMinIntervalBetweenRecoveryRequests(int intervalSeconds) {
        UofProducerConfigurationImpl producerConfiguration = (UofProducerConfigurationImpl) configuration.getProducer();
        producerConfiguration.setMinIntervalBetweenRecoveryRequest(intervalSeconds);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientTimeout(int httpClientTimeout) {
        Preconditions.checkNotNull(httpClientTimeout);
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setHttpClientTimeout(httpClientTimeout);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientRecoveryTimeout(int httpClientRecoveryTimeout) {
        Preconditions.checkNotNull(httpClientRecoveryTimeout);
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setHttpClientRecoveryTimeout(httpClientRecoveryTimeout);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientFastFailingTimeout(int timeout) {
        Preconditions.checkNotNull(timeout);
        getApiConfig().setHttpClientFastFailingTimeout(timeout);
        RuntimeConfiguration.setFastHttpClientTimeout(of(timeout, ChronoUnit.SECONDS));
        return (T) this;
    }

    private UofApiConfigurationImpl getApiConfig() {
        return (UofApiConfigurationImpl) configuration.getApi();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientMaxConnTotal(int httpClientMaxConnTotal) {
        Preconditions.checkNotNull(httpClientMaxConnTotal);
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setHttpClientMaxConnTotal(httpClientMaxConnTotal);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientMaxConnPerRoute(int httpClientMaxConnPerRoute) {
        Preconditions.checkNotNull(httpClientMaxConnPerRoute);
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setHttpClientMaxConnPerRoute(httpClientMaxConnPerRoute);
        return (T) this;
    }

    @Override
    public T setSportEventCacheTimeout(int timeoutInHours) {
        UofCacheConfigurationImpl cacheConfiguration = (UofCacheConfigurationImpl) configuration.getCache();
        cacheConfiguration.setSportEventCacheTimeout(timeoutInHours);
        return (T) this;
    }

    @Override
    public T setSportEventStatusCacheTimeout(int timeoutInMinutes) {
        UofCacheConfigurationImpl cacheConfiguration = (UofCacheConfigurationImpl) configuration.getCache();
        cacheConfiguration.setSportEventStatusCacheTimeout(timeoutInMinutes);
        return (T) this;
    }

    @Override
    public T setProfileCacheTimeout(int timeoutInHours) {
        UofCacheConfigurationImpl cacheConfiguration = (UofCacheConfigurationImpl) configuration.getCache();
        cacheConfiguration.setProfileCacheTimeout(timeoutInHours);
        return (T) this;
    }

    @Override
    public T setVariantMarketDescriptionCacheTimeout(int timeoutInHours) {
        UofCacheConfigurationImpl cacheConfiguration = (UofCacheConfigurationImpl) configuration.getCache();
        cacheConfiguration.setVariantMarketDescriptionCacheTimeout(timeoutInHours);
        return (T) this;
    }

    @Override
    public T setIgnoreBetPalTimelineSportEventStatusCacheTimeout(int timeoutInHours) {
        UofCacheConfigurationImpl cacheConfiguration = (UofCacheConfigurationImpl) configuration.getCache();
        cacheConfiguration.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(timeoutInHours);
        return (T) this;
    }

    @Override
    public T setIgnoreBetPalTimelineSportEventStatus(boolean ignore) {
        getUofCacheConfiguration().setIgnoreBetPalTimelineSportEventStatus(ignore);
        RuntimeConfiguration.setIgnoreBetPalTimelineSportEventStatus(ignore);
        return (T) this;
    }

    private UofCacheConfigurationImpl getUofCacheConfiguration() {
        return (UofCacheConfigurationImpl) configuration.getCache();
    }

    @Override
    public T setRabbitConnectionTimeout(int timeoutInSeconds) {
        ((UofRabbitConfigurationImpl) configuration.getRabbit()).setConnectionTimeout(timeoutInSeconds);
        RuntimeConfiguration.setRabbitConnectionTimeout(timeoutInSeconds);

        return (T) this;
    }

    @Override
    public T setRabbitHeartbeat(int heartbeatInSeconds) {
        ((UofRabbitConfigurationImpl) configuration.getRabbit()).setHeartBeat(heartbeatInSeconds);
        RuntimeConfiguration.setRabbitHeartbeat(heartbeatInSeconds);
        return (T) this;
    }

    @Override
    public T setStatisticsInterval(int intervalInMinutes) {
        UofAdditionalConfigurationImpl additionalConfiguration = (UofAdditionalConfigurationImpl) configuration.getAdditional();
        additionalConfiguration.setStatisticsInterval(intervalInMinutes);
        return (T) this;
    }

    @Override
    public T omitMarketMappings(boolean omit) {
        UofAdditionalConfigurationImpl additionalConfiguration = (UofAdditionalConfigurationImpl) configuration.getAdditional();
        additionalConfiguration.setOmitMarketMappings(omit);
        return (T) this;
    }

    @Override
    public T enableUsageExport(boolean enable) {
        UofUsageConfigurationImpl usageConfiguration = (UofUsageConfigurationImpl) configuration.getUsage();
        usageConfiguration.setExportEnabled(enable);
        return (T) this;
    }
}
