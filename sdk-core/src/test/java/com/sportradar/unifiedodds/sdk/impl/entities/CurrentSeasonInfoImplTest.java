/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.unifiedodds.sdk.impl.entities.CurrentSeasonAssertions.assertThat;
import static com.sportradar.utils.Urns.SportEvents.urnForAnySeason;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCi;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.entities.CurrentSeasonInfo;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CurrentSeasonInfoImplTest {

    public static final String TRANSLATIONS =
        "com.sportradar.unifiedodds.sdk.impl.entities.CurrentSeasonInfoImplTest#translations";

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Nested
    public class ScheduledSportEventIdsRetrieval {

        private final SportEntityFactory anyFactory = mock(SportEntityFactory.class);
        private final TournamentCi anyTournamentCi = mock(TournamentCi.class);
        private final Urn seasonUrn = urnForAnySeason();
        private final DataRouterManager dataRouterManager = mock(DataRouterManager.class);
        private final Locale inEnglish = ENGLISH;

        @Test
        public void returnsNullOnFailureWhenConfiguredToCatchExceptions() throws CommunicationException {
            final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
                dataRouterManager
            );
            final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
                seasonCiWithUrn(seasonUrn),
                anyTournamentCi,
                sportEventCache,
                anyFactory,
                asList(inEnglish),
                ExceptionHandlingStrategy.Catch
            );
            when(dataRouterManager.requestEventsFor(inEnglish, seasonUrn))
                .thenThrow(CommunicationException.class);

            final List<Competition> schedule = season.getSchedule();

            assertNull(schedule);
        }

        @Test
        public void throwsOnFailureWhenConfiguredToThrowExceptions() throws CommunicationException {
            final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
                dataRouterManager
            );
            final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
                seasonCiWithUrn(seasonUrn),
                anyTournamentCi,
                sportEventCache,
                anyFactory,
                asList(inEnglish),
                ExceptionHandlingStrategy.Throw
            );
            when(dataRouterManager.requestEventsFor(inEnglish, seasonUrn))
                .thenThrow(CommunicationException.class);

            assertThatThrownBy(() -> season.getSchedule())
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("getSchedule failure");
        }

        @Test
        public void sequentiallyAttemptsAllConfiguredLanguagesTillOneFails() throws CommunicationException {
            final Locale firstLanguage = ENGLISH;
            final Locale secondLanguage = FRENCH;
            final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
                dataRouterManager
            );
            final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
                seasonCiWithUrn(seasonUrn),
                anyTournamentCi,
                sportEventCache,
                anyFactory,
                asList(firstLanguage, secondLanguage),
                ExceptionHandlingStrategy.Throw
            );
            when(dataRouterManager.requestEventsFor(firstLanguage, seasonUrn)).thenReturn(asList());
            when(dataRouterManager.requestEventsFor(secondLanguage, seasonUrn))
                .thenThrow(CommunicationException.class);

            assertThatThrownBy(() -> season.getSchedule())
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("getSchedule failure");
        }

        @Test
        public void afterFailingOneLanguageSubsequentOnesAreNotAttempted() throws CommunicationException {
            final Locale firstLanguage = ENGLISH;
            final Locale secondLanguage = FRENCH;
            final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
                dataRouterManager
            );
            final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
                seasonCiWithUrn(seasonUrn),
                anyTournamentCi,
                sportEventCache,
                anyFactory,
                asList(firstLanguage, secondLanguage),
                ExceptionHandlingStrategy.Throw
            );
            when(dataRouterManager.requestEventsFor(firstLanguage, seasonUrn))
                .thenThrow(CommunicationException.class);

            assertThatThrownBy(() -> season.getSchedule())
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("getSchedule failure");
            verify(dataRouterManager, times(1)).requestEventsFor(any(), any(Urn.class));
        }

        private SeasonCi seasonCiWithUrn(final Urn urn) {
            final SeasonCi season = mock(SeasonCi.class);
            when(season.getId()).thenReturn(urn);
            return season;
        }
    }

    @Nested
    public class Name {

        private final ExceptionHandlingStrategy anyExceptionHandling = ExceptionHandlingStrategy.Throw;
        private final TournamentCi anyTournamentCi = mock(TournamentCi.class);
        private final SportEventCache anySportEventCache = mock(SportEventCache.class);
        private final SportEntityFactory anyFactory = mock(SportEntityFactory.class);

        @Test
        public void doesNotConstructWithNullLanguages() {
            Map<Locale, String> anyTranslations = Maps.of(ENGLISH, "anyTranslation");

            assertThatThrownBy(() ->
                    new CurrentSeasonInfoImpl(
                        seasonCiWithName(anyTranslations),
                        anyTournamentCi,
                        anySportEventCache,
                        anyFactory,
                        null,
                        anyExceptionHandling
                    )
                )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("locales");
        }

        @Test
        public void doesNotConstructWithoutLanguages() {
            Map<Locale, String> anyTranslations = Maps.of(ENGLISH, "anyTranslation");
            List<Locale> noLanguages = asList();

            assertThatThrownBy(() ->
                    new CurrentSeasonInfoImpl(
                        seasonCiWithName(anyTranslations),
                        anyTournamentCi,
                        anySportEventCache,
                        anyFactory,
                        noLanguages,
                        anyExceptionHandling
                    )
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("locales");
        }

        @Test
        public void getsNoTranslationsWhenNoneAvailable() {
            Map<Locale, String> noTranslations = emptyMap();
            val season = new CurrentSeasonInfoImpl(
                seasonCiWithName(noTranslations),
                anyTournamentCi,
                anySportEventCache,
                anyFactory,
                asList(ENGLISH),
                anyExceptionHandling
            );

            assertThat(season).hasNameNotTranslatedTo(ENGLISH);
        }

        @Test
        public void getsNoTranslationWhenDesiredOneIsUnavailable() {
            val season = new CurrentSeasonInfoImpl(
                seasonCiWithName(Maps.of(ENGLISH, UNDER_20_EN)),
                anyTournamentCi,
                anySportEventCache,
                anyFactory,
                asList(ENGLISH),
                anyExceptionHandling
            );

            assertThat(season).hasNameNotTranslatedTo(FRENCH);
        }

        @Test
        public void getsNoTranslationWhenConfiguredLanguageDoesNotMatchTranslation() {
            val season = new CurrentSeasonInfoImpl(
                seasonCiWithName(Maps.of(ENGLISH, UNDER_20_EN)),
                anyTournamentCi,
                anySportEventCache,
                anyFactory,
                asList(FRENCH),
                anyExceptionHandling
            );

            assertThat(season).hasNameNotTranslatedTo(FRENCH);
            assertThat(season).hasNameNotTranslatedTo(ENGLISH);
        }

        @ParameterizedTest
        @MethodSource(TRANSLATIONS)
        public void getsNameInTheOnlyLanguageAvailable(Locale language, String translation) {
            val season = new CurrentSeasonInfoImpl(
                seasonCiWithName(Maps.of(language, translation)),
                anyTournamentCi,
                anySportEventCache,
                anyFactory,
                asList(language),
                anyExceptionHandling
            );

            assertThat(season).hasNameTranslated(language, translation);
        }

        @Test
        public void getsNameInMultipleLanguages() {
            val season = new CurrentSeasonInfoImpl(
                seasonCiWithName(Maps.of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR)),
                anyTournamentCi,
                anySportEventCache,
                anyFactory,
                asList(ENGLISH, FRENCH),
                ExceptionHandlingStrategy.Throw
            );

            assertThat(season).hasNameTranslated(ENGLISH, UNDER_20_EN);
            assertThat(season).hasNameTranslated(FRENCH, UNDER_20_FR);
        }

        private SeasonCi seasonCiWithName(Map<Locale, String> name) {
            final SeasonCi season = mock(SeasonCi.class);
            name.forEach((locale, translation) -> when(season.getName(locale)).thenReturn(translation));
            return season;
        }
    }
}
