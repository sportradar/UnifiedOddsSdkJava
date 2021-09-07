/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;

import java.util.*;

/**
 * The base class for configuration builders
 */
abstract class ConfigurationBuilderBaseImpl<T> implements ConfigurationBuilderBase<T> {
    final SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader;
    final SDKConfigurationYamlReader sdkConfigurationYamlReader;

    private final Set<Locale> supportedLocales = new LinkedHashSet<>();
    final Set<Integer> disabledProducers = new HashSet<>();
    Locale defaultLocale = null;
    ExceptionHandlingStrategy exceptionHandlingStrategy = ExceptionHandlingStrategy.Catch;
    Integer nodeId = null;
    Integer httpClientTimeout = null;
    Integer httpClientMaxConnTotal = null;
    Integer httpClientMaxConnPerRoute = null;
    Integer recoveryHttpClientTimeout = null;
    Integer recoveryHttpClientMaxConnTotal = null;
    Integer recoveryHttpClientMaxConnPerRoute = null;

    ConfigurationBuilderBaseImpl(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
        this.sdkConfigurationYamlReader = sdkConfigurationYamlReader;
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);

        this.sdkConfigurationPropertiesReader = sdkConfigurationPropertiesReader;
    }

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden.
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T loadConfigFromSdkProperties() {
        loadConfigFrom(sdkConfigurationPropertiesReader);
        return (T) this;
    }

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden.
     * <p>
     * The YAML file should be named "application.yml" and localed in the application resources folder
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T loadConfigFromApplicationYml() {
        loadConfigFrom(sdkConfigurationYamlReader);
        return (T) this;
    }

    /**
     * Sets the default language for the translatable data
     *
     * @param defaultLocale a {@link Locale} which will be used as default
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setDefaultLocale(Locale defaultLocale) {
        Preconditions.checkNotNull(defaultLocale);

        this.defaultLocale = defaultLocale;

        return (T) this;
    }

    /**
     * Sets the languages in which translatable data is available
     *
     * @param supportedLocales a {@link List} of {@link Locale}s in which translatable data should be available
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setDesiredLocales(List<Locale> supportedLocales) {
        Preconditions.checkNotNull(supportedLocales);

        this.supportedLocales.clear();
        this.supportedLocales.addAll(supportedLocales);

        return (T) this;
    }

    /**
     * Sets the value specifying how exceptions thrown in the SDK are handled
     *
     * @param exceptionHandlingStrategy a {@link ExceptionHandlingStrategy} enum specifying how exceptions thrown in the SDK are handled
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setExceptionHandlingStrategy(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        return (T) this;
    }

    /**
     * Sets the node id used to separate between SDK instances associated with the same account
     *
     * @param nodeId the node id to be set
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setSdkNodeId(int nodeId) {

        this.nodeId = nodeId;
        return (T) this;
    }

    /**
     * Specifies the producers which should be disabled (i.e. no recovery, messages get discarded, ...)
     *
     * @param producerIds the list of producer ids specifying the producers which should be disabled
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setDisabledProducers(List<Integer> producerIds) {
        Preconditions.checkNotNull(producerIds);

        disabledProducers.addAll(producerIds);
        return (T) this;
    }

    /**
     * Returns a full list of all supported locales, including the default one
     *
     * @return a full list of all supported locales, including the default one
     */
    Set<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    /**
     * Sets the timeout which should be used on HTTP requests(seconds)
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientTimeout(Integer httpClientTimeout) {
        Preconditions.checkNotNull(httpClientTimeout);

        this.httpClientTimeout = httpClientTimeout;
        return (T) this;
    }

    /**
     * Sets connection pool size for http client.
     * Should be set to low value to avoid resource overuse.
     * Default: 20
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientMaxConnTotal(Integer httpClientMaxConnTotal) {
        Preconditions.checkNotNull(httpClientMaxConnTotal);

        this.httpClientMaxConnTotal = httpClientMaxConnTotal;
        return (T) this;
    }

    /**
     * Sets maximum number of concurrent connections per route for http client.
     * Should be set to low value to avoid resource overuse.
     * Default: 15
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setHttpClientMaxConnPerRoute(Integer httpClientMaxConnPerRoute) {
        Preconditions.checkNotNull(httpClientMaxConnPerRoute);

        this.httpClientMaxConnPerRoute = httpClientMaxConnPerRoute;
        return (T) this;
    }

    /**
     * Sets the timeout which should be used on HTTP requests for recovery endpoints(seconds)
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setRecoveryHttpClientTimeout(Integer recoveryHttpClientTimeout) {
        Preconditions.checkNotNull(recoveryHttpClientTimeout);

        this.recoveryHttpClientTimeout = recoveryHttpClientTimeout;
        return (T) this;
    }

    /**
     * Sets connection pool size for recovery http client.
     * Should be set to low value to avoid resource overuse.
     * Default: 20
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setRecoveryHttpClientMaxConnTotal(Integer recoveryHttpClientMaxConnTotal) {
        Preconditions.checkNotNull(recoveryHttpClientMaxConnTotal);

        this.recoveryHttpClientMaxConnTotal = recoveryHttpClientMaxConnTotal;
        return (T) this;
    }

    /**
     * Sets maximum number of concurrent connections per route for recovery http client
     * Should be set to low value to avoid resource overuse.
     * Default: 15
     *
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setRecoveryHttpClientMaxConnPerRoute(Integer recoveryHttpClientMaxConnPerRoute) {
        Preconditions.checkNotNull(recoveryHttpClientMaxConnPerRoute);

        this.recoveryHttpClientMaxConnPerRoute = recoveryHttpClientMaxConnPerRoute;
        return (T) this;
    }

    /**
     * Loads the properties that are relevant to the builder from the provided {@link SDKConfigurationReader}
     *
     * @param sdkConfigurationReader the reader from which the properties should be red
     */
    private void loadConfigFrom(SDKConfigurationReader sdkConfigurationReader) {
        Preconditions.checkNotNull(sdkConfigurationReader);

        sdkConfigurationReader.readDefaultLocale().ifPresent(this::setDefaultLocale);
        sdkConfigurationReader.readExceptionHandlingStrategy().ifPresent(this::setExceptionHandlingStrategy);
        sdkConfigurationReader.readSdkNodeId().ifPresent(this::setSdkNodeId);
        setDesiredLocales(sdkConfigurationReader.readDesiredLocales());
        setDisabledProducers(sdkConfigurationReader.readDisabledProducers());
        sdkConfigurationReader.readHttpClientTimeout().ifPresent(this::setHttpClientTimeout);
        sdkConfigurationReader.readHttpClientMaxConnTotal().ifPresent(this::setHttpClientMaxConnTotal);
        sdkConfigurationReader.readHttpClientMaxConnPerRoute().ifPresent(this::setHttpClientMaxConnPerRoute);
        sdkConfigurationReader.readHttpClientTimeout().ifPresent(this::setRecoveryHttpClientTimeout);
        sdkConfigurationReader.readRecoveryHttpClientMaxConnTotal().ifPresent(this::setRecoveryHttpClientMaxConnTotal);
        sdkConfigurationReader.readRecoveryHttpClientMaxConnPerRoute().ifPresent(this::setRecoveryHttpClientMaxConnPerRoute);
    }
}
