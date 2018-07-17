/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentGroup;
import com.sportradar.utils.URN;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A group representation used by caching components
 */
public class GroupCI {
    /**
     * The name of the group
     */
    private final String name;

    /**
     * A {@link List} of associated competitor identifiers
     */
    private final List<URN> competitorIds;

    /**
     * Initializes a new instance of the {@link GroupCI} class.
     *
     * @param group - {@link SAPITournamentGroup} containing information about the group
     * @param locale - {@link Locale} specifying the language of the <i>group</i>
     */
    public GroupCI(SAPITournamentGroup group, Locale locale) {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(locale);

        name = group.getName();

        competitorIds = new ArrayList<>();
        if (group.getCompetitor() != null) {
            competitorIds.addAll(group.getCompetitor().stream().
                    map(cmp -> URN.parse(cmp.getId())).collect(Collectors.toList()));
        }
    }

    /**
     * Merges the information from the provided {@link SAPITournamentGroup} into the current instance
     *
     * @param group - {@link SAPITournamentGroup} containing information about the group
     * @param locale - {@link Locale} specifying the language of the <i>group</i>
     */
    public void merge(SAPITournamentGroup group, Locale locale) {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(locale);

        if (group.getCompetitor() != null) {
            group.getCompetitor().forEach(mergeCompetitor -> {
                URN cId = URN.parse(mergeCompetitor.getId());
                if (!competitorIds.contains(cId)) {
                    competitorIds.add(cId);
                }
            });
        }
    }

    /**
     * Returns the name of the group
     *
     * @return - the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link List} of competitor identifiers associated with this group
     *
     * @return - a {@link List} of competitor identifiers associated with this group
     */
    public List<URN> getCompetitorIds() {
        return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
    }
}
