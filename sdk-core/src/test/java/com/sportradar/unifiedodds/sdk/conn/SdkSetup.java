/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.impl.Constants.UF_VIRTUALHOST;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.extended.UofSdkExt;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SdkSetup {

    private final Credentials sdkCredentials;

    private final BaseUrl rabbitBaseUrl;

    private final BaseUrl sportsApiBaseUrl;

    private final int nodeId;
    private boolean configureSession;

    private Optional<UofExtListener> rawMessagesListener = Optional.empty();

    private Optional<UofListener> messagesListener = Optional.empty();
    private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
    private Optional<Locale> defaultLanguage = Optional.empty();

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

    public UofSdk withoutFeed() throws InitException {
        UofConfiguration config = UofSdk
            .getUofConfigurationBuilder()
            .setAccessToken(sdkCredentials.getUsername())
            .selectCustom()
            .setApiUseSsl(false)
            .setApiHost(sportsApiBaseUrl.get())
            .setDefaultLanguage(defaultLanguage.orElse(Languages.any()))
            .setExceptionHandlingStrategy(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()))
            .setNodeId(nodeId)
            .build();

        return createSdk(config);
    }

    public UofSdk withOpenedFeed() throws InitException {
        UofConfiguration config = UofSdk
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
            .setMessagingVirtualHost(UF_VIRTUALHOST)
            .setExceptionHandlingStrategy(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()))
            .setNodeId(nodeId)
            .build();

        UofSdk sdk = createSdk(config);

        if (configureSession) {
            configure1Session(sdk);
        }

        sdk.open();
        return sdk;
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
