/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.entities.Reference;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An utility class that contains various methods which perform common language tasks
 */
public final class SdkHelper {
    /**
     * Calculates and returns the missing locales within the provided {@link List}
     *
     * @param have - a {@link List} that contains all the available locales
     * @param want - a {@link List} of locales that are required
     * @return - returns a {@link List} of missing locales
     */
    public static List<Locale> findMissingLocales(Collection<Locale> have, List<Locale> want) {
        Preconditions.checkNotNull(have);
        Preconditions.checkNotNull(want);

        return want.stream().filter(locale -> !have.contains(locale)).collect(Collectors.toList());
    }

    /**
     * Filters out the translated data not needed
     *
     * @param data the data to be filtered
     * @param filterLocales the requested locales
     * @return the filtered map data set
     */
    public static Map<Locale, String> filterLocales(Map<Locale, String> data, List<Locale> filterLocales) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(filterLocales);

        return data.entrySet().stream()
                .filter(entry ->
                        filterLocales.contains(entry.getKey()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue)
                );
    }

    /**
     * Get the abbreviation from the input string
     * @param input input text to be abbreviated
     * @param length of the abbreviation
     * @return the abbreviated input string in upper case (it does not return null)
     */
    public static String getAbbreviationFromName(String input, int length)
    {
        if (length < 1)
        {
            length = Integer.MAX_VALUE;
        }
        if (input == null || input.isEmpty())
        {
            return "";
        }
        return input.length() > length ? input.substring(0, length).toUpperCase() : input.toUpperCase();
    }

    /**
     * Get competitor reference from a list of competitors
     * Note: reference must be checked and updated, since it is not sure that references on summary are the same as on fixture
     * @param competitors competitor id with which is associated reference
     * @return map of references per competitor id
     */
    public static Map<URN, ReferenceIdCI> ParseCompetitorsReferences(List<SAPITeam> competitors,
                                                                         Map<URN, ReferenceIdCI> currentCompetitorsReferences)
    {
        if(competitors == null){
            return currentCompetitorsReferences;
        }

        Map<URN, ReferenceIdCI> competitorsReferences = currentCompetitorsReferences == null
                ? new HashMap<>()
                : currentCompetitorsReferences;
        for (SAPITeam competitor : competitors) {
            if(competitor.getReferenceIds() != null) {
                ReferenceIdCI newReferenceId = new ReferenceIdCI(competitor.getReferenceIds().getReferenceId().stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(HashMap::new, (map, i) -> map.put(i.getName(), i.getValue()), HashMap::putAll));

                if(newReferenceId==null) {
                    continue;
                }

                URN competitorId = URN.parse(competitor.getId());
                if(competitorsReferences.containsKey(competitorId)) {
                    ReferenceIdCI oldReference = competitorsReferences.get(competitorId);
                    oldReference.merge(newReferenceId.getReferenceIds());
                    competitorsReferences.put(competitorId, newReferenceId);
                }
                else {
                    competitorsReferences.put(competitorId, newReferenceId);
                }
            }
        }

        return competitorsReferences;
    }

    /**
     * Get competitor reference from a list of competitors
     * Note: reference must be checked and updated, since it is not sure that references on summary are the same as on fixture
     * @param competitors competitor id with which is associated reference
     * @return map of references per competitor id
     */
    public static Map<URN, ReferenceIdCI> ParseTeamCompetitorsReferences(List<SAPITeamCompetitor> competitors,
                                                                         Map<URN, ReferenceIdCI> currentCompetitorsReferences)
    {
        if(competitors == null){
            return currentCompetitorsReferences;
        }

        Map<URN, ReferenceIdCI> competitorsReferences = currentCompetitorsReferences == null
                ? new HashMap<>()
                : currentCompetitorsReferences;
        for (SAPITeam competitor : competitors) {
            if(competitor.getReferenceIds() != null) {
                ReferenceIdCI newReferenceId = new ReferenceIdCI(competitor.getReferenceIds().getReferenceId().stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(HashMap::new, (map, i) -> map.put(i.getName(), i.getValue()), HashMap::putAll));

                if(newReferenceId==null) {
                    continue;
                }

                URN competitorId = URN.parse(competitor.getId());
                if(competitorsReferences.containsKey(competitorId)) {
                    ReferenceIdCI oldReference = competitorsReferences.get(competitorId);
                    oldReference.merge(newReferenceId.getReferenceIds());
                    competitorsReferences.put(competitorId, newReferenceId);
                }
                else {
                    competitorsReferences.put(competitorId, newReferenceId);
                }
            }
        }

        return competitorsReferences;
    }

    public static Locale checkConfigurationLocales(Locale defaultLocale, Set<Locale> supportedLocales) {
        if (defaultLocale == null && !supportedLocales.isEmpty())
        {
            defaultLocale = supportedLocales.iterator().next();
        }
        if (!supportedLocales.contains(defaultLocale))
        {
            supportedLocales.add(defaultLocale);
        }

        if (defaultLocale == null)
        {
            throw new InvalidParameterException("Missing default locale");
        }
        if (supportedLocales == null || supportedLocales.isEmpty())
        {
            throw new InvalidParameterException("Missing supported locales");
        }
        return defaultLocale;
    }
}
