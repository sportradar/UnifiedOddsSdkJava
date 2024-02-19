/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.impl.markets.NameProviders.usingFactory;
import static com.sportradar.utils.domain.markets.MarketIds.anyMarketId;
import static com.sportradar.utils.domain.names.Languages.anyLanguages;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.utils.domain.names.Languages;
import lombok.val;
import org.junit.Test;

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
        val marketDescriptorProvider = mock(MarketDescriptionProvider.class);
        when(marketDescriptorProvider.getMarketDescription(anyInt(), anyMap(), anyList(), anyBoolean()))
            .thenThrow(CacheItemNotFoundException.class);
        val nameProvider = usingFactory()
            .withMarketDescriptorProvider(marketDescriptorProvider)
            .withExceptionHandlingStrategy(Throw)
            .construct();

        assertThatThrownBy(() -> nameProvider.getMarketNames(anyLanguages()))
            .isInstanceOf(NameGenerationException.class)
            .hasMessageContaining("Failed to retrieve market name descriptor");
        assertThatThrownBy(() -> nameProvider.getMarketName(Languages.any()))
            .isInstanceOf(NameGenerationException.class)
            .hasMessageContaining("Failed to retrieve market name descriptor");
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
