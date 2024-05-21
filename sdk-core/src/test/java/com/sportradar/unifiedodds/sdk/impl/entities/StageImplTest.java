/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.StageCi;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class StageImplTest {

    private SportEventCache sportEventCache;
    private SportEntityFactory sportEntityFactory;
    private SportEventStatusFactory sportEventStatusFactory;

    @BeforeEach
    void stub() {
        sportEventCache = mock(SportEventCache.class);
        sportEntityFactory = mock(SportEntityFactory.class);
        sportEventStatusFactory = mock(SportEventStatusFactory.class);
    }

    private StageImpl stage(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        return new StageImpl(
            Urns.SportEvents.urnForAnyStage(),
            Urns.Sports.urnForAnySport(),
            sportEventCache,
            sportEventStatusFactory,
            sportEntityFactory,
            ImmutableList.of(),
            exceptionHandlingStrategy
        );
    }

    private StageImpl stage(Urn id) {
        return new StageImpl(
            id,
            Urns.Sports.urnForAnySport(),
            sportEventCache,
            sportEventStatusFactory,
            sportEntityFactory,
            ImmutableList.of(),
            ExceptionHandlingStrategy.Throw
        );
    }

    @Nested
    class IsStartTimeTbd {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void whenNoStageCiInCache(ExceptionHandlingStrategy strategy) {
            StageAssert.assertThat(stage(strategy)).doesNotHaveStartTimeTbd(strategy);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void returnsValueWhenStageFoundInCache(boolean isStartTimeTbd) throws Exception {
            Urn id = Urns.SportEvents.urnForAnyStage();
            StageCi stageCi = stageCi(isStartTimeTbd);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(stageCi);

            assertThat(stage(id).isStartTimeTbd()).isEqualTo(isStartTimeTbd);
        }

        @Test
        void returnsNullWhenStageFoundInCacheBuStartTimeTbdEmpty() throws Exception {
            Urn id = Urns.SportEvents.urnForAnyStage();
            StageCi stageCi = stageCi(null);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(stageCi);

            assertThat(stage(id).isStartTimeTbd()).isNull();
        }

        private StageCi stageCi(Boolean isStartTimeTbd) {
            StageCi stage = mock(StageCi.class);
            when(stage.isStartTimeTbd()).thenReturn(Optional.ofNullable(isStartTimeTbd));
            return stage;
        }
    }
}
