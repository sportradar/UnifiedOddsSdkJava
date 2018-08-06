/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.examples.replay;

import com.google.common.collect.ImmutableList;
import com.sportradar.utils.URN;

import java.util.List;

/**
 * An utility class used to access sample events
 */
public final class ExampleReplayEvents {
    private ExampleReplayEvents() {
        // static util
    }

    public static final List<SampleEvent> SAMPLE_EVENTS = ImmutableList.<SampleEvent>builder()
            .add(event("Soccer Match - English Premier League 2017 (Watford vs Westham)", "sr:match:11830662"))
            .add(event("Soccer Match w Overtime - Primavera Cup", "sr:match:12865222"))
            .add(event("Soccer Match w Overtime & Penalty Shootout - KNVB beker 17/18 - FC Twente Enschede vs Ajax Amsterdam", "sr:match:12873164"))
            .add(event("Soccer Match with Rollback Betsettlement from Prematch Producer", "sr:match:11958226"))
            .add(event("Soccer Match aborted mid-game - new match played later (first match considered cancelled according to betting rules)", "sr:match:11971876"))
            .add(event("Soccer Match w PlayerProps (prematch odds only)", "sr:match:12055466"))
            .add(event("Tennis Match - ATP Paris Final 2017", "sr:match:12927908"))
            .add(event("Tennis Match where one of the players retired", "sr:match:12675240"))
            .add(event("Tennis Match with bet_cancel adjustments using rollback_bet_cancel", "sr:match:13616027"))
            .add(event("Tennis Match w voided markets due to temporary loss of coverage - no ability to verify results", "sr:match:13600533"))
            .add(event("Basketball Match - NBA Final 2017 - (Golden State Warriors vs Cleveland Cavaliers)", "sr:match:11733773"))
            .add(event("Basketball Match w voided DrawNoBet (2nd half draw)", "sr:match:12953638"))
            .add(event("Basketball Match w PlayerProps", "sr:match:12233896"))
            .add(event("Icehockey Match - NHL Final 2017 (6th match - (Nashville Predators vs Pittsburg Penguins)", "sr:match:11784628"))
            .add(event("Icehockey Match with Rollback BetCancel", "sr:match:11878140"))
            .add(event("Icehockey Match with overtime + rollback_bet_cancel + match_status=\"aet\"", "sr:match:11878386"))
            .add(event("American Football Game - NFL 2018/2018 (Chicago Bears vs Atlanta Falcons)", "sr:match:11538563"))
            .add(event("American Football Game w PlayerProps", "sr:match:13552497"))
            .add(event("Handball Match - DHB Pokal 17/18 (SG Flensburg-Handewitt vs Fuchse Berlin)", "sr:match:12362564"))
            .add(event("Baseball Game - MLB 2017 (Final Los Angeles Dodgers vs Houston Astros)", "sr:match:12906380"))
            .add(event("Badminton Game - Indonesia Masters 2018", "sr:match:13600687"))
            .add(event("Snooker - International Championship 2017 (Final Best-of-19 frames)", "sr:match:12927314"))
            .add(event("Darts - PDC World Championship 17/18 - (Final)", "sr:match:13451765"))
            .add(event("CS:GO (ESL Pro League 2018)", "sr:match:13497893"))
            .add(event("Dota2 (The International 2017 - Final)", "sr:match:12209528"))
            .add(event("League of Legends Match (LCK Spring 2018)", "sr:match:13516251"))
            .add(event("Cricket Match [Premium Cricket] - The Ashes 2017 (Australia vs England)", "sr:match:11836360"))
            .add(event("Cricket Match (rain affected) [Premium Cricket] - ODI Series New Zealand vs. Pakistan 2018", "sr:match:13073610"))
            .add(event("Volleyball Match (includes bet_cancels)", "sr:match:12716714"))
            .add(event("Volleyball match where Betradar loses coverage mid-match - no ability to verify results", "sr:match:13582831"))
            .add(event("Aussie Rules Match (AFL 2017 Final)", "sr:match:12587650"))
            .add(event("Table Tennis Match (World Cup 2017 Final", "sr:match:12820410"))
            .add(event("Squash Match (Qatar Classic 2017)", "sr:match:12841530"))
            .add(event("Beach Volleyball", "sr:match:13682571"))
            .add(event("Badminton", "sr:match:13600687"))
            .add(event("Bowls", "sr:match:13530237"))
            .add(event("Rugby League", "sr:match:12979908"))
            .add(event("Rugby Union", "sr:match:12420636"))
            .add(event("Rugby Union 7s", "sr:match:13673067"))
            .add(event("Handball", "sr:match:12362564"))
            .add(event("Futsal", "sr:match:12363102"))
            .add(event("Golf Winner Events + Three Balls - South African Open (Winner events + Three balls)", "sr:simple_tournament:66820"))
            .add(event("Season Outrights (Long-term Outrights) - NFL 2017/18", "sr:season:40175"))
            .add(event("Race Outrights (Short-term Outrights) - Cycling Tour Down Under 2018", "sr:stage:329361"))
            .build();

    private static SampleEvent event(String description, String eventId) {
        return new SampleEvent(description, URN.parse(eventId));
    }

    public static class SampleEvent {
        private final String description;
        private final URN eventId;

        SampleEvent(String description, URN eventId) {

            this.description = description;
            this.eventId = eventId;
        }

        public String getDescription() {
            return description;
        }

        public URN getEventId() {
            return eventId;
        }

        @Override
        public String toString() {
            return String.format("EventId: '%s', Event: '%s'", eventId, description);
        }
    }
}
