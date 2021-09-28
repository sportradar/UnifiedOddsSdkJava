/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
class SDKPropertiesReaderUtil {
    private SDKPropertiesReaderUtil() {
        // static util class
    }

    static final String ACCESS_TOKEN = "sample-props-token";
    static final int INACTIVITY_SECONDS = 33;
    static final Locale DEFAULT_LOCALE = Locale.ITALIAN;
    static final List<Locale> DESIRED_LOCALES = Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.ITALIAN);
    static final String API_HOST = "sample-props-api-host";
    static final String MESSAGING_HOST = "sample-props-messaging-host";
    static final int MESSAGING_PORT = 77;
    static final String MESSAGING_USERNAME = "messaging-props-uname";
    static final String MESSAGING_PASSWORD = "messaging-props-password";
    static final String MESSAGING_VHOST = "messaging-props-vhost";
    static final boolean USE_API_SSL = false;
    static final boolean USE_MESSAGING_SSL = false;
    static final List<Integer> DISABLED_PRODUCERS = Arrays.asList(7,8,9);
    static final int MAX_RECOVERY_TIME = 77;
    static final int MAX_RECOVERY_TIME_YAML = 78;
    static final int SDK_NODE_ID = -99;
    static final ExceptionHandlingStrategy EXCEPTION_HANDLING = ExceptionHandlingStrategy.Throw;
    static final Environment ENVIRONMENT = Environment.Production;

    static SDKConfigurationPropertiesReader getReaderWithFullData() {
        SDKConfigurationPropertiesReader mock = Mockito.mock(SDKConfigurationPropertiesReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readMaxInactivitySeconds()).thenReturn(Optional.of(INACTIVITY_SECONDS));
        Mockito.when(mock.readDefaultLocale()).thenReturn(Optional.of(DEFAULT_LOCALE));
        Mockito.when(mock.readDesiredLocales()).thenReturn(DESIRED_LOCALES);
        Mockito.when(mock.readApiHost()).thenReturn(Optional.of(API_HOST));
        Mockito.when(mock.readMessagingHost()).thenReturn(Optional.of(MESSAGING_HOST));
        Mockito.when(mock.readMessagingPort()).thenReturn(Optional.of(MESSAGING_PORT));
        Mockito.when(mock.readMessagingUsername()).thenReturn(Optional.of(MESSAGING_USERNAME));
        Mockito.when(mock.readMessagingPassword()).thenReturn(Optional.of(MESSAGING_PASSWORD));
        Mockito.when(mock.readMessagingVirtualHost()).thenReturn(Optional.of(MESSAGING_VHOST));
        Mockito.when(mock.readUseApiSsl()).thenReturn(Optional.of(USE_API_SSL));
        Mockito.when(mock.readUseMessagingSsl()).thenReturn(Optional.of(USE_MESSAGING_SSL));
        Mockito.when(mock.readDisabledProducers()).thenReturn(DISABLED_PRODUCERS);
        Mockito.when(mock.readMaxRecoveryTime()).thenReturn(Optional.of(MAX_RECOVERY_TIME));
        Mockito.when(mock.readSdkNodeId()).thenReturn(Optional.of(SDK_NODE_ID));
        Mockito.when(mock.readExceptionHandlingStrategy()).thenReturn(Optional.of(EXCEPTION_HANDLING));

        return mock;
    }

    static SDKConfigurationPropertiesReader getReaderWithMinData() {
        SDKConfigurationPropertiesReader mock = Mockito.mock(SDKConfigurationPropertiesReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLocale()).thenReturn(Optional.of(DEFAULT_LOCALE));
        Mockito.when(mock.readDesiredLocales()).thenReturn(DESIRED_LOCALES);
        Mockito.when(mock.readSdkNodeId()).thenReturn(Optional.of(SDK_NODE_ID));
        Mockito.when(mock.readUfEnvironment()).thenReturn(ENVIRONMENT);

        return mock;
    }

    static SDKConfigurationYamlReader getYamlReaderWithFullData() {
        SDKConfigurationYamlReader mock = Mockito.mock(SDKConfigurationYamlReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readMaxInactivitySeconds()).thenReturn(Optional.of(INACTIVITY_SECONDS));
        Mockito.when(mock.readDefaultLocale()).thenReturn(Optional.of(DEFAULT_LOCALE));
        Mockito.when(mock.readDesiredLocales()).thenReturn(DESIRED_LOCALES);
        Mockito.when(mock.readApiHost()).thenReturn(Optional.of(API_HOST));
        Mockito.when(mock.readMessagingHost()).thenReturn(Optional.of(MESSAGING_HOST));
        Mockito.when(mock.readMessagingPort()).thenReturn(Optional.of(MESSAGING_PORT));
        Mockito.when(mock.readMessagingUsername()).thenReturn(Optional.of(MESSAGING_USERNAME));
        Mockito.when(mock.readMessagingPassword()).thenReturn(Optional.of(MESSAGING_PASSWORD));
        Mockito.when(mock.readMessagingVirtualHost()).thenReturn(Optional.of(MESSAGING_VHOST));
        Mockito.when(mock.readUseApiSsl()).thenReturn(Optional.of(USE_API_SSL));
        Mockito.when(mock.readUseMessagingSsl()).thenReturn(Optional.of(USE_MESSAGING_SSL));
        Mockito.when(mock.readDisabledProducers()).thenReturn(DISABLED_PRODUCERS);
        Mockito.when(mock.readMaxRecoveryTime()).thenReturn(Optional.of(MAX_RECOVERY_TIME_YAML));
        Mockito.when(mock.readSdkNodeId()).thenReturn(Optional.of(SDK_NODE_ID));
        Mockito.when(mock.readExceptionHandlingStrategy()).thenReturn(Optional.of(EXCEPTION_HANDLING));

        return mock;
    }

    static SDKConfigurationYamlReader getYamlReaderWithMinData() {
        SDKConfigurationYamlReader mock = Mockito.mock(SDKConfigurationYamlReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLocale()).thenReturn(Optional.of(DEFAULT_LOCALE));
        Mockito.when(mock.readDesiredLocales()).thenReturn(DESIRED_LOCALES);
        Mockito.when(mock.readSdkNodeId()).thenReturn(Optional.of(SDK_NODE_ID));
        Mockito.when(mock.readUfEnvironment()).thenReturn(ENVIRONMENT);

        return mock;
    }
}
