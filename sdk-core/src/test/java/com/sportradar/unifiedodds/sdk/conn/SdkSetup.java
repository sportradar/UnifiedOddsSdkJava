/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.impl.Constants.UF_VIRTUALHOST;
import static java.util.Collections.emptyList;

import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.CustomConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.extended.UofSdkExt;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.domain.names.Languages;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SdkSetup {

    private final Credentials sdkCredentials;

    private final BaseUrl rabbitBaseUrl;

    private final BaseUrl sportsApiBaseUrl;

    private final int nodeId;
    private boolean configureSession;

    private int httpClientFastFailingTimeout;
    private int httpClientTimeout;

    private Optional<UofExtListener> rawMessagesListener = Optional.empty();

    private Optional<UofListener> messagesListener = Optional.empty();
    private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
    private boolean useDefaultExceptionHandlingStrategy;
    private Optional<Locale> defaultLanguage = Optional.empty();
    private Optional<List<Locale>> desiredLanguages = Optional.empty();

    public static SdkSetup with(
        Credentials sdkCredentials,
        BaseUrl rabbitBaseUrl,
        BaseUrl sportsApiBaseUrl,
        int nodeId
    ) {
        return new SdkSetup(sdkCredentials, rabbitBaseUrl, sportsApiBaseUrl, nodeId);
    }

    public SdkSetup with(ListenerCollectingRawMessages collectingRawMessagesListener) {
        this.rawMessagesListener = Optional.of(collectingRawMessagesListener);
        return this;
    }

    public SdkSetup with(ExceptionHandlingStrategy strategy) {
        this.exceptionHandlingStrategy = Optional.of(strategy);
        return this;
    }

    public SdkSetup with(ListenerCollectingMessages collectingMessagesListener) {
        this.messagesListener = Optional.of(collectingMessagesListener);
        return this;
    }

    public SdkSetup with1Session() {
        configureSession = true;
        return this;
    }

    public SdkSetup withDefaultLanguage(Locale language) {
        defaultLanguage = Optional.of(language);
        return this;
    }

    public SdkSetup withDesiredLanguages(Locale language, Locale... otherLocales) {
        desiredLanguages = Optional.of(Lists.asList(language, otherLocales));
        return this;
    }

    public SdkSetup withDefaultExceptionHandlingStrategy() {
        useDefaultExceptionHandlingStrategy = true;
        return this;
    }

    public SdkSetup withClientFastFailingTimeout(int fastFailingTimeout) {
        httpClientFastFailingTimeout = fastFailingTimeout;
        return this;
    }

    public SdkSetup withClientTimeout(int clientTimeout) {
        httpClientTimeout = clientTimeout;
        return this;
    }

    public UofSdk withoutFeed() throws InitException {
        CustomConfigurationBuilder configBuilder = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken(sdkCredentials.getUsername())
            .selectCustom()
            .setApiUseSsl(false)
            .setApiHost(sportsApiBaseUrl.get())
            .setDefaultLanguage(defaultLanguage.orElse(Languages.any()))
            .setDesiredLanguages(desiredLanguages.orElse(emptyList()))
            .setNodeId(nodeId)
            .enableUsageExport(false);

        setHttpClientFastFailingTimeout(configBuilder);
        setHttpClientTimeout(configBuilder);
        setExceptionHandlingStrategy(configBuilder);

        UofConfiguration config = configBuilder.build();

        return createSdk(config);
    }

    public UofSdk withOpenedFeed() throws InitException {
        CustomConfigurationBuilder configBuilder = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken(sdkCredentials.getUsername())
            .selectCustom()
            .setApiUseSsl(false)
            .setMessagingUsername(sdkCredentials.getUsername())
            .setMessagingPassword(sdkCredentials.getPassword())
            .setMessagingHost(rabbitBaseUrl.getHost())
            .setMessagingPort(rabbitBaseUrl.getPort())
            .setMessagingUseSsl(false)
            .setApiHost(sportsApiBaseUrl.get())
            .setDefaultLanguage(defaultLanguage.orElse(Languages.any()))
            .setDesiredLanguages(desiredLanguages.orElse(emptyList()))
            .setMessagingVirtualHost(UF_VIRTUALHOST)
            .setNodeId(nodeId)
            .enableUsageExport(false);

        setHttpClientFastFailingTimeout(configBuilder);
        setHttpClientTimeout(configBuilder);
        setExceptionHandlingStrategy(configBuilder);

        UofConfiguration config = configBuilder.build();
        UofSdk sdk = createSdk(config);

        if (configureSession) {
            configure1Session(sdk);
        }

        sdk.open();
        return sdk;
    }

    private void setHttpClientTimeout(CustomConfigurationBuilder configBuilder) {
        if (httpClientTimeout > 0) {
            configBuilder.setHttpClientTimeout(httpClientTimeout);
        }
    }

    private void setHttpClientFastFailingTimeout(CustomConfigurationBuilder configBuilder) {
        if (httpClientFastFailingTimeout > 0) {
            configBuilder.setHttpClientFastFailingTimeout(httpClientFastFailingTimeout);
        }
    }

    private void setExceptionHandlingStrategy(CustomConfigurationBuilder config) {
        if (!useDefaultExceptionHandlingStrategy) {
            config.setExceptionHandlingStrategy(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()));
        }
    }

    private UofSdk createSdk(UofConfiguration config) {
        if (rawMessagesListener.isPresent()) {
            return new UofSdkExt(new NoOpUofGlobalEventsListener(), config, rawMessagesListener.get());
        } else {
            return new UofSdk(new NoOpUofGlobalEventsListener(), config);
        }
    }

    public void configure1Session(final UofSdk sdk) {
        sdk
            .getSessionBuilder()
            .setListener(messagesListener.orElse(new NoOpUofListener()))
            .setMessageInterest(MessageInterest.AllMessages)
            .build();
    }
}
