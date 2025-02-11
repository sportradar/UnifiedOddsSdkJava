/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.LotteryCi;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.LotteryImpl;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class LotteryImplTest {

    private SportEventCache sportEventCache;
    private SportEntityFactory sportEntityFactory;

    @BeforeEach
    void stub() {
        sportEventCache = mock(SportEventCache.class);
        sportEntityFactory = mock(SportEntityFactory.class);
    }

    private LotteryImpl lottery(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        return new LotteryImpl(
            Urns.SportEvents.any(),
            Urns.Sports.urnForAnySport(),
            ImmutableList.of(),
            sportEventCache,
            sportEntityFactory,
            exceptionHandlingStrategy
        );
    }

    private LotteryImpl lottery(Urn id) {
        return new LotteryImpl(
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
        void whenNoLotteryCiInCache(ExceptionHandlingStrategy strategy) {
            LotteryAssert.assertThat(lottery(strategy)).doesNotHaveStartTimeTbd(strategy);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void returnsValueWhenLotteryFoundInCache(boolean isStartTimeTbd) throws Exception {
            Urn id = Urns.SportEvents.getForAnyMatch();
            LotteryCi lottery = lotteryCi(isStartTimeTbd);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(lottery);

            assertThat(lottery(id).isStartTimeTbd()).isEqualTo(isStartTimeTbd);
        }

        @Test
        void returnsNullWhenLotteryFoundInCacheBuStartTimeTbdEmpty() throws Exception {
            Urn id = Urns.SportEvents.getForAnyMatch();
            LotteryCi lottery = lotteryCi(null);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(lottery);

            assertThat(lottery(id).isStartTimeTbd()).isNull();
        }

        private LotteryCi lotteryCi(Boolean isStartTimeTbd) {
            LotteryCi lottery = mock(LotteryCi.class);
            when(lottery.isStartTimeTbd())
                .thenReturn(Optional.ofNullable(isStartTimeTbd))
                .thenReturn(Optional.empty());
            return lottery;
        }
    }
}
