/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("ClassFanOutComplexity")
public class UofConfigurationStub implements UofConfiguration {

    private UofCacheConfiguration cache = new UofCacheConfigurationStub();
    private UofApiConfigurationStub apiConfig = new UofApiConfigurationStub();
    private UofRabbitConfiguration rabbit = new UofRabbitConfigurationStub();
    private final UofProducerConfiguration producerConfig = new UofProducerConfigurationStub();
    private Environment environment;
    private Integer nodeId;
    private String accessToken;
    private Locale defaultLanguage;
    private List<Locale> languages;
    private ExceptionHandlingStrategy exceptionHandlingStrategy;
    private BookmakerDetails bookmakerDetails;
    private UofAdditionalConfiguration additional;
    private UofUsageConfiguration usage;
    private UofPrivateKeyJwtAuthenticationStub clientAuthentication;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    public void setDefaultLanguage(Locale defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public List<Locale> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Locale> languages) {
        this.languages = languages;
    }

    @Override
    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public ExceptionHandlingStrategy getExceptionHandlingStrategy() {
        return exceptionHandlingStrategy;
    }

    public void setExceptionHandlingStrategy(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    @Override
    public BookmakerDetails getBookmakerDetails() {
        return bookmakerDetails;
    }

    public void setBookmakerDetails(BookmakerDetails bookmakerDetails) {
        this.bookmakerDetails = bookmakerDetails;
    }

    @SuppressWarnings("HiddenField")
    public void setApi(UofApiConfigurationStub apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Override
    public UofApiConfiguration getApi() {
        return apiConfig;
    }

    public void setRabbit(UofRabbitConfiguration rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public UofRabbitConfiguration getRabbit() {
        return rabbit;
    }

    @Override
    public UofCacheConfiguration getCache() {
        return cache;
    }

    public void setCache(UofCacheConfiguration cache) {
        this.cache = cache;
    }

    @Override
    public UofProducerConfiguration getProducer() {
        return producerConfig;
    }

    @Override
    public UofAdditionalConfiguration getAdditional() {
        return additional;
    }

    public void setAdditional(UofAdditionalConfiguration additional) {
        this.additional = additional;
    }

    @Override
    public UofUsageConfiguration getUsage() {
        return usage;
    }

    public void setUsage(UofUsageConfiguration usage) {
        this.usage = usage;
    }

    @Override
    public UofClientAuthentication.PrivateKeyJwt getClientAuthentication() {
        return clientAuthentication;
    }

    public void setClientAuthentication(UofPrivateKeyJwtAuthenticationStub clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public UofPrivateKeyJwtAuthenticationStub getClientAuthenticationStub() {
        return clientAuthentication;
    }

    public UofApiConfigurationStub getApiStub() {
        return apiConfig;
    }
}
