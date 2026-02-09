/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiDrawSummaries.MarruecosKenoLottery.marruecosKenoLotteryDrawSummary;
import static com.sportradar.unifiedodds.sdk.conn.SapiLotterySchedules.marruecosKenoLottery;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.google.common.collect.Streams;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawResult;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawSummary;
import com.sportradar.unifiedodds.sdk.entities.Draw;
import com.sportradar.unifiedodds.sdk.entities.Lottery;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.DrawSummaryDataProviders;
import com.sportradar.unifiedodds.sdk.impl.LotteryScheduleDataProviders;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mockito;

@SuppressWarnings("ClassFanOutComplexity")
class SportEventCacheDrawResultsTest {

    private static final String DRAW_RESULTS_PROPERTIES_FROM_DRAW_SUMMARY_IN_ENGLISH =
        "com.sportradar.unifiedodds.sdk.internal.caching.DrawResultsPropertyProviders" +
        "#drawResultsPropertiesFromDrawSummaryInEnglish";
    private static final String SINGLE_BALL_DRAW_NAME_FROM_DRAW_SUMMARY_IN_FRENCH =
        "com.sportradar.unifiedodds.sdk.internal.caching.DrawResultsPropertyProviders" +
        "#singleBallDrawNameFromDrawSummaryInFrench";

    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @Test
    void requiresNonNullDrawValue() throws Exception {
        val drawSummary = marruecosKenoLotteryDrawSummary();
        drawSummary.getDrawResult().getDraws().getDraw().get(0).setValue(null);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummary.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummary
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, singletonList(ENGLISH), false);

        assertThatThrownBy(() -> ((Draw) sportEvent).getResults()).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource(DRAW_RESULTS_PROPERTIES_FROM_DRAW_SUMMARY_IN_ENGLISH)
    void fetchesDrawProperties(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiDrawSummary> sapiProperty,
        Object expected
    ) throws Exception {
        val drawSummary = marruecosKenoLotteryDrawSummary();
        sapiProperty.setOn(drawSummary);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummary.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummary
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(DRAW_RESULTS_PROPERTIES_FROM_DRAW_SUMMARY_IN_ENGLISH)
    void fetchesDrawPropertiesForFirstRequestedLanguageEvenIfMoreLanguagesAreRequested(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiDrawSummary> sapiProperty,
        Object expected
    ) throws Exception {
        val drawSummaryEnglish = marruecosKenoLotteryDrawSummary(ENGLISH);
        sapiProperty.setOn(drawSummaryEnglish);
        val drawSummaryFrench = marruecosKenoLotteryDrawSummary(FRENCH);
        drawSummaryFrench.setDrawResult(null);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummaryEnglish.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummaryEnglish,
            in(FRENCH),
            valueOf(drawId),
            drawSummaryFrench
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, asList(ENGLISH, FRENCH), false);
        ((Draw) sportEvent).getResults();

        InOrder inOrder = inOrder(drawSummaryProvider);
        inOrder.verify(drawSummaryProvider).getData(eq(ENGLISH), anyString());
        inOrder.verify(drawSummaryProvider).getData(eq(FRENCH), anyString());
        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @Test
    void drawResultSupportsOnlyLanguagesSuppliedOnItsCreationAndDropsOnTheFloorRequestsForAnyNewLanguagesAfterThat()
        throws Exception {
        val drawSummaryEnglish = marruecosKenoLotteryDrawSummary(ENGLISH);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummaryEnglish.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummaryEnglish
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, asList(ENGLISH), false);

        assertThat(((Draw) sportEvent).getResults().get(0).getName(ENGLISH))
            .isEqualTo(drawSummaryEnglish.getDrawResult().getDraws().getDraw().get(0).getName());
        assertThat(((Draw) sportEvent).getResults().get(0).getName(FRENCH)).isNull();
        verify(drawSummaryProvider, times(1)).getData(any(Locale.class), anyString());
    }

    @Test
    void drawResultSupportsOnlyLanguagesSuppliedByLotteryObjectTheDrawWasRetrievedFrom() throws Exception {
        val lotteryScheduleEnglish = marruecosKenoLottery(ENGLISH);
        val drawSummaryEnglish = marruecosKenoLotteryDrawSummary(ENGLISH);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummaryEnglish.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummaryEnglish
        );

        Urn lotteryId = Urn.parse(lotteryScheduleEnglish.getLottery().getId());
        val lotteryScheduleProvider = LotteryScheduleDataProviders.providing(
            in(ENGLISH),
            with(lotteryId.toString()),
            lotteryScheduleEnglish
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .withLotterySchedule(lotteryScheduleProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(lotteryId, asList(ENGLISH), false);

        Lottery lottery = (Lottery) sportEvent;
        assertThat(lottery.getName(ENGLISH)).isNotNull();
        assertThat(lottery.getScheduledDraws().get(0).getResults().get(0).getName(ENGLISH)).isNotNull();
        assertThat(lottery.getScheduledDraws().get(0).getResults().get(0).getName(FRENCH)).isNull();
        verify(drawSummaryProvider, times(1)).getData(any(Locale.class), anyString());
    }

    @ParameterizedTest
    @MethodSource(SINGLE_BALL_DRAW_NAME_FROM_DRAW_SUMMARY_IN_FRENCH)
    void singleBallDrawNameInSecondAndSubsequentRequestedLanguageIsAlsoTranslatedWithoutLoosingFirstLanguageTranslation(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiDrawSummary> sapiProperty,
        Object expected
    ) throws Exception {
        val drawSummaryEnglish = marruecosKenoLotteryDrawSummary(ENGLISH);
        val drawSummaryFrench = marruecosKenoLotteryDrawSummary(FRENCH);
        sapiProperty.setOn(drawSummaryFrench);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummaryEnglish.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummaryEnglish,
            in(FRENCH),
            valueOf(drawId),
            drawSummaryFrench
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, asList(ENGLISH, FRENCH), false);

        assertThat(((Draw) sportEvent).getResults().get(0).getName(ENGLISH))
            .isEqualTo(drawSummaryEnglish.getDrawResult().getDraws().getDraw().get(0).getName());
        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @Test
    void singleBallDrawsWithinDrawEventAreRecognisedOnceOnInitialLanguageFetchOnlyWhileOnesAppearingLaterWillBeDropped()
        throws Exception {
        val drawSummaryEnglish = marruecosKenoLotteryDrawSummary(ENGLISH);
        removeAllSingleBallDrawsButFirstOne(drawSummaryEnglish);
        drawSummaryEnglish.getDrawResult().getDraws().getDraw().get(0).setValue(5);

        val drawSummaryFrench = marruecosKenoLotteryDrawSummary(FRENCH);
        removeAllSingleBallDrawsButFirstOne(drawSummaryFrench);
        drawSummaryFrench.getDrawResult().getDraws().getDraw().get(0).setValue(10);

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummaryEnglish.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummaryEnglish,
            in(FRENCH),
            valueOf(drawId),
            drawSummaryFrench
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withDrawSummary(drawSummaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, asList(ENGLISH, FRENCH), false);

        assertThat(((Draw) sportEvent).getResults()).hasSize(1);
        assertThat(((Draw) sportEvent).getResults().get(0).getName(ENGLISH)).isNotNull();
        assertThat(((Draw) sportEvent).getResults().get(0).getName(FRENCH)).isNull();
    }

    private static SapiDrawResult.SapiDraws.SapiDraw removeAllSingleBallDrawsButFirstOne(
        SapiDrawSummary drawSummary
    ) {
        val drawInEnglish = drawSummary.getDrawResult().getDraws().getDraw().get(0);
        drawSummary.getDrawResult().getDraws().getDraw().clear();
        drawSummary.getDrawResult().getDraws().getDraw().add(drawInEnglish);
        return drawInEnglish;
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals" })
class DrawResultsPropertyProviders {

    private static Stream<Arguments> drawResultsPropertiesFromDrawSummaryInEnglish() {
        return Streams.concat(
            resultsWrappingFromDrawSummary(),
            drawResultNameFromDrawSummary(ENGLISH),
            drawResultValueFromDrawSummary()
        );
    }

    private static Stream<Arguments> singleBallDrawNameFromDrawSummaryInFrench() {
        return Streams.concat(drawResultNameFromDrawSummary(FRENCH));
    }

    @NotNull
    private static Stream<Arguments> drawResultValueFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "draw value in draw results",
                draw -> draw.getResults().get(0).getValue(),
                s -> s.getDrawResult().getDraws().getDraw().get(0).setValue(8),
                8
            )
        );
    }

    private static Stream<Arguments> drawResultNameFromDrawSummary(Locale language) {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "draw name in draw results",
                draw -> draw.getResults().get(0).getName(language),
                s -> s.getDrawResult().getDraws().getDraw().get(0).setName("single ball draw"),
                "single ball draw"
            ),
            argumentPropertyFromDrawSummary(
                "empty draw name in draw results",
                draw -> draw.getResults().get(0).getName(language),
                s -> s.getDrawResult().getDraws().getDraw().get(0).setName(""),
                ""
            ),
            argumentPropertyFromDrawSummary(
                "null draw name in draw results - is translated to empty string",
                draw -> draw.getResults().get(0).getName(language),
                s -> s.getDrawResult().getDraws().getDraw().get(0).setName(null),
                ""
            )
        );
    }

    private static Stream<Arguments> resultsWrappingFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "null draw results wrapper in xml draw results in null DrawResult",
                draw -> draw.getResults(),
                s -> s.setDrawResult(null),
                null
            ),
            argumentPropertyFromDrawSummary(
                "null collection in draws wrapper from SapiDrawResults will return null DrawResults",
                draw -> draw.getResults(),
                s -> s.getDrawResult().setDraws(null),
                null
            ),
            argumentPropertyFromDrawSummary(
                "empty list of Draws from SapiDrawResults will return null results",
                draw -> draw.getResults(),
                s -> s.getDrawResult().getDraws().getDraw().clear(),
                null
            )
        );
    }

    static Arguments argumentPropertyFromDrawSummary(
        String propertyName,
        PropertyGetterFrom<Draw> propertyGetter,
        PropertySetterTo<SapiDrawSummary> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
