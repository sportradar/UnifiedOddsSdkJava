/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.sapiTeamExtended;
import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.*;
import java.util.List;
import lombok.val;

@SuppressWarnings({ "MultipleStringLiterals", "MagicNumber", "ExecutableStatementCount" })
public class SapiCompetitorProfiles {

    public static List<SapiCompetitorProfileEndpoint> profilesFromRootCompetitors(
        SapiTournamentInfoEndpoint tournamentInfo
    ) {
        val rootCompetitors = tournamentInfo.getCompetitors().getCompetitor();
        return rootCompetitors
            .stream()
            .map(c -> {
                val profile = new SapiCompetitorProfileEndpoint();
                SapiTeamExtended team = sapiTeamExtended(c);
                emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(team);
                profile.setCompetitor(team);
                return profile;
            })
            .collect(toList());
    }

    public static List<SapiCompetitorProfileEndpoint> profilesFromGroupCompetitors(
        SapiTournamentInfoEndpoint tournamentInfo
    ) {
        val groupCompetitors = tournamentInfo.getGroups() != null
            ? tournamentInfo
                .getGroups()
                .getGroup()
                .stream()
                .flatMap(g -> g.getCompetitor().stream())
                .collect(toList())
            : null;

        return groupCompetitors
            .stream()
            .map(c -> {
                val profile = new SapiCompetitorProfileEndpoint();
                SapiTeamExtended team = sapiTeamExtended(c);
                emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(team);
                profile.setCompetitor(team);
                return profile;
            })
            .collect(toList());
    }

    public static List<SapiCompetitorProfileEndpoint> profilesFromNestedTournamentCompetitors(
        SapiTournamentInfoEndpoint tournamentInfo
    ) {
        val tournamentCompetitors = tournamentInfo.getTournament().getCompetitors();
        return tournamentCompetitors
            .getCompetitor()
            .stream()
            .map(c -> {
                val profile = new SapiCompetitorProfileEndpoint();
                emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(c);
                profile.setCompetitor(sapiTeamExtended(c));
                return profile;
            })
            .collect(toList());
    }

    public static List<SapiCompetitorProfileEndpoint> profilesFromSapiMatchSummary(
        SapiMatchSummaryEndpoint matchSummary
    ) {
        return matchSummary
            .getSportEvent()
            .getCompetitors()
            .getCompetitor()
            .stream()
            .map(c -> {
                val profile = new SapiCompetitorProfileEndpoint();
                emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(c);
                profile.setCompetitor(sapiTeamExtended(c));
                return profile;
            })
            .collect(toList());
    }

    public static List<SapiCompetitorProfileEndpoint> profilesFromSapiStageSummary(
        SapiStageSummaryEndpoint stageSummary
    ) {
        return stageSummary
            .getSportEvent()
            .getCompetitors()
            .getCompetitor()
            .stream()
            .map(c -> {
                val profile = new SapiCompetitorProfileEndpoint();
                SapiTeamExtended team = sapiTeamExtended(c);
                emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(team);
                profile.setCompetitor(team);
                return profile;
            })
            .collect(toList());
    }

    private static void emptyAssociatedPlayersSinceCompetitorProfileEndpointDoesNotReturnThem(SapiTeam team) {
        team.setPlayers(null);
    }
}
