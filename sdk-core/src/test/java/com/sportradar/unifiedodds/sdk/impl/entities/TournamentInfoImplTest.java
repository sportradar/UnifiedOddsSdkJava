/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.google.common.collect.ImmutableMap.of;
import static com.sportradar.unifiedodds.sdk.impl.entities.TournamentInfoAssertions.assertThat;
import static java.util.Arrays.asList;
import static java.util.Locale.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TournamentInfoImplTest {

    private static final String NAME_EN = "Worldcup";
    private static final String NAME_FR = "Coupe du monde";
    private final SportEventCache anySportEventCache = mock(SportEventCache.class);
    private final SportEntityFactory anySportEvents = mock(SportEntityFactory.class);
    private final TournamentCi tournamentCi = mock(TournamentCi.class);
    private final ExceptionHandlingStrategy anyExceptionStrategy = ExceptionHandlingStrategy.Throw;

    @Test
    public void getsNameInMultipleLanguages() {
        List<Locale> languages = asList(ENGLISH, FRENCH);
        when(tournamentCi.getNames(languages)).thenReturn(of(ENGLISH, NAME_EN, FRENCH, NAME_FR));
        val tournament = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            languages,
            anyExceptionStrategy
        );

        assertThat(tournament).hasNameTranslated(ENGLISH, NAME_EN);
        assertThat(tournament).hasNameTranslated(FRENCH, NAME_FR);
    }

    @ParameterizedTest
    @MethodSource("translations")
    public void getsNameInTheOnlyLanguageAvailable(Locale language, String translation) {
        when(tournamentCi.getNames(asList(language))).thenReturn(of(language, translation));
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(language),
            anyExceptionStrategy
        );

        assertThat(manager).hasNameTranslated(language, translation);
    }

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, NAME_EN }, { FRENCH, NAME_FR } };
    }

    @Test
    public void doesNotGetNameInUnavailableLanguage() {
        when(tournamentCi.getNames(asList(ENGLISH))).thenReturn(of(ENGLISH, NAME_EN));
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(ENGLISH),
            anyExceptionStrategy
        );

        assertThat(manager).hasNameNotTranslatedTo(FRENCH);
    }

    @Test
    public void getsNullAsNameWhenNoLanguagesAreAvailable() {
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(),
            anyExceptionStrategy
        );

        assertThat(manager).hasNameNotTranslatedTo(FRENCH);
    }

    @Test
    public void getsNullWhenLanguageIsConfiguredButCacheDoesNotProvideIt() {
        when(tournamentCi.getNames(asList(ENGLISH))).thenReturn(of());
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(ENGLISH),
            anyExceptionStrategy
        );

        assertThat(manager).hasNameNotTranslatedTo(ENGLISH);
    }

    @Test
    public void getsNullWhenLanguageIsNotConfigured() {
        when(tournamentCi.getNames(asList())).thenReturn(of());
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(),
            anyExceptionStrategy
        );

        assertThat(manager).hasNameNotTranslatedTo(ENGLISH);
    }

    @Test
    public void doesNotReturnNullCollectionWhenCacheReturnsNull() {
        when(tournamentCi.getNames(asList())).thenReturn(null);
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(),
            anyExceptionStrategy
        );

        assertNotNull(manager.getNames());
    }

    @Test
    public void returnedNamesAreImmutable() {
        when(tournamentCi.getNames(asList())).thenReturn(null);
        val manager = new TournamentInfoImpl(
            tournamentCi,
            anySportEventCache,
            anySportEvents,
            asList(),
            anyExceptionStrategy
        );

        Map<Locale, String> names = manager.getNames();

        assertThatThrownBy(() -> names.put(CHINESE, "worldcup in chinese"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
