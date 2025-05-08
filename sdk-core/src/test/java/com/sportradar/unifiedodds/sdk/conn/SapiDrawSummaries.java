/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.WORLD_LOTTERY_USA_CATEGORY_ID;
import static com.sportradar.unifiedodds.sdk.SapiCategories.getSapiCategory;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.WORLD_LOTTERY_SPORT_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class SapiDrawSummaries {

    public static final class MarruecosKenoLottery {

        public static SapiDrawSummary marruecosKenoLotteryDrawSummary() {
            val summary = new SapiDrawSummary();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setDrawFixture(drawFixture());
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
            lottery.setCategory(
                getSapiCategory(Urn.parse(WORLD_LOTTERY_USA_CATEGORY_ID), in(ENGLISH)).getCategory()
            );
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
    }
}
