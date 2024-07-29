/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.deserialize;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.serialize;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.val;

public class ProfileCaches {

    @SneakyThrows
    public static void exportAndImportTheOnlyItemIn(ProfileCacheImpl cache) {
        val exported = cache.exportItems();
        assertThat(exported).hasSize(1);
        val serialized = serialize(exported.get(0));
        val deserialized = deserialize(serialized);
        cache.importItems(asList((ExportableCompetitorCi) deserialized));
    }

    public static class BuilderStubbingOutDataRouterManager {

        private Optional<DataRouterManager> dataRouterManager = Optional.empty();

        private Optional<Locale> language = Optional.empty();

        public static BuilderStubbingOutDataRouterManager stubbingOutDataRouterManager() {
            return new BuilderStubbingOutDataRouterManager();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = Optional.of(dataRouterManager);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager withDefaultLanguage(Locale language) {
            this.language = Optional.of(language);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public ProfileCacheImpl build() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getDefaultLocale()).thenReturn(language.orElse(Languages.any()));
            when(config.getExceptionHandlingStrategy()).thenReturn(anyErrorHandlingStrategy());
            return new ProfileCacheImpl(
                new CacheItemFactoryImpl(
                    dataRouterManager.orElse(mock(DataRouterManager.class)),
                    config,
                    CacheBuilder.newBuilder().build()
                ),
                dataRouterManager.orElse(mock(DataRouterManager.class)),
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().build()
            );
        }
    }
}
