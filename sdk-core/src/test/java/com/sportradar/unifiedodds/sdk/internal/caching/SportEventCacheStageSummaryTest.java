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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.Stage;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
import com.sportradar.unifiedodds.sdk.impl.assertions.SportSummaryAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.SportEventAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
class SportEventCacheStageSummaryTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportEventCacheStageSummaryTest#englishAndChinese";
    private static final String TOURNAMENT_STAGE_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.TournamentStagePropertyProviders#tournamentStageProperties";

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

    @ParameterizedTest
    @MethodSource(TOURNAMENT_STAGE_PROPERTIES)
    void fetchesStageTournamentProperties(
        PropertyGetterFrom<Stage> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val stage = formulaOne2025();
        val stageId = Urn.parse(stage.getTournament().getId());

        sapiProperty.setOn(stage);

        val summaryProvider = SummaryDataProviders
            .summaryDataProvider()
            .providing(in(ENGLISH), with(stageId.toString()), stage)
            .build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Catch)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Catch)
            .with(sportEventCache)
            .build();

        val sportEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_STAGE_PROPERTIES)
    void fetchesStageTournamentPropertiesInNonDefaultLanguage(
        PropertyGetterFrom<Stage> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val stage = formulaOne2025();
        val stageId = Urn.parse(stage.getTournament().getId());

        sapiProperty.setOn(stage);

        val summaryProvider = SummaryDataProviders
            .summaryDataProvider()
            .providing(in(ENGLISH), with(stageId.toString()), stage)
            .providing(in(GERMAN), with(stageId.toString()), stage)
            .build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(GERMAN)
            .with(ExceptionHandlingStrategy.Catch)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(GERMAN)
            .with(ExceptionHandlingStrategy.Catch)
            .with(sportEventCache)
            .build();

        val sportEvent = (Stage) sportEntityFactory.buildSportEvent(stageId, asList(ENGLISH, GERMAN), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    private SapiCategory categoryFrom(SapiStageSummaryEndpoint raceStage) {
        return raceStage.getSportEvent().getTournament().getCategory();
    }

    private Urn sportIdFrom(SapiStageSummaryEndpoint stage) {
        return Urn.parse(stage.getSportEvent().getTournament().getSport().getId());
    }

    private SapiSport sportFrom(SapiStageSummaryEndpoint raceStage) {
        return raceStage.getSportEvent().getTournament().getSport();
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber" })
class TournamentStagePropertyProviders {

    static Stream<Arguments> tournamentStageProperties() {
        return Stream.of(
            arguments(
                "name - available",
                stage -> stage.getName(ENGLISH),
                p -> {
                    p.getTournament().setName("Tournament Name");
                },
                "Tournament Name"
            ),
            arguments(
                "name - missing",
                stage -> stage.getName(ENGLISH),
                p -> p.getTournament().setName(null),
                ""
            ),
            arguments(
                "name - empty",
                stage -> stage.getName(ENGLISH),
                p -> p.getTournament().setName(""),
                ""
            ),
            arguments(
                "scheduledTime - available",
                SportEvent::getScheduledTime,
                p -> {
                    val date = LocalDateTime.of(2025, 3, 4, 2, 3, 4);
                    p.getTournament().setScheduled(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2025, 3, 4, 2, 3, 4).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledTime - missing",
                SportEvent::getScheduledTime,
                p -> p.getTournament().setScheduled(null),
                null
            ),
            arguments(
                "scheduledEndTime - available",
                SportEvent::getScheduledEndTime,
                p -> {
                    val date = LocalDateTime.of(2024, 5, 6, 7, 8, 9);
                    p.getTournament().setScheduledEnd(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2024, 5, 6, 7, 8, 9).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledEndTime - missing",
                SportEvent::getScheduledEndTime,
                p -> p.getTournament().setScheduledEnd(null),
                null
            )
        );
    }

    static Arguments arguments(
        String propertyName,
        PropertyGetterFrom<Stage> propertyGetter,
        PropertySetterTo<SapiTournamentInfoEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
