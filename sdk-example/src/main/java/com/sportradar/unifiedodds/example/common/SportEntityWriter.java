/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * A simple demo entity utility which extracts data from the provided entities
 */
public class SportEntityWriter {
    private List<Locale> locales;
    private Locale defaultLocale;
    private boolean writeNonCacheableData;
    private boolean writeLog;
    private final Logger logger;

    public SportEntityWriter(List<Locale> locales, boolean writeNonCacheableData, boolean writeLog){
        logger = LoggerFactory.getLogger(this.getClass().getName());
        this.locales = locales;
        this.writeNonCacheableData = writeNonCacheableData;
        this.writeLog = writeLog;
        this.defaultLocale = locales.stream().findFirst().orElse(Locale.ENGLISH);
    }

    /**
     * Builds and returns a description for the provided {@link SportEvent}
     *
     * @param event the {@link SportEvent} from which to compose the description
     * @return a {@link String} describing the provided {@link SportEvent}
     */
    public String writeBaseEventData(SportEvent event) {
        return String.format("Id:'%s', SportId:'%s', Name:'%s', ScheduledTime:'%s', ScheduledEndTime:'%s'",
                event.getId(),
                event.getSportId(),
                event.getName(defaultLocale),
                event.getScheduledTime(),
                event.getScheduledEndTime());
    }

    /**
     * Builds and returns a description for the provided {@link Competition}
     *
     * @param event the {@link Competition} from which to compose the description
     * @return a {@link String} describing the provided {@link Competition}
     */
    public String writeData(Competition event) {
        String baselineDescription = writeBaseEventData(event);

        if(event.getStatus() != null)
        {
            EventStatus sesStatus = event.getStatus().getStatus();

            if(event.getEventStatus() != sesStatus) {
                logger.warn(String.format("%s: status mismatch: ES:%s != SES:%s ", event.getId(), event.getEventStatus(), sesStatus));
            }
            if(event.getStatus() instanceof MatchStatus) {
                MatchStatus matchStatus = (MatchStatus) event.getStatus();
                if(matchStatus != null && matchStatus.getMatchStatus()!=null) {
                    logger.debug(String.format("%s: status: ES:%s || SES:%s || MS:%s-%s ", event.getId(), event.getEventStatus(), sesStatus, matchStatus.getMatchStatus().getId(), matchStatus.getMatchStatus().getDescription()));
                }
            }
        }

        return baselineDescription + String.format(", Status:[%s], EventStatus:%s, BookingStatus:%s, %s, %s",
                writeData(event.getStatus()),
                event.getEventStatus(),
                event.getBookingStatus(),
                event.getVenue(),
                event.getConditions());
    }

    /**
     * Builds and returns a description for the provided {@link Stage}
     *
     * @param event the {@link Stage} from which to compose the description
     * @return a {@link String} describing the provided {@link Stage}
     */
    public String writeData(Stage event) {
        String baselineDescription = writeData((Competition) event);

        StringJoiner stages = new StringJoiner(",");
        if (event.getStages() != null) {
            event.getStages().forEach(s -> stages.add(s.getId().toString()));
        }

        return String.format("Stage[%s, Category:[%s], Sport:[%s], Parent:'%s', StageType:%s, Stages:[%s]]",
                baselineDescription,
                event.getCategory(),
                event.getSport(),
//                event.getParentStage() != null ? writeData(event.getParentStage()) : "no parent event",
                event.getParentStage() != null ? event.getParentStage().getId() : "no parent event",
                event.getStageType(),
                stages.toString());
    }

    /**
     * Builds and returns a description for the provided {@link Match}
     *
     * @param event the {@link Match} from which to compose the description
     * @return a {@link String} describing the provided {@link Match}
     */
    public String writeData(Match event) {
        String baselineDescription = writeData((Competition) event);

        return String.format("Match[%s, HomeCompetitor:[%s], AwayCompetitor:[%s], Season:[%s], TournamentRound:[%s], Status:[%s]]",
                baselineDescription,
                writeData(event.getHomeCompetitor()),
                writeData(event.getAwayCompetitor()),
                writeData(event.getSeason()),
                writeData(event.getTournamentRound()),
                writeData(event.getStatus()));
    }

    /**
     * Builds and returns a description for the provided {@link SoccerEvent}
     *
     * @param event the {@link SoccerEvent} from which to compose the description
     * @return a {@link String} describing the provided {@link SoccerEvent}
     */
    public String writeData(SoccerEvent event) {
        String baselineDescription = writeData((Match) event);

        return String.format("SoccerEvent-%s, Status:'%s']",
                baselineDescription,
                writeData(event.getStatus()));
    }

    /**
     * Builds and returns a description for the provided {@link Tournament}
     *
     * @param tournament the {@link Tournament} from which to compose the description
     * @return a {@link String} describing the provided {@link Tournament}
     */
    public String writeData(Tournament tournament, boolean onlyBasics) {
        if (tournament == null) {
            return "Missing tournament data";
        }

        String baseline = writeBaseEventData(tournament);
        if (onlyBasics) {
            return baseline;
        }

        String competitorsStr = "";
        if(tournament.getCurrentSeason() != null)
        {
            competitorsStr = writeData(tournament.getCurrentSeason().getCompetitors(), true);
        }

        return String.format("Tournament[%s, Category:'%s', CurrentSeasonInfo:'%s', %s]",
                baseline,
                tournament.getCategory(),
                tournament.getCurrentSeason(),
                competitorsStr);
    }

    /**
     * Builds and returns a description for the provided {@link Season}
     *
     * @param season the {@link Season} from which to compose the description
     * @return a {@link String} describing the provided {@link Season}
     */
    public String writeData(Season season) {
        String baselineDescription = writeBaseEventData(season);

        String groups = null;
        if (season.getGroups() != null) {
            StringJoiner sj = new StringJoiner(",");
            season.getGroups().forEach(g -> sj.add(String.format("Name:'%s', CompetitorsCount:'%s'",
                    g.getName(),
                    g.getCompetitors() == null ? 0 : g.getCompetitors().size())));
            groups = sj.toString();
        }

        return String.format("Season[%s, id:'%s', name:'%s', year:'%s', groups:'%s', coverage:'%s']",
                baselineDescription,
                season.getId(),
                season.getName(defaultLocale),
                season.getYear(),
                groups == null ? "no groups" : groups,
                season.getSeasonCoverage());
    }

    /**
     * Builds and returns a description for the provided {@link BasicTournament}
     *
     * @param tournament the {@link BasicTournament} from which to build the string
     * @return a {@link String} describing the provided {@link BasicTournament}
     */
    public String writeData(BasicTournament tournament) {
        String baselineDescription = writeBaseEventData(tournament);

        return String.format("BasicTournament[%s, Sport:[%s], Category:[%s], %s, Coverage:[%s]]",
                baselineDescription,
                tournament.getSport(),
                tournament.getCategory(),
                writeData(tournament.getCompetitors(), true),
                tournament.getTournamentCoverage());
    }

    /**
     * Builds and returns a description for the provided {@link Draw}
     *
     * @param draw the {@link Draw} from which to build the string
     * @return a {@link String} describing the provided {@link Draw}
     */
    public String writeData(Draw draw) {
        String baselineDescription = writeBaseEventData(draw);

        return baselineDescription + "\n\t" + String.format("Draw{id:''}",
                draw.getLottery(),
                draw.getId(),
                draw.getStatus(),
                draw.getResults() == null ? "unknown" : draw.getResults().size());
    }

    /**
     * Builds and returns a description for the provided {@link Lottery}
     *
     * @param lottery the {@link Lottery} from which to build the string
     * @return a {@link String} describing the provided {@link Lottery}
     */
    public String writeData(Lottery lottery) {
        String baselineDescription = writeBaseEventData(lottery);

        return baselineDescription + "\n\t" + String.format("Lottery{id:''}",
                lottery.getBonusInfo(),
                lottery.getId(),
                lottery.getCategory(),
                lottery.getDrawInfo(),
                lottery.getScheduledDraws() == null ? "unknown" : lottery.getScheduledDraws().size());
    }

    /**
     * Builds and returns a description for the provided {@link Round}
     *
     * @param tournamentRound the {@link Round} from which to compose the description
     * @return a {@link String} describing the provided {@link Round}
     */
    private String writeData(Round tournamentRound) {
        if (tournamentRound == null) {
            return "No tournament round info available";
        }

        return String.format("Name:'%s', GroupName:'%s', GroupId:'%s', Type:%s, Number:%s", tournamentRound.getName(defaultLocale), tournamentRound.getGroupName(), tournamentRound.getGroupId(), tournamentRound.getType(), tournamentRound.getNumber());
    }

    /**
     * Builds and returns a description for the provided {@link SeasonInfo}
     *
     * @param season the {@link SeasonInfo} from which to compose the description
     * @return a {@link String} describing the provided {@link SeasonInfo}
     */
    private String writeData(SeasonInfo season) {
        if (season == null) {
            return "null";
        }

        return String.format("%s[%s]  %s - s%", season.getName(defaultLocale), season.getId(), season.getStartDate(), season.getEndDate());
    }

    /**
     * Builds and returns a description for the provided {@link Competitor}
     *
     * @param competitor the {@link Competitor} from which to compose the description
     * @return a {@link String} describing the provided {@link Competitor}
     */
    private String writeData(Competitor competitor) {
        return String.format("Id:%s, Name:%s, Abr:%s, Country:%s, CountryCode:%s, IsVirtual:%s, References:[%s]",
                competitor.getId(),
                competitor.getName(defaultLocale),
                competitor.getAbbreviation(defaultLocale),
                competitor.getCountry(defaultLocale),
                competitor.getCountryCode(),
                competitor.isVirtual(),
                writeData(competitor.getReferences()));
    }

    /**
     * Builds and returns a description for the provided {@link TeamCompetitor}
     *
     * @param competitor the {@link TeamCompetitor} from which to compose the description
     * @return a {@link String} describing the provided {@link TeamCompetitor}
     */
    private String writeData(TeamCompetitor competitor) {
        return String.format("%s, Qualifier:'%s'", writeData((Competitor) competitor), competitor.getQualifier());
    }

    /**
     * Builds and returns a description for the provided {@link Reference}
     *
     * @param reference the {@link Reference} from which to compose the description
     * @return a {@link String} describing the provided {@link Reference}
     */
    private String writeData(Reference reference) {
        if(reference == null) {
            return "";
        }
        return String.format("BetfairId:%s, BetradarId:%s, RN:%s",
                reference.getBetfairId(),
                reference.getBetradarId(),
                reference.getRotationNumber());
    }

    private String writeData(List<Competitor> competitors, boolean full)
    {
        if(competitors == null)
        {
            return "Competitors:[]";
        }
        StringJoiner sj = new StringJoiner(" | ");
        competitors.forEach(competitor ->
        {
            if(full) {
                sj.add(writeData(competitor));
            }
            else
            {
                sj.add(String.format("Id:%s, Name:%s", competitor.getId(), competitor.getName(defaultLocale)));
            }
        });
        return String.format("Competitors:[%s]", sj.toString());
    }

    /**
     * Builds and returns a description for the provided {@link CompetitionStatus}
     *
     * @param status the {@link CompetitionStatus} from which to compose the description
     * @return a {@link String} describing the provided {@link CompetitionStatus}
     */
    private String writeData(CompetitionStatus status) {
        if(status == null){
            return null;
        }
        return String.format("Status:%s, ReportingStatus:%s, WinnerId:%s", status.getStatus(), status.getReportingStatus(), status.getWinnerId());
    }

    /**
     * Builds and returns a description for the provided {@link MatchStatus}
     *
     * @param status the {@link MatchStatus} from which to compose the description
     * @return a {@link String} describing the provided {@link MatchStatus}
     */
    private String writeData(MatchStatus status) {
        if(status == null){
            return null;
        }
        return writeData((CompetitionStatus) status) + ", " + String.format("HomeScore:%s, AwayScore:%s", status.getHomeScore(), status.getAwayScore());
    }

    /**
     * Builds and returns a description for the provided {@link SoccerStatus}
     *
     * @param status the {@link SoccerStatus} from which to compose the description
     * @return a {@link String} describing the provided {@link SoccerStatus}
     */
    private String writeData(SoccerStatus status) {
        return writeData((MatchStatus) status) + ", " + String.format("Statistics:'%s'", writeData(status.getStatistics()));
    }

    /**
     * Builds and returns a description for the provided {@link SoccerStatistics}
     *
     * @param statistics the {@link SoccerStatistics} from which to compose the description
     * @return a {@link String} describing the provided {@link SoccerStatistics}
     */
    private String writeData(SoccerStatistics statistics) {
        if(statistics == null) {
            return "No statistics";
        }

        String totalStats = null;
        if (statistics.getTotalStatistics() != null) {
            StringJoiner sj = new StringJoiner(",");
            statistics.getTotalStatistics().forEach(s -> sj.add(String.format("[HomeAway:'%s', YellowCards:'%s', RedCards:'%s', CornerKicks:'%s']", s.getHomeAway(), s.getYellowCards(), s.getRedCards(), s.getCornerKicks())));
            totalStats = sj.toString();
        }

        return String.format("TotalStatistics:'%s', PeriodStatistics:'%s'",
                totalStats == null ? "No total statistics" : totalStats,
                statistics.getPeriodStatistics() == null ? "No period statistics" : statistics.getPeriodStatistics().size() + " periods available");
    }
    
    public void writeData(SportEvent sportEvent) {
        String description = null;
        if (sportEvent != null) {
            // the same kind of entities are returned on the callbacks of the OddsFeedListener/MessageListener
            if (sportEvent instanceof Tournament) {
                description = writeData((Tournament) sportEvent, false);
            } else if (sportEvent instanceof BasicTournament) {
                description = writeData((BasicTournament) sportEvent);
            }  else if (sportEvent instanceof Season) {
                description = writeData((Season) sportEvent);
            } else if (sportEvent instanceof SoccerEvent) {
                description = writeData((SoccerEvent) sportEvent);
            } else if (sportEvent instanceof Match) {
                description = writeData((Match) sportEvent);
            } else if (sportEvent instanceof Stage) {
                description = writeData((Stage) sportEvent);
            } else if (sportEvent instanceof Lottery) {
                description = writeData((Lottery) sportEvent);
            } else if (sportEvent instanceof Draw) {
                description = writeData((Draw) sportEvent);
            } else {
                description = writeBaseEventData(sportEvent);
            }
        }

        if(description == null)
        {
            description = "Sport event data was not found for id: " + sportEvent;
        }

        writeMessage(description);
    }

    public static String writeSportEventData(SportEvent sportEvent, boolean writeLog, List<Locale> desiredLocales) {
        String description = null;
        if (sportEvent != null) {
            SportEntityWriter sportEntityWriter = new SportEntityWriter(desiredLocales, false, writeLog);

            if (sportEvent instanceof Tournament) {
                description = sportEntityWriter.writeData((Tournament) sportEvent, false);
            } else if (sportEvent instanceof BasicTournament) {
                description = sportEntityWriter.writeData((BasicTournament) sportEvent);
            }  else if (sportEvent instanceof Season) {
                description = sportEntityWriter.writeData((Season) sportEvent);
            } else if (sportEvent instanceof SoccerEvent) {
                description = sportEntityWriter.writeData((SoccerEvent) sportEvent);
            } else if (sportEvent instanceof Match) {
                description = sportEntityWriter.writeData((Match) sportEvent);
            } else if (sportEvent instanceof Stage) {
                description = sportEntityWriter.writeData((Stage) sportEvent);
            } else if (sportEvent instanceof Lottery) {
                description = sportEntityWriter.writeData((Lottery) sportEvent);
            } else if (sportEvent instanceof Draw) {
                description = sportEntityWriter.writeData((Draw) sportEvent);
            } else {
                description = sportEntityWriter.writeBaseEventData(sportEvent);
            }
            sportEntityWriter.writeMessage(description);
        }

        return description;
    }

    public void writeMessage(String message)
    {
        if (writeLog) {
            logger.info(message);
        } else {
            System.out.print(message);
        }
    }
}
