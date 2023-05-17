/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.BetstopReasonsDescriptions;
import com.sportradar.uf.sportsapi.datamodel.BettingStatusDescriptions;
import com.sportradar.uf.sportsapi.datamodel.MatchStatusDescriptions;
import com.sportradar.uf.sportsapi.datamodel.VoidReasonsDescriptions;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

/**
 * A simple key-value representation used by the caching components
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "ReturnCount" })
public class NamedValueCI {

    /**
     * The id of the current instance
     */
    private final int id;

    /**
     * The description of the current instance
     */
    private final String description;

    /**
     * Initializes a new instance of the {@link NamedValueCI}
     *
     * @param id - the identifier
     * @param description - the description of the instance
     */
    public NamedValueCI(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * The id of the current instance
     *
     * @return - the id of the current instance
     */
    public int getId() {
        return id;
    }

    /**
     * The description of the current instance
     *
     * @return - the description of the current instance
     */
    public String getDescription() {
        return description;
    }

    /**
     * Maps a fetched schema object to a new {@link NamedValueCI}
     *
     * @param fetch - the fetched schema object
     * @param <T> - the type of the fetched schema object
     * @return - a {@link List} of mapped objects
     */
    public static <T> List<NamedValueCI> mapToNamedValuesCI(T fetch) {
        if (fetch instanceof MatchStatusDescriptions) {
            return ((MatchStatusDescriptions) fetch).getMatchStatus()
                .stream()
                .map(v -> new NamedValueCI(Math.toIntExact(v.getId()), v.getDescription()))
                .collect(Collectors.toList());
        } else if (fetch instanceof VoidReasonsDescriptions) {
            return ((VoidReasonsDescriptions) fetch).getVoidReason()
                .stream()
                .map(v -> new NamedValueCI(Math.toIntExact(v.getId()), v.getDescription()))
                .collect(Collectors.toList());
        } else if (fetch instanceof BetstopReasonsDescriptions) {
            return ((BetstopReasonsDescriptions) fetch).getBetstopReason()
                .stream()
                .map(v -> new NamedValueCI(Math.toIntExact(v.getId()), v.getDescription()))
                .collect(Collectors.toList());
        } else if (fetch instanceof BettingStatusDescriptions) {
            return ((BettingStatusDescriptions) fetch).getBettingStatus()
                .stream()
                .map(v -> new NamedValueCI(Math.toIntExact(v.getId()), v.getDescription()))
                .collect(Collectors.toList());
        }
        LoggerFactory
            .getLogger(NamedValueCI.class)
            .error("Mapping unknown fetched API object >>> " + fetch.getClass().getName());
        return Collections.emptyList();
    }
}
