/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Test;

public class DataProviderTest {

    private static final String ANY_FORMAT_WITH_2_PARAMS = "/anyParams/%s/and/%s";
    private static final Locale ANY_LANGUAGE = Locale.CHINESE;
    private static final String ANY_STRING_PARAM = "any";
    private SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);

    @Test
    public void takesUrlFromConfiguration() {
        when(config.getApiHostAndPort()).thenReturn("someHost");
        val dataProvider = new DataProvider<>(
            ANY_FORMAT_WITH_2_PARAMS,
            config,
            mock(HttpDataFetcher.class),
            mock(Deserializer.class)
        );

        assertThat(dataProvider.getFinalUrl(ANY_LANGUAGE, ANY_STRING_PARAM)).startsWith("http://someHost");
    }

    @Test
    public void usesHttpsIfConfiguredSo() {
        when(config.getApiHostAndPort()).thenReturn("someHost");
        when(config.getUseApiSsl()).thenReturn(true);
        val dataProvider = new DataProvider<>(
            ANY_FORMAT_WITH_2_PARAMS,
            config,
            mock(HttpDataFetcher.class),
            mock(Deserializer.class)
        );

        assertThat(dataProvider.getFinalUrl(ANY_LANGUAGE, ANY_STRING_PARAM)).startsWith("https://someHost");
    }

    @Test
    public void injectsProvidedParameters() {
        val dataProvider = new DataProvider<>(
            "/param1/%s/param2/%s",
            config,
            mock(HttpDataFetcher.class),
            mock(Deserializer.class)
        );

        String secondParam = "secondParam";
        assertThat(dataProvider.getFinalUrl(Locale.FRENCH, secondParam))
            .endsWith("/param1/fr/param2/secondParam");
    }
}
