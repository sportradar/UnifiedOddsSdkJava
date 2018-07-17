/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * An interface providing methods to access group details
 */
public interface Group {
    /**
     * Returns the name of the group
     *
     * @return - the name of the group
     */
    String getName();

    /**
     * Returns an unmodifiable {@link List} representing group competitors
     *
     * @return - an unmodifiable {@link List} representing group competitors(if available); otherwise null
     */
    List<Competitor> getCompetitors();
}
