/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings(
    {
        "MagicNumber",
        "ClassDataAbstractionCoupling",
        "MultipleStringLiterals",
        "ExecutableStatementCount",
        "ClassFanOutComplexity",
        "MethodLength",
    }
)
public class SapiMatchTimelines {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    );

    public static final class Soccer {

        public static final class FkTosnoGuorKarelia {

            public static SapiMatchTimelineEndpoint fkTosnoGuorKareliaMatchTimeline() {
                val timeline = new SapiMatchTimelineEndpoint();
                timeline.setGeneratedAt(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:29:02+00:00"))
                );
                timeline.setSportEvent(sportEvent());
                timeline.setSportEventConditions(new SapiSportEventConditions());
                timeline.setSportEventStatus(sportEventStatus());
                timeline.setTimeline(timelineEvents());
                return timeline;
            }

            private static SapiSportEvent sportEvent() {
                val sportEvent = new SapiSportEvent();
                sportEvent.setId("sr:match:63629821");
                sportEvent.setScheduled(XmlGregorianCalendars.now());
                sportEvent.setStartTimeTbd(false);
                sportEvent.setTournamentRound(tournamentRound());
                sportEvent.setTournament(tournament());
                sportEvent.setCompetitors(competitors());
                return sportEvent;
            }

            private static SapiMatchRound tournamentRound() {
                val round = new SapiMatchRound();
                round.setBetradarId(16315);
                round.setBetradarName("Zone North-West");
                return round;
            }

            private static SapiTournament tournament() {
                val tournament = new SapiTournament();
                tournament.setId("sr:simple_tournament:16315");
                tournament.setName("Zone North-West");
                tournament.setSport(sport());
                tournament.setCategory(category());
                return tournament;
            }

            private static SapiSport sport() {
                val sport = new SapiSport();
                sport.setId("sr:sport:1");
                sport.setName("Soccer");
                return sport;
            }

            private static SapiCategory category() {
                val category = new SapiCategory();
                category.setId("sr:category:21");
                category.setName("Russia");
                category.setCountryCode("RUS");
                return category;
            }

            private static SapiSportEventCompetitors competitors() {
                val homeCompetitor = new SapiTeamCompetitor();
                homeCompetitor.setQualifier("home");
                homeCompetitor.setId("sr:competitor:107219");
                homeCompetitor.setName("FK Tosno");
                homeCompetitor.setAbbreviation("TOS");
                homeCompetitor.setCountry("Russia");
                homeCompetitor.setCountryCode("RUS");
                homeCompetitor.setGender("male");
                homeCompetitor.setReferenceIds(homeCompetitorReferenceIds());

                val awayCompetitor = new SapiTeamCompetitor();
                awayCompetitor.setQualifier("away");
                awayCompetitor.setId("sr:competitor:1269770");
                awayCompetitor.setName("Guor Karelia");
                awayCompetitor.setAbbreviation("GUO");
                awayCompetitor.setCountry("Russia");
                awayCompetitor.setCountryCode("RUS");
                awayCompetitor.setGender("male");
                awayCompetitor.setReferenceIds(awayCompetitorReferenceIds());

                val competitors = new SapiSportEventCompetitors();
                competitors.getCompetitor().add(homeCompetitor);
                competitors.getCompetitor().add(awayCompetitor);
                return competitors;
            }

            private static SapiCompetitorReferenceIds homeCompetitorReferenceIds() {
                val referenceId = new SapiCompetitorReferenceIds.SapiReferenceId();
                referenceId.setName("betradar");
                referenceId.setValue("25160633");

                val referenceIds = new SapiCompetitorReferenceIds();
                referenceIds.getReferenceId().add(referenceId);
                return referenceIds;
            }

            private static SapiCompetitorReferenceIds awayCompetitorReferenceIds() {
                val referenceId = new SapiCompetitorReferenceIds.SapiReferenceId();
                referenceId.setName("betradar");
                referenceId.setValue("24896299");

                val referenceIds = new SapiCompetitorReferenceIds();
                referenceIds.getReferenceId().add(referenceId);
                return referenceIds;
            }

            private static SapiSportEventStatus sportEventStatus() {
                val status = new SapiSportEventStatus();
                status.setHomeScore("1");
                status.setAwayScore("0");
                status.setStatusCode(1);
                status.setMatchStatusCode(6);
                status.setStatus("live");
                status.setMatchStatus("1st_half");
                status.setPeriod(1);
                return status;
            }

            private static SapiTimeline timelineEvents() {
                val timeline = new SapiTimeline();
                timeline.getEvent().add(matchStartedEvent());
                timeline.getEvent().add(periondStartEvent());
                timeline.getEvent().add(firstCornerKickEvent());
                timeline.getEvent().add(scoreChangeEvent());
                timeline.getEvent().add(secondCornerKickEvent());

                return timeline;
            }

            @NotNull
            private static SapiBasicEvent secondCornerKickEvent() {
                val cornerKick2 = new SapiBasicEvent();
                cornerKick2.setId(2147138477L);
                cornerKick2.setType("corner_kick");
                cornerKick2.setTime(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:24:31+00:00"))
                );
                cornerKick2.setMatchTime(21);
                cornerKick2.setMatchClock("20:47");
                cornerKick2.setTeam("home");
                return cornerKick2;
            }

            @NotNull
            private static SapiBasicEvent scoreChangeEvent() {
                val scoreChange = new SapiBasicEvent();
                scoreChange.setId(2147145833L);
                scoreChange.setType("score_change");
                scoreChange.setTime(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:11:33+00:00"))
                );
                scoreChange.setMatchTime(8);
                scoreChange.setMatchClock("7:42");
                scoreChange.setTeam("home");
                scoreChange.setX(95);
                scoreChange.setY(47);
                scoreChange.setHomeScore("1");
                scoreChange.setAwayScore("0");
                return scoreChange;
            }

            @NotNull
            private static SapiBasicEvent firstCornerKickEvent() {
                val cornerKick1 = new SapiBasicEvent();
                cornerKick1.setId(2147146581L);
                cornerKick1.setType("corner_kick");
                cornerKick1.setTime(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:10:10+00:00"))
                );
                cornerKick1.setMatchTime(7);
                cornerKick1.setMatchClock("6:26");
                cornerKick1.setTeam("home");
                return cornerKick1;
            }

            @NotNull
            private static SapiBasicEvent periondStartEvent() {
                val periodStart = new SapiBasicEvent();
                periodStart.setId(2147150475L);
                periodStart.setType("period_start");
                periodStart.setTime(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:03:45+00:00"))
                );
                periodStart.setPeriodName("1st half");
                periodStart.setPeriod("1");
                periodStart.setMatchStatusCode(6);
                return periodStart;
            }

            @NotNull
            private static SapiBasicEvent matchStartedEvent() {
                val matchStarted = new SapiBasicEvent();
                matchStarted.setId(2147150479L);
                matchStarted.setType("match_started");
                matchStarted.setTime(
                    XmlGregorianCalendars.forTime(parseDateTime("2025-09-23T11:03:45+00:00"))
                );
                return matchStarted;
            }

            private static LocalDateTime parseDateTime(String dateTime) {
                return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
            }
        }
    }
}
