/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiSportEvents.SkiJumping.FourHillsTournament.FOUR_HILLS_TOURNAMENT_STAGE_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Qualifying.bahrainGrandPrix2025QualifyingStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Race.bahrainGrandPrix2025RaceStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Races.Practice3.bahrainGrandPrix2025Practice3Stage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.bahrainGrandPrix2025;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.GrandPrix2024.grandPrix2024RaceStageEndpoint;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.SkiJumping.FourHillsTournament.Insbruck.insbruckFourHillsSkiJumping;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FormulaOne2025.formulaOne2025;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.entities.Stage;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
import com.sportradar.unifiedodds.sdk.impl.assertions.SportSummaryAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.SportEventAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
class SportEventCacheStageTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportEventCacheStageTest#englishAndChinese";

    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(ENGLISH_AND_CHINESE)
    void fetchesSport(Locale language) throws Exception {
        val stage = grandPrix2024RaceStageEndpoint();
        val stageId = Urn.parse(stage.getSportEvent().getId());

        val summaryProvider = SummaryDataProviders.providing(in(language), with(stageId.toString()), stage);

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(language), categoryFrom(grandPrix2024RaceStageEndpoint()))
            .providing(in(language), sportFrom(grandPrix2024RaceStageEndpoint()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val stageEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, singletonList(language), false);

        val sport = stageEvent.getSport();
        SportSummaryAssert
            .assertThat(sport, in(language))
            .hasIdAndNameEqualTo(getSapiSport(sportIdFrom(stage), in(language)));

        val sportId = stageEvent.getSportId();
        assertThat(sportId).isEqualTo(sportIdFrom(stage));
    }

    @Test
    void fetchesParentStage() throws Exception {
        val stage = bahrainGrandPrix2025();
        val stageId = Urn.parse(stage.getSportEvent().getId());

        val summaryProvider = SummaryDataProviders
            .summaryDataProvider()
            .providing(in(ENGLISH), with(stageId.toString()), stage)
            .providing(in(ENGLISH), with(formulaOne2025().getTournament().getId()), formulaOne2025())
            .build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(grandPrix2024RaceStageEndpoint()))
            .providing(in(ENGLISH), sportFrom(grandPrix2024RaceStageEndpoint()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val stageEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, singletonList(ENGLISH), false);

        SportEventAssert
            .assertThat(stageEvent.getParentStage())
            .hasName(with(formulaOne2025().getTournament().getName(), in(ENGLISH)));
    }

    @Test
    void fetchesStagesList() throws Exception {
        val stage = bahrainGrandPrix2025();
        val stageId = Urn.parse(stage.getSportEvent().getId());

        val summaryProviderBuilder = SummaryDataProviders
            .summaryDataProvider()
            .providing(in(ENGLISH), with(stageId.toString()), stage)
            .providing(
                in(ENGLISH),
                with(bahrainGrandPrix2025RaceStage().getSportEvent().getId()),
                bahrainGrandPrix2025RaceStage()
            )
            .providing(
                in(ENGLISH),
                with(bahrainGrandPrix2025Practice3Stage().getSportEvent().getId()),
                bahrainGrandPrix2025Practice3Stage()
            )
            .providing(
                in(ENGLISH),
                with(bahrainGrandPrix2025QualifyingStage().getSportEvent().getId()),
                bahrainGrandPrix2025QualifyingStage()
            );

        val summaryProvider = summaryProviderBuilder.build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(grandPrix2024RaceStageEndpoint()))
            .providing(in(ENGLISH), sportFrom(grandPrix2024RaceStageEndpoint()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val stageEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, singletonList(ENGLISH), false);

        assertThat(stageEvent.getStages().stream().map(s -> s.getName(ENGLISH)))
            .containsExactly(
                bahrainGrandPrix2025Practice3Stage().getSportEvent().getName(),
                bahrainGrandPrix2025QualifyingStage().getSportEvent().getName(),
                bahrainGrandPrix2025RaceStage().getSportEvent().getName()
            );
    }

    @Test
    void fetchesAdditionalParentStages() throws Exception {
        val stage = insbruckFourHillsSkiJumping();
        val stageId = Urn.parse(stage.getSportEvent().getId());

        val summaryProviderBuilder = SummaryDataProviders
            .summaryDataProvider()
            .providing(in(ENGLISH), with(stageId.toString()), stage)
            .providing(
                in(ENGLISH),
                with(insbruckFourHillsSkiJumping().getSportEvent().getId()),
                insbruckFourHillsSkiJumping()
            );

        val summaryProvider = summaryProviderBuilder.build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(insbruckFourHillsSkiJumping()))
            .providing(in(ENGLISH), sportFrom(insbruckFourHillsSkiJumping()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val stageEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, singletonList(ENGLISH), false);

        assertThat(stageEvent.getAdditionalParentStages().stream().map(Stage::getId))
            .containsExactly(Urn.parse(FOUR_HILLS_TOURNAMENT_STAGE_ID));
    }

    private SapiCategory categoryFrom(SapiStageSummaryEndpoint lotterySchedule) {
        return lotterySchedule.getSportEvent().getTournament().getCategory();
    }

    private Urn sportIdFrom(SapiStageSummaryEndpoint stage) {
        return Urn.parse(stage.getSportEvent().getTournament().getSport().getId());
    }

    private SapiSport sportFrom(SapiStageSummaryEndpoint stage) {
        return stage.getSportEvent().getTournament().getSport();
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }
}
