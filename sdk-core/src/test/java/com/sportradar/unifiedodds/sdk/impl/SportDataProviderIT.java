/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.SapiCategories.*;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubQueryParameter.FixtureChangesQueryParameter.AFTER_DATE_TIME;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubQueryParameter.FixtureChangesQueryParameter.SPORT_ID;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubQueryParameter.queryParameter;
import static com.sportradar.unifiedodds.sdk.conn.SapiFixtureChanges.fixtureChanges;
import static com.sportradar.unifiedodds.sdk.conn.SapiResultChanges.resultChanges;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.common.Pair;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.sportsapi.datamodel.SapiFixtureChangesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiResultChangesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentExtended;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.Languages;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SportDataProviderIT {

    private static final String LANGUAGES = "com.sportradar.unifiedodds.sdk.impl.Parameters#languages";
    private static final String SPORTS = "com.sportradar.unifiedodds.sdk.impl.Parameters#sports";
    private static final String DATES = "com.sportradar.unifiedodds.sdk.impl.Parameters#dates";
    private static final String DATES_AND_LANGUAGES =
        "com.sportradar.unifiedodds.sdk.impl.Parameters#datesAngLanguages";
    private static final String DATES_SPORTS_AND_LANGUAGES =
        "com.sportradar.unifiedodds.sdk.impl.Parameters#datesSportsAndLanguages";

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private BaseUrl sportsApiBaseUrl;

    private SportDataProviderIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @Nested
    class GetFixtureChanges {

        @BeforeEach
        void stub() {
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forDefaultLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubFixtureChanges(aLanguage, fixtureChanges());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges();

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forRequestedLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubFixtureChanges(aLanguage, fixtureChanges());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(ENGLISH)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges(aLanguage);

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(SPORTS)
        void forRequestedSportOnly(Urn sportId) throws Exception {
            val aLanguage = Languages.any();

            apiSimulator.stubFixtureChanges(
                aLanguage,
                fixtureChanges(),
                queryParameter(SPORT_ID, sportId.toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges(null, sportId);

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES)
        void afterGivenDateAndDefaultLanguage(Date afterDate) throws Exception {
            val aLanguage = Languages.any();

            apiSimulator.stubFixtureChanges(
                aLanguage,
                fixtureChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges(afterDate, null);

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES_AND_LANGUAGES)
        void afterGivenDateAndGivenLanguage(Date afterDate, Locale language) throws Exception {
            apiSimulator.stubFixtureChanges(
                language,
                fixtureChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges(afterDate, null, language);

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES_SPORTS_AND_LANGUAGES)
        void forAllPossibleParameters(Date afterDate, Urn sportId, Locale language) throws Exception {
            apiSimulator.stubFixtureChanges(
                language,
                fixtureChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString()),
                queryParameter(SPORT_ID, sportId.toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getFixtureChanges(afterDate, sportId, language);

                assertThat(actual(changes)).isEqualTo(expected(fixtureChanges()));
            }
        }

        private List<Pair<String, Date>> actual(List<FixtureChange> changes) {
            return changes
                .stream()
                .map(c -> new Pair<>(c.getSportEventId().toString(), c.getUpdateTime()))
                .collect(toList());
        }

        private List<Pair<String, Date>> expected(SapiFixtureChangesEndpoint sapiFixtureChangesEndpoint) {
            return sapiFixtureChangesEndpoint
                .getFixtureChange()
                .stream()
                .map(c -> new Pair<>(c.getSportEventId(), SdkHelper.toDate(c.getUpdateTime())))
                .collect(toList());
        }
    }

    @Nested
    class GetResultChanges {

        @BeforeEach
        void stub() {
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forDefaultLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubResultChanges(aLanguage, resultChanges());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges();

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forRequestedLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubResultChanges(aLanguage, resultChanges());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(ENGLISH)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges(aLanguage);

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(SPORTS)
        void forRequestedSportOnly(Urn sportId) throws Exception {
            val aLanguage = Languages.any();

            apiSimulator.stubResultChanges(
                aLanguage,
                resultChanges(),
                queryParameter(SPORT_ID, sportId.toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges(null, sportId);

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES)
        void afterGivenDateAndDefaultLanguage(Date afterDate) throws Exception {
            val aLanguage = Languages.any();

            apiSimulator.stubResultChanges(
                aLanguage,
                resultChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges(afterDate, null);

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES_AND_LANGUAGES)
        void afterGivenDateAndGivenLanguage(Date afterDate, Locale language) throws Exception {
            apiSimulator.stubResultChanges(
                language,
                resultChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges(afterDate, null, language);

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        @ParameterizedTest
        @MethodSource(DATES_SPORTS_AND_LANGUAGES)
        void forAllPossibleParameters(Date afterDate, Urn sportId, Locale language) throws Exception {
            apiSimulator.stubResultChanges(
                language,
                resultChanges(),
                queryParameter(AFTER_DATE_TIME, afterDate.toInstant().toString()),
                queryParameter(SPORT_ID, sportId.toString())
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val changes = sportDataProvider.getResultChanges(afterDate, sportId, language);

                assertThat(actual(changes)).isEqualTo(expected(resultChanges()));
            }
        }

        private List<Pair<String, Date>> actual(List<ResultChange> changes) {
            return changes
                .stream()
                .map(c -> new Pair<>(c.getSportEventId().toString(), c.getUpdateTime()))
                .collect(toList());
        }

        private List<Pair<String, Date>> expected(SapiResultChangesEndpoint resultChanges) {
            return resultChanges
                .getResultChange()
                .stream()
                .map(c -> new Pair<>(c.getSportEventId(), SdkHelper.toDate(c.getUpdateTime())))
                .collect(toList());
        }
    }

    @Nested
    class GetSports {

        @BeforeEach
        void stub() {
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forDefaultLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val sports = sportDataProvider.getSports();

                assertThat(actual(sports, in(aLanguage)))
                    .containsExactlyInAnyOrderElementsOf(expected(allSports()));
            }
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forGivenLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val sports = sportDataProvider.getSports(aLanguage);

                assertThat(actual(sports, in(aLanguage)))
                    .containsExactlyInAnyOrderElementsOf(expected(allSports()));
            }
        }

        private List<String> actual(
            List<com.sportradar.unifiedodds.sdk.entities.Sport> sports,
            LanguageHolder language
        ) {
            return sports.stream().map(s -> join(s.getId(), s.getName(language.get()))).collect(toList());
        }

        private List<String> expected(SapiSportsEndpoint sports) {
            return sports.getSport().stream().map(s -> join(s.getId(), s.getName())).collect(toList());
        }
    }

    @Nested
    class GetActiveTournaments {

        @BeforeEach
        void stub() {
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forDefaultLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubAllSports(aLanguage, soccer());
            apiSimulator.stubSportCategories(aLanguage, soccer(), international());
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val tournaments = sportDataProvider.getActiveTournaments();

                assertThat(actual(tournaments, in(aLanguage)))
                    .containsExactlyInAnyOrderElementsOf(expected(tournamentEuro2024()));
            }
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void forGivenLanguage(Locale aLanguage) throws Exception {
            apiSimulator.stubAllSports(aLanguage, soccer());
            apiSimulator.stubSportCategories(aLanguage, soccer(), international());
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(Languages.any())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val tournaments = sportDataProvider.getActiveTournaments(aLanguage);

                assertThat(actual(tournaments, in(aLanguage)))
                    .containsExactlyInAnyOrderElementsOf(expected(tournamentEuro2024()));
            }
        }

        @Test
        void forGivenSportNameForDefaultLanguageIgnoringCase() throws Exception {
            val aLanguage = ENGLISH;
            apiSimulator.stubAllSports(aLanguage, soccer(), tennis(), golf());
            apiSimulator.stubSportCategories(aLanguage, soccer(), international());
            apiSimulator.stubSportCategories(aLanguage, tennis(), atp());
            apiSimulator.stubSportCategories(aLanguage, golf(), men());
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val tournaments = sportDataProvider.getActiveTournaments("SocCeR");

                assertThat(actual(tournaments, in(aLanguage)))
                    .containsExactlyInAnyOrderElementsOf(expected(tournamentEuro2024()));
            }
        }

        private List<String> actual(
            List<com.sportradar.unifiedodds.sdk.entities.SportEvent> tournaments,
            LanguageHolder language
        ) {
            return tournaments
                .stream()
                .map(s -> join(s.getId(), s.getName(language.get())))
                .collect(toList());
        }

        private List<String> expected(SapiTournamentExtended tournament) {
            return singletonList(join(tournament.getId(), tournament.getName()));
        }
    }

    private static String join(Object... parts) {
        return Arrays.stream(parts).map(Object::toString).collect(joining(" - "));
    }
}

class Parameters {

    public static final Instant NOW_INSTANT = Instant.ofEpochSecond(1729079835);
    public static final Date TODAY = Date.from(NOW_INSTANT);
    public static final Date YESTERDAY = Date.from(NOW_INSTANT.minus(1, ChronoUnit.DAYS));
    public static final Date DAY_BEFORE_YESTERDAY = Date.from(NOW_INSTANT.minus(2, ChronoUnit.DAYS));

    public static Stream<Locale> languages() {
        return Stream.of(ENGLISH, GERMAN, FRENCH);
    }

    public static Stream<Urn> sports() {
        return Stream.of(Sport.FOOTBALL, Sport.TENNIS, Sport.ESPORT_DOTA).map(Sport::getUrn);
    }

    public static Stream<Date> dates() {
        return Stream.of(DAY_BEFORE_YESTERDAY, YESTERDAY, TODAY);
    }

    public static Stream<Arguments> datesAngLanguages() {
        return Stream.of(
            Arguments.of(DAY_BEFORE_YESTERDAY, ENGLISH),
            Arguments.of(YESTERDAY, GERMAN),
            Arguments.of(TODAY, FRENCH)
        );
    }

    public static Stream<Arguments> datesSportsAndLanguages() {
        return Stream.of(
            Arguments.of(DAY_BEFORE_YESTERDAY, Sport.FOOTBALL.getUrn(), ENGLISH),
            Arguments.of(YESTERDAY, Sport.TENNIS.getUrn(), GERMAN),
            Arguments.of(TODAY, Sport.ESPORT_DOTA.getUrn(), FRENCH)
        );
    }
}
