/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.DrawCi;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.DrawImpl;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class DrawImplTest {

    private SportEventCache sportEventCache;
    private SportEntityFactory sportEntityFactory;

    @BeforeEach
    void stub() {
        sportEventCache = mock(SportEventCache.class);
        sportEntityFactory = mock(SportEntityFactory.class);
    }

    private DrawImpl draw(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        return new DrawImpl(
            Urns.SportEvents.any(),
            Urns.Sports.urnForAnySport(),
            ImmutableList.of(),
            sportEventCache,
            sportEntityFactory,
            exceptionHandlingStrategy
        );
    }

    private DrawImpl draw(Urn id) {
        return new DrawImpl(
            id,
            Urns.Sports.urnForAnySport(),
            ImmutableList.of(),
            sportEventCache,
            sportEntityFactory,
            ExceptionHandlingStrategy.Throw
        );
    }

    @Nested
    class IsStartTimeTbd {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void whenNoDrawCiInCache(ExceptionHandlingStrategy strategy) {
            DrawAssert.assertThat(draw(strategy)).doesNotHaveStartTimeTbd(strategy);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void returnsValueWhenDrawFoundInCache(boolean isStartTimeTbd) throws Exception {
            Urn id = Urns.SportEvents.getForAnyMatch();
            DrawCi draw = drawCi(isStartTimeTbd);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(draw);

            assertThat(draw(id).isStartTimeTbd()).isEqualTo(isStartTimeTbd);
        }

        @Test
        void returnsNullWhenDrawFoundInCacheBuStartTimeTbdEmpty() throws Exception {
            Urn id = Urns.SportEvents.getForAnyMatch();
            DrawCi draw = drawCi(null);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(draw);

            assertThat(draw(id).isStartTimeTbd()).isNull();
        }

        private DrawCi drawCi(Boolean isStartTimeTbd) {
            DrawCi draw = mock(DrawCi.class);
            when(draw.isStartTimeTbd()).thenReturn(Optional.ofNullable(isStartTimeTbd));
            return draw;
        }
    }
}
