/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.ci.GroupCi;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Group;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a competition group
 */
@SuppressWarnings({ "ConstantName", "UnnecessaryParentheses" })
public class GroupImpl implements Group {

    private static final Logger logger = LoggerFactory.getLogger(GroupImpl.class);
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
     * A {@link List} of available translation{@link Locale}s
     */
    private final List<Locale> locales;

    /**
     * The factory used to build competitor instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * The exception strategy that should be used to handle errors
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new instance of the {@link GroupImpl} class
     * @param groupCi - a {@link GroupCi} used to create new instance
     * @param locales - a {@link List} in which is provided the {@link GroupCi}
     * @param sportEntityFactory - the factory used to build competitor instances
     * @param exceptionHandlingStrategy - the exception strategy that should be used to handle errors
     */
    GroupImpl(
        GroupCi groupCi,
        List<Locale> locales,
        SportEntityFactory sportEntityFactory,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(groupCi);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = groupCi.getId();
        this.name = groupCi.getName();
        this.competitorIds = groupCi.getCompetitorIds();
        this.locales = locales;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns the name of the group
     *
     * @return - the name of the group
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns an unmodifiable {@link List} representing group competitors
     * @see com.google.common.collect.ImmutableList
     *
     * @return - an unmodifiable {@link List} representing group competitors(if available); otherwise null
     */
    @Override
    public List<Competitor> getCompetitors() {
        try {
            return competitorIds == null
                ? null
                : competitorIds
                    .stream()
                    .map(c -> {
                        try {
                            return sportEntityFactory.buildCompetitor(c, null, null, null, null, locales);
                        } catch (ObjectNotFoundException e) {
                            throw new StreamWrapperException(e.getMessage(), e);
                        }
                    })
                    .collect(ImmutableList.toImmutableList());
        } catch (StreamWrapperException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(
                    "Group competitors could not be provided",
                    e
                );
            } else {
                logger.warn("Group competitors could not be provided", e);
            }
        }
        return null;
    }

    /**
     * Returns the id of the group
     *
     * @return - the id of the group
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns a {@link String} describing the current {@link Group} instance
     *
     * @return - a {@link String} describing the current {@link Group} instance
     */
    @Override
    public String toString() {
        return (
            "GroupImpl{" +
            "id='" +
            id +
            '\'' +
            "name='" +
            name +
            '\'' +
            ", competitorIds=" +
            competitorIds +
            '}'
        );
    }
}
