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
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MatchImplTest {

    public static final String TRANSLATIONS =
        "com.sportradar.unifiedodds.sdk.impl.entities.MatchImplTest#translations";

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Nested
    public class Name {

        private final Locale anyLanguage = ENGLISH;
        private final Urn matchUrn = Urns.SportEvents.getForAnyMatch();
        private final Urn anySportUrn = Urns.Sports.urnForAnySport();
        private final ExceptionHandlingStrategy anyExceptionHandling = Throw;
        private final String matchImpl = "MatchImpl";
        private final String ciTypeMissMatch = "CI type miss-match";
        private final SportEntityFactory entityFactory = mock(SportEntityFactory.class);
        private final SportEventCache sportEventCache = mock(SportEventCache.class);
        private final SportEventStatusFactory statusFactory = mock(SportEventStatusFactory.class);

        @Test
        public void doesNotConstructNullLanguages() {
            assertThatThrownBy(() ->
                    new MatchImpl(
                        matchUrn,
                        anySportUrn,
                        sportEventCache,
                        statusFactory,
                        entityFactory,
                        null,
                        anyExceptionHandling
                    )
                )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("locales");
        }

        @Test
        public void throwsWhenNoCacheItemIsAcquiredAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(null);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Throw
            );

            assertThatThrownBy(() -> match.getName(anyLanguage))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining(matchImpl)
                .hasMessageContaining(ciTypeMissMatch);
        }

        @Test
        public void returnsNullWhenNoCacheItemIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(null);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Catch
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullWhenNoCacheItemIsAcquired()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(null);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                null
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void throwsWhenNonMatchCiIsAcquiredAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Throw
            );

            assertThatThrownBy(() -> match.getName(anyLanguage))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining(matchImpl)
                .hasMessageContaining(ciTypeMissMatch);
        }

        @Test
        public void returnsNullWhenNonMatchCiIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Catch
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullWhenNonMatchCiIsAcquiredAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(mock(DrawCi.class));
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                null
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void throwsWhenMatchCiIsNotFoundAndSdkThrowsErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Throw
            );

            assertThatThrownBy(() -> match.getName(anyLanguage))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("loadMatchCI, CI not found")
                .hasRootCauseInstanceOf(CacheItemNotFoundException.class);
        }

        @Test
        public void returnsNullMatchCiIsNotFoundAndSdkCatchesErrors() throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                Catch
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void sdkCatchesErrorsByDefaultHenceReturnsNullMatchCiIsNotFoundAndSdkCatchesErrors()
            throws CacheItemNotFoundException {
            when(sportEventCache.getEventCacheItem(matchUrn)).thenThrow(CacheItemNotFoundException.class);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(anyLanguage),
                null
            );

            assertNull(match.getName(anyLanguage));
        }

        @Test
        public void getsRequestTranslationIfNoneIsConfigured() throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            List<Locale> noConfiguredLanguages = asList();
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                noConfiguredLanguages,
                anyExceptionHandling
            );

            assertThat(match).hasNameTranslated(ENGLISH, UNDER_20_EN);
        }

        @Test
        public void getsNoTranslationsIfRequestedTranslationIsNotAvailable()
            throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(FRENCH),
                anyExceptionHandling
            );

            assertThat(match).hasNameTranslated(ENGLISH, UNDER_20_EN);
            assertThat(match).hasNameNotTranslatedTo(FRENCH);
        }

        @Test
        public void getsNoTranslationsIfSomeAreConfiguredButNoneAreAvailable()
            throws CacheItemNotFoundException {
            Map<Locale, String> noTranslationsAvailable = Collections.emptyMap();
            val matchCi = createMatchCiWithTranslatedName(noTranslationsAvailable);
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH),
                anyExceptionHandling
            );

            assertThat(match).hasNameNotTranslatedTo(ENGLISH);
        }

        @Test
        public void getsNoTranslationIfDesiredOneIsUnavailable() throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH),
                anyExceptionHandling
            );

            assertThat(match).hasNameNotTranslatedTo(FRENCH);
        }

        @ParameterizedTest
        @MethodSource(TRANSLATIONS)
        public void getsTheOnlyTranslationAvailable(Locale language, String translation)
            throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(language, translation));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(language),
                anyExceptionHandling
            );

            assertThat(match).hasNameTranslated(language, translation);
        }

        @Test
        public void getsMultipleTranslationsAvailable() throws CacheItemNotFoundException {
            val matchCi = createMatchCiWithTranslatedName(of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);
            Match match = new MatchImpl(
                matchUrn,
                anySportUrn,
                sportEventCache,
                statusFactory,
                entityFactory,
                asList(ENGLISH, FRENCH),
                anyExceptionHandling
            );

            assertThat(match).hasNameTranslated(ENGLISH, UNDER_20_EN);
            assertThat(match).hasNameTranslated(FRENCH, UNDER_20_FR);
        }

        private MatchCi createMatchCiWithTranslatedName(Map<Locale, String> translatedName) {
            val matchCi = mock(MatchCi.class);
            translatedName.forEach((key, value) ->
                when(matchCi.getNames(singletonList(key))).thenReturn(translatedName)
            );
            return matchCi;
        }
    }

    @Nested
    public class StartTimeTbd {

        private static final String UNDER_20_EN = "Under 20";
        private final Urn matchUrn = Urns.SportEvents.getForAnyMatch();
        private final Urn anySportUrn = Urns.Sports.urnForAnySport();
        private final ExceptionHandlingStrategy anyExceptionHandling = Throw;
        private final SportEntityFactory entityFactory = mock(SportEntityFactory.class);
        private final SportEventCache sportEventCache = mock(SportEventCache.class);
        private final SportEventStatusFactory statusFactory = mock(SportEventStatusFactory.class);
        private final Locale language = ENGLISH;
        private MatchImpl match;

        @BeforeEach
        public void createMatch() {
            match =
                new MatchImpl(
                    matchUrn,
                    anySportUrn,
                    sportEventCache,
                    statusFactory,
                    entityFactory,
                    singletonList(language),
                    anyExceptionHandling
                );
        }

        @Test
        public void startTimeTbdNull() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(null));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);

            assertThat(match.isStartTimeTbd()).isNull();
        }

        @Test
        public void startTimeIsToBeDefined() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(true));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);

            assertThat(match.isStartTimeTbd()).isTrue();
        }

        @Test
        public void startTimeIsNotToBeDefined() throws Exception {
            MatchCi matchCi = matchCi(of(language, UNDER_20_EN), withStartTimeTbd(false));
            when(sportEventCache.getEventCacheItem(matchUrn)).thenReturn(matchCi);

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
