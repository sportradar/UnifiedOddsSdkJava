/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagers.failsToProvideSportEventsList;
import static com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagers.providingSportEventsList;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.*;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.*;
import static com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviders.stubbingOutSportDataProvider;
import static com.sportradar.unifiedodds.sdk.internal.impl.Sports.soccer;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.assertions.SportAssert;
import com.sportradar.utils.Urns;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "MagicNumber", "LineLength" })
class SportDataProviderImplTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImplTest#englishAndChinese";
    private static final String ERROR_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImplTest#errorHandlingStrategies";

    @Nested
    class GetSports {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsListOfSports(Locale language) {
            val sportEntityFactory = providingSports(in(language), soccer(in(language)));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sports = provider.getSports();

            Assertions.assertThat(sports).hasSize(1);
            SportAssert.assertThat(sports.get(0), in(language)).hasIdAndNameEqualTo(soccer(in(language)));
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetListOfSports(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEntityFactory = failingToProvideSports(in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(provider::getSports)
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getSports()).isNull();
            }
        }
    }

    @Nested
    class GetSportEvent {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsSportEvent(Locale language) {
            val aMatch = anyMatch();
            val sportEntityFactory = providingSportEvent(in(language), aMatch);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sportEvent = provider.getSportEvent(aMatch.getId());

            Assertions.assertThat(sportEvent).isSameAs(aMatch);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetSportEvent(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.any();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getSportEvent(sportEventId))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getSportEvent(sportEventId)).isNull();
            }
        }
    }

    @Nested
    class GetSportEventForGivenLanguage {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsSportEventForGivenLanguage(Locale language) {
            val aMatch = anyMatch();
            val sportEntityFactory = providingSportEvent(in(language), aMatch);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sportEvent = provider.getSportEvent(aMatch.getId(), language);

            Assertions.assertThat(sportEvent).isSameAs(aMatch);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetSportEventForGivenLanguage(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.any();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getSportEvent(sportEventId, aLanguage))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getSportEvent(sportEventId, aLanguage)).isNull();
            }
        }
    }

    @Nested
    class GetSportEventForEventChange {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsSportEventForEventChange(Locale language) {
            val aMatch = anyMatch();
            val sportEntityFactory = providingSportEvent(in(language), aMatch);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sportEvent = provider.getSportEventForEventChange(aMatch.getId());

            Assertions.assertThat(sportEvent).isSameAs(aMatch);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetSportEventForEventChange(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.any();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getSportEventForEventChange(sportEventId))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getSportEventForEventChange(sportEventId)).isNull();
            }
        }
    }

    @Nested
    class GetLongTermEvent {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsLongTermEvent(Locale language) {
            val aTournament = anyTournament();
            val sportEntityFactory = providingSportEvent(in(language), aTournament);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sportEvent = provider.getLongTermEvent(aTournament.getId());

            Assertions.assertThat(sportEvent).isSameAs(aTournament);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void returnsNullIfRequestedEventIsNotLongTermEvent(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val aStage = anyStage();
            val sportEntityFactory = providingSportEvent(in(aLanguage), aStage);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            val competition = provider.getLongTermEvent(aStage.getId());

            Assertions.assertThat(competition).isNull();
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetLongTermEvent(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.urnForAnyTournament();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getLongTermEvent(sportEventId))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getLongTermEvent(sportEventId)).isNull();
            }
        }
    }

    @Nested
    class GetLongTermEventForGivenLanguage {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsLongTermEventForGivenLanguage(Locale language) {
            val aTournament = anyTournament();
            val sportEntityFactory = providingSportEvent(in(language), aTournament);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val sportEvent = provider.getLongTermEvent(aTournament.getId(), language);

            Assertions.assertThat(sportEvent).isSameAs(aTournament);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetLongTermEventForGivenLanguage(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.urnForAnyTournament();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getLongTermEvent(sportEventId, aLanguage))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getLongTermEvent(sportEventId, aLanguage)).isNull();
            }
        }
    }

    @Nested
    class GetCompetition {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsCompetition(Locale language) {
            val aStage = anyStage();
            val sportEntityFactory = providingSportEvent(in(language), aStage);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val competition = provider.getCompetition(aStage.getId());

            Assertions.assertThat(competition).isSameAs(aStage);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void returnsNullIfRequestedEventIsNotCompetition(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val aTournament = anyTournament();
            val sportEntityFactory = providingSportEvent(in(aLanguage), aTournament);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            val competition = provider.getCompetition(aTournament.getId());

            Assertions.assertThat(competition).isNull();
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetCompetition(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.urnForAnyStage();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getCompetition(sportEventId))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getCompetition(sportEventId)).isNull();
            }
        }
    }

    @Nested
    class GetCompetitionForGivenLanguage {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsCompetitionForGivenLanguage(Locale language) {
            val aStage = anyStage();
            val sportEntityFactory = providingSportEvent(in(language), aStage);

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .build();

            val competition = provider.getCompetition(aStage.getId(), language);

            Assertions.assertThat(competition).isSameAs(aStage);
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToGetCompetitionForGivenLanguage(ExceptionHandlingStrategy errorHandlingStrategy) {
            val aLanguage = ENGLISH;
            val sportEventId = Urns.SportEvents.urnForAnyStage();
            val sportEntityFactory = failingToProvideSportEvent(sportEventId, in(aLanguage));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getCompetition(sportEventId, aLanguage))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getCompetition(sportEventId, aLanguage)).isNull();
            }
        }
    }

    @Nested
    class GetListOfSportEvents {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsListOfSportEvents(Locale language) {
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = providingSportEvents(in(language), with(ids), asList(aStage, aMatch));

            val dataRouterManager = providingSportEventsList(
                in(language),
                from(1),
                to(10),
                asList(aStage, aMatch)
            );

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            val sportEvents = provider.getListOfSportEvents(1, 10);

            Assertions.assertThat(sportEvents.get(0)).isSameAs(aStage);
            Assertions.assertThat(sportEvents.get(1)).isSameAs(aMatch);
        }

        @Test
        void getListOfSportEventsValidatesInput() {
            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(ENGLISH)
                .with(ExceptionHandlingStrategy.Throw)
                .build();

            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(-1, 10))
                .isInstanceOf(IllegalArgumentException.class);
            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 1001))
                .isInstanceOf(IllegalArgumentException.class);
            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsNoListOfSportEventsWhenNoEventsFound(Locale language) {
            val dataRouterManager = providingSportEventsList(in(language), from(1), to(10), emptyList());

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(dataRouterManager)
                .build();

            val sportEvents = provider.getListOfSportEvents(1, 10);

            Assertions.assertThat(sportEvents).isNull();
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToProvideListOfSportEventsWhenDataRouterManagerThrowsCommunicationException(
            ExceptionHandlingStrategy errorHandlingStrategy
        ) {
            val aLanguage = ENGLISH;
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = providingSportEvents(in(aLanguage), with(ids), asList(aStage, aMatch));

            val dataRouterManager = failsToProvideSportEventsList(in(aLanguage), from(1), to(10));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 10))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getListOfSportEvents(1, 10)).isNull();
            }
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToProvideListOfSportEventsWhenFactoryThrowsObjectNotFound(
            ExceptionHandlingStrategy errorHandlingStrategy
        ) {
            val aLanguage = ENGLISH;
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = failingToProvideSportEvents(in(aLanguage), with(ids));

            val dataRouterManager = failsToProvideSportEventsList(in(aLanguage), from(1), to(10));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 10))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getListOfSportEvents(1, 10)).isNull();
            }
        }
    }

    @Nested
    class GetListOfSportEventsForGivenLanguage {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsListOfSportEventsForGivenLanguage(Locale language) {
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = providingSportEvents(in(language), with(ids), asList(aStage, aMatch));

            val dataRouterManager = providingSportEventsList(
                in(language),
                from(1),
                to(10),
                asList(aStage, aMatch)
            );

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            val sportEvents = provider.getListOfSportEvents(1, 10, language);

            Assertions.assertThat(sportEvents.get(0)).isSameAs(aStage);
            Assertions.assertThat(sportEvents.get(1)).isSameAs(aMatch);
        }

        @Test
        void getListOfSportEventsForGivenLanguageValidatesInput() {
            val language = ENGLISH;

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .build();

            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(-1, 10, language))
                .isInstanceOf(IllegalArgumentException.class);
            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 1001, language))
                .isInstanceOf(IllegalArgumentException.class);
            Assertions
                .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 0, language))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void getsNoListOfSportEventsWhenNoEventsFound(Locale language) {
            val dataRouterManager = providingSportEventsList(in(language), from(1), to(10), emptyList());

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(dataRouterManager)
                .build();

            val sportEvents = provider.getListOfSportEvents(1, 10, language);

            Assertions.assertThat(sportEvents).isNull();
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToProvideListOfSportEventsWhenDataRouterManagerThrowsCommunicationException(
            ExceptionHandlingStrategy errorHandlingStrategy
        ) {
            val aLanguage = ENGLISH;
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = providingSportEvents(in(aLanguage), with(ids), asList(aStage, aMatch));

            val dataRouterManager = failsToProvideSportEventsList(in(aLanguage), from(1), to(10));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 10, aLanguage))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getListOfSportEvents(1, 10, aLanguage)).isNull();
            }
        }

        @ParameterizedTest
        @MethodSource(ERROR_HANDLING_STRATEGIES)
        void failsToProvideListOfSportEventsWhenFactoryThrowsObjectNotFound(
            ExceptionHandlingStrategy errorHandlingStrategy
        ) {
            val aLanguage = ENGLISH;
            val aStage = anyStage();
            val aMatch = anyMatch();
            val ids = asList(aStage.getId(), aMatch.getId());
            val sportEntityFactory = failingToProvideSportEvents(in(aLanguage), with(ids));

            val dataRouterManager = failsToProvideSportEventsList(in(aLanguage), from(1), to(10));

            val provider = stubbingOutSportDataProvider()
                .withDesiredLocale(aLanguage)
                .with(errorHandlingStrategy)
                .with(sportEntityFactory)
                .with(dataRouterManager)
                .build();

            if (errorHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                Assertions
                    .assertThatThrownBy(() -> provider.getListOfSportEvents(1, 10, aLanguage))
                    .isInstanceOf(ObjectNotFoundException.class);
            } else {
                Assertions.assertThat(provider.getListOfSportEvents(1, 10, aLanguage)).isNull();
            }
        }
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, Locale.SIMPLIFIED_CHINESE);
    }

    @SuppressWarnings("unused")
    static Stream<ExceptionHandlingStrategy> errorHandlingStrategies() {
        return Stream.of(ExceptionHandlingStrategy.Throw, ExceptionHandlingStrategy.Catch);
    }
}
