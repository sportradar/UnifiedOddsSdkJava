/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiDrawSummaries.MarruecosKenoLottery.marruecosKenoLotteryDrawSummary;
import static com.sportradar.unifiedodds.sdk.conn.SapiLotterySchedules.marruecosKenoLottery;
import static com.sportradar.unifiedodds.sdk.conn.SapiLotterySchedules.sportAndCategoryFrom;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Streams;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.impl.DrawSummaryDataProviders;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
class SportEventCacheDrawTest {

    private static final String NON_SPORT_DRAW_PROPERTIES_FROM_SUMMARY =
        "com.sportradar.unifiedodds.sdk.internal.caching.DrawPropertyProviders#nonSportDrawPropertiesFromDrawSummary";

    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @Test
    void requiresNonNullDrawId() {
        DataRouterImpl dataRouter = new DataRouterImpl();

        val dataRouterManager = dataRouterManagerBuilder.with(dataRouter).build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        assertThatThrownBy(() -> sportEntityFactory.buildSportEvent(null, singletonList(ENGLISH), false))
            .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource(NON_SPORT_DRAW_PROPERTIES_FROM_SUMMARY)
    void fetchesNonSportDrawProperties(
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

    @Test
    void retrievesSportIdWhichWasProvidedOnCreationOfTheDraw() throws Exception {
        val drawSummary = marruecosKenoLotteryDrawSummary();

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

        Urn injectedSportId = Urn.parse("sr:sport:9797");
        val sportEvent = sportEntityFactory.buildSportEvent(
            drawId,
            injectedSportId,
            singletonList(ENGLISH),
            false
        );

        assertThat(sportEvent.getSportId()).isEqualTo(injectedSportId);
    }

    @Test
    void retrievesSportIdCrosscheckingItWithSportsDataCacheIfSportIdWasNotInjectedIntoTheDrawOnCreation()
        throws Exception {
        val drawSummary = marruecosKenoLotteryDrawSummary();

        DataRouterImpl dataRouter = new DataRouterImpl();
        Urn drawId = Urn.parse(drawSummary.getDrawFixture().getId());
        val drawSummaryProvider = DrawSummaryDataProviders.providing(
            in(ENGLISH),
            valueOf(drawId),
            drawSummary
        );

        val sportsDataCache = SportsDataCaches.providing(
            in(ENGLISH),
            sportAndCategoryFrom(marruecosKenoLottery())
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
            .with(sportsDataCache)
            .build();

        val sportEvent = sportEntityFactory.buildSportEvent(drawId, singletonList(ENGLISH), false);

        assertThat(sportEvent.getSportId()).isEqualTo(marruecosKenoLottery().getLottery().getSport().getId());
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals" })
class DrawPropertyProviders {

    private static Stream<Arguments> nonSportDrawPropertiesFromDrawSummary() {
        return Streams.concat(
            idFromDrawSummary(),
            lotteryIdFromDrawSummary(),
            scheduledDateFromDrawSummary(),
            statusFromDrawSummary(),
            displayIdFromDrawSummary(),
            startToBeDecidedFieldIsNeverPopulatedInDrawSummary(),
            nameFieldIsNeverPopulatedInDrawSummary(),
            replacedByFieldIsNeverPopulatedInDrawSummary(),
            scheduledEndTimeFieldIsNeverPopulatedInDrawSummary()
        );
    }

    private static Stream<? extends Arguments> startToBeDecidedFieldIsNeverPopulatedInDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "startToBeDecided is never populated in draw summary and is always null",
                draw -> draw.isStartTimeTbd(),
                s -> {},
                null
            )
        );
    }

    private static Stream<? extends Arguments> nameFieldIsNeverPopulatedInDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "name is never populated in draw summary and is always null",
                draw -> draw.getName(Locale.ENGLISH),
                s -> {},
                null
            )
        );
    }

    private static Stream<? extends Arguments> replacedByFieldIsNeverPopulatedInDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "replacedBy is never populated in draw summary and is always null",
                draw -> draw.getReplacedBy(),
                s -> {},
                null
            )
        );
    }

    private static Stream<? extends Arguments> scheduledEndTimeFieldIsNeverPopulatedInDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "scheduledEndTime is never populated in draw summary and is always null",
                draw -> draw.getScheduledEndTime(),
                s -> {},
                null
            )
        );
    }

    private static Stream<Arguments> idFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "id",
                Draw::getId,
                s -> s.getDrawFixture().setId("wns:draw:1112"),
                Urn.parse("wns:draw:1112")
            )
        );
    }

    private static Stream<Arguments> lotteryIdFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "lottery id",
                draw -> draw.getLottery().getId(),
                s -> s.getDrawFixture().getLottery().setId("wns:lottery:2211"),
                Urn.parse("wns:lottery:2211")
            )
        );
    }

    private static Stream<Arguments> scheduledDateFromDrawSummary() {
        Date fixedDate = com.sportradar.utils.generic.testing.Dates.date(2025, 4, 24);
        XMLGregorianCalendar fixedXmlDate = XmlGregorianCalendars.date(2025, 4, 24);
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "draw date",
                Draw::getScheduledTime,
                s -> s.getDrawFixture().setDrawDate(fixedXmlDate),
                fixedDate
            ),
            argumentPropertyFromDrawSummary(
                "null draw date",
                Draw::getScheduledTime,
                s -> s.getDrawFixture().setDrawDate(null),
                null
            )
        );
    }

    private static Stream<Arguments> statusFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "status - closed",
                Draw::getStatus,
                s -> s.getDrawFixture().setStatus(SapiDrawStatus.CLOSED),
                DrawStatus.Closed
            ),
            argumentPropertyFromDrawSummary(
                "status - canceled",
                Draw::getStatus,
                s -> s.getDrawFixture().setStatus(SapiDrawStatus.CANCELED),
                DrawStatus.Cancelled
            ),
            argumentPropertyFromDrawSummary(
                "status - open",
                Draw::getStatus,
                s -> s.getDrawFixture().setStatus(SapiDrawStatus.OPEN),
                DrawStatus.Open
            ),
            argumentPropertyFromDrawSummary(
                "status - finished",
                Draw::getStatus,
                s -> s.getDrawFixture().setStatus(SapiDrawStatus.FINISHED),
                DrawStatus.Finished
            ),
            argumentPropertyFromDrawSummary(
                "status - null is translated as unknown",
                Draw::getStatus,
                s -> s.getDrawFixture().setStatus(null),
                DrawStatus.Unknown
            )
        );
    }

    private static Stream<Arguments> displayIdFromDrawSummary() {
        return Stream.of(
            argumentPropertyFromDrawSummary(
                "display id",
                Draw::getDisplayId,
                s -> s.getDrawFixture().setDisplayId(5),
                5
            ),
            argumentPropertyFromDrawSummary(
                "display id - null",
                Draw::getDisplayId,
                s -> s.getDrawFixture().setDisplayId(null),
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
