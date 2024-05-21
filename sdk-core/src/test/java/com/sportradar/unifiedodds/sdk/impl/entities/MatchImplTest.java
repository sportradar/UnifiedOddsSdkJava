/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.google.common.collect.ImmutableMap.of;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.impl.entities.MatchAssertions.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DrawCi;
import com.sportradar.unifiedodds.sdk.caching.MatchCi;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.*;
import java.util.function.Supplier;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class MatchImplTest {

    @RunWith(JUnitParamsRunner.class)
    public static class Name {

        private static final Locale ANY_LANGUAGE = ENGLISH;
        private static final String UNDER_20_EN = "Under 20";
        private static final String UNDER_20_FR = "moins de 20 ans";
        private static final Urn MATCH_URN = Urns.SportEvents.getForAnyMatch();
        private static final Urn ANY_SPORT_URN = Urns.Sports.urnForAnySport();
        private static final ExceptionHandlingStrategy ANY_EXCEPTION_HANDLING = Throw;
        private static final String MATCH_IMPL = "MatchImpl";
        private static final String CI_TYPE_MISS_MATCH = "CI type miss-match";
        private final SportEntityFactory entityFactory = mock(SportEntityFactory.class);
        private final SportEventCache sportEventCache = mock(SportEventCache.class);
        private final SportEventStatusFactory statusFactory = mock(SportEventStatusFactory.class);

        @Test
        public void doesNotConstructNullLanguages() {
            assertThatThrownBy(() ->
                    new MatchImpl(
                        MATCH_URN,
                        ANY_SPORT_URN,
                        sportEventCache,
                        statusFactory,
                        entityFactory,
                        null,
                        ANY_EXCEPTION_HANDLING
                    )
                )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("locales");
        }

        @Test
        public void throwsWhenNoCacheItemIsAcquiredAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(null);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Throw
            );

            assertThatThrownBy(() -> match.getName(ANY_LANGUAGE))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining(MATCH_IMPL)
                .hasMessageContaining(CI_TYPE_MISS_MATCH);
        }

        @Test
        public void returnsNullWhenNoCacheItemIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(null);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Catch
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullWhenNoCacheItemIsAcquired()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(null);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                null
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void throwsWhenNonMatchCiIsAcquiredAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Throw
            );

            assertThatThrownBy(() -> match.getName(ANY_LANGUAGE))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining(MATCH_IMPL)
                .hasMessageContaining(CI_TYPE_MISS_MATCH);
        }

        @Test
        public void returnsNullWhenNonMatchCiIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Catch
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullWhenNonMatchCiIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                null
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void throwsWhenMatchCiIsNotFoundAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Throw
            );

            assertThatThrownBy(() -> match.getName(ANY_LANGUAGE))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("loadMatchCI, CI not found")
                .hasRootCauseInstanceOf(CacheItemNotFoundException.class);
        }

        @Test
        public void returnsNullMatchCiIsNotFoundAndSdkCatchesErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                Catch
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullMatchCiIsNotFoundAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ANY_LANGUAGE),
                null
            );

            assertNull(match.getName(ANY_LANGUAGE));
        }

        @Test
        public void getsNoTranslationsIfNoneIsConfiguredThoughSomeAreAvailable()
            throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            List<Locale> noConfiguredLanguages = asList();
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                noConfiguredLanguages,
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameNotTranslatedTo(ENGLISH);
        }

        @Test
        public void getsNoTranslationsIfConfiguredAndAvailableOnesNotIntersect()
            throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(FRENCH),
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameNotTranslatedTo(ENGLISH);
            assertThat(match).hasNameNotTranslatedTo(FRENCH);
        }

        @Test
        public void getsNoTranslationsIfSomeAreConfiguredButNoneAreAvailable()
            throws CacheItemNotFoundException {
            Map<Locale, String> noTranslationsAvailable = Collections.emptyMap();
            val matchCi = createMatchCiWithTranslatedName(noTranslationsAvailable);
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH),
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameNotTranslatedTo(ENGLISH);
        }

        @Test
        public void getsNoTranslationIfDesiredOneIsUnavailable() throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH),
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameNotTranslatedTo(FRENCH);
        }

        @Test
        @Parameters(method = "translations")
        public void getsTheOnlyTranslationAvailable(Locale language, String translation)
            throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(language, translation));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(language),
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameTranslated(language, translation);
        }

        private Object[] translations() {
            return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
        }

        @Test
        public void getsMultipleTranslationsAvailable() throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
            Match match = new MatchImpl(
                MATCH_URN,
                ANY_SPORT_URN,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH, FRENCH),
                ANY_EXCEPTION_HANDLING
            );

            assertThat(match).hasNameTranslated(ENGLISH, UNDER_20_EN);
            assertThat(match).hasNameTranslated(FRENCH, UNDER_20_FR);
        }

        private MatchCi createMatchCiWithTranslatedName(Map<Locale, String> translatedName) {
            val matchCi = mock(MatchCi.class);
            when(matchCi.getNames(new ArrayList<>(translatedName.keySet()))).thenReturn(translatedName);
            return matchCi;
        }
    }

    @RunWith(JUnitParamsRunner.class)
    public static class StartTimeTbd {

        private static final String UNDER_20_EN = "Under 20";
        private static final Urn MATCH_URN = Urns.SportEvents.getForAnyMatch();
        private static final Urn ANY_SPORT_URN = Urns.Sports.urnForAnySport();
        private static final ExceptionHandlingStrategy ANY_EXCEPTION_HANDLING = Throw;
        private final SportEntityFactory entityFactory = mock(SportEntityFactory.class);
        private final SportEventCache sportEventCache = mock(SportEventCache.class);
        private final SportEventStatusFactory statusFactory = mock(SportEventStatusFactory.class);
        private final Locale language = ENGLISH;
        private MatchImpl match;

        @Before
        public void createMatch() {
            match =
                new MatchImpl(
                    MATCH_URN,
                    ANY_SPORT_URN,
                    sportEventCache,
                    statusFactory,
                    entityFactory,
                    singletonList(language),
                    ANY_EXCEPTION_HANDLING
                );
        }

        @Test
        public void startTimeTbdNull() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(null));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);

            assertThat(match.isStartTimeTbd()).isNull();
        }

        @Test
        public void startTimeIsToBeDefined() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(true));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);

            assertThat(match.isStartTimeTbd()).isTrue();
        }

        @Test
        public void startTimeIsNotToBeDefined() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(false));
            when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);

            assertThat(match.isStartTimeTbd()).isFalse();
        }

        private Supplier<Boolean> withStartTimeTbd(Boolean timestampTbd) {
            return () -> timestampTbd;
        }

        private MatchCi matchCi(Map<Locale, String> translatedName, Supplier<Boolean> timestampTbd) {
            val matchCi = mock(MatchCi.class);
            when(matchCi.getNames(new ArrayList<>(translatedName.keySet()))).thenReturn(translatedName);
            when(matchCi.isStartTimeTbd()).thenReturn(ofNullable(timestampTbd.get()));
            return matchCi;
        }
    }
}
