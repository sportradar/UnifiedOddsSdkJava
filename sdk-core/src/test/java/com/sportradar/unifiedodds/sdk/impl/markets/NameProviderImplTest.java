/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviders.subbingOutCaches;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.impl.markets.NameProviders.usingFactory;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.Languages.anyLanguages;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.utils.domain.names.Languages;
import lombok.val;
import org.junit.jupiter.api.Test;

public class NameProviderImplTest {

    @Test
    public void failsToConstructWithoutRequiredArguments() {
        val profiles = mock(ProfileCache.class);
        val expr = mock(NameExpressionFactory.class);
        val config = mock(SdkInternalConfiguration.class);
        val time = mock(TimeUtils.class);
        val desc = mock(MarketDescriptionProvider.class);
        assertThatThrownBy(() -> new NameProviderFactoryImpl(null, profiles, expr, config, time))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NameProviderFactoryImpl(desc, null, expr, config, time))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NameProviderFactoryImpl(desc, profiles, null, config, time))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NameProviderFactoryImpl(desc, profiles, expr, null, time))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NameProviderFactoryImpl(desc, profiles, expr, config, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void throwsOnMarketNotFoundWhenSdkConfiguredToThrow() throws CacheItemNotFoundException {
        val nameProvider = usingFactory()
            .withMarketDescriptorProvider(subbingOutCaches().build())
            .withExceptionHandlingStrategy(Throw)
            .construct();

        assertThatThrownBy(() -> nameProvider.getMarketNames(singletonList(Languages.any())))
            .isInstanceOf(NameGenerationException.class)
            .hasMessageContaining("Failed to retrieve market name descriptor");
        assertThatThrownBy(() -> nameProvider.getMarketName(Languages.any()))
            .isInstanceOf(NameGenerationException.class)
            .hasMessageContaining("Failed to retrieve market name descriptor");
    }

    @Test
    public void returnsNullOnMarketMissingNameWhenSdkConfiguredToCatch() {
        val aLanguage = ENGLISH;
        val nameProvider = usingFactory()
            .withMarketDescriptorProvider(
                providing(
                    in(aLanguage),
                    namesOf(nullifyName(oddEvenMarketDescription(aLanguage)), in(aLanguage))
                )
            )
            .withExceptionHandlingStrategy(Catch)
            .construct();

        assertThat(nameProvider.getMarketName(aLanguage)).isNull();
    }

    private DescMarket nullifyName(DescMarket market) {
        market.setName("");
        return market;
    }

    @Test
    public void swallowsOnMarketNotFoundWhenSdkConfiguredToCatch() {
        val nameProvider = usingFactory()
            .withMarketDescriptorProvider(noMarketDescribingProvider())
            .withExceptionHandlingStrategy(Catch)
            .construct();

        assertThat(nameProvider.getMarketNames(anyLanguages())).isNull();
        assertThat(nameProvider.getMarketName(Languages.any())).isNull();
    }
}
