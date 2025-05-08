/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.SapiCategories.getSapiCategory;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiDrawSummaries.MarruecosKenoLottery.marruecosKenoLotteryDrawSummary;
import static com.sportradar.unifiedodds.sdk.conn.SapiLotterySchedules.marruecosKenoLottery;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.WORLD_LOTTERY_SPORT_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.impl.DrawSummaryDataProviders;
import com.sportradar.unifiedodds.sdk.impl.LotteryScheduleDataProviders;
import com.sportradar.unifiedodds.sdk.impl.SportsCategoriesDataProviders;
import com.sportradar.unifiedodds.sdk.impl.SportsDataProviders;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
class SportEventCacheLotteryTest {

    private static final String LOTTERY_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.LotteryPropertyProviders#lotteryProperties";

    private static final String DRAW_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.LotteryPropertyProviders#drawProperties";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(LOTTERY_PROPERTIES)
    void fetchesLotteryProperties(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiLotterySchedule> sapiProperty,
        Object expected
    ) throws Exception {
        val lotterySchedule = marruecosKenoLottery();
        sapiProperty.setOn(lotterySchedule);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn lotteryId = Urn.parse(lotterySchedule.getLottery().getId());
        val lotteryScheduleProvider = LotteryScheduleDataProviders.providing(
            in(ENGLISH),
            with(lotteryId.toString()),
            lotterySchedule
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(marruecosKenoLottery())),
            categoriesFrom(marruecosKenoLottery())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withLotterySchedule(lotteryScheduleProvider)
            .withSports(sportsProvider)
            .withSportCategories(categories)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(marruecosKenoLottery()))
            .providing(in(ENGLISH), sportFrom(marruecosKenoLottery()));

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

        val sportEvent = sportEntityFactory.buildSportEvent(lotteryId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @Test
    void fetchesLotteryCategory() throws Exception {
        val lotterySchedule = marruecosKenoLottery();

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn lotteryId = Urn.parse(lotterySchedule.getLottery().getId());
        val lotteryScheduleProvider = LotteryScheduleDataProviders.providing(
            in(ENGLISH),
            with(lotteryId.toString()),
            lotterySchedule
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(marruecosKenoLottery())),
            categoriesFrom(marruecosKenoLottery())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withLotterySchedule(lotteryScheduleProvider)
            .withSports(sportsProvider)
            .withSportCategories(categories)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(marruecosKenoLottery()))
            .providing(in(ENGLISH), sportFrom(marruecosKenoLottery()));

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

        val lottery = (Lottery) sportEntityFactory.buildSportEvent(lotteryId, singletonList(ENGLISH), false);
        val actualCategory = lottery.getCategory();

        val expectedCategory = getSapiCategory(categoryIdFrom(lotterySchedule), in(ENGLISH));
        assertThat(actualCategory.getId()).isEqualTo(categoryIdFrom(lotterySchedule));
        assertThat(actualCategory.getCountryCode())
            .isEqualTo(expectedCategory.getCategory().getCountryCode());
        assertThat(actualCategory.getNames())
            .isEqualTo(ImmutableMap.of(ENGLISH, expectedCategory.getCategory().getName()));
        assertThat(actualCategory.getName(ENGLISH)).isEqualTo(expectedCategory.getCategory().getName());
    }

    @ParameterizedTest
    @MethodSource(DRAW_PROPERTIES)
    void fetchesDrawProperties(
        PropertyGetterFrom<Draw> property,
        PropertySetterTo<SapiDrawEvent> sapiProperty,
        Object expected
    ) throws Exception {
        val lotterySchedule = marruecosKenoLottery();
        sapiProperty.setOn(lotterySchedule.getDrawEvents().getDrawEvent().get(0));

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn lotteryId = Urn.parse(lotterySchedule.getLottery().getId());
        val lotteryScheduleProvider = LotteryScheduleDataProviders.providing(
            in(ENGLISH),
            with(lotteryId.toString()),
            lotterySchedule
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(marruecosKenoLottery())),
            categoriesFrom(marruecosKenoLottery())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withLotterySchedule(lotteryScheduleProvider)
            .withSports(sportsProvider)
            .withSportCategories(categories)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(marruecosKenoLottery()))
            .providing(in(ENGLISH), sportFrom(marruecosKenoLottery()));

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

        val lottery = (Lottery) sportEntityFactory.buildSportEvent(lotteryId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(lottery.getScheduledDraws().get(0))).isEqualTo(expected);
    }

    @Test
    void fetchesLotteryFromDrawCallingDrawSummary() throws Exception {
        val lotterySchedule = marruecosKenoLottery();

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn lotteryId = Urn.parse(lotterySchedule.getLottery().getId());
        val lotteryScheduleProvider = LotteryScheduleDataProviders.providing(
            in(ENGLISH),
            with(lotteryId.toString()),
            lotterySchedule
        );
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            firstDrawId(lotterySchedule),
            marruecosKenoLotteryDrawSummary()
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(marruecosKenoLottery())),
            categoriesFrom(marruecosKenoLottery())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withLotterySchedule(lotteryScheduleProvider)
            .withDrawSummary(drawSummaryProvider)
            .withSports(sportsProvider)
            .withSportCategories(categories)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(marruecosKenoLottery()))
            .providing(in(ENGLISH), sportFrom(marruecosKenoLottery()));

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

        val lottery = (Lottery) sportEntityFactory.buildSportEvent(lotteryId, singletonList(ENGLISH), false);

        val firstDraw = lottery.getScheduledDraws().get(0);

        assertThat(firstDraw.getLottery().getId()).isEqualTo(lotteryId);
    }

    private String firstDrawId(SapiLotterySchedule schedule) {
        return schedule.getDrawEvents().getDrawEvent().get(0).getId();
    }

    private SapiCategory categoryFrom(SapiLotterySchedule lotterySchedule) {
        return lotterySchedule.getLottery().getCategory();
    }

    private Urn categoryIdFrom(SapiLotterySchedule lotterySchedule) {
        return Urn.parse(lotterySchedule.getLottery().getCategory().getId());
    }

    private String sportIdFrom(SapiLotterySchedule lotterySchedule) {
        return lotterySchedule.getLottery().getSport().getId();
    }

    private SapiSportCategoriesEndpoint categoriesFrom(SapiLotterySchedule lotterySchedule) {
        val categories = new SapiSportCategoriesEndpoint();
        categories.setSport(lotterySchedule.getLottery().getSport());
        categories.setCategories(new SapiCategories());
        categories.getCategories().getCategory().add(lotterySchedule.getLottery().getCategory());
        return categories;
    }

    private SapiSportsEndpoint sportsFrom(SapiTournamentExtended tournament) {
        SapiSportsEndpoint sports = new SapiSportsEndpoint();
        sports.getSport().add(tournament.getSport());
        return sports;
    }

    private SapiSport sportFrom(SapiLotterySchedule lotterySchedule) {
        return lotterySchedule.getLottery().getSport();
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals" })
class LotteryPropertyProviders {

    private static final SapiSport WORLD_LOTTERY_SPORT_IN_ENGLISH = getSapiSport(
        Urn.parse(WORLD_LOTTERY_SPORT_ID),
        in(ENGLISH)
    );

    private static Stream<Arguments> lotteryProperties() {
        return Stream.of(
            arguments(
                "id",
                Lottery::getId,
                s -> s.getLottery().setId("wns:lottery:1112"),
                Urn.parse("wns:lottery:1112")
            ),
            arguments(
                "sportId",
                Lottery::getSportId,
                s -> s.getLottery().setSport(WORLD_LOTTERY_SPORT_IN_ENGLISH),
                Urn.parse(WORLD_LOTTERY_SPORT_ID)
            ),
            arguments(
                "sport - id",
                l -> l.getSport().getId(),
                s -> s.getLottery().setSport(WORLD_LOTTERY_SPORT_IN_ENGLISH),
                Urn.parse(WORLD_LOTTERY_SPORT_ID)
            ),
            arguments(
                "sport - name",
                l -> l.getSport().getName(ENGLISH),
                s -> s.getLottery().setSport(WORLD_LOTTERY_SPORT_IN_ENGLISH),
                WORLD_LOTTERY_SPORT_IN_ENGLISH.getName()
            ),
            arguments(
                "name - available",
                l -> l.getName(ENGLISH),
                s -> s.getLottery().setName("The Best Lottery"),
                "The Best Lottery"
            ),
            arguments("name - empty", l -> l.getName(ENGLISH), s -> s.getLottery().setName(""), ""),
            arguments("name - missing", l -> l.getName(ENGLISH), s -> s.getLottery().setName(null), ""),
            arguments(
                "tournamentCoverage - always null",
                LongTermEvent::getTournamentCoverage,
                s -> {},
                null
            ),
            arguments("scheduledTime - always null", LongTermEvent::getScheduledTime, s -> {}, null),
            arguments("scheduledEndTime - always null", LongTermEvent::getScheduledEndTime, s -> {}, null),
            arguments("isStartTimeTbd - always null", LongTermEvent::isStartTimeTbd, s -> {}, null),
            arguments("replacedBy - always null", LongTermEvent::getReplacedBy, s -> {}, null),
            arguments("toString - is available", l -> l.toString().contains("LotteryImpl"), s -> {}, true),
            arguments(
                "drawInfo - missing",
                Lottery::getDrawInfo,
                s -> s.getLottery().setDrawInfo(null),
                null
            ),
            arguments(
                "drawInfo - drawType RNG",
                l -> l.getDrawInfo().getDrawType(),
                s -> s.getLottery().getDrawInfo().setDrawType(SapiDrawType.RNG),
                DrawType.Rng
            ),
            arguments(
                "drawInfo - drawType DRUM",
                l -> l.getDrawInfo().getDrawType(),
                s -> s.getLottery().getDrawInfo().setDrawType(SapiDrawType.DRUM),
                DrawType.Drum
            ),
            arguments(
                "drawInfo - drawType missing",
                l -> l.getDrawInfo().getDrawType(),
                s -> s.getLottery().getDrawInfo().setDrawType(null),
                DrawType.Unknown
            ),
            arguments(
                "drawInfo - gameType available",
                l -> l.getDrawInfo().getGameType(),
                s -> s.getLottery().getDrawInfo().setGameType("6/41"),
                "6/41"
            ),
            arguments(
                "drawInfo - gameType missing",
                l -> l.getDrawInfo().getGameType(),
                s -> s.getLottery().getDrawInfo().setGameType(null),
                null
            ),
            arguments(
                "drawInfo - timeType INTERVAL",
                l -> l.getDrawInfo().getTimeType(),
                s -> s.getLottery().getDrawInfo().setTimeType(SapiTimeType.INTERVAL),
                TimeType.Interval
            ),
            arguments(
                "drawInfo - timeType FIXED",
                l -> l.getDrawInfo().getTimeType(),
                s -> s.getLottery().getDrawInfo().setTimeType(SapiTimeType.FIXED),
                TimeType.Fixed
            ),
            arguments(
                "drawInfo - timeType missing",
                l -> l.getDrawInfo().getTimeType(),
                s -> s.getLottery().getDrawInfo().setTimeType(null),
                TimeType.Unknown
            ),
            arguments(
                "bonusInfo - bonusBalls available",
                l -> l.getBonusInfo().getBonusBalls(),
                s -> s.getLottery().getBonusInfo().setBonusBalls(2),
                2
            ),
            arguments(
                "bonusInfo - bonusBalls missing",
                l -> l.getBonusInfo().getBonusBalls(),
                s -> s.getLottery().getBonusInfo().setBonusBalls(null),
                null
            ),
            arguments(
                "bonusInfo - bonusDrum ADDITIONAL",
                l -> l.getBonusInfo().getBonusDrumType(),
                s -> s.getLottery().getBonusInfo().setBonusDrum(SapiBonusDrumType.ADDITIONAL),
                BonusDrumType.Additional
            ),
            arguments(
                "bonusInfo - bonusDrum SAME",
                l -> l.getBonusInfo().getBonusDrumType(),
                s -> s.getLottery().getBonusInfo().setBonusDrum(SapiBonusDrumType.SAME),
                BonusDrumType.Same
            ),
            arguments(
                "bonusInfo - bonusDrum missing",
                l -> l.getBonusInfo().getBonusDrumType(),
                s -> s.getLottery().getBonusInfo().setBonusDrum(null),
                null
            ),
            arguments(
                "bonusInfo - bonusRange available",
                l -> l.getBonusInfo().getBonusRange(),
                s -> s.getLottery().getBonusInfo().setBonusRange("1-41"),
                "1-41"
            ),
            arguments(
                "bonusInfo - bonusRange empty",
                l -> l.getBonusInfo().getBonusRange(),
                s -> s.getLottery().getBonusInfo().setBonusRange(""),
                ""
            ),
            arguments(
                "bonusInfo - bonusRange missing",
                l -> l.getBonusInfo().getBonusRange(),
                s -> s.getLottery().getBonusInfo().setBonusRange(null),
                null
            )
        );
    }

    private static Stream<Arguments> drawProperties() {
        return Stream.of(
            drawArguments("id", Draw::getId, draw -> draw.setId("wns:draw:1112"), Urn.parse("wns:draw:1112")),
            drawArguments("name is always null", d -> d.getName(ENGLISH), draw -> {}, null),
            drawArguments(
                "status - open",
                Draw::getStatus,
                draw -> draw.setStatus(SapiDrawStatus.OPEN),
                DrawStatus.Open
            ),
            drawArguments(
                "status - closed",
                Draw::getStatus,
                draw -> draw.setStatus(SapiDrawStatus.CLOSED),
                DrawStatus.Closed
            ),
            drawArguments(
                "status - finished",
                Draw::getStatus,
                draw -> draw.setStatus(SapiDrawStatus.FINISHED),
                DrawStatus.Finished
            ),
            drawArguments(
                "status - cancelled",
                Draw::getStatus,
                draw -> draw.setStatus(SapiDrawStatus.CANCELED),
                DrawStatus.Cancelled
            )
        );
    }

    private static SapiReferenceIds addReference(String reference, String id) {
        SapiReferenceIds referenceIds = new SapiReferenceIds();
        SapiReferenceIds.SapiReferenceId referenceId = new SapiReferenceIds.SapiReferenceId();
        referenceId.setName(reference);
        referenceId.setValue(id);
        referenceIds.getReferenceId().add(referenceId);
        return referenceIds;
    }

    static Arguments drawArguments(
        String propertyName,
        PropertyGetterFrom<Draw> propertyGetter,
        PropertySetterTo<SapiDrawEvent> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }

    static Arguments arguments(
        String propertyName,
        PropertyGetterFrom<Lottery> propertyGetter,
        PropertySetterTo<SapiLotterySchedule> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
