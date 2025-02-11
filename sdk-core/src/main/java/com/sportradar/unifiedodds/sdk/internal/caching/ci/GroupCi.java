/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentGroup;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableGroupCi;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A group representation used by caching components
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class GroupCi {

    /**
     * The id of the group
     */
    private final String id;

    /**
     * The name of the group
     */
    private final String name;

    /**
     * A {@link List} of associated competitor identifiers
     */
    private final List<Urn> competitorIds;

    /**
     * A {@link Map} of competitors id and their references that participate in the sport event
     * associated with the current instance
     */
    private Map<Urn, ReferenceIdCi> competitorsReferences;

    /**
     * Initializes a new instance of the {@link GroupCi} class.
     *
     * @param group - {@link SapiTournamentGroup} containing information about the group
     * @param locale - {@link Locale} specifying the language of the <i>group</i>
     */
    public GroupCi(SapiTournamentGroup group, Locale locale) {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(locale);

        id = group.getId();
        name = group.getName();

        competitorIds = new ArrayList<>();
        competitorsReferences = new HashMap<>();
        if (group.getCompetitor() != null) {
            competitorIds.addAll(
                group.getCompetitor().stream().map(cmp -> Urn.parse(cmp.getId())).collect(Collectors.toList())
            );
            competitorsReferences =
                SdkHelper.parseCompetitorsReferences(group.getCompetitor(), competitorsReferences);
        }
    }

    public GroupCi(ExportableGroupCi exportable) {
        Preconditions.checkNotNull(exportable);

        id = exportable.getId();
        name = exportable.getName();
        competitorIds = exportable.getCompetitorIds().stream().map(Urn::parse).collect(Collectors.toList());
        competitorsReferences =
            exportable
                .getCompetitorsReferences()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(r -> Urn.parse(r.getKey()), r -> new ReferenceIdCi(r.getValue())));
    }

    /**
     * Merges the information from the provided {@link SapiTournamentGroup} into the current instance
     *
     * @param group - {@link SapiTournamentGroup} containing information about the group
     * @param locale - {@link Locale} specifying the language of the <i>group</i>
     */
    public void merge(SapiTournamentGroup group, Locale locale) {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(locale);

        if (group.getCompetitor() != null) {
            if (competitorIds != null && (competitorIds.size() != group.getCompetitor().size())) {
                competitorIds.clear();
            }
            group
                .getCompetitor()
                .forEach(c -> {
                    if (!competitorIds.contains(Urn.parse(c.getId()))) {
                        competitorIds.clear();
                    }
                });
            group
                .getCompetitor()
                .forEach(mergeCompetitor -> {
                    Urn cId = Urn.parse(mergeCompetitor.getId());
                    if (!competitorIds.contains(cId)) {
                        competitorIds.add(cId);
                    }
                });
            competitorsReferences =
                SdkHelper.parseCompetitorsReferences(group.getCompetitor(), competitorsReferences);
        } else if (competitorIds.size() > 0) {
            competitorIds.clear();
            competitorsReferences.clear();
        }
    }

    /**
     * Returns the id of the group
     *
     * @return - the id of the group
     */
    public String getId() {
        return id;
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
    public List<Urn> getCompetitorIds() {
        return competitorIds == null ? null : ImmutableList.copyOf(competitorIds);
    }

    /**
     * Returns list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     *
     * @return list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    public Map<Urn, ReferenceIdCi> getCompetitorsReferences() {
        return competitorsReferences == null ? null : ImmutableMap.copyOf(competitorsReferences);
    }

    public ExportableGroupCi export() {
        return new ExportableGroupCi(
            id,
            name,
            competitorIds.stream().map(Urn::toString).collect(Collectors.toList()),
            competitorsReferences
                .entrySet()
                .stream()
                .collect(Collectors.toMap(c -> c.getKey().toString(), c -> c.getValue().getReferenceIds()))
        );
    }
}
