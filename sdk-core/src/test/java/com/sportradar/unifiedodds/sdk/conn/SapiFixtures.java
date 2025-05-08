/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.GERMANY_SCOTLAND_MATCH_URN;
import static java.util.Arrays.asList;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.val;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "ClassDataAbstractionCoupling" })
public class SapiFixtures {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    );

    public static SapiFixturesEndpoint soccerMatchGermanyScotlandEuro2024() {
        SapiFixture fixture = new SapiFixture();
        fixture.setStartTimeConfirmed(true);
        fixture.setStartTime(XmlGregorianCalendars.forTime(parseDateTime("2024-06-14T19:00:00+00:00")));
        fixture.setLiveodds("not_available");
        fixture.setStatus("closed");
        fixture.setNextLiveTime("2024-06-14T19:00:00+00:00");
        fixture.setId(GERMANY_SCOTLAND_MATCH_URN);
        fixture.setScheduled(XmlGregorianCalendars.forTime(parseDateTime("2024-06-14T19:00:00+00:00")));
        fixture.setStartTimeTbd(false);
        fixture.setTournamentRound(tournamentRound());
        fixture.setSeason(season());
        fixture.setTournament(tournament());
        fixture.setCompetitors(competitors());
        fixture.setVenue(venue());
        fixture.setTvChannels(tvChannels());
        fixture.setExtraInfo(extraInfo());
        fixture.setCoverageInfo(coverageInfo());
        fixture.setProductInfo(productInfo());
        fixture.setReferenceIds(referenceIds());
        val result = new SapiFixturesEndpoint();
        result.setFixture(fixture);
        return result;
    }

    private static LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    private static SapiMatchRound tournamentRound() {
        SapiMatchRound round = new SapiMatchRound();
        round.setType("group");
        round.setNumber(1);
        round.setGroupLongName("UEFA Euro, Group A");
        round.setGroup("A");
        round.setGroupId("sr:group:80247");
        round.setBetradarId(1688);
        round.setBetradarName("UEFA Euro, Group A");
        round.setPhase("group_stage");
        return round;
    }

    private static SapiSeasonExtended season() {
        SapiSeasonExtended season = new SapiSeasonExtended();
        season.setStartDate(XmlGregorianCalendars.forTime(parseDateTime("2024-06-14T00:00:00+00:00")));
        season.setEndDate(XmlGregorianCalendars.forTime(parseDateTime("2024-07-14T00:00:00+00:00")));
        season.setYear("2024");
        season.setTournamentId("sr:tournament:1");
        season.setId("sr:season:92261");
        season.setName("UEFA Euro - 2024");
        return season;
    }

    private static SapiTournament tournament() {
        SapiTournament tournament = new SapiTournament();
        tournament.setId("sr:tournament:1");
        tournament.setName("UEFA Euro");
        tournament.setSport(sport());
        tournament.setCategory(category());
        return tournament;
    }

    private static SapiSport sport() {
        SapiSport sport = new SapiSport();
        sport.setId("sr:sport:1");
        sport.setName("Soccer");
        return sport;
    }

    private static SapiCategory category() {
        SapiCategory category = new SapiCategory();
        category.setId("sr:category:4");
        category.setName("International");
        return category;
    }

    private static SapiSportEventCompetitors competitors() {
        SapiTeamCompetitor germany = new SapiTeamCompetitor();
        germany.setQualifier("home");
        germany.setId("sr:competitor:4711");
        germany.setName("Germany");
        germany.setAbbreviation("GER");
        germany.setCountry("Germany");
        germany.setCountryCode("DEU");
        germany.setGender("male");
        germany.setReferenceIds(sapiCompetitorReferenceIds(competitorReferenceId("betradar", "6171")));

        SapiTeamCompetitor scotland = new SapiTeamCompetitor();
        scotland.setQualifier("away");
        scotland.setId("sr:competitor:4695");
        scotland.setName("Scotland");
        scotland.setAbbreviation("SCO");
        scotland.setCountry("Scotland");
        scotland.setCountryCode("SCO");
        scotland.setGender("male");
        scotland.setReferenceIds(sapiCompetitorReferenceIds(competitorReferenceId("betradar", "9534")));

        val sapiCompetitors = new SapiSportEventCompetitors();
        sapiCompetitors.getCompetitor().add(germany);
        sapiCompetitors.getCompetitor().add(scotland);
        return sapiCompetitors;
    }

    private static SapiCompetitorReferenceIds sapiCompetitorReferenceIds(
        SapiCompetitorReferenceIds.SapiReferenceId referenceId
    ) {
        val ids = new SapiCompetitorReferenceIds();
        ids.getReferenceId().add(referenceId);
        return ids;
    }

    private static SapiVenue venue() {
        SapiVenue venue = new SapiVenue();
        venue.setId("sr:venue:574");
        venue.setName("Allianz Arena");
        venue.setCapacity(75000);
        venue.setCityName("Munich");
        venue.setCountryName("Germany");
        venue.setCountryCode("DEU");
        venue.setMapCoordinates("48.2188,11.6247");
        return venue;
    }

    private static SapiTvChannels tvChannels() {
        val channels = new SapiTvChannels();
        channels
            .getTvChannel()
            .addAll(
                asList(
                    tvChannel("SRF zwei HD - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("TF1 HD FR - Astra 1C-1H / 2C (19.2E)"),
                    tvChannel("SKY Sport 251 HD - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("Sky Sport Uno HD - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("Sky Sport Calcio HD - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("Rai 1 - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("Sport TV1 HD - Hispasat 1B/1C/1D (30.0 W) - NOS PT"),
                    tvChannel("ZDF HD - Astra 1C-1H / 2C (19.2E)"),
                    tvChannel("ERT1 HD - Hot Bird 1/2/3/4/6 (13.0E)"),
                    tvChannel("beIN Sports 1 HD FR - Astra 1C-1H / 2C (19.2E)"),
                    tvChannel("M4 Sport HD - Thor 2/3 (1.0W)"),
                    tvChannel("FOX US"),
                    tvChannel("ServusTV HD Oesterreich - Astra 1C-1H / 2C (19.2E)"),
                    tvChannel("TRT 1 HD - Türksat 1C / Eurasiasat 1 (42.0E)"),
                    tvChannel("TRT1 HD - Eutelsat W3A (7.0E)"),
                    tvChannel("TV4 SE HD - Thor 2/3 (1.0W)"),
                    tvChannel("Fubo TV"),
                    tvChannel("ViX+")
                )
            );
        return channels;
    }

    private static SapiTvChannel tvChannel(String name) {
        SapiTvChannel channel = new SapiTvChannel();
        channel.setName(name);
        return channel;
    }

    private static SapiExtraInfo extraInfo() {
        val extraInfo = new SapiExtraInfo();
        extraInfo
            .getInfo()
            .addAll(
                asList(
                    extraInfo("RTS", "not_available"),
                    extraInfo("coverage_source", "venue"),
                    extraInfo("extended_live_markets_offered", "true"),
                    extraInfo("streaming", "false"),
                    extraInfo("auto_traded", "false"),
                    extraInfo("neutral_ground", "false"),
                    extraInfo("period_length", "45"),
                    extraInfo("early_ctrl_settlements", "true")
                )
            );
        return extraInfo;
    }

    private static SapiInfo extraInfo(String key, String value) {
        SapiInfo info = new SapiInfo();
        info.setKey(key);
        info.setValue(value);
        return info;
    }

    private static SapiCoverageInfo coverageInfo() {
        SapiCoverageInfo info = new SapiCoverageInfo();
        info.setLevel("gold");
        info.setLiveCoverage(true);
        info.setCoveredFrom("venue");
        info
            .getCoverage()
            .addAll(
                asList(
                    coverage("basic_score"),
                    coverage("key_events"),
                    coverage("detailed_events"),
                    coverage("lineups"),
                    coverage("commentary"),
                    coverage("extended_markets")
                )
            );
        return info;
    }

    private static SapiCoverage coverage(String includes) {
        SapiCoverage coverage = new SapiCoverage();
        coverage.setIncludes(includes);
        return coverage;
    }

    private static SapiProductInfo productInfo() {
        SapiProductInfo info = new SapiProductInfo();
        info.setIsInLiveScore(new SapiProductInfoItem());
        info.setIsInHostedStatistics(new SapiProductInfoItem());
        info.setIsInLiveCenterSoccer(new SapiProductInfoItem());
        info.setIsInLiveMatchTracker(new SapiProductInfoItem());
        info.setLinks(new SapiProductInfoLinks());
        info
            .getLinks()
            .getLink()
            .add(
                productLink(
                    "live_match_tracker",
                    "https://widgets.sir.sportradar.com/sportradar/en/standalone/match.lmtPlus#matchId=45870785"
                )
            );
        return info;
    }

    private static SapiProductInfoLink productLink(String name, String ref) {
        SapiProductInfoLink link = new SapiProductInfoLink();
        link.setName(name);
        link.setRef(ref);
        return link;
    }

    private static SapiReferenceIds referenceIds() {
        val referenceIds = new SapiReferenceIds();
        referenceIds
            .getReferenceId()
            .addAll(
                asList(matchReferenceId("BetradarCtrl", "111539556"), matchReferenceId("aams", "3248142"))
            );
        return referenceIds;
    }

    private static SapiCompetitorReferenceIds.SapiReferenceId competitorReferenceId(
        String name,
        String value
    ) {
        SapiCompetitorReferenceIds.SapiReferenceId id = new SapiCompetitorReferenceIds.SapiReferenceId();
        id.setName(name);
        id.setValue(value);
        return id;
    }

    private static SapiReferenceIds.SapiReferenceId matchReferenceId(String name, String value) {
        SapiReferenceIds.SapiReferenceId id = new SapiReferenceIds.SapiReferenceId();
        id.setName(name);
        id.setValue(value);
        return id;
    }
}
