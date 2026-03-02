/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@SuppressWarnings({ "MagicNumber", "ClassDataAbstractionCoupling" })
public class SapiLotterySchedules {

    public static final String MARROCO_CATEGORY_ID = "sr:category:1252";

    public static SapiSportAndCategoryExtractorFromLotterySchedule sportAndCategoryFrom(
        SapiLotterySchedule schedule
    ) {
        return new SapiSportAndCategoryExtractorFromLotterySchedule(schedule);
    }

    public static SapiLotterySchedule marruecosKenoLottery() {
        return marruecosKenoLottery(ENGLISH);
    }

    public static SapiLotterySchedule marruecosKenoLottery(Locale language) {
        val schedule = new SapiLotterySchedule();
        schedule.setLottery(lottery(language));
        schedule.setDrawEvents(drawEvents());
        return schedule;
    }

    private static SapiLottery lottery(Locale language) {
        val lottery = new SapiLottery();
        lottery.setId("wns:lottery:131921");
        String name = TranslatedLottery.forLanguage(language).getName();
        lottery.setName(name);
        lottery.setSport(getSapiSport(Urn.parse("sr:sport:108"), in(language)));
        lottery.setCategory(getCategory(language));
        lottery.setDrawInfo(drawInfo());
        lottery.setBonusInfo(bonusInfo());
        return lottery;
    }

    public static SapiCategory getCategory(Locale language) {
        SapiCategory sapiCategory = new SapiCategory();
        sapiCategory.setId(MARROCO_CATEGORY_ID);
        sapiCategory.setName(TranslatedLottery.forLanguage(language).getCategoryName());
        return sapiCategory;
    }

    private static SapiLottery.SapiBonusInfo bonusInfo() {
        val bonusInfo = new SapiLottery.SapiBonusInfo();
        bonusInfo.setBonusBalls(0);
        return bonusInfo;
    }

    private static SapiLottery.SapiDrawInfo drawInfo() {
        val drawInfo = new SapiLottery.SapiDrawInfo();
        drawInfo.setDrawType(SapiDrawType.RNG);
        drawInfo.setTimeType(SapiTimeType.INTERVAL);
        drawInfo.setGameType("20/80");
        return drawInfo;
    }

    private static SapiDrawEvents drawEvents() {
        val events = new SapiDrawEvents();
        events.getDrawEvent().add(firstDraw());
        events.getDrawEvent().add(secondDraw());
        events.getDrawEvent().add(thirdDraw());
        return events;
    }

    private static SapiDrawEvent firstDraw() {
        val drawEvent = new SapiDrawEvent();
        drawEvent.setId("wns:draw:3963074099");
        drawEvent.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 24, 9, 53, 0)));
        drawEvent.setStatus(SapiDrawStatus.FINISHED);
        drawEvent.setDisplayId(1039);
        return drawEvent;
    }

    private static SapiDrawEvent secondDraw() {
        val drawEvent = new SapiDrawEvent();
        drawEvent.setId("wns:draw:3963074132");
        drawEvent.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 24, 9, 58, 0)));
        drawEvent.setStatus(SapiDrawStatus.FINISHED);
        drawEvent.setDisplayId(1050);
        return drawEvent;
    }

    private static SapiDrawEvent thirdDraw() {
        val drawEvent = new SapiDrawEvent();
        drawEvent.setId("wns:draw:3963074168");
        drawEvent.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 24, 10, 3, 0)));
        drawEvent.setStatus(SapiDrawStatus.FINISHED);
        drawEvent.setDisplayId(1062);
        return drawEvent;
    }

    @RequiredArgsConstructor
    public static class SapiSportAndCategoryExtractorFromLotterySchedule
        implements SportsDataCaches.SapiSportAndCategory {

        private final SapiLotterySchedule schedule;

        @Override
        public SapiSport getSport() {
            return schedule.getLottery().getSport();
        }

        @Override
        public SapiCategory getCategory() {
            return schedule.getLottery().getCategory();
        }
    }

    @RequiredArgsConstructor
    @Getter
    private enum TranslatedLottery {
        EN(ENGLISH, "Marruecos Keno 20/80", "Marroco"),
        FR(Locale.FRENCH, "Maroc Keno 20/80", "Maroc");

        private final Locale language;

        @Getter
        private final String name;

        @Getter
        private final String categoryName;

        public static TranslatedLottery forLanguage(Locale language) {
            return Arrays
                .stream(values())
                .filter(t -> t.language.equals(language))
                .findFirst()
                .orElseThrow(() ->
                    new AssertionError(
                        "Test DSL does not support language " +
                        language +
                        " when generating Lottery Schedules"
                    )
                );
        }
    }
}
