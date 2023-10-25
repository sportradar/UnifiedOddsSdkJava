/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentSetting;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;
import java.util.Locale;

public class BookmakerDetailsProviderFactory {

    public static final int HTTP_PORT = 80;

    private LogHttpDataFetcher httpDataFetcher;
    private Deserializer deserializer;
    private SdkInternalConfiguration config;

    BookmakerDetailsProviderFactory(
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        SdkInternalConfiguration config
    ) {
        this.httpDataFetcher = httpDataFetcher;
        this.deserializer = deserializer;
        this.config = config;
    }

    public DataProvider<BookmakerDetails> targetingProduction() {
        EnvironmentSetting setting = EnvironmentManager.getSetting(Environment.Production);
        return new DataProvider<>(
            "/users/whoami.xml",
            hostAndPort(setting.getApiHost(), config.getApiPort()),
            true,
            Locale.ENGLISH,
            httpDataFetcher,
            deserializer
        );
    }

    public DataProvider<BookmakerDetails> targetingIntegration() {
        EnvironmentSetting setting = EnvironmentManager.getSetting(Environment.Integration);
        return new DataProvider<>(
            "/users/whoami.xml",
            hostAndPort(setting.getApiHost(), config.getApiPort()),
            true,
            Locale.ENGLISH,
            httpDataFetcher,
            deserializer
        );
    }

    private String hostAndPort(String host, int port) {
        return host + (port == HTTP_PORT || port == 0 ? "" : ":" + port);
    }
}
