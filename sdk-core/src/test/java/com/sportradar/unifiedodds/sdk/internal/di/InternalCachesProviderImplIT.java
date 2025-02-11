/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import static com.sportradar.unifiedodds.sdk.internal.di.InternalCachesProviderImplIT.MultipleCasesPerTestDueToUsingSleepToReduceImpactOnBuildTime.CacheGroup.Presence.GONE;
import static com.sportradar.unifiedodds.sdk.internal.di.InternalCachesProviderImplIT.MultipleCasesPerTestDueToUsingSleepToReduceImpactOnBuildTime.CacheGroup.Presence.PRESENT;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.cfg.UofCacheConfigurationStub;
import com.sportradar.unifiedodds.sdk.internal.caching.SdkCacheRemovalListener;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.LogsMock;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class InternalCachesProviderImplIT {

    public static final Duration ANY_TIMEOUT = Duration.ofHours(2);
    public static final Duration ONE_SECOND = Duration.ofSeconds(1);
    public static final int HALF_SECOND = 500;
    public static final int THREE_QUARTERS_OF_SECOND = 750;
    public static final int MORE_THAN_SECOND = 1500;

    private InternalCachesProviderImplIT() {}

    public static class MultipleCasesPerTestDueToUsingSleepToReduceImpactOnBuildTime {

        @Test
        public void removesItemAfterConfiguredTimeout() throws InterruptedException {
            val cachesExpiringAfterTimeoutSinceLastRead = new CacheGroup(
                newCacheProvider(c -> c.setVariantMarketDescriptionCacheTimeout(ONE_SECOND))
                    .getVariantMarketCache(),
                newCacheProvider(c -> c.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(ONE_SECOND))
                    .getIgnoreEventsTimelineCache()
            );

            val cachesExpiringAfterTimeoutSinceLastWrite = new CacheGroup(
                newCacheProvider(c -> c.setSportEventCacheTimeout(ONE_SECOND)).getSportEventCache(),
                newCacheProvider(c -> c.setSportEventStatusCacheTimeout(ONE_SECOND))
                    .getSportEventStatusCache(),
                newCacheProvider(c -> c.setProfileCacheTimeout(ONE_SECOND)).getPlayerProfileCache(),
                newCacheProvider(c -> c.setProfileCacheTimeout(ONE_SECOND)).getCompetitorCache()
            );

            cachesExpiringAfterTimeoutSinceLastRead.addElementToEach();
            cachesExpiringAfterTimeoutSinceLastWrite.addElementToEach();

            cachesExpiringAfterTimeoutSinceLastRead.checkElementInEachIs(PRESENT);
            cachesExpiringAfterTimeoutSinceLastWrite.checkElementInEachIs(PRESENT);

            Thread.sleep(HALF_SECOND);

            cachesExpiringAfterTimeoutSinceLastRead.checkElementInEachIs(PRESENT);
            cachesExpiringAfterTimeoutSinceLastWrite.checkElementInEachIs(PRESENT);

            Thread.sleep(THREE_QUARTERS_OF_SECOND);

            cachesExpiringAfterTimeoutSinceLastRead.checkElementInEachIs(PRESENT);
            cachesExpiringAfterTimeoutSinceLastWrite.checkElementInEachIs(GONE);

            Thread.sleep(MORE_THAN_SECOND);

            cachesExpiringAfterTimeoutSinceLastRead.checkElementInEachIs(GONE);
            cachesExpiringAfterTimeoutSinceLastWrite.checkElementInEachIs(GONE);

            cachesExpiringAfterTimeoutSinceLastWrite.ensureExpirationWasLogged(
                "SportEventCache",
                "PlayerProfileCache",
                "CompetitorProfileCache",
                "SportEventStatusCache"
            );
        }

        private InternalCachesProviderImpl newCacheProvider(
            Consumer<UofCacheConfigurationStub> adjustTimeouts
        ) {
            return new InternalCachesProviderImpl(configWithAnyTimeoutsAnd(adjustTimeouts));
        }

        private UofCacheConfigurationStub configWithAnyTimeoutsAnd(
            Consumer<UofCacheConfigurationStub> adjustTimeouts
        ) {
            val cacheConfig = new UofCacheConfigurationStub();
            cacheConfig.setVariantMarketDescriptionCacheTimeout(ANY_TIMEOUT);
            cacheConfig.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(ANY_TIMEOUT);
            cacheConfig.setProfileCacheTimeout(ANY_TIMEOUT);
            cacheConfig.setSportEventStatusCacheTimeout(ANY_TIMEOUT);
            cacheConfig.setSportEventCacheTimeout(ANY_TIMEOUT);
            adjustTimeouts.accept(cacheConfig);
            return cacheConfig;
        }

        static class CacheGroup {

            private static final LogsMock LOGGER_SPY = LogsMock.createCapturingFor(
                SdkCacheRemovalListener.class
            );

            private static final String SOME_KEY = "someKey";

            private List<Cache> caches;

            public CacheGroup(Cache... caches) {
                this.caches = asList(caches);
            }

            public void addElementToEach() {
                caches.forEach(c -> c.put(SOME_KEY, "someValue"));
            }

            public void checkElementInEachIs(Presence presence) {
                caches.forEach(c -> presence.validate(c));
            }

            public void ensureExpirationWasLogged(String... cacheNames) {
                makeAdditionalWriteAsGuavaPerformsCleanupOnSubsequentWrites();
                validateExpirationWasLogged(cacheNames);
            }

            private void makeAdditionalWriteAsGuavaPerformsCleanupOnSubsequentWrites() {
                caches.forEach(cache -> cache.put("unrelatedItemKey", "unrelatedItemValue"));
            }

            private void validateExpirationWasLogged(String[] cacheNames) {
                if (cacheNames.length != caches.size()) {
                    throw new RuntimeException("not all caches were provided names for");
                } else {
                    asList(cacheNames)
                        .forEach(name -> LOGGER_SPY.verifyLoggedLineContainingAll(name, "EXPIRED"));
                }
            }

            enum Presence {
                PRESENT {
                    @Override
                    public void validate(Cache cache) {
                        assertThat(cache.getIfPresent(SOME_KEY)).isNotNull();
                    }
                },
                GONE {
                    @Override
                    public void validate(Cache cache) {
                        assertThat(cache.getIfPresent(SOME_KEY)).isNull();
                    }
                };

                public abstract void validate(Cache cache);
            }
        }
    }
}
