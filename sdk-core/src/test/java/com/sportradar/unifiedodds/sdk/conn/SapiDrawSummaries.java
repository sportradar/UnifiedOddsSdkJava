/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiSports.WORLD_LOTTERY_SPORT_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class SapiDrawSummaries {

    @SuppressWarnings("ClassDataAbstractionCoupling")
    public static final class MarruecosKenoLottery {

        public static SapiDrawSummary marruecosKenoLotteryDrawSummary() {
            val summary = new SapiDrawSummary();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setDrawFixture(drawFixture());
            summary.setDrawResult(drawResult(ENGLISH));
            return summary;
        }

        public static SapiDrawSummary marruecosKenoLotteryDrawSummary(Locale language) {
            val summary = new SapiDrawSummary();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setDrawFixture(drawFixture());
            summary.setDrawResult(drawResult(language));
            return summary;
        }

        private static SapiDrawFixture drawFixture() {
            val fixture = new SapiDrawFixture();
            fixture.setId("wns:draw:3963074099");
            fixture.setDrawDate(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 24, 9, 53, 0)));
            fixture.setStatus(SapiDrawStatus.FINISHED);
            fixture.setDisplayId(1039);
            fixture.setLottery(lottery());
            return fixture;
        }

        private static SapiLottery lottery() {
            val lottery = new SapiLottery();
            lottery.setId("wns:lottery:131921");
            lottery.setName("Marruecos Keno 20/80");
            lottery.setSport(getSapiSport(Urn.parse(WORLD_LOTTERY_SPORT_ID), in(ENGLISH)));
            lottery.setCategory(SapiLotterySchedules.getCategory(ENGLISH));
            lottery.setDrawInfo(drawInfo());
            lottery.setBonusInfo(bonusInfo());
            return lottery;
        }

        private static SapiLottery.SapiDrawInfo drawInfo() {
            val drawInfo = new SapiLottery.SapiDrawInfo();
            drawInfo.setDrawType(SapiDrawType.RNG);
            drawInfo.setTimeType(SapiTimeType.INTERVAL);
            drawInfo.setGameType("20/80");
            return drawInfo;
        }

        private static SapiLottery.SapiBonusInfo bonusInfo() {
            val bonusInfo = new SapiLottery.SapiBonusInfo();
            bonusInfo.setBonusBalls(0);
            return bonusInfo;
        }

        private static SapiDrawResult drawResult(Locale language) {
            val drawResult = new SapiDrawResult();
            drawResult.setDraws(draws(language));
            return drawResult;
        }

        private static SapiDrawResult.SapiDraws draws(Locale language) {
            val draws = new SapiDrawResult.SapiDraws();
            draws.setChronological(false);

            String prefix;
            if (language.equals(Locale.ENGLISH)) {
                prefix = "draw_";
            } else if (language.equals(Locale.FRENCH)) {
                prefix = "tirage_";
            } else {
                throw new AssertionError("Unsupported language by test DSL " + language);
            }

            draws.getDraw().add(createDraw(prefix + "1", 5));
            draws.getDraw().add(createDraw(prefix + "2", 7));
            draws.getDraw().add(createDraw(prefix + "3", 14));
            draws.getDraw().add(createDraw(prefix + "4", 18));
            draws.getDraw().add(createDraw(prefix + "5", 22));
            draws.getDraw().add(createDraw(prefix + "6", 29));
            draws.getDraw().add(createDraw(prefix + "7", 34));
            draws.getDraw().add(createDraw(prefix + "8", 35));
            draws.getDraw().add(createDraw(prefix + "9", 37));
            draws.getDraw().add(createDraw(prefix + "10", 38));
            draws.getDraw().add(createDraw(prefix + "11", 41));
            draws.getDraw().add(createDraw(prefix + "12", 42));
            draws.getDraw().add(createDraw(prefix + "13", 47));
            draws.getDraw().add(createDraw(prefix + "14", 51));
            draws.getDraw().add(createDraw(prefix + "15", 55));
            draws.getDraw().add(createDraw(prefix + "16", 64));
            draws.getDraw().add(createDraw(prefix + "17", 65));
            draws.getDraw().add(createDraw(prefix + "18", 71));
            draws.getDraw().add(createDraw(prefix + "19", 72));
            draws.getDraw().add(createDraw(prefix + "20", 80));

            return draws;
        }

        private static SapiDrawResult.SapiDraws.SapiDraw createDraw(String name, int value) {
            val draw = new SapiDrawResult.SapiDraws.SapiDraw();
            draw.setName(name);
            draw.setValue(value);
            return draw;
        }
    }
}
