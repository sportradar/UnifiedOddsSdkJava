/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.mockito.Mockito;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "DeclarationOrder" })
class SdkPropertiesReaderUtil {

    private SdkPropertiesReaderUtil() {
        // static util class
    }

    static final String ACCESS_TOKEN = "sample-props-token";
    static final int INACTIVITY_SECONDS = 33;
    static final Locale DEFAULT_LANGUAGE = Locale.ITALIAN;
    static final List<Locale> DESIRED_LANGUAGES = Arrays.asList(
        Locale.ENGLISH,
        Locale.FRENCH,
        Locale.ITALIAN
    );
    static final String API_HOST = "sample-props-api-host";
    static final int API_PORT = 80;
    static final String MESSAGING_HOST = "sample-props-messaging-host";
    static final int MESSAGING_PORT = 77;
    static final String MESSAGING_USERNAME = "messaging-props-uname";
    static final String MESSAGING_PASSWORD = "messaging-props-password";
    static final String MESSAGING_VHOST = "messaging-props-vhost";
    static final boolean API_USE_SSL = false;
    static final boolean MESSAGING_USE_SSL = false;
    static final List<Integer> DISABLED_PRODUCERS = Arrays.asList(7, 8, 9);
    static final int NODE_ID = -99;
    static final ExceptionHandlingStrategy EXCEPTION_HANDLING = ExceptionHandlingStrategy.Throw;
    static final Environment ENVIRONMENT = Environment.Production;

    static SdkConfigurationPropertiesReader getReaderWithFullData() {
        SdkConfigurationPropertiesReader mock = Mockito.mock(SdkConfigurationPropertiesReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLanguage()).thenReturn(Optional.of(DEFAULT_LANGUAGE));
        Mockito.when(mock.readDesiredLanguages()).thenReturn(DESIRED_LANGUAGES);
        Mockito.when(mock.readApiHost()).thenReturn(Optional.of(API_HOST));
        Mockito.when(mock.readApiPort()).thenReturn(Optional.of(API_PORT));
        Mockito.when(mock.readMessagingHost()).thenReturn(Optional.of(MESSAGING_HOST));
        Mockito.when(mock.readMessagingPort()).thenReturn(Optional.of(MESSAGING_PORT));
        Mockito.when(mock.readMessagingUsername()).thenReturn(Optional.of(MESSAGING_USERNAME));
        Mockito.when(mock.readMessagingPassword()).thenReturn(Optional.of(MESSAGING_PASSWORD));
        Mockito.when(mock.readMessagingVirtualHost()).thenReturn(Optional.of(MESSAGING_VHOST));
        Mockito.when(mock.readApiUseSsl()).thenReturn(Optional.of(API_USE_SSL));
        Mockito.when(mock.readMessagingUseSsl()).thenReturn(Optional.of(MESSAGING_USE_SSL));
        Mockito.when(mock.readDisabledProducers()).thenReturn(DISABLED_PRODUCERS);
        Mockito.when(mock.readNodeId()).thenReturn(Optional.of(NODE_ID));
        Mockito.when(mock.readExceptionHandlingStrategy()).thenReturn(Optional.of(EXCEPTION_HANDLING));

        return mock;
    }

    static SdkConfigurationPropertiesReader getReaderWithMinData() {
        SdkConfigurationPropertiesReader mock = Mockito.mock(SdkConfigurationPropertiesReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLanguage()).thenReturn(Optional.of(DEFAULT_LANGUAGE));
        Mockito.when(mock.readDesiredLanguages()).thenReturn(DESIRED_LANGUAGES);
        Mockito.when(mock.readNodeId()).thenReturn(Optional.of(NODE_ID));
        Mockito.when(mock.readEnvironment()).thenReturn(ENVIRONMENT);

        return mock;
    }

    static SdkConfigurationYamlReader getYamlReaderWithFullData() {
        SdkConfigurationYamlReader mock = Mockito.mock(SdkConfigurationYamlReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLanguage()).thenReturn(Optional.of(DEFAULT_LANGUAGE));
        Mockito.when(mock.readDesiredLanguages()).thenReturn(DESIRED_LANGUAGES);
        Mockito.when(mock.readApiHost()).thenReturn(Optional.of(API_HOST));
        Mockito.when(mock.readApiPort()).thenReturn(Optional.of(API_PORT));
        Mockito.when(mock.readMessagingHost()).thenReturn(Optional.of(MESSAGING_HOST));
        Mockito.when(mock.readMessagingPort()).thenReturn(Optional.of(MESSAGING_PORT));
        Mockito.when(mock.readMessagingUsername()).thenReturn(Optional.of(MESSAGING_USERNAME));
        Mockito.when(mock.readMessagingPassword()).thenReturn(Optional.of(MESSAGING_PASSWORD));
        Mockito.when(mock.readMessagingVirtualHost()).thenReturn(Optional.of(MESSAGING_VHOST));
        Mockito.when(mock.readApiUseSsl()).thenReturn(Optional.of(API_USE_SSL));
        Mockito.when(mock.readMessagingUseSsl()).thenReturn(Optional.of(MESSAGING_USE_SSL));
        Mockito.when(mock.readDisabledProducers()).thenReturn(DISABLED_PRODUCERS);
        Mockito.when(mock.readNodeId()).thenReturn(Optional.of(NODE_ID));
        Mockito.when(mock.readExceptionHandlingStrategy()).thenReturn(Optional.of(EXCEPTION_HANDLING));

        return mock;
    }

    static SdkConfigurationYamlReader getYamlReaderWithMinData() {
        SdkConfigurationYamlReader mock = Mockito.mock(SdkConfigurationYamlReader.class);

        Mockito.when(mock.readAccessToken()).thenReturn(Optional.of(ACCESS_TOKEN));
        Mockito.when(mock.readDefaultLanguage()).thenReturn(Optional.of(DEFAULT_LANGUAGE));
        Mockito.when(mock.readDesiredLanguages()).thenReturn(DESIRED_LANGUAGES);
        Mockito.when(mock.readNodeId()).thenReturn(Optional.of(NODE_ID));
        Mockito.when(mock.readEnvironment()).thenReturn(ENVIRONMENT);

        return mock;
    }
}
