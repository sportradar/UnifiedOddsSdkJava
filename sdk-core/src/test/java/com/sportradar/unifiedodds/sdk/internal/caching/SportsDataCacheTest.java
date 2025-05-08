/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.SapiCategories.getSapiCategory;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.impl.SportsCategoriesDataProviders.providingAllCategories;
import static com.sportradar.unifiedodds.sdk.impl.SportsDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.impl.TournamentsDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentExtended;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentsEndpoint;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.impl.assertions.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SportsDataCacheTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCacheTestData#englishAndChinese";
    private static final String ALL_SPORTS_IN_ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCacheTestData#allSportsInEnglishAndChinese";
    private static final String CATEGORIES_IN_ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCacheTestData#categoriesInEnglishAndChinese";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(ENGLISH_AND_CHINESE)
    void getsAllSports(Locale language) throws Exception {
        val dataRouter = new DataRouterImpl();
        val sportsProvider = providing(in(language), allSports(in(language)));
        val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

        val categoriesProvider = providingAllCategories(in(language));

        val dataRouterManager = dataRouterManagerBuilder
            .withSports(sportsProvider)
            .withTournaments(tournamentsProvider)
            .withSportCategories(categoriesProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportsDataCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportsDataCache)
            .build();

        val sports = sportEntityFactory.buildSports(singletonList(language));

        SportListAssert
            .assertThat(sports, in(language))
            .containsExactlyAllElementsInAnyOrderComparingIdAndName(allSports(in(language)));
    }

    @ParameterizedTest
    @MethodSource(ALL_SPORTS_IN_ENGLISH_AND_CHINESE)
    void getsSingleSport(Urn sportId, Locale language) throws Exception {
        val dataRouter = new DataRouterImpl();
        val sportsProvider = providing(in(language), allSports(in(language)));
        val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

        val categoriesProvider = providingAllCategories(in(language));

        val dataRouterManager = dataRouterManagerBuilder
            .withSports(sportsProvider)
            .withTournaments(tournamentsProvider)
            .withSportCategories(categoriesProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportsDataCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportsDataCache)
            .build();

        val sport = sportEntityFactory.buildSport(sportId, singletonList(language));

        SportAssert.assertThat(sport, in(language)).hasIdAndNameEqualTo(getSapiSport(sportId, in(language)));
    }

    @ParameterizedTest
    @MethodSource(ALL_SPORTS_IN_ENGLISH_AND_CHINESE)
    void getsCategoriesFromSport(Urn sportId, Locale language) throws Exception {
        val dataRouter = new DataRouterImpl();
        val sportsProvider = providing(in(language), allSports(in(language)));
        val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

        val categoriesProvider = providingAllCategories(in(language));

        val dataRouterManager = dataRouterManagerBuilder
            .withSports(sportsProvider)
            .withTournaments(tournamentsProvider)
            .withSportCategories(categoriesProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportsDataCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportsDataCache)
            .build();

        val sport = sportEntityFactory.buildSport(sportId, singletonList(language));

        val expectedCategoriesForSport = SapiCategories.allCategories(in(language)).get(sportId);

        CategoryListAssert
            .assertThat(sport.getCategories(), in(language))
            .containsExactlyAllElementsInAnyOrderComparingIdNameAndCountryCode(expectedCategoriesForSport);
    }

    @Test
    void gettingSingleCategoryFailsIfCacheIsNotPopulatedBeforehandByOtherCalls() throws Exception {
        val language = ENGLISH;
        val dataRouter = new DataRouterImpl();
        val sportsProvider = providing(in(language), allSports(in(language)));
        val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

        val categoriesProvider = providingAllCategories(in(language));

        val dataRouterManager = dataRouterManagerBuilder
            .withSports(sportsProvider)
            .withTournaments(tournamentsProvider)
            .withSportCategories(categoriesProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportsDataCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportsDataCache)
            .build();

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() ->
                sportEntityFactory.buildCategory(
                    Urn.parse(SapiCategories.atp().getId()),
                    singletonList(language)
                )
            );
    }

    @ParameterizedTest
    @MethodSource(CATEGORIES_IN_ENGLISH_AND_CHINESE)
    void getsSingleCategoryProvidedItWasLoadedToCacheBeforehandByGetAllSports(
        Urn categoryId,
        Locale language
    ) throws Exception {
        val dataRouter = new DataRouterImpl();
        val sportsProvider = providing(in(language), allSports(in(language)));
        val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

        val categoriesProvider = providingAllCategories(in(language));

        val dataRouterManager = dataRouterManagerBuilder
            .withSports(sportsProvider)
            .withTournaments(tournamentsProvider)
            .withSportCategories(categoriesProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportsDataCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportsDataCache)
            .build();

        sportEntityFactory.buildSports(singletonList(language));
        val category = sportEntityFactory.buildCategory(categoryId, singletonList(language));

        CategoryAssert
            .assertThat(category, in(language))
            .hasIdNameAndCountryCodeEqualTo(getSapiCategory(categoryId, in(language)));
    }

    @Nested
    class ExportImport {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_CHINESE)
        void exportsAndImportsSportsDataToEmptyCache(Locale language) throws Exception {
            val dataRouter = new DataRouterImpl();
            val sportsProvider = providing(in(language), allSports(in(language)));
            val tournamentsProvider = providing(in(language), tournaments(tournamentEuro2024()));

            val categoriesProvider = providingAllCategories(in(language));

            val dataRouterManager = dataRouterManagerBuilder
                .withSports(sportsProvider)
                .withTournaments(tournamentsProvider)
                .withSportCategories(categoriesProvider)
                .with(dataRouter)
                .build();

            val sportsDataCacheForExport = stubbingOutDataRouterManager()
                .withDefaultLanguage(language)
                .with(dataRouterManager)
                .build();

            dataRouter.setDataListeners(singletonList(sportsDataCacheForExport));

            val before = sportsDataCacheForExport.getSports(singletonList(language));

            val sportsDataCacheForImport = stubbingOutDataRouterManager()
                .withDefaultLanguage(language)
                .with(dataRouterManager)
                .build();

            sportsDataCacheForImport.importItems(sportsDataCacheForExport.exportItems());

            val after = sportsDataCacheForImport.getSports(singletonList(language));
            SportDataListAssert.assertThat(before).containsExactlyAllElementsInAnyOrder(after);
        }
    }

    private SapiTournamentsEndpoint tournaments(SapiTournamentExtended tournament) {
        val tournaments = new SapiTournamentsEndpoint();
        tournaments.getTournament().add(tournament);
        return tournaments;
    }
}

@SuppressWarnings({ "unused" })
class SportsDataCacheTestData {

    private static final int NUMBER_OF_SPORTS_OR_CATEGORIES_UNDER_TEST = 100;

    private static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }

    static Stream<Arguments> allSportsInEnglishAndChinese() {
        return allSports()
            .getSport()
            .stream()
            .map(SapiSport::getId)
            .sorted()
            .map(Urn::parse)
            .limit(NUMBER_OF_SPORTS_OR_CATEGORIES_UNDER_TEST)
            .flatMap(sapiSportId ->
                Stream.of(Arguments.of(sapiSportId, ENGLISH), Arguments.of(sapiSportId, CHINESE))
            );
    }

    static Stream<Arguments> categoriesInEnglishAndChinese() {
        val allUniqueCategoryIds = SapiCategories
            .allCategories(in(ENGLISH))
            .values()
            .stream()
            .flatMap(List::stream)
            .map(SapiCategory::getId)
            .collect(Collectors.toSet());
        return allUniqueCategoryIds
            .stream()
            .sorted()
            .limit(NUMBER_OF_SPORTS_OR_CATEGORIES_UNDER_TEST)
            .map(Urn::parse)
            .flatMap(categoryId ->
                Stream.of(Arguments.of(categoryId, ENGLISH), Arguments.of(categoryId, CHINESE))
            );
    }
}
