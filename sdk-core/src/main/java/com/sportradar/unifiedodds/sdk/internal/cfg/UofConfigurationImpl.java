/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.utils.SdkHelper;
import io.opentelemetry.api.internal.StringUtils;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to specify various configuration parameters for a session to the Sportradar
 * system(s)
 */
@SuppressWarnings(
    {
        "MethodLength",
        "NPathComplexity",
        "ParameterNumber",
        "UnnecessaryParentheses",
        "checkstyle:ClassFanOutComplexity",
        "ClassDataAbstractionCoupling",
    }
)
public class UofConfigurationImpl implements UofConfiguration {

    private String accessToken;
    private Locale defaultLanguage;
    private final List<Locale> languages;
    private ExceptionHandlingStrategy exceptionHandlingStrategy;
    private Environment environment;
    private int nodeId;
    private BookmakerDetails bookmakerDetails;
    private final UofApiConfigurationImpl apiConfiguration;
    private final UofRabbitConfigurationImpl rabbitConfiguration;
    private final UofCacheConfigurationImpl cacheConfiguration;
    private final UofProducerConfigurationImpl producerConfiguration;
    private final UofAdditionalConfigurationImpl additionalConfiguration;
    private UofUsageConfigurationImpl usageConfiguration;
    private final Function<UofConfiguration, WhoAmIReader> buildWhoAmIReader;
    private final Function<UofConfiguration, ProducerDataProvider> buildProducerDataProvider;

    public UofConfigurationImpl(
        Function<UofConfiguration, WhoAmIReader> buildWhoAmIReader,
        Function<UofConfiguration, ProducerDataProvider> buildProducerDataProvider
    ) {
        Preconditions.checkNotNull(buildWhoAmIReader);
        Preconditions.checkNotNull(buildProducerDataProvider);

        this.buildWhoAmIReader = buildWhoAmIReader;
        this.buildProducerDataProvider = buildProducerDataProvider;
        bookmakerDetails = null;
        languages = new ArrayList<>();
        exceptionHandlingStrategy = ExceptionHandlingStrategy.Throw;
        apiConfiguration = new UofApiConfigurationImpl();
        rabbitConfiguration = new UofRabbitConfigurationImpl();
        cacheConfiguration = new UofCacheConfigurationImpl();
        producerConfiguration = new UofProducerConfigurationImpl();
        additionalConfiguration = new UofAdditionalConfigurationImpl();
        usageConfiguration = new UofUsageConfigurationImpl();
        environment = Environment.Integration;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public List<Locale> getLanguages() {
        return languages;
    }

    @Override
    public Integer getNodeId() {
        return nodeId;
    }

    @Override
    public ExceptionHandlingStrategy getExceptionHandlingStrategy() {
        return exceptionHandlingStrategy;
    }

    @Override
    public BookmakerDetails getBookmakerDetails() {
        return bookmakerDetails;
    }

    @Override
    public UofApiConfiguration getApi() {
        return apiConfiguration;
    }

    @Override
    public UofRabbitConfiguration getRabbit() {
        return rabbitConfiguration;
    }

    @Override
    public UofCacheConfiguration getCache() {
        return cacheConfiguration;
    }

    @Override
    public UofProducerConfiguration getProducer() {
        return producerConfiguration;
    }

    @Override
    public UofAdditionalConfiguration getAdditional() {
        return additionalConfiguration;
    }

    @Override
    public UofUsageConfiguration getUsage() {
        return usageConfiguration;
    }

    public void setAccessToken(String token) {
        if (!Strings.isNullOrEmpty(token)) {
            this.accessToken = token;
        }
    }

    public void setDefaultLanguage(Locale language) {
        Preconditions.checkNotNull(language);

        this.defaultLanguage = language;
    }

    public void setLanguages(List<Locale> languageList) {
        Preconditions.checkNotNull(languageList);

        if (!languageList.isEmpty()) {
            this.languages.clear();
            this.languages.addAll(languageList.stream().distinct().collect(Collectors.toList()));
        }
    }

    public void setExceptionHandlingStrategy(ExceptionHandlingStrategy handlingStrategy) {
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.exceptionHandlingStrategy = handlingStrategy;
    }

    public void setNodeId(int id) {
        this.nodeId = id;
    }

    public void setEnableUsageExport(boolean enableUsageExport) {
        usageConfiguration.setExportEnabled(enableUsageExport);
    }

    @SuppressWarnings("checkstyle:NestedIfDepth")
    public void updateSdkEnvironment(Environment env) {
        if (
            environment != env ||
            SdkHelper.stringIsNullOrEmpty(rabbitConfiguration.getHost()) ||
            SdkHelper.stringIsNullOrEmpty(apiConfiguration.getHost())
        ) {
            environment = env;

            if (environment != Environment.Custom) {
                rabbitConfiguration.setHost(EnvironmentManager.getMqHost(environment));
                apiConfiguration.setHost(EnvironmentManager.getApiHost(environment));
            } else {
                if (SdkHelper.stringIsNullOrEmpty(rabbitConfiguration.getHost())) {
                    rabbitConfiguration.setHost(EnvironmentManager.getMqHost(Environment.Integration));
                }

                if (SdkHelper.stringIsNullOrEmpty(apiConfiguration.getHost())) {
                    apiConfiguration.setHost(EnvironmentManager.getApiHost(Environment.Integration));
                }
            }

            checkAndUpdateConnectionSettings();
        }
    }

    public void checkAndUpdateConnectionSettings() {
        final int httpSubstringIndex = 7;
        final int httpsSubstringIndex = 8;
        if (apiConfiguration.getHost().toLowerCase().startsWith("http://")) { //remove leading http://
            apiConfiguration.setHost(apiConfiguration.getHost().substring(httpSubstringIndex));
            apiConfiguration.useSsl(false);
        } else if (apiConfiguration.getHost().toLowerCase().startsWith("https://")) { //remove leading https://
            apiConfiguration.setHost(apiConfiguration.getHost().substring(httpsSubstringIndex));
            apiConfiguration.useSsl(true);
        }

        if (environment != Environment.Custom) {
            int newPort = rabbitConfiguration.getUseSsl()
                ? EnvironmentManager.DEFAULT_MQ_HOST_PORT
                : EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1;
            rabbitConfiguration.setPort(newPort);
            rabbitConfiguration.setUsername(accessToken);
            rabbitConfiguration.setPassword(null);
        }
    }

    public String getApiHostAndPort() {
        final int defaultHttpPort = 80;
        String portString = apiConfiguration.getPort() == defaultHttpPort || apiConfiguration.getPort() == 0
            ? ""
            : ":" + apiConfiguration.getPort();
        return apiConfiguration.getHost() + portString;
    }

    public void validateMinimumSettings() {
        if (defaultLanguage == null && !languages.isEmpty()) {
            defaultLanguage = languages.get(0);
        }
        if (!languages.contains(defaultLanguage)) {
            languages.add(0, defaultLanguage);
        }
        if (defaultLanguage == null) {
            throw new InvalidParameterException("Missing default language");
        }

        if (SdkHelper.stringIsNullOrEmpty(accessToken)) {
            throw new InvalidParameterException("Missing access token");
        }
    }

    public void acquireBookmakerDetailsAndProducerData() {
        loadBookmakerDetails();
        loadAvailableProducers();
        updateUsageHost();
    }

    private void updateUsageHost() {
        UofUsageConfigurationImpl usageConfig = (UofUsageConfigurationImpl) getUsage();
        usageConfig.setHost(EnvironmentManager.getUsageHost(environment));
        usageConfiguration = usageConfig;
    }

    private void loadBookmakerDetails() {
        BookmakerDetails details = buildWhoAmIReader.apply(this).getBookmakerDetails();
        if (details == null) {
            throw new IllegalStateException("Missing bookmaker details");
        }
        bookmakerDetails = details;
        updateRabbitConfigFromBookmakerDetails();
    }

    private void loadAvailableProducers() {
        List<ProducerData> apiProducers = buildProducerDataProvider.apply(this).getAvailableProducers();
        if (apiProducers == null || apiProducers.isEmpty()) {
            throw new IllegalStateException("Missing available producers");
        }

        List<Producer> producerList = apiProducers
            .stream()
            .map(ProducerImpl::new)
            .collect(ImmutableList.toImmutableList());
        UofProducerConfigurationImpl producer = (UofProducerConfigurationImpl) getProducer();
        producer.setAvailableProducers(producerList);
    }

    private void updateRabbitConfigFromBookmakerDetails() {
        if (StringUtils.isNullOrEmpty(rabbitConfiguration.getUsername())) {
            rabbitConfiguration.setUsername(accessToken);
        }
        if (StringUtils.isNullOrEmpty(rabbitConfiguration.getVirtualHost())) {
            rabbitConfiguration.setVirtualHost(bookmakerDetails.getVirtualHost());
        }
    }

    @Override
    public String toString() {
        String obfuscatedToken = SdkHelper.obfuscate(accessToken);
        String languageList = Arrays.toString(languages.stream().map(Locale::getISO3Language).toArray());
        String bookmakerId = bookmakerDetails == null
            ? ""
            : String.valueOf(bookmakerDetails.getBookmakerId());

        return new StringJoiner(", ", "UofConfiguration{", "}")
            .add("accessToken=" + obfuscatedToken)
            .add("environment=" + environment)
            .add("nodeId=" + nodeId)
            .add("defaultLanguage=" + defaultLanguage)
            .add("languages=(" + languageList + ")")
            .add("exceptionHandlingStrategy=" + exceptionHandlingStrategy)
            .add("bookmakerId=" + bookmakerId)
            .add(apiConfiguration.toString())
            .add(rabbitConfiguration.toString())
            .add(cacheConfiguration.toString())
            .add(producerConfiguration.toString())
            .add(additionalConfiguration.toString())
            .add(usageConfiguration.toString())
            .toString();
    }
}
