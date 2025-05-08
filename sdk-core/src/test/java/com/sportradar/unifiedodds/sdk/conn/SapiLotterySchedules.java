/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.utils.domain.names.LanguageHolder.in;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class SapiLotterySchedules {

    public static SapiLotterySchedule marruecosKenoLottery() {
        val schedule = new SapiLotterySchedule();
        schedule.setLottery(lottery(Locale.ENGLISH));
        schedule.setDrawEvents(drawEvents());
        return schedule;
    }

    private static SapiLottery lottery(Locale language) {
        val lottery = new SapiLottery();
        lottery.setId("wns:lottery:131921");
        lottery.setName("Marruecos Keno 20/80");
        lottery.setSport(getSapiSport(Urn.parse("sr:sport:108"), in(language)));
        lottery.setCategory(
            SapiCategories.getSapiCategory(Urn.parse("sr:category:1039"), in(language)).getCategory()
        );
        lottery.setDrawInfo(drawInfo());
        lottery.setBonusInfo(bonusInfo());
        return lottery;
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
}
