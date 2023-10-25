/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base class for configuration builders
 */
@SuppressWarnings(
    { "DeclarationOrder", "ExplicitInitialization", "HiddenField", "LineLength", "VisibilityModifier" }
)
abstract class ConfigurationBuilderBaseImpl<T> implements ConfigurationBuilderBase<T> {

    final SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader;
    final SdkConfigurationYamlReader sdkConfigurationYamlReader;
    protected final UofConfigurationImpl configuration;

    ConfigurationBuilderBaseImpl(
        UofConfigurationImpl config,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);
        Preconditions.checkNotNull(sdkConfigurationYamlReader);

        this.configuration = config;
        this.sdkConfigurationYamlReader = sdkConfigurationYamlReader;
        this.sdkConfigurationPropertiesReader = sdkConfigurationPropertiesReader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T loadConfigFromSdkProperties() {
        updateFieldsFromConfig(sdkConfigurationPropertiesReader);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T loadConfigFromApplicationYml() {
        updateFieldsFromConfig(sdkConfigurationYamlReader);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setDefaultLanguage(Locale defaultLanguage) {
        Preconditions.checkNotNull(defaultLanguage);
        configuration.setDefaultLanguage(defaultLanguage);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setDesiredLanguages(List<Locale> desiredLanguages) {
        configuration.getLanguages().clear();
        if (desiredLanguages != null) {
            configuration
                .getLanguages()
                .addAll(desiredLanguages.stream().distinct().collect(Collectors.toList()));
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setExceptionHandlingStrategy(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        configuration.setExceptionHandlingStrategy(exceptionHandlingStrategy);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setNodeId(int nodeId) {
        configuration.setNodeId(nodeId);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setDisabledProducers(List<Integer> producerIds) {
        UofProducerConfigurationImpl producerConfiguration = (UofProducerConfigurationImpl) configuration.getProducer();
        producerConfiguration.getDisabledProducers().clear();
        if (producerIds != null) {
            producerConfiguration.setDisabledProducers(producerIds);
        }
        return (T) this;
    }

    protected void updateFieldsFromConfig(SdkConfigurationReader sdkConfigurationReader) {
        Preconditions.checkNotNull(sdkConfigurationReader);

        setDesiredLanguages(sdkConfigurationReader.readDesiredLanguages());
        if (sdkConfigurationReader.readDefaultLanguage().isPresent()) {
            sdkConfigurationReader.readDefaultLanguage().ifPresent(this::setDefaultLanguage);
            if (!configuration.getLanguages().contains(configuration.getDefaultLanguage())) {
                configuration.getLanguages().add(0, configuration.getDefaultLanguage());
            }
        }
        configuration.validateMinimumSettings();

        setDisabledProducers(sdkConfigurationReader.readDisabledProducers());
        sdkConfigurationReader.readNodeId().ifPresent(this::setNodeId);
        sdkConfigurationReader.readExceptionHandlingStrategy().ifPresent(this::setExceptionHandlingStrategy);
    }

    protected void updateCustomFieldsFromConfig(SdkConfigurationReader sdkConfigurationReader) {
        Preconditions.checkNotNull(sdkConfigurationReader);

        configuration.updateSdkEnvironment(sdkConfigurationReader.readEnvironment());

        UofRabbitConfigurationImpl rabbitConfig = (UofRabbitConfigurationImpl) configuration.getRabbit();
        int port = sdkConfigurationReader.readMessagingPort().orElse(0);
        if (port > 0) {
            rabbitConfig.setPort(port);
        }
        sdkConfigurationReader.readMessagingHost().ifPresent(rabbitConfig::setHost);
        sdkConfigurationReader.readMessagingUseSsl().ifPresent(rabbitConfig::useSsl);
        sdkConfigurationReader.readMessagingUsername().ifPresent(rabbitConfig::setUsername);
        sdkConfigurationReader.readMessagingPassword().ifPresent(rabbitConfig::setPassword);
        sdkConfigurationReader.readMessagingVirtualHost().ifPresent(rabbitConfig::setVirtualHost);

        UofApiConfigurationImpl apiConfig = (UofApiConfigurationImpl) configuration.getApi();
        sdkConfigurationReader.readApiHost().ifPresent(apiConfig::setHost);
        sdkConfigurationReader.readApiUseSsl().ifPresent(apiConfig::useSsl);
        sdkConfigurationReader.readApiPort().ifPresent(apiConfig::setPort);

        configuration.checkAndUpdateConnectionSettings();
    }
}
